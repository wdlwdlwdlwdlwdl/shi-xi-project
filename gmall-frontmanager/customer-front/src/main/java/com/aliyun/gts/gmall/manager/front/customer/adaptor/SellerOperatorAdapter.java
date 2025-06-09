package com.aliyun.gts.gmall.manager.front.customer.adaptor;

import com.aliyun.gts.gmall.center.user.api.dto.output.SellerIndicatorDTO;
import com.aliyun.gts.gmall.center.user.api.facade.SellerIndicatorReadFacade;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.manager.framework.common.DubboBuilder;
import com.aliyun.gts.gmall.manager.front.common.config.DatasourceConfig;
import com.aliyun.gts.gmall.manager.front.common.consts.DsIdConst;
import com.aliyun.gts.gmall.manager.utils.CacheUtils;
import com.aliyun.gts.gmall.platform.operator.api.dto.input.OperatorByOutIdQuery;
import com.aliyun.gts.gmall.platform.operator.api.dto.input.RoleByNameQuery;
import com.aliyun.gts.gmall.platform.operator.api.dto.output.OperatorDTO;
import com.aliyun.gts.gmall.platform.operator.api.facade.OperatorReadFacade;
import com.aliyun.gts.gmall.platform.operator.api.facade.RoleReadFacade;
import com.aliyun.gts.gmall.platform.user.api.dto.input.CommonByIdQuery;
import com.aliyun.gts.gmall.platform.user.api.dto.input.SellerQuery;
import com.aliyun.gts.gmall.platform.user.api.dto.input.ShopConfigQuery;
import com.aliyun.gts.gmall.platform.user.api.dto.output.ESignInfoConfigDTO;
import com.aliyun.gts.gmall.platform.user.api.dto.output.SellerDTO;
import com.aliyun.gts.gmall.platform.user.api.dto.output.ShopConfigDTO;
import com.aliyun.gts.gmall.platform.user.api.facade.SellerReadFacade;
import com.google.common.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Function;

import static com.aliyun.gts.gmall.manager.front.common.exception.FrontCommonResponseCode.CUSTOMER_CENTER_ERROR;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/8/11 16:46
 */
@Slf4j
@Service
public class SellerOperatorAdapter {

    private static final Cache<String, ?> sellerCache = CacheUtils.defaultLocalCache();

    @Resource
    private OperatorReadFacade operatorReadFacade;
    @Resource
    private RoleReadFacade roleReadFacade;
    @Resource
    private SellerReadFacade sellerReadFacade;
    @Resource
    private SellerIndicatorReadFacade sellerIndicatorReadFacade;

    @Autowired
    private DatasourceConfig datasourceConfig;
    private final DubboBuilder builder = DubboBuilder.builder().logger(log).sysCode(CUSTOMER_CENTER_ERROR).build();

    public OperatorDTO querySellerMainOperatorId(Long sellerId) {
        OperatorByOutIdQuery query = new OperatorByOutIdQuery();
        query.setOperatorType("seller");
        query.setOutId(sellerId);
        try {
            RpcResponse<OperatorDTO> rsp = operatorReadFacade.queryMainOperator(query);
            return rsp.getData();
        } catch (Exception e) {
            log.error("querySellerMainOperatorId," + sellerId, e);
        }
        return null;
    }

    /**
     * 查询卖家运营账号
     * @param mainOperatorId
     * @param roleName
     * @return
     */
    public List<Long> querySellerOperatorIds(Long mainOperatorId, String roleName) {
        RoleByNameQuery roleQuery = new RoleByNameQuery();
        roleQuery.setMainOperatorId(mainOperatorId);
        roleQuery.setOperatorType("seller");
        roleQuery.setRoleName(roleName);
        try {
            RpcResponse<List<Long>> response = roleReadFacade.queryOperatorOfRole(roleQuery);
            return response.getData();
        }catch (Exception e){
            log.error("querySellerOperatorIds,"+mainOperatorId,e);
        }
        return null;
    }

    public ShopConfigDTO queryShopById(Long sellerId) {
        Callable<ShopConfigDTO> loader = () -> {
            ShopConfigQuery req = new ShopConfigQuery();
            req.setSellerId(sellerId);
            return builder.create(datasourceConfig)
                    .id(DsIdConst.customer_seller_shop_query)
                    .queryFunc((Function<ShopConfigQuery, ?>) sellerReadFacade::queryShop)
                    .strong(Boolean.FALSE)
                    .query(req);
        };
        return CacheUtils.getNullableQuietly(sellerCache, "SHOP_" + sellerId, loader);
    }

    public SellerDTO querySellerById(Long sellerId) {
        Callable<SellerDTO> loader = () -> {
            CommonByIdQuery req = new CommonByIdQuery();
            req.setId(sellerId);
            return builder.create(datasourceConfig)
                    .id(DsIdConst.customer_seller_query)
                    .queryFunc((Function<CommonByIdQuery, ?>) sellerReadFacade::query)
                    .strong(Boolean.FALSE)
                    .query(req);
        };
        return CacheUtils.getNullableQuietly(sellerCache, "SELLER_" + sellerId, loader);
    }

    public SellerIndicatorDTO querySellerIndicator(Long sellerId) {
        Callable<SellerIndicatorDTO> loader = () -> {
            CommonByIdQuery req = new CommonByIdQuery();
            req.setId(sellerId);
            return builder.create(datasourceConfig)
                    .id(DsIdConst.customer_seller_indicator_query)
                    .queryFunc((Function<CommonByIdQuery, ?>) sellerIndicatorReadFacade::queryBySellerId)
                    .strong(Boolean.FALSE)
                    .query(req);
        };
        return CacheUtils.getNullableQuietly(sellerCache, "SIND_" + sellerId, loader);
    }

    public PageInfo<SellerDTO> pageQuery(SellerQuery query) {
        return builder.create(datasourceConfig)
                .id(DsIdConst.customer_seller_query)
                .queryFunc((Function<SellerQuery, ?>) sellerReadFacade::pageQuery)
                .strong(Boolean.TRUE)
                .query(query);
    }

    public ESignInfoConfigDTO queryESign(Long sellerId) {
        CommonByIdQuery idQuery = new CommonByIdQuery();
        idQuery.setId(sellerId);
        return builder.create(datasourceConfig)
                .id(DsIdConst.customer_seller_query)
                .queryFunc((Function<CommonByIdQuery, ?>) sellerReadFacade::queryESign)
                .strong(Boolean.TRUE)
                .query(idQuery);
    }
}
