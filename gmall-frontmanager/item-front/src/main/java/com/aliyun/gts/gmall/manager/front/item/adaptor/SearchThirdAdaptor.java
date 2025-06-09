package com.aliyun.gts.gmall.manager.front.item.adaptor;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.manager.front.customer.adaptor.SellerAdapter;
import com.aliyun.gts.gmall.manager.front.item.dto.input.query.BrandIdsQuery;
import com.aliyun.gts.gmall.manager.front.item.dto.output.BrandVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.KvModel;
import com.aliyun.gts.gmall.manager.front.item.facade.BrandFacade;
import com.aliyun.gts.gmall.platform.item.api.dto.output.category.CategoryDTO;
import com.aliyun.gts.gmall.platform.user.api.dto.output.ShopConfigDTO;
import com.aliyun.gts.gmall.searcher.api.dto.input.RelatedSkuSuggestExtendThirdQuery;
import com.aliyun.gts.gmall.searcher.api.dto.input.SearchActionSuggestThirdQuery;
import com.aliyun.gts.gmall.searcher.api.dto.input.SkuSearchPageBrandExtendThirdQuery;
import com.aliyun.gts.gmall.searcher.api.dto.input.SkuSearchPageCategoryExtendThirdQuery;
import com.aliyun.gts.gmall.searcher.api.dto.input.SkuSearchPageSellerExtendThirdQuery;
import com.aliyun.gts.gmall.searcher.api.dto.input.SkuSearchPageSkuExtendThirdQuery;
import com.aliyun.gts.gmall.searcher.api.dto.input.SkuSearchPageThirdQuery;
import com.aliyun.gts.gmall.searcher.api.dto.output.item.ActionSuggestDTO;
import com.aliyun.gts.gmall.searcher.api.dto.output.item.SkuSearchPageThirdDTO;
import com.aliyun.gts.gmall.searcher.api.facade.SearchThirdFacade;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SearchThirdAdaptor {
    @Autowired
    private SearchThirdFacade searchThirdFacade;

    @Autowired
    private CategoryAdaptor categoryAdaptor;

    @Autowired
    private SellerAdapter sellerAdaptor;

    @Autowired
    private BrandFacade brandFacade;

    /**
     * 根据关键字查询
     * 
     * @param query
     * 
     * @return
     */
    public SkuSearchPageThirdDTO queryPageSkuKeyword(SkuSearchPageThirdQuery query) {
        RpcResponse<SkuSearchPageThirdDTO> rpcResp2 = searchThirdFacade.queryPageSkuKeywordV2(query);
        if (!rpcResp2.isSuccess() || Objects.isNull(rpcResp2.getData())) {
            return null;
        }
        return rpcResp2.getData();
    }

    /**
     * 搜索建议
     * 
     * @param query
     * @return
     */
    public ActionSuggestDTO querySearchActionSuggest(SearchActionSuggestThirdQuery query) {
        RpcResponse<ActionSuggestDTO> rpcResp = searchThirdFacade.querySearchActionSuggest(query);
        if (!rpcResp.isSuccess() || Objects.isNull(rpcResp.getData())) {
            return null;
        }
        return rpcResp.getData();
    }

    // --------------------------------------------------------以下是固定关键字查询阔扩展---------------------------------------------------------------
    /**
     * 周边推荐
     * 
     * @param query
     * 
     * @return
     */
    public SkuSearchPageThirdDTO queryRelatedSkuSuggestExtend(RelatedSkuSuggestExtendThirdQuery query) {
        CategoryDTO categoryDto = categoryAdaptor.queryCategoryById(query.getCategoryId());
        if (Objects.isNull(categoryDto)) {
            return null;
        }
        query.setCategoryId_2(categoryDto.getParentId());
        RpcResponse<SkuSearchPageThirdDTO> rpcResp = searchThirdFacade.queryRelatedSkuSuggestExtend(query);
        if (!rpcResp.isSuccess() || Objects.isNull(rpcResp.getData())) {
            return null;
        }
        return rpcResp.getData();
    }
    
    /**
     * 固定关键字查询-类目
     * 
     * @param query
     * 
     * @return
     */
    public SkuSearchPageThirdDTO queryPageCategoryExtend(SkuSearchPageCategoryExtendThirdQuery query) {
        RpcResponse<SkuSearchPageThirdDTO> rpcResp2 = searchThirdFacade.queryPageCategoryExtend(query);
        if (!rpcResp2.isSuccess() || Objects.isNull(rpcResp2.getData())) {
            return null;
        }
        return rpcResp2.getData();
    }

    /**
     * 固定关键字查询-品牌
     * 
     * @param query
     * 
     * @return
     */
    public SkuSearchPageThirdDTO queryPageBrandExtend(SkuSearchPageBrandExtendThirdQuery query) {
        RpcResponse<SkuSearchPageThirdDTO> rpcResp = searchThirdFacade.queryPageBrandExtend(query);
        if (!rpcResp.isSuccess() || Objects.isNull(rpcResp.getData())) {
            return null;
        }
        return rpcResp.getData();
    }

    /**
     * 固定关键字查询-商家
     * 
     * @param query
     * 
     * @return
     */
    public SkuSearchPageThirdDTO queryPageSellerExtend(SkuSearchPageSellerExtendThirdQuery query) {
        RpcResponse<SkuSearchPageThirdDTO> rpcResp = searchThirdFacade.queryPageSellerExtend(query);
        if (!rpcResp.isSuccess() || Objects.isNull(rpcResp.getData())) {
            return null;
        }
        return rpcResp.getData();
    }

    /**
     * 固定关键字查询-sku
     * 
     * @param query
     * 
     * @return
     */
    public SkuSearchPageThirdDTO queryPageSkuExtend(SkuSearchPageSkuExtendThirdQuery query) {
        RpcResponse<SkuSearchPageThirdDTO> rpcResp = searchThirdFacade.queryPageSkuExtend(query);
        if (!rpcResp.isSuccess() || Objects.isNull(rpcResp.getData())) {
            return null;
        }
        return rpcResp.getData();
    }

    // --------------------------------------------------------以下是数据补充---------------------------------------------------------------
    public List<KvModel<Long, String>> querySellerByIds(List<Long> sellerIds) {
        if (CollectionUtils.isEmpty(sellerIds)) {
            return Collections.emptyList();
        }

        List<ShopConfigDTO> shopConfigDtoList = sellerAdaptor.queryShopBySellerIds(sellerIds);
        return shopConfigDtoList.stream().map(n -> new KvModel<>(n.getSellerId(), n.getName())).collect(Collectors.toList());
    }

    public List<KvModel<Long, String>> queryBrandByIds(List<Long> brandIds) {
        if (CollectionUtils.isEmpty(brandIds)) {
            return Collections.emptyList();
        }

        BrandIdsQuery rpcReq = new BrandIdsQuery();
        rpcReq.setIds(brandIds);
        List<BrandVO> brandVoList = brandFacade.queryAllByParam(rpcReq);
        return brandVoList.stream().map(n -> new KvModel<>(n.getId(), n.getName())).collect(Collectors.toList());
    }

}
