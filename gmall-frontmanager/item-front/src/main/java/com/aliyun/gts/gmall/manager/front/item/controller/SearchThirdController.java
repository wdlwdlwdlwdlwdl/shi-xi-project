package com.aliyun.gts.gmall.manager.front.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.manager.biz.output.CustDTO;
import com.aliyun.gts.gmall.manager.front.common.util.UserHolder;
import com.aliyun.gts.gmall.manager.front.item.adaptor.SearchThirdAdaptor;
import com.aliyun.gts.gmall.manager.front.item.convertor.SearchThirdConvertor;
import com.aliyun.gts.gmall.manager.front.item.dto.input.query.RelatedSkuSuggestExtendThirdRestQuery;
import com.aliyun.gts.gmall.manager.front.item.dto.input.query.SearchActionSuggestThirdRestQuery;
import com.aliyun.gts.gmall.manager.front.item.dto.input.query.SkuSearchPageBrandExtendThirdRestQuery;
import com.aliyun.gts.gmall.manager.front.item.dto.input.query.SkuSearchPageCategoryExtendThirdRestQuery;
import com.aliyun.gts.gmall.manager.front.item.dto.input.query.SkuSearchPageSellerExtendThirdRestQuery;
import com.aliyun.gts.gmall.manager.front.item.dto.input.query.SkuSearchPageSkuExtendThirdRestQuery;
import com.aliyun.gts.gmall.manager.front.item.dto.input.query.SkuSearchPageThirdRestQuery;
import com.aliyun.gts.gmall.manager.front.item.dto.output.ActionSuggestVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.SkuSearchPageThirdVO;
import com.aliyun.gts.gmall.searcher.api.dto.input.RelatedSkuSuggestExtendThirdQuery;
import com.aliyun.gts.gmall.searcher.api.dto.input.SearchActionSuggestThirdQuery;
import com.aliyun.gts.gmall.searcher.api.dto.input.SkuSearchPageBrandExtendThirdQuery;
import com.aliyun.gts.gmall.searcher.api.dto.input.SkuSearchPageCategoryExtendThirdQuery;
import com.aliyun.gts.gmall.searcher.api.dto.input.SkuSearchPageSellerExtendThirdQuery;
import com.aliyun.gts.gmall.searcher.api.dto.input.SkuSearchPageSkuExtendThirdQuery;
import com.aliyun.gts.gmall.searcher.api.dto.input.SkuSearchPageThirdQuery;
import com.aliyun.gts.gmall.searcher.api.dto.output.item.ActionSuggestDTO;
import com.aliyun.gts.gmall.searcher.api.dto.output.item.SkuSearchPageThirdDTO;

/**
 * 
 * @Title: SearchThirdController.java
 * @Description: 三方search对接
 * @author zhao.qi
 * @date 2024年11月28日 17:32:08
 * @version V1.0
 */
@RequestMapping("/api/item/third")
@RestController
public class SearchThirdController {
    @Autowired
    private SearchThirdAdaptor searchThirdAdaptor;

    @Autowired
    private SearchThirdConvertor searchThirdConvertor;

    /**
     * 根据关键字查询
     * 
     * @param query
     * @return
     */
    @PostMapping("/keyword")
    public @ResponseBody RestResponse<SkuSearchPageThirdVO> queryPageSkuKeyword(@RequestBody SkuSearchPageThirdRestQuery query) {
        CustDTO user = UserHolder.getUser();
        SkuSearchPageThirdQuery rpcQuery = searchThirdConvertor.toSkuSearchPageThirdQuery(query, user);
        SkuSearchPageThirdDTO skuSearchPageThirdDto = searchThirdAdaptor.queryPageSkuKeyword(rpcQuery);
        SkuSearchPageThirdVO skuSearchPageThirdVo = searchThirdConvertor.toSkuSearchPageThirdVo(skuSearchPageThirdDto);
        skuSearchPageThirdVo.setBrands(searchThirdAdaptor.queryBrandByIds(skuSearchPageThirdDto.getBrandIds()));
        skuSearchPageThirdVo.setSellers(searchThirdAdaptor.querySellerByIds(skuSearchPageThirdDto.getSellerIds()));
        return RestResponse.okWithoutMsg(skuSearchPageThirdVo);
    }

    /**
     * 搜索建议
     * 
     * @param query
     * @return
     */
    @PostMapping("/action/suggest")
    public @ResponseBody RestResponse<ActionSuggestVO> querySearchActionSuggest(@RequestBody SearchActionSuggestThirdRestQuery query) {
        SearchActionSuggestThirdQuery rpcQuery = searchThirdConvertor.toSearchActionSuggestThirdQuery(query);
        ActionSuggestDTO actionSuggestDto = searchThirdAdaptor.querySearchActionSuggest(rpcQuery);
        return RestResponse.okWithoutMsg(searchThirdConvertor.toActionSuggestVo(actionSuggestDto));
    }

