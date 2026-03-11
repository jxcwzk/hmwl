package com.hmwl.service.impl;

import com.hmwl.entity.SenderImage;
import com.hmwl.mapper.SenderImageMapper;
import com.hmwl.service.SenderImageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class SenderImageServiceImpl extends ServiceImpl<SenderImageMapper, SenderImage> implements SenderImageService {

    @Autowired
    private COSClient cosClient;
    
    @Value("${cos.bucketName}")
    private String bucketName;
    
    @Value("${cos.baseUrl}")
    private String baseUrl;

    @Override
    public SenderImage uploadToCos(Long orderId, String orderNo, MultipartFile file) throws Exception {
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String timestamp = String.valueOf(System.currentTimeMillis());
        String filename = "sender/" + orderNo + "/" + "sender_" + orderNo + "_" + timestamp + suffix;

        try {
            System.out.println("开始上传发货单图片到COS，bucketName: " + bucketName);
            System.out.println("文件名: " + filename);
            
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, filename, file.getInputStream(), null);
            PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
            
            System.out.println("上传到COS成功，ETag: " + putObjectResult.getETag());

            String imageUrl = baseUrl + "/" + filename;
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
        return baseMapper.selectList(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SenderImage>()
                .eq(SenderImage::getOrderId, orderId)
                .eq(SenderImage::getStatus, 1));
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
            cosClient.deleteObject(bucketName, cosKey);
        }

        return removeById(id);
    }
}
