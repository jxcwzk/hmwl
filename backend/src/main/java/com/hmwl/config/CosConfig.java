package com.hmwl.config;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.region.Region;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
public class CosConfig {

    @Value("${cos.secretId:}")
    private String secretId;
    
    @Value("${cos.secretKey:}")
    private String secretKey;
    
    @Value("${cos.region:}")
    private String region;
    
    @Bean
    public COSClient cosClient() {
        if (secretId == null || secretId.isEmpty() || secretId.equals("YOUR_COS_SECRET_ID") ||
            secretKey == null || secretKey.isEmpty() || secretKey.equals("YOUR_COS_SECRET_KEY")) {
            System.out.println("WARNING: COS credentials not configured or using placeholder values. COS upload functionality will be disabled.");
            return null;
        }
        BasicCOSCredentials credentials = new BasicCOSCredentials(secretId, secretKey);
        ClientConfig clientConfig = new ClientConfig(new Region(region));
        return new COSClient(credentials, clientConfig);
    }
}
