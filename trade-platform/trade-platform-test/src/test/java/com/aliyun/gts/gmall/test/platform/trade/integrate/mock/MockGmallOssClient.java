package com.aliyun.gts.gmall.test.platform.trade.integrate.mock;

import com.aliyun.gts.gmall.middleware.oss.GmallOssClient;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.PutObjectResult;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class MockGmallOssClient extends GmallOssClient {

    public MockGmallOssClient() {
        super("127.0.0.1", "MOCK", "MOCK");
    }

    @Override
    public OSSClient getOssClient() {
        return new OSSClient("127.0.0.1", "MOCK", "MOCK") {
            @Override
            public PutObjectResult putObject(String bucketName, String key, InputStream input) throws OSSException, ClientException {
                return null;
            }
        };
    }
}
