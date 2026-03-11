package com.hmwl.test;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.model.Bucket;
import com.qcloud.cos.region.Region;

import java.util.List;

public class CosTest {
    public static void main(String[] args) {
        String secretId = System.getProperty("cos.secretId", System.getenv("COS_SECRET_ID"));
        String secretKey = System.getProperty("cos.secretKey", System.getenv("COS_SECRET_KEY"));
        String region = System.getProperty("cos.region", "ap-shanghai");
        String bucketName = System.getProperty("cos.bucketName", "jxcwzk-1316797858");

        if (secretId == null || secretId.isEmpty() || secretKey == null || secretKey.isEmpty()) {
            System.out.println("错误: 请通过 -D 参数或环境变量传入 COS 凭证");
            System.out.println("示例: java -Dcos.secretId=YOUR_SECRET_ID -Dcos.secretKey=YOUR_SECRET_KEY CosTest");
            System.exit(1);
        }

        BasicCOSCredentials credentials = new BasicCOSCredentials(secretId, secretKey);
        ClientConfig clientConfig = new ClientConfig(new Region(region));
        COSClient cosClient = new COSClient(credentials, clientConfig);

        try {
            System.out.println("测试列出存储桶...");
            List<Bucket> buckets = cosClient.listBuckets();
            System.out.println("存储桶列表:");
            for (Bucket bucket : buckets) {
                System.out.println("- " + bucket.getName());
            }

            System.out.println("\n测试存储桶是否存在...");
            boolean bucketExists = cosClient.doesBucketExist(bucketName);
            System.out.println("存储桶 " + bucketName + " 是否存在: " + bucketExists);

            System.out.println("\nCOS连接测试成功!");
        } catch (Exception e) {
            System.out.println("COS连接测试失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cosClient.shutdown();
        }
    }
}
