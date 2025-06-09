package com.aliyun.gts.gmall.manager.front.item.controller;

import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.manager.front.common.dto.input.OssPolicyRestQuery;
import com.aliyun.gts.gmall.manager.front.common.dto.input.OssQueryReq;
import com.aliyun.gts.gmall.manager.front.item.adaptor.FileMiddlewareAdaptor;
import com.aliyun.gts.gmall.manager.front.item.dto.utils.ItemFrontResponseCode;
import com.aliyun.gts.gmall.middleware.minio.GmallMinioClient;
import com.aliyun.gts.gmall.middleware.minio.MinioPolicyDO;
import io.swagger.annotations.Api;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * minio文件上传
 * @author tiansong
 */
@Api(value = "minio文件上传")
@RestController
public class MinioRestController {

    @Autowired(required = false)
    GmallMinioClient publicGmallMinioClient;

    @Autowired
    private FileMiddlewareAdaptor fileMiddlewareAdaptor;

    @RequestMapping("/api/item/getMinioPolicy")
    public RestResponse<MinioPolicyDO> getPolicy(@RequestBody OssPolicyRestQuery minioPolicyRestQuery) {
        String bucket = fileMiddlewareAdaptor.getBucket(minioPolicyRestQuery.getBizType());
        String dir = fileMiddlewareAdaptor.getBucketDir(minioPolicyRestQuery.getBizType());
        if (StringUtils.isBlank(bucket)) {
            throw new GmallException(ItemFrontResponseCode.OSS_BUCKET_EMPTY);
        }
        return RestResponse.okWithoutMsg(
                publicGmallMinioClient.generatePostObject(bucket, dir , minioPolicyRestQuery.getFileName(), fileMiddlewareAdaptor.getFileMax()));
    }

    @RequestMapping("/api/minio/getUrl")
    public RestResponse<String> getOssHttpUrl(@RequestBody OssQueryReq req){
        return RestResponse.okWithoutMsg(publicGmallMinioClient.getFileHttpUrl(req.getMinioUrl() , true));
    }
}