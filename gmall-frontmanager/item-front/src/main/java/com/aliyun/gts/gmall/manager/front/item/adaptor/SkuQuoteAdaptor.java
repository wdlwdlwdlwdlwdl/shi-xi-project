package com.aliyun.gts.gmall.manager.front.item.adaptor;

import static com.aliyun.gts.gmall.manager.front.common.exception.FrontCommonResponseCode.ITEM_CENTER_ERROR;
import java.util.List;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson.JSON;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.manager.framework.common.DubboBuilder;
import com.aliyun.gts.gmall.manager.framework.common.DubboDataSource;
import com.aliyun.gts.gmall.manager.front.common.config.DatasourceConfig;
import com.aliyun.gts.gmall.manager.front.common.consts.DsIdConst;
import com.aliyun.gts.gmall.manager.utils.CacheUtils;
import com.aliyun.gts.gmall.platform.item.api.dto.input.item.SkuQuoteStockQueryReq;
import com.aliyun.gts.gmall.platform.item.api.dto.output.item.SkuQuoteWarehourseStockDTO2;
import com.aliyun.gts.gmall.platform.item.api.facade.skuquote.SkuQuoteReadFacade;
import com.google.common.cache.Cache;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @Title: SkuQuoteAdaptor.java
 * @Description: TODO(用一句话描述该文件做什么)
 * @author zhao.qi
 * @date 2025年1月22日 21:55:20
 * @version V1.0
 */
@Slf4j
@Component
public class SkuQuoteAdaptor {
    private static final Cache<String, ?> skuQuoteCache = CacheUtils.defaultLocalCache(30);

    @Autowired
    private SkuQuoteReadFacade skuQuoteReadFacade;

    @Autowired
    private DatasourceConfig datasourceConfig;

    private final DubboBuilder builder = DubboBuilder.builder().logger(log).sysCode(ITEM_CENTER_ERROR).build();

    /**
     * 批量查询商家基本信息
     * 
     * @param sellerIds 商家id
     * @return
     */
    public List<SkuQuoteWarehourseStockDTO2> queryStocks(List<Long> quoteIds) {
        // 1. RPC 请求生成器
        Function<List<Long>, SkuQuoteStockQueryReq> rpcReqCreator = keys -> {
            SkuQuoteStockQueryReq rpcReq = new SkuQuoteStockQueryReq();
            rpcReq.setSkuQuoteIds(quoteIds);
            return rpcReq;
        };

        // 2. 查询函数
        Function<SkuQuoteStockQueryReq, RpcResponse<List<SkuQuoteWarehourseStockDTO2>>> rpcFunc = skuQuoteReadFacade::querySkuQuoteStockByQuoteIds2;

        // 3. 键提供者
        Function<SkuQuoteWarehourseStockDTO2, Long> keyProvider = SkuQuoteWarehourseStockDTO2::getSkuQuoteId;

        // 4. dubbo的数据源
        DubboDataSource dubboDataSource =
                builder.create(datasourceConfig).id(DsIdConst.item_sku_quote_warehouse_query).queryFunc(rpcFunc).strong(Boolean.FALSE);

        // 8. 调用 getNullableQuietly 方法
        List<SkuQuoteWarehourseStockDTO2> list =
                CacheUtils.getNullableQuietly(skuQuoteCache, "SKUQUOTE_WAREHOUSE_", quoteIds, rpcReqCreator, keyProvider, dubboDataSource);
        log.info("[SellerAdapter]批量查询Quote仓储信息, quoteIds({})={}, 结果={}", JSON.toJSONString(quoteIds), quoteIds.size(), JSON.toJSONString(list));
        return list;
    }

}
