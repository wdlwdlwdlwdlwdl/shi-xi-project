package com.aliyun.gts.gmall.platform.trade.persistence.repository.impl;

import com.alibaba.fastjson.JSON;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.middleware.oss.GmallOssClient;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonErrorCode;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.repository.OssRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OssRepositoryImpl implements OssRepository {
    private static final String PATH_SNAPSHOT = "ordersnap/";

    @Autowired
    private GmallOssClient publicGmallOssClient;
    @Autowired
    private GmallOssClient internalGmallOssClient;

    @Value("${oss.bucket.trade:gmalltrade}")
    private String tradeBucketName;

    @Value("${oss.bucket.tradedir:}")
    private String tradeDir;

    @Override
    public String saveOrderSnapshot(CreatingOrder order) {
        List<Long> mainIds = order.getMainOrders().stream().map(main -> main.getPrimaryOrderId()).collect(Collectors.toList());
        if (mainIds.size() > 3) {
            mainIds = mainIds.subList(0, 3);
        }
        String path = tradeDir + PATH_SNAPSHOT + StringUtils.join(mainIds, '_') + ".json";
        String data = JSON.toJSONString(order);
        try {
            InputStream in = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
            internalGmallOssClient.getOssClient().putObject(tradeBucketName, path, in);
        } catch (Exception e) {
            throw new GmallException(CommonErrorCode.SERVER_ERROR);
        }
        String ossUrl = publicGmallOssClient.getOssFileUrl(tradeBucketName, path);
        return ossUrl;
    }

    public static void main(String[] args) {
        List<Integer> list = Arrays.asList(1,2,3,4);
        System.out.println(list.subList(0, 3));
    }
}
