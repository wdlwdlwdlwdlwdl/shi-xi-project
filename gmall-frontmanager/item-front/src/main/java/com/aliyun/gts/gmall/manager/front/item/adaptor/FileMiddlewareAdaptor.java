package com.aliyun.gts.gmall.manager.front.item.adaptor;


import com.aliyun.gts.gmall.manager.utils.OssTypeEnum;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 文件中间件处理器
 * @anthor shifeng
 * @version 1.0.1
 */
@Service
public class FileMiddlewareAdaptor {

    // 会员上传
    @Value("${oss.enabled:false}")
    @Getter
    private boolean ossEnabled;

    // 会员上传
    @Value("${oss.bucket.custLogo:gmall-custlogo-dev}")
    @Getter
    private String ossCustomerLogoBucket;
    @Value("${oss.bucket.custLogoDir:custlogo}")
    @Getter
    private String ossCustomerLogoDir;

    // 评价上传
    @Value("${oss.bucket.custEvaluation:gmall-evaluation-dev}")
    @Getter
    private String ossEvaluationBucket;
    @Value("${oss.bucket.custEvaluationDir:evaluation}")
    @Getter
    private String ossCustEvaluationDir;

    //转换
    @Value("${oss.bucket.custReversal:gmall-reversal-dev}")
    @Getter
    private String ossCustReversalBucket;
    @Value("${oss.bucket.custReversalDir:custReversal}")
    @Getter
    private String ossCustReversalDir;

    // 申请
    @Value("${oss.bucket.custApply:gmall-apply-dev}")
    @Getter
    private String ossCustApplyBucket;
    @Value("${oss.bucket.custApplyDir:custApply}")
    @Getter
    private String ossApplyDir;

    @Value("${oss.fileMax:104857600}")
    @Getter
    private long ossFileMax;

    // 会员上传
    @Value("${minio.enabled:false}")
    @Getter
    private boolean minioEnabled;
    // 会员上传
    @Value("${minio.bucket.custLogo:gmall-custlogo-dev}")
    @Getter
    private String minioCustomerLogoBucket;
    @Value("${minio.bucket.custLogoDir:custLogo}")
    @Getter
    private String minioCustomerLogoDir;

    // 评价上传
    @Value("${minio.bucket.custEvaluation:gmall-evaluation-dev}")
    @Getter
    private String minioEvaluationBucket;
    @Value("${minio.bucket.custEvaluationDir:evaluation}")
    @Getter
    private String minioEvaluationDir;

    //转换
    @Value("${minio.bucket.custReversal:gmall-reversal-dev}")
    @Getter
    private String minioReversalBucket;
    @Value("${minio.bucket.custReversalDir:custReversal}")
    @Getter
    private String minioReversalDir;

    @Getter
    @Value("${minio.bucket.custApply:gmall-apply-dev}")
    private String minioApplyBucket;
    @Value("${minio.bucket.custApplyDir:custApply}")
    @Getter
    private String minioApplyDir;

    /**
     * 100m
     */
    @Value("${minio.fileMax:104857600}")
    @Getter
    private long minioFileMax;

    // 会员上传桶
    public String getCustLogo() {
        return ossEnabled ? ossCustomerLogoBucket : minioCustomerLogoBucket;
    }

    // 会员上传文件夹
    public String getCustLogoDir() {
        return ossEnabled ? ossCustomerLogoDir : minioCustomerLogoDir;
    }

    // 评价桶
    public String getCustEvaluation() {
        return ossEnabled ? ossEvaluationBucket : minioEvaluationBucket;
    }

    // 评价文件件
    public String getCustEvaluationDir() {
        return ossEnabled ? ossCustEvaluationDir : minioEvaluationDir;
    }

    //
    public String getCustReversal() {
        return ossEnabled ? ossCustReversalBucket : minioReversalBucket;
    }

    // 评价文件件
    public String getCustReversalDir() {
        return ossEnabled ? ossCustReversalDir : minioReversalDir;
    }

    // 评价桶
    public String getCustApply() {
        return ossEnabled ? ossCustApplyBucket : minioApplyBucket;
    }

    // 评价文件件
    public String getCustApplyDir() {
        return ossEnabled ? ossApplyDir : minioApplyDir;
    }

    public long getFileMax() {
        return ossEnabled ? ossFileMax : minioFileMax;
    }


    /**
     * 获取业务桶
     * @param bizType
     * @return
     */
    public String getBucket(OssTypeEnum bizType) {
        if (OssTypeEnum.CUSTOMER_LOGO.equals(bizType)) {
           return getCustLogo();
        }
        if (OssTypeEnum.EVALUATION.equals(bizType)) {
            return getCustEvaluation();
        }
        if (OssTypeEnum.REVERSAL.equals(bizType)) {
            return getCustReversal();
        }
        if (OssTypeEnum.APPLY.equals(bizType)) {
            return getCustApply();
        }
        return "";
    }

    /**
     * 获取业务文件夹
     * @param bizType
     * @return
     */
    public String getBucketDir(OssTypeEnum bizType) {
        if (OssTypeEnum.CUSTOMER_LOGO.equals(bizType)) {
            return getCustLogoDir();
        }
        if (OssTypeEnum.EVALUATION.equals(bizType)) {
            return getCustEvaluationDir();
        }
        if (OssTypeEnum.REVERSAL.equals(bizType)) {
            return getCustReversalDir();
        }
        if (OssTypeEnum.APPLY.equals(bizType)) {
            return getCustApplyDir();
        }
        return "";
    }




}
