package org.example.tiktokproject.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;

public class OssUtils {

    // 创建 OSSClient 实例
    public static OSS createOSSClient() {
        return new OSSClientBuilder().build(ENDPOINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
    }
}