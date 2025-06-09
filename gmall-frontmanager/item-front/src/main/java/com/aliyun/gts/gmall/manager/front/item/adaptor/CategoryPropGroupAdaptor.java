package com.aliyun.gts.gmall.manager.front.item.adaptor;

import static com.aliyun.gts.gmall.manager.front.common.exception.FrontCommonResponseCode.ITEM_CENTER_ERROR;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSON;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.manager.framework.common.DubboBuilder;
import com.aliyun.gts.gmall.manager.framework.common.DubboDataSource;
import com.aliyun.gts.gmall.manager.front.common.config.DatasourceConfig;
import com.aliyun.gts.gmall.manager.front.common.consts.DsIdConst;
import com.aliyun.gts.gmall.manager.utils.CacheUtils;
import com.aliyun.gts.gmall.platform.item.api.dto.input.category.CategoryPropGroupQueryReq;
import com.aliyun.gts.gmall.platform.item.api.dto.input.property.category.CatPropValueAllQueryReq;
import com.aliyun.gts.gmall.platform.item.api.dto.output.category.CategoryPropGroupDTO;
import com.aliyun.gts.gmall.platform.item.api.dto.output.property.category.CatPropValueDTO;
import com.aliyun.gts.gmall.platform.item.api.facade.category.CategoryPropGroupReadFacade;
import com.aliyun.gts.gmall.platform.item.api.facade.property.PropertyReadFacade;
import com.google.common.cache.Cache;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CategoryPropGroupAdaptor {
    private final DubboBuilder builder = DubboBuilder.builder().logger(log).sysCode(ITEM_CENTER_ERROR).build();

    @Autowired
    private DatasourceConfig datasourceConfig;

    @Autowired
    private CategoryPropGroupReadFacade categoryPropGroupReadFacade;

    @Autowired
    private PropertyReadFacade propertyReadFacade;

    private static final Cache<String, ?> categoryPropCache = CacheUtils.defaultLocalCache(120);

    /**
     * 查询商品属性组
     * 
     * @param categoryId 类目ID
     * @return list
     */
    public List<CategoryPropGroupDTO> queryCategoryPropGroup(Long categoryId) {
        // 1. RPC 请求生成器
        Function<List<Long>, CategoryPropGroupQueryReq> rpcReqCreator = keys -> {
            CategoryPropGroupQueryReq rpcReq = new CategoryPropGroupQueryReq();
            rpcReq.setCategoryId(categoryId);
            return rpcReq;
        };

        // 2. 查询函数
        Function<CategoryPropGroupQueryReq, RpcResponse<List<CategoryPropGroupDTO>>> rpcFunc = categoryPropGroupReadFacade::queryByCategoryId;

        // 3. 键提供者
        Function<CategoryPropGroupDTO, Long> keyProvider = CategoryPropGroupDTO::getId;

        // 4. dubbo的数据源
        DubboDataSource dubboDataSource = builder.create(datasourceConfig).id(DsIdConst.category_prop_query).queryFunc(rpcFunc).strong(Boolean.FALSE);

        // 5. 调用 getNullableQuietly 方法
        List<Long> categoryIdList = new ArrayList<>();
        categoryIdList.add(categoryId);
        List<CategoryPropGroupDTO> list =
                CacheUtils.getNullableQuietly(categoryPropCache, "CATEGORY_PROP_GROUP_", categoryIdList, rpcReqCreator, keyProvider, dubboDataSource);
        log.info("[CategoryPropGroupAdaptor]批量查询商品属性信息, categoryId({})={}, 结果={}", JSON.toJSONString(categoryId), categoryId, JSON.toJSONString(list));
        return list;
    }

    /**
     * 查询类目属性组，类目属性
     * 
     * @param categoryId 类目ID
     * @return list
     */
    public List<CategoryPropGroupDTO> queryCategoryByIds(Long categoryId) {
        // 1. RPC 请求生成器 
        Function<List<Long>, CategoryPropGroupQueryReq> rpcReqCreator = keys -> {
            CategoryPropGroupQueryReq rpcReq = new CategoryPropGroupQueryReq();
            rpcReq.setCategoryId(categoryId);
            return rpcReq;
        };

        // 2. 查询函数
        Function<CategoryPropGroupQueryReq, RpcResponse<List<CategoryPropGroupDTO>>> rpcFunc = categoryPropGroupReadFacade::queryByCategoryId;

        // 3. 键提供者
        Function<CategoryPropGroupDTO, Long> keyProvider = CategoryPropGroupDTO::getId;

        // 4. dubbo的数据源
        DubboDataSource dubboDataSource = builder.create(datasourceConfig).id(DsIdConst.customer_seller_shop_query).queryFunc(rpcFunc).strong(Boolean.TRUE);
        List<Long> categoryIdList = new ArrayList<>();
        categoryIdList.add(categoryId);
        // 5. 调用 getNullableQuietly 方法
        return CacheUtils.getNullableQuietly(categoryPropCache, "CATEGORY_PROP_", categoryIdList, rpcReqCreator, keyProvider, dubboDataSource);
    }

    /**
     * 批量查询属性值(非手动输入数据)
     * 
     * @return list
     */
    public List<CatPropValueDTO> queryPropValuesByCategoryId(Long categoryId, List<Long> prodIds) {
        // 1. RPC 请求生成器
        Function<List<Long>, CatPropValueAllQueryReq> rpcReqCreator = keys -> {
            CatPropValueAllQueryReq rpcReq = new CatPropValueAllQueryReq();
            rpcReq.setStatus(1);
            List<Long> categoryIdList = new ArrayList<>();
            categoryIdList.add(categoryId);
            rpcReq.setCategoryIds(categoryIdList);
            rpcReq.setPropIds(prodIds);
            return rpcReq;
        };

        // 2. 查询函数
        Function<CatPropValueAllQueryReq, RpcResponse<List<CatPropValueDTO>>> rpcFunc = propertyReadFacade::queryCatPropValueList;

        // 3. 键提供者
        Function<CatPropValueDTO, Long> keyProvider = CatPropValueDTO::getId;

        // 4. dubbo的数据源
        DubboDataSource dubboDataSource = builder.create(datasourceConfig).id(DsIdConst.category_prop_value_query).queryFunc(rpcFunc).strong(Boolean.FALSE);
        List<Long> categoryIdList = new ArrayList<>();
        categoryIdList.add(categoryId);
        // 5. 调用 getNullableQuietly 方法
        List<CatPropValueDTO> list =
                CacheUtils.getNullableQuietly(categoryPropCache, "CATEGORY_PROP_VALUE_", categoryIdList, rpcReqCreator, keyProvider, dubboDataSource);
        log.info("[CategoryPropGroupAdaptor]批量查询商品属性值信息, categoryId({})={}, 结果={}", JSON.toJSONString(categoryIdList), JSON.toJSONString(list));
        return list;
    }

}
