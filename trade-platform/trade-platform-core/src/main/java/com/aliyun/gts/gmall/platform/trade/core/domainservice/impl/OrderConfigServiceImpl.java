package com.aliyun.gts.gmall.platform.trade.core.domainservice.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonErrorCode;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderConfigService;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcSellerConfigDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.user.SellerTradeConfig;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcSellerConfigRepository;
import com.aliyun.gts.gmall.platform.trade.domain.util.ErrorUtils;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class OrderConfigServiceImpl implements OrderConfigService {
    private static final String EXT_PREFIX = "ext.";

    @Autowired
    private TcSellerConfigRepository tcSellerConfigRepository;

    private final Cache<Long, SellerTradeConfig> cache = CacheBuilder.newBuilder()
            .maximumSize(10000).expireAfterWrite(30, TimeUnit.SECONDS).build();

    @Override
    public SellerTradeConfig getSellerConfig(Long sellerId) {
        try {
            return cache.get(sellerId, () -> getSellerConfigNoCache(sellerId));
        } catch (ExecutionException e) {
            log.error("fail getSellerConfig: {}", sellerId, e);
            throw new GmallException(CommonErrorCode.SERVER_ERROR);
        }
    }

    @Override
    public SellerTradeConfig getSellerConfigNoCache(Long sellerId) {
        List<TcSellerConfigDO> list = tcSellerConfigRepository.queryBySeller(sellerId);
        JSONObject sellerMap = new JSONObject();
        JSONObject map = new JSONObject();

        JSONObject sellerExtMap = new JSONObject();
        JSONObject extMap = new JSONObject();

        for (TcSellerConfigDO cfg : list) {
            if (cfg.getSellerId().longValue() == 0L) {
                if (StringUtils.startsWith(cfg.getConfName(), EXT_PREFIX)) {
                    String name = cfg.getConfName().substring(EXT_PREFIX.length());
                    extMap.put(name, cfg.getConfValue());
                } else {
                    map.put(cfg.getConfName(), cfg.getConfValue());
                }
            } else {
                if (StringUtils.startsWith(cfg.getConfName(), EXT_PREFIX)) {
                    String name = cfg.getConfName().substring(EXT_PREFIX.length());
                    sellerExtMap.put(name, cfg.getConfValue());
                } else {
                    sellerMap.put(cfg.getConfName(), cfg.getConfValue());
                }
            }
        }

        // seller配置覆盖默认配置
        map.putAll(sellerMap);
        SellerTradeConfig conf = JSON.toJavaObject(map, SellerTradeConfig.class);

        // 扩展map
        extMap.putAll(sellerExtMap);
        Map<String, String> extendMap = new HashMap<>();
        for (Entry<String, Object> en : extMap.entrySet()) {
            extendMap.put(en.getKey(), String.valueOf(en.getValue()));
        }
        conf.setExtendConfigs(extendMap);
        return conf;
    }

    @Override
    public void saveSellerConfig(Long sellerId, SellerTradeConfig config) {
        Map<String, String> extendConfigs = config.getExtendConfigs();
        config.setExtendConfigs(null);

        JSONObject json = (JSONObject) JSON.toJSON(config);
        List<TcSellerConfigDO> list = new ArrayList<>();
        for (Entry<String, Object> field : json.entrySet()) {
            String name = field.getKey();
            Object value = field.getValue();
            if (value == null) {
                continue;
            }
            list.add(create(name, value, sellerId));
        }
        if (extendConfigs != null) {
            for (Entry<String, String> en : extendConfigs.entrySet()) {
                String name = en.getKey();
                String value = en.getValue();
                if (value == null) {
                    continue;
                }
                list.add(create(EXT_PREFIX + name, value, sellerId));
            }
        }

        if (!list.isEmpty()) {
            tcSellerConfigRepository.save(list);
        }
    }

    private TcSellerConfigDO create(String name, Object value, Long sellerId) {
        TcSellerConfigDO c = new TcSellerConfigDO();
        c.setConfName(name);
        c.setConfValue(String.valueOf(value));
        c.setSellerId(sellerId);
        return c;
    }
}
