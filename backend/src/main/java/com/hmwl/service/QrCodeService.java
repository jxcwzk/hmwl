package com.hmwl.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class QrCodeService {

    @Autowired
    private COSClient cosClient;

    @Value("${cos.bucketName}")
    private String bucketName;

    @Value("${cos.baseUrl}")
    private String baseUrl;

    public String generateAndUploadQrCode(String orderNo) {
        try {
            byte[] qrCodeImage = generateQrCodeImage(orderNo);
            String fileName = "qrcode/" + orderNo + "_" + UUID.randomUUID().toString().substring(0, 8) + ".png";
            
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileName, 
                new ByteArrayInputStream(qrCodeImage), null);
            PutObjectResult result = cosClient.putObject(putObjectRequest);
            
            return baseUrl + "/" + fileName;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("生成二维码失败: " + e.getMessage());
        }
    }

    private byte[] generateQrCodeImage(String content) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 1);
        
        BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, 300, 300, hints);
        
        return createPngFromMatrix(bitMatrix);
    }

    private byte[] createPngFromMatrix(BitMatrix matrix) throws IOException {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        
        java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(width, height, 
            java.awt.image.BufferedImage.TYPE_INT_RGB);
        
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? 0x000000 : 0xFFFFFF);
            }
        }
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        javax.imageio.ImageIO.write(image, "png", baos);
        return baos.toByteArray();
    }
}
