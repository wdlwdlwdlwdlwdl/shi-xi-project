package com.aliyun.gts.gmall.manager.front.item.adaptor;


import static com.aliyun.gts.gmall.manager.front.common.exception.FrontCommonResponseCode.ITEM_CENTER_ERROR;
import java.util.List;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSON;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.api.util.MagicgreekUtils;
import com.aliyun.gts.gmall.manager.framework.common.DubboBuilder;
import com.aliyun.gts.gmall.manager.framework.common.DubboDataSource;
import com.aliyun.gts.gmall.manager.front.common.config.DatasourceConfig;
import com.aliyun.gts.gmall.manager.front.common.consts.DsIdConst;
import com.aliyun.gts.gmall.manager.utils.CacheUtils;
import com.aliyun.gts.gmall.platform.item.api.dto.input.commercial.DeliveryTypeFullInfoQueryReq;
import com.aliyun.gts.gmall.platform.item.api.dto.output.commercial.DeliveryTypeFullInfoDTO;
import com.aliyun.gts.gmall.platform.item.api.facade.commercial.CommercialReadFacade;
import com.google.common.cache.Cache;
import lombok.extern.slf4j.Slf4j;

/**
 * 商家物流数据
 *
 * @author tiansong
 */
@Slf4j
@Service
public class CommercialAdaptor {
    @Autowired
    private CommercialReadFacade commercialReadFacade;

    @Autowired
    private DatasourceConfig datasourceConfig;
    private static final Cache<String, ?> deliveryCache = CacheUtils.defaultLocalCache(30);
    private final DubboBuilder builder = DubboBuilder.builder().logger(log).sysCode(ITEM_CENTER_ERROR).build();

    /**
     * 查询物流详情信息
     * 
     * @param categoryId 类目Id
     * @param skuId skuId
     * @param sellerIds 商家Ids
     * @param skuQuoteIds skuQuoteIds
     * @param cityCode 送达城市code
     * @return list
     */
    public List<DeliveryTypeFullInfoDTO> querySelfDeliveryType(Long categoryId, Long skuId, List<Long> sellerIds, List<Long> skuQuoteIds, String cityCode) {
        // 1. RPC 请求生成器
        Function<List<Long>, DeliveryTypeFullInfoQueryReq> rpcReqCreator = keys -> {
            DeliveryTypeFullInfoQueryReq rpcReq = new DeliveryTypeFullInfoQueryReq();
            rpcReq.setCategoryId(categoryId);
            rpcReq.setSkuId(skuId);
            rpcReq.setSellerIds(sellerIds);
            rpcReq.setSkuQuoteIds(skuQuoteIds);
            rpcReq.setCityCode(cityCode);
            rpcReq.setQueryTimeliness(true);
            return rpcReq;
        };

        // 2. 查询函数
        Function<DeliveryTypeFullInfoQueryReq, RpcResponse<List<DeliveryTypeFullInfoDTO>>> rpcFunc = commercialReadFacade::queryDeliveryTypeFullInfo;

        // 3. 键提供者
        Function<DeliveryTypeFullInfoDTO, Long> keyProvider = DeliveryTypeFullInfoDTO::getSellerId;

        // 4. dubbo的数据源
        DubboDataSource dubboDataSource = builder.create(datasourceConfig).id(DsIdConst.item_freight_query).queryFunc(rpcFunc).strong(Boolean.FALSE);

        // 8. 调用 getNullableQuietly 方法 SELF_DELIVERY deliveryCache
        List<DeliveryTypeFullInfoDTO> list = CacheUtils.getNullableQuietly(deliveryCache, MagicgreekUtils.strJoin("SELF_DELIVERY", "_", skuId, cityCode),
                sellerIds, rpcReqCreator, keyProvider, dubboDataSource);
        log.info("[CommercialAdaptor]批量查询商家物流信息, sellerIds({})={}, 结果={}", JSON.toJSONString(sellerIds), sellerIds.size(), JSON.toJSONString(list));
        return list;
    }

}
