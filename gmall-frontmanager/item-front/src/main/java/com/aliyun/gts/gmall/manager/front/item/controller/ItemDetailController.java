package com.aliyun.gts.gmall.manager.front.item.controller;

import java.util.List;

import com.aliyun.gts.gmall.manager.biz.output.CustDTO;
import com.aliyun.gts.gmall.manager.front.common.util.UserHolder;
import com.aliyun.gts.gmall.manager.front.item.dto.input.query.*;
import com.aliyun.gts.gmall.manager.front.item.dto.output.*;
import com.aliyun.gts.gmall.manager.front.item.dto.temp.ItemSaleSellerQuery;
import com.aliyun.gts.gmall.manager.front.item.facade.ItemFacade;
import com.aliyun.gts.gmall.platform.item.api.facade.category.CategoryReadFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.manager.front.item.dto.output.sku.ItemSkuFeaturesVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.sku.ItemSkuV2VO;
import com.aliyun.gts.gmall.manager.front.item.facade.ItemDetailFacade;
import com.aliyun.gts.gmall.platform.promotion.api.facade.cust.PromotionReadFacade;
import io.swagger.annotations.ApiOperation;

/**
 * 商品详情请求
 * 
 * @Title: ItemDetailController.java
 * @Description: TODO(用一句话描述该文件做什么)
 * @author zhao.qi
 * @date 2024年10月29日 16:37:51
 * @version V1.0
 */
@RestController
@RequestMapping("/api/item")
public class ItemDetailController {
    @Autowired
    private ItemDetailFacade itemDetailFacade;

    @Autowired
    private ItemFacade itemFacade;

    @ApiOperation(value = "商品详情主接口")
    @PostMapping("/queryDetail")
    public @ResponseBody RestResponse<ItemDetailVO> queryDetail(@RequestBody ItemDetailRestQuery itemDetailRestQuery) {
        return RestResponse.okWithoutMsg(itemDetailFacade.queryItemDetail(itemDetailRestQuery));
    }

    @ApiOperation("商品SKU单独模块；购物车等场景使用")
    @PostMapping("/querySku")
    public @ResponseBody RestResponse<ItemDetailVO> querySku(@RequestBody ItemSkuRestQuery itemSkuRestQuery) {
        return RestResponse.okWithoutMsg(itemDetailFacade.queryItemSku(itemSkuRestQuery));
    }

    @ApiOperation("商品详情评价列表")
    @PostMapping("/queryEvaluationList")
    public @ResponseBody RestResponse<PageInfo<ItemEvaluationVO>> queryEvaluationList(@RequestBody EvaluationRestQuery evaluationRestQuery) {
        return RestResponse.okWithoutMsg(itemDetailFacade.queryEvaluationList(evaluationRestQuery));
    }

    @ApiOperation("商品详情热门评价列表")
    @PostMapping("/queryHotEvaluationList")
    public @ResponseBody RestResponse<PageInfo<ItemEvaluationVO>> queryHotEvaluationList(@RequestBody EvaluationRestQuery evaluationRestQuery) {
        return RestResponse.okWithoutMsg(itemDetailFacade.queryHotEvaluationList(evaluationRestQuery));
    }

    @ApiOperation("商品物流模块")
    @PostMapping("/queryItemFreight")
    public @ResponseBody RestResponse<ItemFreightVO> queryItemFreight(@RequestBody ItemFreightRestQuery itemFreightRestQuery) {
        return RestResponse.okWithoutMsg(itemDetailFacade.queryItemFreight(itemFreightRestQuery));
    }


    @ApiOperation("多sku多库存选中后商品价格计算(不包括优惠)")
    @PostMapping("/queryItemPrice")
    public @ResponseBody RestResponse<ItemPriceVO> queryItemPrice(@RequestBody ItemPriceRestQuery query) {
        PromotionReadFacade c;
        return RestResponse.okWithoutMsg(itemDetailFacade.queryItemPrice(query));
    }

    @ApiOperation("查询商品sku")
    @PostMapping("/queryItemSkuV2")
    public @ResponseBody RestResponse<ItemSkuV2VO> queryItemSkuV2(@RequestBody ItemSkuRestQuery query) {
        return RestResponse.okWithoutMsg(itemDetailFacade.queryItemSkuV2(query));
    }

    @ApiOperation("查询商品sku的features")
    @PostMapping("/queryItemSkuFeatures")
    public @ResponseBody RestResponse<List<ItemSkuFeaturesVO>> queryItemSkuFeatures(@RequestBody ItemSkuRestQuery query) {
        return RestResponse.okWithoutMsg(itemDetailFacade.queryItemSkuFeatures(query));
    }

    @ApiOperation("查询商品分期")
    @PostMapping("/queryItemDeadline")
    public @ResponseBody RestResponse<List<ItemDeadlineVO>> queryItemDeadline(@RequestBody ItemDeadlineRestQuery query) {
        return RestResponse.okWithoutMsg(itemDetailFacade.queryItemDeadline(query));
    }

    @ApiOperation("查询商品特性")
    @PostMapping("/queryItemCharacteristics")
    public @ResponseBody RestResponse<List<ItemCharacteristicsVO>> queryItemCharacteristics(@RequestBody ItemDetailV2RestQuery query) {
        return RestResponse.okWithoutMsg(itemDetailFacade.queryItemCharacteristics(query));
    }

