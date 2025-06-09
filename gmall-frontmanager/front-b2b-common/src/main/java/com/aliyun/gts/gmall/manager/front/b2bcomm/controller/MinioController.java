package com.aliyun.gts.gmall.manager.front.b2bcomm.controller;


import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.manager.front.b2bcomm.input.OssPolicyReq;
import com.aliyun.gts.gmall.middleware.minio.GmallMinioClient;
import com.aliyun.gts.gmall.middleware.minio.MinioPolicyDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 */
@RestController
@RequestMapping("/api/minio")
public class MinioController {

    @Autowired(required = false)
    GmallMinioClient publicGmallMinioClient;

    @Value("${minio.bucket.sourcing:sourceCommon}")
    String sourcingBucket;
    /**
     * 100m
     */
    @Value("${minio.fileMax:104857600}")
    long fileMax;

    @RequestMapping("/sourcing/getPolicy")
    public RestResponse<MinioPolicyDO> getPolicy(@RequestBody OssPolicyReq req) {
        return RestResponse.okWithoutMsg(publicGmallMinioClient.generatePostObject(sourcingBucket,
            req.getFileName(), fileMax));
    }

}
