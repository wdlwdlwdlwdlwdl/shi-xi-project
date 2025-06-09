package com.aliyun.gts.gmall.manager.front.b2bcomm.controller;


import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.manager.front.b2bcomm.input.OssPolicyReq;
import com.aliyun.gts.gmall.manager.front.b2bcomm.input.OssQueryReq;
import com.aliyun.gts.gmall.middleware.oss.GmallOssClient;
import com.aliyun.gts.gmall.middleware.oss.OssPolicyDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 */
@RestController
@RequestMapping("/api/oss")
public class OssController {

    @Autowired
    GmallOssClient publicGmallOssClient;

    @Value("${oss.bucket.sourcing:sourceCommon}")
    String sourcingBucket;
    /**
     * 100m
     */
    @Value("${oss.fileMax:104857600}")
    long fileMax;

    @RequestMapping("/sourcing/getPolicy")
    public RestResponse<OssPolicyDO> getPolicy(@RequestBody OssPolicyReq req) {
        return RestResponse.okWithoutMsg(publicGmallOssClient.generatePostObject(sourcingBucket,
            req.getFileName(),fileMax));
    }

//    @RequestMapping("/getUrl")
//    public RestResponse<String> getOssHttpUrl(@RequestBody OssQueryReq req){
//        return RestResponse.okWithoutMsg(publicGmallOssClient.getFileHttpUrl(req.getOssUrl() , true));
//    }
}