    // --------------------------------------------------------以下是固定关键字查询阔扩展---------------------------------------------------------------
    /**
     * 周边推荐
     * 
     * @param query
     * @return
     */
    @PostMapping("/keyword/extend/related/suggest")
    public @ResponseBody RestResponse<SkuSearchPageThirdVO> querySkuAroundSuggest(@RequestBody RelatedSkuSuggestExtendThirdRestQuery query) {
        CustDTO user = UserHolder.getUser();
        RelatedSkuSuggestExtendThirdQuery rpcQuery = searchThirdConvertor.toRelatedSkuSuggestExtendThirdQuery(query, user);
        SkuSearchPageThirdDTO relatedSkuSearchThirdDto = searchThirdAdaptor.queryRelatedSkuSuggestExtend(rpcQuery);
        return RestResponse.okWithoutMsg(searchThirdConvertor.toSkuSearchPageThirdVo(relatedSkuSearchThirdDto));
    }

    /**
     * 固定关键字查询-类目
     * 
     * @param query
     * @return
     */
    @PostMapping("/keyword/extend/category")
    public @ResponseBody RestResponse<SkuSearchPageThirdVO> queryPageCategoryExtend(@RequestBody SkuSearchPageCategoryExtendThirdRestQuery query) {
        CustDTO user = UserHolder.getUser();
        SkuSearchPageCategoryExtendThirdQuery rpcQuery = searchThirdConvertor.toSkuSearchPageCategoryExtendThirdQuery(query, user);
        SkuSearchPageThirdDTO skuSearchPageThirdDto = searchThirdAdaptor.queryPageCategoryExtend(rpcQuery);
        return RestResponse.okWithoutMsg(searchThirdConvertor.toSkuSearchPageThirdVo(skuSearchPageThirdDto));
    }

    /**
     * 固定关键字查询-品牌
     * 
     * @param query
     * @return
     */
    @PostMapping("/keyword/extend/brand")
    public @ResponseBody RestResponse<SkuSearchPageThirdVO> queryPageBrandExtend(@RequestBody SkuSearchPageBrandExtendThirdRestQuery query) {
        CustDTO user = UserHolder.getUser();
        SkuSearchPageBrandExtendThirdQuery rpcQuery = searchThirdConvertor.toSkuSearchPageBrandExtendThirdQuery(query, user);
        SkuSearchPageThirdDTO skuSearchPageThirdDto = searchThirdAdaptor.queryPageBrandExtend(rpcQuery);
        return RestResponse.okWithoutMsg(searchThirdConvertor.toSkuSearchPageThirdVo(skuSearchPageThirdDto));
    }

    /**
     * 固定关键字查询-商家
     * 
     * @param query
     * @return
     */
    @PostMapping("/keyword/extend/seller")
    public @ResponseBody RestResponse<SkuSearchPageThirdVO> queryPageSellerExtend(@RequestBody SkuSearchPageSellerExtendThirdRestQuery query) {
        CustDTO user = UserHolder.getUser();
        SkuSearchPageSellerExtendThirdQuery rpcQuery = searchThirdConvertor.toSkuSearchPageSellerExtendThirdQuery(query, user);
        SkuSearchPageThirdDTO skuSearchPageThirdDto = searchThirdAdaptor.queryPageSellerExtend(rpcQuery);
        return RestResponse.okWithoutMsg(searchThirdConvertor.toSkuSearchPageThirdVo(skuSearchPageThirdDto));
    }

    /**
     * 固定关键字查询-sku
     * 
     * @param query
     * @return
     */
    @PostMapping("/keyword/extend/sku")
    public @ResponseBody RestResponse<SkuSearchPageThirdVO> queryPageSkuExtend(@RequestBody SkuSearchPageSkuExtendThirdRestQuery query) {
        CustDTO user = UserHolder.getUser();
        SkuSearchPageSkuExtendThirdQuery rpcQuery = searchThirdConvertor.toSkuSearchPageSkuExtendThirdQuery(query, user);
        SkuSearchPageThirdDTO skuSearchPageThirdDto = searchThirdAdaptor.queryPageSkuExtend(rpcQuery);
        return RestResponse.okWithoutMsg(searchThirdConvertor.toSkuSearchPageThirdVo(skuSearchPageThirdDto));
    }
}
