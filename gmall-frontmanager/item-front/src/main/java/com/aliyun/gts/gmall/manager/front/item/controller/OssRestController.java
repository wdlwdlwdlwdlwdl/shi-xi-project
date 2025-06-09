package com.aliyun.gts.gmall.manager.front.item.controller;

import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.manager.front.common.dto.input.OssPolicyRestQuery;
import com.aliyun.gts.gmall.manager.front.common.dto.input.OssQueryReq;
import com.aliyun.gts.gmall.manager.front.item.adaptor.FileMiddlewareAdaptor;
import com.aliyun.gts.gmall.manager.front.item.dto.utils.ItemFrontResponseCode;
import com.aliyun.gts.gmall.middleware.oss.GmallOssClient;
import com.aliyun.gts.gmall.middleware.oss.OssPolicyDO;
import io.swagger.annotations.Api;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * oss文件上传
 *
 * @author tiansong
 */
@Api(value = "oss文件上传")
@RestController
public class OssRestController {

    @Resource
    GmallOssClient publicGmallOssClient;

    @Autowired
    private FileMiddlewareAdaptor fileMiddlewareAdaptor;

    @RequestMapping("/api/item/getPolicy")
    public RestResponse<OssPolicyDO> getPolicy(@RequestBody OssPolicyRestQuery ossPolicyRestQuery) {
        String bucket = fileMiddlewareAdaptor.getBucket(ossPolicyRestQuery.getBizType());
        String dir = fileMiddlewareAdaptor.getBucketDir(ossPolicyRestQuery.getBizType());
        if (StringUtils.isBlank(bucket)) {
            throw new GmallException(ItemFrontResponseCode.OSS_BUCKET_EMPTY);
        }
        return RestResponse.okWithoutMsg(publicGmallOssClient.generatePostObject(bucket, dir , ossPolicyRestQuery.getFileName(), fileMiddlewareAdaptor.getFileMax()));
    }

    @RequestMapping("/api/oss/getUrl")
    public RestResponse<String> getOssHttpUrl(@RequestBody OssQueryReq req){
        return RestResponse.okWithoutMsg(publicGmallOssClient.getFileHttpUrl(req.getOssUrl() , true));
    }
}