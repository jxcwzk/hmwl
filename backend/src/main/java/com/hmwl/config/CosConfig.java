package com.hmwl.config;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.region.Region;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CosConfig {

    @Value("${cos.secretId:${COS_SECRET_ID}}")
    private String secretId;
    
    @Value("${cos.secretKey:${COS_SECRET_KEY}}")
    private String secretKey;
    
    @Value("${cos.region}")
    private String region;
    
    @Bean
    public COSClient cosClient() {
        if (secretId == null || secretId.startsWith("${") || secretKey == null || secretKey.startsWith("${")) {
            throw new IllegalStateException("COS credentials not configured. Set environment variables COS_SECRET_ID and COS_SECRET_KEY, or configure in application-dev.yml");
        }
        BasicCOSCredentials credentials = new BasicCOSCredentials(secretId, secretKey);
        ClientConfig clientConfig = new ClientConfig(new Region(region));
        return new COSClient(credentials, clientConfig);
    }
}
