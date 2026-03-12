package com.hmwl.service.impl;

import com.hmwl.entity.SenderImage;
import com.hmwl.mapper.SenderImageMapper;
import com.hmwl.service.SenderImageService;
import com.hmwl.utils.ImageCompressUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.GeneratePresignedUrlRequest;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.http.HttpMethodName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.List;

@Service
public class SenderImageServiceImpl extends ServiceImpl<SenderImageMapper, SenderImage> implements SenderImageService {

    @Autowired
    private COSClient cosClient;
    
    @Value("${cos.bucketName}")
    private String bucketName;
    
    @Value("${cos.baseUrl}")
    private String baseUrl;
    
    @Value("${cos.secretId}")
    private String secretId;
    
    @Value("${cos.secretKey}")
    private String secretKey;
    
    @Value("${cos.region:ap-shanghai}")
    private String region;

    private String generatePresignedUrl(String key) {
        try {
            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, key, HttpMethodName.GET);
            Date expiration = new Date(System.currentTimeMillis() + 3600L * 1000 * 24 * 7);
            request.setExpiration(expiration);
            request.addRequestParameter("host", bucketName + ".cos." + region + ".myqcloud.com");
            URL url = cosClient.generatePresignedUrl(request);
            return url.toString();
        } catch (Exception e) {
            System.out.println("生成签名URL失败: " + e.getMessage());
            e.printStackTrace();
            return baseUrl + "/" + key;
        }
    }

    @Override
    public SenderImage uploadToCos(Long orderId, String orderNo, MultipartFile file) throws Exception {
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String timestamp = String.valueOf(System.currentTimeMillis());
        String filename = "sender/" + orderNo + "/" + "sender_" + orderNo + "_" + timestamp + suffix;

        try {
            System.out.println("开始上传发货单图片到COS，bucketName: " + bucketName);
            System.out.println("文件名: " + filename);
            System.out.println("原始文件大小: " + file.getSize() + " bytes (" + (file.getSize() / 1024.0 / 1024.0) + " MB)");

            InputStream inputStream = ImageCompressUtil.compressImageIfNeeded(file);

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, filename, inputStream, null);
            PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
            
            System.out.println("上传到COS成功，ETag: " + putObjectResult.getETag());

            String imageUrl = generatePresignedUrl(filename);
            System.out.println("图片URL: " + imageUrl);

            SenderImage senderImage = new SenderImage();
            senderImage.setOrderId(orderId);
            senderImage.setImageUrl(imageUrl);
            senderImage.setStatus(1);
            senderImage.setCreateTime(new java.util.Date());
            senderImage.setUpdateTime(new java.util.Date());
            save(senderImage);

            return senderImage;
        } catch (Exception e) {
            System.out.println("上传到COS失败: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public List<SenderImage> listByOrderId(Long orderId) {
        List<SenderImage> list = baseMapper.selectList(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SenderImage>()
                .eq(SenderImage::getOrderId, orderId)
                .eq(SenderImage::getStatus, 1));
        
        for (SenderImage image : list) {
            String imageUrl = image.getImageUrl();
            if (imageUrl != null && imageUrl.startsWith(baseUrl)) {
                String cosKey = imageUrl.substring(baseUrl.length() + 1);
                image.setImageUrl(generatePresignedUrl(cosKey));
            }
        }
        return list;
    }

    @Override
    public boolean deleteImage(Long id) throws Exception {
        SenderImage senderImage = baseMapper.selectById(id);
        if (senderImage == null) {
            throw new Exception("图片不存在");
        }

        String imageUrl = senderImage.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty() && imageUrl.startsWith(baseUrl)) {
            String cosKey = imageUrl.substring(baseUrl.length() + 1);
            if (!cosKey.contains("?")) {
                cosKey = imageUrl.substring(baseUrl.length() + 1, imageUrl.indexOf("?"));
            }
            cosClient.deleteObject(bucketName, cosKey);
        }

        return removeById(id);
    }
}