    /**
     * 查询商品明细
     * 
     * @param req
     * @return
     */
    @PostMapping("/queryItemDetail")
    public @ResponseBody RestResponse<ItemDetailV2VO> queryItemDetail(@RequestBody ItemDetailV2RestQuery req) {
        return RestResponse.okWithoutMsg(itemDetailFacade.queryItemDetailV2(req));
    }

    /**
     * 查询商品价格
     * @param req
     * @return  ItemSkuPriceRestQuery
     */
    @PostMapping("/querySkuPrice")
    public @ResponseBody RestResponse<ItemSkuPriceVO> querySkuPrice(@RequestBody ItemSkuPriceRestQuery req) {
        return RestResponse.okWithoutMsg(itemDetailFacade.querySkuPrice(req));
    }

    /**
     * 查询商品商家列表
     * 
     * @param query
     * @return
     */
    @PostMapping("/queryItemMerchantInfo")
    public @ResponseBody RestResponse<ItemDetailV2VO> queryItemMerchantInfo(@RequestBody ItemSaleSellerQuery query) {
        CustDTO user = UserHolder.getUser();
        return RestResponse.okWithoutMsg(itemFacade.query(query, user));
    }

    /**
     * 优惠券信息
     * @param req CouponInfoReqQuery
     * @return CouponInfoVO
     */
    @PostMapping("/queryCouponInfo")
    public @ResponseBody RestResponse<List<CouponInstanceVO>> queryCouponInfo(@RequestBody CouponInfoReqQuery req) {
        return RestResponse.okWithoutMsg(itemDetailFacade.queryCouponInfo(req));
    }

    /**
     * sku所有订单评价平均评分
     * @param req  skuId
     * @return SkuAverageScoreVO
     */
    @PostMapping("/querySkuAverageScore")
    public @ResponseBody RestResponse<SkuAverageScoreVO> querySkuAverageScore(@RequestBody SkuAverageScoreQuery req) {
        return RestResponse.okWithoutMsg(null);
    }

    /**
     * 评价图片 （所有）
     * @param req SkuEvaluatePictureQuery skuId
     * @return SkuEvaluatePictureVO
     */
    @PostMapping("/querySkuEvaluatePicture")
    public @ResponseBody RestResponse<List<SkuEvaluatePictureVO>> querySkuEvaluatePicture(@RequestBody SkuEvaluatePictureQuery req) {
        return RestResponse.okWithoutMsg( null);
    }

    /**
     * 单个评价详情
     * @param req  skuId
     * @return SkuEvaluateInfoVO
     */
    @PostMapping("/querySkuEvaluateInfo")
    public @ResponseBody RestResponse<List<SkuEvaluateInfoVO>> querySkuEvaluateInfo(@RequestBody SkuEvaluateInfoQuery req) {
        return RestResponse.okWithoutMsg(itemDetailFacade.querySkuEvaluateInfo(req));
    }

    /**
     * 评价列表
     * @param req SkuEvaluateQuery
     * @return SkuEvaluateInfoVO
     */
    @PostMapping("/querySkuEvaluateList")
    public @ResponseBody RestResponse<PageInfo<SkuEvaluateInfoVO>> querySkuEvaluateList(@RequestBody SkuEvaluateQuery req) {
        return RestResponse.okWithoutMsg(itemDetailFacade.querySkuEvaluateList(req));
    }
    //
    //类似商品推荐接口

    /**
     * 商品详情页面 添加购物车 价格计算接口
     * @anthor shifeng
     * @param itemSkuCartPriceRestQuery
     * @return ItemSkuPriceRestQuery
     * 2025-1-22 18:34:13
     */
    @PostMapping("/calcItemAddCart")
    public @ResponseBody RestResponse<ItemSkuCartPriceVO> calcItemAddCart(@RequestBody ItemSkuCartPriceRestQuery itemSkuCartPriceRestQuery) {
        CustDTO user = UserHolder.getUser();
        if (user != null) {
            itemSkuCartPriceRestQuery.setCustId(user.getCustId());
        }
        return RestResponse.okWithoutMsg(itemDetailFacade.calcItemAddCart(itemSkuCartPriceRestQuery));
    }


    /**
     * 生成商详二维码  ShortLink
     * @anthor shifeng
     * @param itemDetailLinkRestQuery
     * @return ItemSkuPriceRestQuery
     * 2025-1-22 18:34:13
     */
    @PostMapping("/itemLinkShort")
    public @ResponseBody RestResponse<ItemDetailLinkVO> itemLinkShort(@RequestBody ItemDetailLinkRestQuery itemDetailLinkRestQuery) {
        return RestResponse.okWithoutMsg(itemDetailFacade.itemLinkShort(itemDetailLinkRestQuery));
    }

    /**
     * 获取类目级别
     *
     * @param categoryId
     * @return
     */
    @PostMapping("/queryCategoryLevel")
    public @ResponseBody RestResponse<Integer> queryCategoryLevel(@RequestBody CategoryQuery categoryQuery) {
        return itemDetailFacade.queryCategoryLevel(categoryQuery.getCategoryId());
    }
}
