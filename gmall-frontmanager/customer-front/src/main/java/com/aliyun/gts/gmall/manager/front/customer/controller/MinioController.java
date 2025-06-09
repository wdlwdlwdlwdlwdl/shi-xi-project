package com.aliyun.gts.gmall.manager.front.customer.controller;

import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.framework.server.permission.Permission;
import com.aliyun.gts.gmall.manager.front.common.dto.input.OssPolicyRestQuery;
import com.aliyun.gts.gmall.manager.utils.OssTypeEnum;
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
@RequestMapping(value = {"/api/minio"})
public class MinioController {

    @Autowired(required = false)
    private GmallMinioClient gmallMinioClient;

    @Value("${oss.fileMax:104857600}")
    private long fileMax;

    @Value("${minio.bucket.evaluation:}")
    private String evaluationBucket;

    @Value("${minio.bucket.evaluationDir:}")
    private String evaluationDir;


    @Value("${minio.bucket.customer:}")
    private String customerBucket;

    @Value("${minio.bucket.customerDir:}")
    private String customerDir;

    /**
     * minio文件上传
     * @param ossPolicyRestQuery
     * @return
     */
    @RequestMapping("/getPolicy")
    public RestResponse<MinioPolicyDO> getPolicy(@RequestBody OssPolicyRestQuery ossPolicyRestQuery) {
        String bucket = "";
        String dir = "";
        if(ossPolicyRestQuery.getBizType() == OssTypeEnum.EVALUATION){
            bucket = evaluationBucket;
            dir = evaluationDir;
        }else if (ossPolicyRestQuery.getBizType() == OssTypeEnum.CUSTOMER){
            bucket = customerBucket;
            dir = customerDir;
        }
        return RestResponse.okWithoutMsg(gmallMinioClient.generatePostObject(bucket , dir, ossPolicyRestQuery.getFileName() , fileMax));
    }
}
