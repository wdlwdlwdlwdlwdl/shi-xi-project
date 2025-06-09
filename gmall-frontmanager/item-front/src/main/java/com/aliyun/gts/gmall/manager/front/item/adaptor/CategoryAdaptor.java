package com.aliyun.gts.gmall.manager.front.item.adaptor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import com.aliyun.gts.gmall.manager.front.item.dto.input.query.CategoryRestQuery;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.manager.framework.common.DubboBuilder;
import com.aliyun.gts.gmall.manager.framework.common.DubboDataSource;
import com.aliyun.gts.gmall.manager.front.common.config.DatasourceConfig;
import com.aliyun.gts.gmall.manager.front.common.consts.DsIdConst;
import com.aliyun.gts.gmall.manager.front.common.exception.FrontCommonResponseCode;
import com.aliyun.gts.gmall.manager.front.item.convertor.CategoryConvertor;
import com.aliyun.gts.gmall.manager.front.item.dto.input.query.CategoryAllByParamV2Query;
import com.aliyun.gts.gmall.manager.front.item.dto.output.CategoryTreeVO;
import com.aliyun.gts.gmall.manager.utils.CacheUtils;
import com.aliyun.gts.gmall.platform.item.api.dto.input.category.CategoryQueryByIdsReq;
import com.aliyun.gts.gmall.platform.item.api.dto.input.category.CategoryQueryByParamV2Req;
import com.aliyun.gts.gmall.platform.item.api.dto.output.category.CategoryDTO;
import com.aliyun.gts.gmall.platform.item.api.dto.output.category.CategoryTreeDTO;
import com.aliyun.gts.gmall.platform.item.api.facade.category.CategoryReadFacade;
import com.google.common.cache.Cache;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @Title: CategoryAdaptor.java
 * @Description: 类目适配
 * @author zhao.qi
 * @date 2024年11月15日 09:30:31
 * @version V1.0
 */
@Slf4j
@Service
public class CategoryAdaptor {
    private static final Cache<String, ?> categoryCache = CacheUtils.defaultLocalCache(3600);

    @Autowired
    private CategoryReadFacade categoryReadFacade;

    @Autowired
    private CategoryConvertor categoryConvertor;

    @Autowired
    private DatasourceConfig datasourceConfig;
    DubboBuilder builder = DubboBuilder.builder().logger(log).sysCode(FrontCommonResponseCode.ITEM_CENTER_ERROR).build();

    /**
     * 查询商品类目信息
     * 
     * @param categoryId  类目id
     * @return
     */
    public CategoryDTO queryCategoryById(Long categoryId) {
        List<Long> categoryIds = new ArrayList<>();
        categoryIds.add(categoryId);
        List<CategoryDTO> queryCategoryByIds = queryCategoryByIds(categoryIds);
        if(CollectionUtils.isEmpty(queryCategoryByIds)){
            return null;
        }
        return queryCategoryByIds.get(0);
    }

    /**
     * 批量查询商品类目信息
     * 
     * @param  categoryIds 类目ids
     * @return
     */
    public List<CategoryDTO> queryCategoryByIds(List<Long> categoryIds) {
        // 1. RPC 请求生成器
        Function<List<Long>, CategoryQueryByIdsReq> rpcReqCreator = keys -> {
            CategoryQueryByIdsReq rpcReq = new CategoryQueryByIdsReq();
            rpcReq.setIds(keys);
            return rpcReq;
        };

        // 2. 查询函数
        Function<CategoryQueryByIdsReq, RpcResponse<List<CategoryDTO>>> rpcFunc = categoryReadFacade::queryByIds;

        // 3. 键提供者
        Function<CategoryDTO, Long> keyProvider = CategoryDTO::getId;

        // 4. dubbo的数据源
        DubboDataSource dubboDataSource = builder.create(datasourceConfig).id(DsIdConst.customer_seller_shop_query).queryFunc(rpcFunc).strong(Boolean.TRUE);

        // 8. 调用 getNullableQuietly 方法
        return CacheUtils.getNullableQuietly(categoryCache, "CATEGORY_", categoryIds, rpcReqCreator, keyProvider, dubboDataSource);
    }

    /**
     * 查询类目树
     * 
     * @param req
     * @return
     */
    public List<CategoryTreeVO> queryAllByParam(CategoryAllByParamV2Query req) {
        CategoryQueryByParamV2Req rpcReq = new CategoryQueryByParamV2Req();
        rpcReq.setParentId(req.getParentId());
        rpcReq.setId(req.getId());
        rpcReq.setCity(req.getCity());

        List<CategoryTreeVO> list = builder.create().id(DsIdConst.category_all_query)
                .queryFunc((Function<CategoryQueryByParamV2Req, RpcResponse<List<CategoryTreeVO>>>) request -> {
                    RpcResponse<List<CategoryTreeDTO>> rpcResp = categoryReadFacade.queryAllByParam(rpcReq);
                    return RpcResponse.ok(categoryConvertor.toCategoryTreeVos(rpcResp.getData()));
                }).query(rpcReq);
        return toCategoryTreeVos(list);
    }

    private List<CategoryTreeVO> toCategoryTreeVos(List<CategoryTreeVO> list) {
        for (CategoryTreeVO x : list) {
            List<CategoryTreeVO> subCategories = x.getSubCategories();
            if (CollectionUtils.isEmpty(subCategories)) {
                subCategories = new ArrayList<>();
            }
            subCategories.add(0, setRootName(x));
            for (CategoryTreeVO y : subCategories) {
                List<CategoryTreeVO> subCategories1 = y.getSubCategories();
                if (CollectionUtils.isEmpty(subCategories1)) {
                    subCategories1 = new ArrayList<>();
                }
                subCategories1.add(0, setRootName(y));
            }
        }
        return list;
    }

    private CategoryTreeVO setRootName(CategoryTreeVO target) {
        CategoryTreeVO source = new CategoryTreeVO();
        BeanUtils.copyProperties(target, source);
        String name = null;
        String language = LocaleContextHolder.getLocale().getLanguage();
        if ("en".equals(language)) {
            name = "All products";
        } else if ("ru".equals(language)) {
            name = "Все товары";
        } else if ("kk".equals(language)) {
            name = "Барлық өнімдер";
        } else {
            name = "全部商品";
        }
        source.setName(name);
        source.setSubCategories(Collections.emptyList());
        return source;
    }

    /**
     * 查询类目树 缓存
     *
     * @param  categoryIds 类目ids
     * @return
     */
    public List<CategoryTreeDTO> queryCategoryTreeByIds(List<Long> categoryIds) {
        // 1. RPC 请求生成器
        Function<List<Long>, CategoryRestQuery> rpcReqCreator = keys -> {
            CategoryRestQuery rpcReq = new CategoryRestQuery();
            rpcReq.setCategoryIds(keys);
            return rpcReq;
        };

        // 2. 查询函数
        Function<CategoryQueryByIdsReq, RpcResponse<List<CategoryTreeDTO>>> rpcFunc = categoryReadFacade::queryCategoryParamIds;

        // 3. 键提供者
        Function<CategoryTreeDTO, Long> keyProvider = CategoryTreeDTO::getId;

        // 4. dubbo的数据源
        DubboDataSource dubboDataSource = builder.create(datasourceConfig).id(DsIdConst.customer_seller_shop_query).queryFunc(rpcFunc).strong(Boolean.TRUE);

        // 8. 调用 getNullableQuietly 方法
        return CacheUtils.getNullableQuietly(categoryCache, "CATEGORY_TREE_", categoryIds, rpcReqCreator, keyProvider, dubboDataSource);
    }
}
