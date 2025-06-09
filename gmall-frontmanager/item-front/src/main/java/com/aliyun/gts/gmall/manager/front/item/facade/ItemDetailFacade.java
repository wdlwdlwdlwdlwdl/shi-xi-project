package com.aliyun.gts.gmall.manager.front.item.facade;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.manager.front.item.dto.input.query.*;
import com.aliyun.gts.gmall.manager.front.item.dto.output.*;
import com.aliyun.gts.gmall.manager.front.item.dto.output.sku.ItemSkuFeaturesVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.sku.ItemSkuV2VO;

import java.util.List;

/**
 * 商品详情接口
 *
 * @author tiansong
 */
public interface ItemDetailFacade {
    /**
     * 商品详情主接口
     *
     * @param itemDetailRestQuery
     * @return
     */
    ItemDetailVO queryItemDetail(ItemDetailRestQuery itemDetailRestQuery);

    /**
     * 商品SKU单独模块
     *
     * @param itemSkuRestQuery
     * @return
     */
    ItemDetailVO queryItemSku(ItemSkuRestQuery itemSkuRestQuery);

    /**
     * 商品详情评价列表查询
     *
     * @param evaluationRestQuery
     * @return
     */
    PageInfo<ItemEvaluationVO> queryEvaluationList(EvaluationRestQuery evaluationRestQuery);

    /**
     * 商品详情热门评价列表查询
     *
     * @param evaluationRestQuery
     * @return
     */
    PageInfo<ItemEvaluationVO> queryHotEvaluationList(EvaluationRestQuery evaluationRestQuery);

    /**
     * 商品详情运费信息查询
     *
     * @param itemFreightRestQuery
     * @return
     */
    ItemFreightVO queryItemFreight(ItemFreightRestQuery itemFreightRestQuery);

    /**
     * 商品详情查询多sku选中后价格
     *
     * @param query
     * @return
     */
    ItemPriceVO queryItemPrice(ItemPriceRestQuery query);

    /**
     * 商品基础信息
     *
     * @param itemDetailRestQuery
     * @return
     */
    ItemDetailVO queryItemBase(ItemDetailRestQuery itemDetailRestQuery);

    /**
     * 商品SKU
     * @param req 请求
     * @return
     */
    ItemSkuV2VO queryItemSkuV2(ItemSkuRestQuery req);

    /**
     * 商品SKU的features查询
     * @param query
     * @return
     */
    List<ItemSkuFeaturesVO> queryItemSkuFeatures(ItemSkuRestQuery query);

    /**
     * 查询商品详细
     * @param query
     * @return
     */
    ItemDetailV2VO queryItemDetailV2(ItemDetailV2RestQuery query);


    ItemSkuPriceVO querySkuPrice(ItemSkuPriceRestQuery query);

    /**
     * 查询商品分期
     * @param query
     * @return
     */
    List<ItemDeadlineVO> queryItemDeadline(ItemDeadlineRestQuery query);

    /**
     * 查询商品特性
     * @param query
     * @return
     */
    List<ItemCharacteristicsVO> queryItemCharacteristics(ItemDetailV2RestQuery query);

    /**
     * 查询商品的商家列表
     *
     * @param query
     * @return
     */
    RestResponse<PageInfo<ItemMerchantInfoVO>> queryItemMerchantInfo(SkuOfferSearchReq query);


    /**
     * 查询优惠券
     * @param query
     * @return
     */
    List<CouponInstanceVO> queryCouponInfo(CouponInfoReqQuery query);

    /**
     * 单个评价详情
     */
    List<SkuEvaluateInfoVO> querySkuEvaluateInfo(SkuEvaluateInfoQuery req);

    /**
     * 评价列表
     */
    PageInfo<SkuEvaluateInfoVO> querySkuEvaluateList( SkuEvaluateQuery req);

    /**
     * 查询加车价格
     * @param itemSkuCartPriceRestQuery
     * @return
     */
    ItemSkuCartPriceVO calcItemAddCart(ItemSkuCartPriceRestQuery itemSkuCartPriceRestQuery);

    /**
     * 压缩短连接
     * @param itemDetailLinkRestQuery
     * @return
     */
    ItemDetailLinkVO itemLinkShort(ItemDetailLinkRestQuery itemDetailLinkRestQuery);

    RestResponse<Integer> queryCategoryLevel(Long categoryId);

}
