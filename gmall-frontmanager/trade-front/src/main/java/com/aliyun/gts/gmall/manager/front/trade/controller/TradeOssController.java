package com.aliyun.gts.gmall.manager.front.trade.controller;

import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.manager.front.common.dto.input.OssPolicyRestQuery;
import com.aliyun.gts.gmall.manager.front.common.dto.input.OssQueryReq;
import com.aliyun.gts.gmall.manager.front.trade.dto.utils.TradeFrontResponseCode;
import com.aliyun.gts.gmall.manager.utils.OssTypeEnum;
import com.aliyun.gts.gmall.middleware.oss.GmallOssClient;
import com.aliyun.gts.gmall.middleware.oss.OssPolicyDO;
import io.swagger.annotations.Api;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(value = "交易oss接口")
@RestController
public class TradeOssController {

    @Resource
    GmallOssClient publicGmallOssClient;

    @Value("${oss.bucket.tradeVoucher:trade-voucher}")
    private String voucherBucket;

    @Value("${oss.bucket.tradeVoucherDir:trade/voucher}")
    private String voucherDir;

    /**
     * 100m
     */
    @Value("${oss.fileMax:104857600}")
    private long fileMax;

    /**
     * 上传策略
     * @param ossPolicyRestQuery
     * @return
     */
    @RequestMapping("/api/trade/oss/getPolicy")
    public RestResponse<OssPolicyDO> getPolicy(@RequestBody OssPolicyRestQuery ossPolicyRestQuery) {
        String bucket = "";
        String dir = "";
        if (OssTypeEnum.VOUCHER.equals(ossPolicyRestQuery.getBizType())) {
            bucket = voucherBucket;
            dir = voucherDir;
        }
        if (StringUtils.isBlank(bucket)) {
            throw new GmallException(TradeFrontResponseCode.OSS_BUCKET_EMPTY);
        }
        return RestResponse.okWithoutMsg(publicGmallOssClient.generatePostObject(bucket, dir , ossPolicyRestQuery.getFileName(),fileMax));
    }

    /**
     * 获取地址
     * @param req
     * @return
     */
    @RequestMapping("/api/trade/oss/getUrl")
    public RestResponse<String> getOssHttpUrl(@RequestBody OssQueryReq req){
        return RestResponse.okWithoutMsg(publicGmallOssClient.getFileHttpUrl(req.getOssUrl() , true));
    }
}