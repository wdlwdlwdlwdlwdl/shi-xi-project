package com.aliyun.gts.gmall.manager.front.item.facade.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Resource;

import com.aliyun.gts.gmall.manager.front.customer.dto.utils.ResponseUtils;
import com.aliyun.gts.gmall.manager.front.item.dto.input.query.*;
import com.aliyun.gts.gmall.manager.front.item.dto.output.*;
import com.aliyun.gts.gmall.platform.item.api.facade.category.CategoryReadFacade;
import com.aliyun.gts.gmall.platform.open.customized.api.dto.input.GoogleShortLinkRpcReq;
import com.aliyun.gts.gmall.platform.open.customized.api.dto.output.ShortLinkDto;
import com.aliyun.gts.gmall.platform.open.customized.api.facade.GoogleFacade;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.aliyun.gts.gmall.center.item.api.dto.output.AgreementPriceDTO;
import com.aliyun.gts.gmall.center.item.api.dto.output.B2bItemDTO;
import com.aliyun.gts.gmall.center.item.api.util.AgreementUtils;
import com.aliyun.gts.gmall.center.item.common.enums.AgreementPriceType;
import com.aliyun.gts.gmall.center.item.common.enums.ItemType;
import com.aliyun.gts.gmall.center.trade.api.dto.input.BuyOrderCntReq;
import com.aliyun.gts.gmall.center.trade.api.facade.PromotionBuyOrderFacade;
import com.aliyun.gts.gmall.center.user.api.dto.input.NewCustomerQueryReq;
import com.aliyun.gts.gmall.center.user.api.facade.CustomerReadExtFacade;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.dto.PageParam;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.framework.server.util.DateTimeUtils;
import com.aliyun.gts.gmall.framework.server.util.PublicFileHttpUrl;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import com.aliyun.gts.gmall.manager.biz.output.CustDTO;
import com.aliyun.gts.gmall.manager.front.common.consts.BizConst;
import com.aliyun.gts.gmall.manager.front.common.consts.ChannelEnum;
import com.aliyun.gts.gmall.manager.front.common.util.DateUtils;
import com.aliyun.gts.gmall.manager.front.common.util.FrontUtils;
import com.aliyun.gts.gmall.manager.front.common.util.GmallBeanUtils;
import com.aliyun.gts.gmall.manager.front.common.util.ItemUtils;
import com.aliyun.gts.gmall.manager.front.common.util.UserHolder;
import com.aliyun.gts.gmall.manager.front.item.adaptor.CategoryPropGroupAdaptor;
import com.aliyun.gts.gmall.manager.front.item.adaptor.FileAdaptor;
import com.aliyun.gts.gmall.manager.front.item.adaptor.ItemAdaptor;
import com.aliyun.gts.gmall.manager.front.item.adaptor.ItemCardEsAdaptor;
import com.aliyun.gts.gmall.manager.front.item.adaptor.ItemPromotionAdaptor;
import com.aliyun.gts.gmall.manager.front.item.adaptor.SearchAdaptor;
import com.aliyun.gts.gmall.manager.front.item.constants.PayModeEnum;
import com.aliyun.gts.gmall.manager.front.item.constants.PromotionToolCodeEnum;
import com.aliyun.gts.gmall.manager.front.item.convertor.CategoryPropGroupConvertor;
import com.aliyun.gts.gmall.manager.front.item.convertor.ItemConvertor;
import com.aliyun.gts.gmall.manager.front.item.dto.output.sku.ItemSkuFeaturesVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.sku.ItemSkuPropVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.sku.ItemSkuV2VO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.sku.ItemSkuVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.sku.ItemSkuValueVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.sku.SkuPropVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.sku.SkuQuoteCityPriceTemp;
import com.aliyun.gts.gmall.manager.front.item.dto.output.sku.SkuQuoteCityPriceVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.sku.SkuQuoteListVO;
import com.aliyun.gts.gmall.manager.front.item.dto.utils.DetailShowType;
import com.aliyun.gts.gmall.manager.front.item.dto.utils.ItemFrontResponseCode;
import com.aliyun.gts.gmall.manager.front.item.dto.utils.PromotionFillUtils;
import com.aliyun.gts.gmall.manager.front.item.facade.ItemDetailFacade;
import com.aliyun.gts.gmall.manager.front.item.localcache.ItemLocalCacheManager;
import com.aliyun.gts.gmall.platform.item.api.dto.output.category.CategoryDTO;
import com.aliyun.gts.gmall.platform.item.api.dto.output.category.CategoryDeliveryDTO;
import com.aliyun.gts.gmall.platform.item.api.dto.output.item.LoanPeriodDTO;
import com.aliyun.gts.gmall.platform.promotion.api.dto.input.PromotionQueryReq;
import com.aliyun.gts.gmall.platform.promotion.api.dto.output.CouponInstanceDTO;
import com.aliyun.gts.gmall.platform.promotion.api.dto.output.ItemPriceDTO;
import com.aliyun.gts.gmall.platform.promotion.api.dto.output.PromotionDetailDTO;
import com.aliyun.gts.gmall.platform.promotion.api.dto.output.display.PromotionInfo;
import com.aliyun.gts.gmall.platform.promotion.api.dto.output.display.PromotionSummation;
import com.aliyun.gts.gmall.platform.promotion.common.constant.AttributesKey;
import com.aliyun.gts.gmall.platform.promotion.common.constant.PromotionExtrasKey;
import com.aliyun.gts.gmall.platform.promotion.common.constant.PromotionToolCodes;
import com.aliyun.gts.gmall.platform.promotion.common.model.ItemDividePriceDTO;
import com.aliyun.gts.gmall.platform.promotion.common.model.LocalizedFormula;
import com.aliyun.gts.gmall.platform.promotion.common.model.LocalizedParams;
import com.aliyun.gts.gmall.platform.promotion.common.model.PromDivideDTO;
import com.aliyun.gts.gmall.platform.promotion.common.model.TargetCust;
import com.aliyun.gts.gmall.platform.promotion.common.model.TargetItem;
import com.aliyun.gts.gmall.platform.promotion.common.model.TargetItemCluster;
import com.aliyun.gts.gmall.platform.promotion.common.type.AssetsType;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.evaluation.EvaluationIdReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.evaluation.EvaluationQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.evaluation.EvaluationDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.evaluation.ItemEvaluationDTO;
import com.aliyun.gts.gmall.platform.trade.api.facade.evaluation.EvaluationReadFacade;
import com.aliyun.gts.gmall.platform.trade.api.facade.order.OrderReadFacade;
import com.aliyun.gts.gmall.platform.trade.common.util.MoneyUtils;
import com.aliyun.gts.gmall.platform.user.api.dto.output.CustomerDTO;
import com.aliyun.gts.gmall.searcher.common.SearchClient;
import com.google.common.collect.Lists;
import cn.hutool.core.bean.BeanUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 商品详情实现
 *
 * @author tiansong
 */
@Slf4j
@Service
public class ItemDetailFacadeImpl implements ItemDetailFacade, GmallBeanUtils {
    @Autowired
    private SearchClient searchClient;
    @Value("${env}")
    protected String env;
    @Resource
    private ItemAdaptor itemAdaptor;
    @Resource
    private SearchAdaptor searchAdaptor;
    @Resource
    private ItemConvertor itemConvertor;
    @Resource
    private ItemLocalCacheManager itemLocalCacheManager;
    @Autowired
    private PublicFileHttpUrl publicFileHttpUrl;
    @Resource
    private PromotionBuyOrderFacade promotionBuyOrderFacade;

    @Autowired
    private CustomerReadExtFacade customerReadExtFacade;

    @Autowired
    private CategoryReadFacade categoryReadFacade;

    @Autowired
    private ItemComponent itemComp;

    @Resource
    private ItemCardEsAdaptor itemCardEsAdaptor;

    @Autowired
    private OrderReadFacade orderReadFacade;

    @Autowired
    private EvaluationReadFacade evaluationReadFacade;

    @Autowired
    private ItemPromotionAdaptor itemPromotionAdaptor;

    @Autowired
    private CategoryPropGroupAdaptor categoryPropGroupAdaptor;

    @Autowired
    private CategoryPropGroupConvertor categoryPropGroupConvertor;

    @Autowired
    private FileAdaptor fileAdaptor;

    @Autowired
    private GoogleFacade googleFacade;

    @NacosValue(value = "${front-manager.promotion.newuser.group.id:100}", autoRefreshed = true)
    private Long SPECIFIC_GROUP_ID;

    @NacosValue(value = "${front-manager.promotion.newuser.limit.day:30}", autoRefreshed = true)
    private Integer limit;

    private static final String AW_PREFIX = "AW_";

    @Value("${front-manager.default-icon}")
    private String defaultIcon;


    @Override
    public ItemDetailVO queryItemDetail(ItemDetailRestQuery itemDetailRestQuery) {
        Long itemId = itemDetailRestQuery.getItemId();
        // 1. 获取商品信息，含sku和类目
        ItemDetailVO itemDetailVO = getItemBase(itemId);
        itemDetailVO = itemConvertor.copy(itemDetailVO);
        // 1.1 获取协议价
        fillAgreementData(itemId, itemDetailRestQuery.getCustId(), itemDetailVO);
        // 2. 获取优惠信息
        PromotionQueryReq detailPromotionQueryReq = this.buildPromotionQuery(itemDetailRestQuery, itemDetailVO);
        PromotionInfo detailDisplay = itemAdaptor.queryPromotion(detailPromotionQueryReq);
        this.fillPromotionData(itemDetailVO, detailDisplay);
        // 3. 获取评价信息
        // long count = searchAdaptor.queryTotalCount(itemDetailRestQuery.getItemId());
        PageInfo<ItemEvaluationVO> evPage = searchAdaptor.queryHot(itemDetailRestQuery.getItemId());
        fillEvaluationData(evPage.getList());
        itemDetailVO.setTotalEvaluation(evPage.getTotal());
        itemDetailVO.setHotEvaluationList(evPage.getList());
        // 4. 获取购物车信息
        Integer cartQuantity = itemAdaptor.queryCartQuantity(itemDetailRestQuery.getCustId());
        itemDetailVO.setCartQuantity(cartQuantity);
        // 5. 展示按钮控制
        // 判断是否奖品
        Boolean isAward;
        if (StringUtils.isBlank(itemDetailRestQuery.getPromotionSource())) {
            isAward = false;
        } else {
            isAward = itemDetailRestQuery.getPromotionSource().startsWith(AW_PREFIX);
        }
        itemDetailVO.setIsAward(isAward);
        ItemDetailButtonVO itemDetailButtonVO = ItemDetailButtonVO.builder().toBuy(true).addCart(addCart(itemDetailVO)).build();
        // 根据展示类型设置
        itemDetailButtonVO.setButtonValue(DetailShowType.find(itemDetailVO.getShowType()));
        // 禁止购买兜底,放在所有的按钮控制判断逻辑最后面
        if (itemDetailVO.getItemBaseVO().isDisableBuy()) {
            itemDetailButtonVO.setToBuy(false);
            itemDetailButtonVO.setAddCart(false);
        }
        itemDetailVO.setShowButtons(itemDetailButtonVO);
        // 6. 定金尾款信息(依赖优惠价格)
        this.fillItemDeposit(itemDetailVO);
        return itemDetailVO;
    }

    private ItemDetailVO getItemBase(Long itemId) {
        ItemDetailVO d = itemLocalCacheManager.get(itemId);
        if (d.getItemBaseVO() != null) {
            // 清除营销限购缓存
            d.getItemBaseVO().setBuyLimitCamp(null);
            d.getItemBaseVO().setDisableBuyCamp(false);
        }
        return d;
    }

    private boolean addCart(ItemDetailVO itemDetailVO) {
        Integer type = itemDetailVO.getItemBaseVO().getItemType();
        // 电子凭证、定金尾款
        if (ItemType.EVOUCHER.getType().equals(type) || ItemType.DEPOSIT.getType().equals(type)) {
            return false;
        }
        // 奖品
        if (Boolean.TRUE.equals(itemDetailVO.getIsAward())) {
            return false;
        }
        // 预售
        if (Integer.valueOf(2).equals(itemDetailVO.getDepositType())) {
            return false;
        }
        // 秒杀在后面根据展示类型里设置
        return true;
    }

    private void fillItemDeposit(ItemDetailVO itemDetailVO) {
        DepositConfigVO depositConfigVO = itemDetailVO.getDepositConfigVO();
        if (depositConfigVO == null) {
            return;
        }
        if (CollectionUtils.isEmpty(itemDetailVO.getItemSkuVOList())) {
            return;
        }
        // 已经设置过了
        if (itemDetailVO.getDepositType() != null) {
            return;
        }
        itemDetailVO.setDepositType(1);
        // 定金价格
        if (depositConfigVO.getType() == DepositConfigVO.TYPE_1) {
            // 按比例
            itemDetailVO.getItemSkuVOList().forEach(x -> {
                if (x.getPromPrice() != null) {
                    x.setDepositPrice(Math.round(x.getPromPrice() * depositConfigVO.getRatio()));
                } else {
                    x.setDepositPrice(Math.round(x.getPrice() * depositConfigVO.getRatio()));
                }
            });
            LongSummaryStatistics minAndMax = itemDetailVO.getItemSkuVOList().stream().mapToLong(ItemSkuVO::getDepositPrice).summaryStatistics();
            itemDetailVO.setDepositPrice(ItemUtils.fen2YuanRange(minAndMax.getMin(), minAndMax.getMax()));
        } else if (depositConfigVO.getType() == DepositConfigVO.TYPE_2) {
            // 固定价格
            itemDetailVO.getItemSkuVOList().forEach(x -> x.setDepositPrice(depositConfigVO.getAmount()));
            itemDetailVO.setDepositPrice(ItemUtils.fen2Yuan(depositConfigVO.getAmount()));
        }
        // 尾款时间
        itemDetailVO.setDepositDayDisplay(String.format(I18NMessageUtils.getMessage("balance.payment.time") + ":尾款金额确认后%s天内", depositConfigVO.getDay())); // #
                                                                                                                                                          // "尾款支付时间
        // 是否可退
        itemDetailVO.setDepositCanRefused(true);
    }

    private void fillEvaluationData(List<ItemEvaluationVO> mainList) {
        // 1 评价补充回复信息
        this.fillEvaluationReply(mainList);
        // 2 评价补充用户信息
        this.fillEvaluationCustomer(mainList);
        // 3 处理评价中的图片和视频格式
        this.fillPicAndVideo(mainList);
    }

    private void fillPromotionData(ItemDetailVO itemDetailVO, PromotionInfo detailDisplay) {
        if (detailDisplay == null) {
            return;
        }
        // 补充优惠券信息
        List<CouponInstanceDTO> coupons = PromotionFillUtils.parseAllCampaign(detailDisplay.getCoupons());
        if (CollectionUtils.isNotEmpty(coupons)) {
            List<ItemPromotionVO> couponList = itemConvertor.convertCoupon(coupons);
            couponList.stream().forEach(itemPromotionVO -> itemPromotionVO.setIsCoupon(true));
            itemDetailVO.setCoupons(couponList);
            buildItemPromotion(couponList);
        }
        // 补充优惠活动信息
        List<PromotionDetailDTO> campaigns = PromotionFillUtils.parseAllCampaign(detailDisplay.getCampaigns());
        if (CollectionUtils.isNotEmpty(campaigns)) {
            List<ItemPromotionVO> campList = itemConvertor.convertCampaigns(campaigns);
            itemDetailVO.setCampaigns(campList);
            buildItemPromotion(campList);
        }
        // 补充商品优惠价格信息
        if (CollectionUtils.isEmpty(detailDisplay.getItemPrices())) {
            return;
        }
        // 第一个商品的价格
        ItemPriceDTO itemPriceDTO = detailDisplay.getItemPrices().get(0);
        if (itemPriceDTO == null) {
            return;
        }
        // sku的价格展示
        this.fillSkuPromotionPrice(itemDetailVO, itemPriceDTO);
        // 设置秒杀信息
        this.fillMiaoshaData(itemDetailVO, itemPriceDTO);
        // 预售活动信息
        this.fillYushouData(itemDetailVO, itemPriceDTO);
        // 限购信息
        if (itemPriceDTO.getBuyLimitNum() != null && itemPriceDTO.getBuyLimitNum() > 0) {
            // 单次下单件数
            itemDetailVO.getItemBaseVO().setBuyLimitCamp(itemPriceDTO.getBuyLimitNum().longValue());
        }
        if (itemPriceDTO.getBuyLimitOrds() != null && itemPriceDTO.getBuyLimitOrds() > 0) {
            // 下单次数限制
            Long buyCnt = queryBuyCnt(itemPriceDTO);
            if (buyCnt != null && buyCnt >= itemPriceDTO.getBuyLimitOrds()) {
                itemDetailVO.getItemBaseVO().setDisableBuyCamp(true);
            }
        }
    }

    private Long queryBuyCnt(ItemPriceDTO itemPriceDTO) {
        Long custId = Optional.ofNullable((CustDTO) UserHolder.getUser()).map(CustDTO::getCustId).orElse(null);
        if (custId == null) {
            return null;
        }
        Long campId = Optional.ofNullable(itemPriceDTO.getCamp()).map(PromotionDetailDTO::getCampaignId).orElse(null);
        if (campId == null) {
            return null;
        }

        BuyOrderCntReq req = new BuyOrderCntReq();
        req.setCampId(campId);
        req.setCustId(custId);
        req.setItemId(itemPriceDTO.getItemId());
        RpcResponse<Long> resp;
        try {
            resp = promotionBuyOrderFacade.queryBuyOrdCnt(req);
        } catch (Exception e) {
            log.warn("queryBuyOrdCnt failed req: {} ", JSON.toJSONString(req), e);
            return null; // 降级不限, 下单时会限住
        }
        if (!resp.isSuccess()) {
            log.warn("queryBuyOrdCnt failed req: {} , resp: {} ", JSON.toJSONString(req), JSON.toJSONString(resp));
            return null; // 降级不限, 下单时会限住
        }
        return resp.getData();
    }

    private void fillSkuPromotionPrice(ItemDetailVO itemDetailVO, ItemPriceDTO itemPriceDTO) {
        // 目前一口价没有预热,不展示价格;秒杀有
        if (itemPriceDTO.isInPreheat() && !PromotionToolCodes.MIAOSHA.equals(itemPriceDTO.getCamp().getPromotionToolCode())) {
            return;
        }
        final Long[] maxPromPrice = {Long.MIN_VALUE};
        final Long[] minPromPrice = {Long.MAX_VALUE};
        // 补充SKU价格
        itemDetailVO.getItemSkuVOList().forEach(itemSkuVO -> {
            LocalizedParams param = new LocalizedParams();
            param.setOrig(itemSkuVO.getPrice());
            param.setSkuId(itemSkuVO.getId());
            Long promPrice = itemPriceDTO.calculateVariablePrice(param);

            // 优惠价保护，避免优惠价高于商品原价
            if (promPrice > itemSkuVO.getPrice()) {
                promPrice = itemSkuVO.getPrice();
            }
            itemSkuVO.setPromPrice(promPrice);
            BigDecimal divide = BigDecimal.valueOf(promPrice).divide(BigDecimal.valueOf(itemSkuVO.getPrice()), 2, RoundingMode.DOWN);
            itemSkuVO.setDiscount(divide.doubleValue());
            if (maxPromPrice[0] < promPrice) {
                maxPromPrice[0] = promPrice;
            }
            if (minPromPrice[0] > promPrice) {
                minPromPrice[0] = promPrice;
            }
        });
        // 商品区间价格优惠
        itemDetailVO.setPromPrice(ItemUtils.fen2YuanRange(minPromPrice[0], maxPromPrice[0]));
    }


    /**
     * 活动信息
     *
     * @param promotions
     */
    private void buildItemPromotion(List<ItemPromotionVO> promotions) {
        if (CollectionUtils.isEmpty(promotions)) {
            return;
        }
        Date now = new Date();
        // 设置活动是否开始
        for (ItemPromotionVO promotion : promotions) {
            Boolean start = DateUtils.isBetween(now, promotion.getStartTime(), promotion.getEndTime());
            promotion.setPromStart(start);
        }
    }

    private PromotionQueryReq buildPromotionQuery(ItemDetailRestQuery itemDetailRestQuery, ItemDetailVO itemDetailVO) {
        PromotionQueryReq detailPromotionQueryReq = new PromotionQueryReq();
        // 设置用户
        TargetCust targetCust = new TargetCust();
        targetCust.setCustId(itemDetailRestQuery.getCustId());
        if (itemDetailRestQuery.getCustId() != null && isNewUser(itemDetailRestQuery.getCustId())) {
            targetCust.setIsNewUser(true);
            targetCust.setSpecificGroupId(SPECIFIC_GROUP_ID);
        }
        detailPromotionQueryReq.setEnableCache(true);
        detailPromotionQueryReq.setPromotionSource(itemDetailRestQuery.getPromotionSource());
        detailPromotionQueryReq.setCust(targetCust);
        detailPromotionQueryReq.setChannel(ChannelEnum.get(itemDetailRestQuery.getChannel()).getId());
        TargetItem targetItem = new TargetItem();
        targetItem.setItemId(itemDetailRestQuery.getItemId());
        targetItem.setItemQty(1);
        targetItem.setSellerId(itemDetailVO.getSellerId());
        if (CollectionUtils.isNotEmpty(itemDetailVO.getItemSkuVOList())) {
            // 选择SKU最高价格查询优惠，避免价格因素造成无法命中优惠的情况；skuId无效，不再传递
            // targetItem.setSkuOriginPrice(
            // itemDetailVO.getItemSkuVOList().stream().mapToLong(ItemSkuVO::getPrice).max().getAsLong());
            // 营销价可设置到SKU后，skuId必传
            ItemSkuVO maxSku = null;
            for (ItemSkuVO sku : itemDetailVO.getItemSkuVOList()) {
                if (maxSku == null || sku.getPrice() > maxSku.getPrice()) {
                    maxSku = sku;
                }
            }
            targetItem.setSkuOriginPrice(maxSku.getPrice());
            targetItem.setSkuId(maxSku.getId());
        }
        // 协议价
        if (itemDetailVO.isAgreementPrice()) {
            targetItem.putAttribute(AttributesKey.TARGET_ITEM_AGREEMENT_PRICE, true);
        }
        detailPromotionQueryReq.setItem(targetItem);
        detailPromotionQueryReq.setWithAttainable(true);
        // 活动预热
        detailPromotionQueryReq.setWithPreheat(true);
        return detailPromotionQueryReq;
    }

    private Boolean isNewUser(Long custId) {
        // 判断是否新人
        NewCustomerQueryReq req = new NewCustomerQueryReq();
        req.setLimit(limit);
        req.setId(custId);
        RpcResponse<Boolean> rpcResponse = customerReadExtFacade.queryIsNewCustomer(req);
        if (rpcResponse.isSuccess()) {
            return rpcResponse.getData();
        }
        return false;
    }

    private void fillPicAndVideo(List<ItemEvaluationVO> mainList) {
        if (CollectionUtils.isEmpty(mainList)) {
            return;
        }

        List<ItemEvaluationVO> all = new ArrayList<>();
        mainList.forEach(evaluationVO -> {
            all.add(evaluationVO);
            if (evaluationVO.getReplyList() != null) {
                all.addAll(evaluationVO.getReplyList());
            }
        });

        all.forEach(evaluationVO -> {
            // 图片转List展示
            // evaluationVO.setRatePicList(FrontUtils.toList(evaluationVO.getRatePic()));
            List<String> picList = FrontUtils.toList(evaluationVO.getRatePic());
            evaluationVO.setRatePicList(ossToHttp(picList));
            evaluationVO.setRatePic(null);
            // 视频转List展示
            // evaluationVO.setRateVideoList(FrontUtils.toList(evaluationVO.getRateVideo()));
            List<String> vdoList = FrontUtils.toList(evaluationVO.getRateVideo());
            evaluationVO.setRateVideoList(ossToHttp(vdoList));
            evaluationVO.setRateVideo(null);
        });
    }

    private List<String> ossToHttp(List<String> source) {
        if (source == null) {
            return source;
        }
        List<String> target = new ArrayList<>();
        for (String path : source) {
            target.add(publicFileHttpUrl.getFileHttpUrl(path));
        }
        return target;
    }


    private void fillEvaluationCustomer(List<ItemEvaluationVO> mainList) {
        if (CollectionUtils.isEmpty(mainList)) {
            return;
        }
        // 获取用户ID
        Set<Long> custIdSet = mainList.stream().filter(evaluationVO -> StringUtils.isNotBlank(evaluationVO.getCustId()))
                .map(evaluationVO -> NumberUtils.toLong(evaluationVO.getCustId())).collect(Collectors.toSet());
        if (CollectionUtils.isNotEmpty(custIdSet)) {
            List<CustomerDTO> customerDTOList = itemAdaptor.queryCustomerByIds(Lists.newArrayList(custIdSet));
            if (CollectionUtils.isNotEmpty(customerDTOList)) {
                Map<Long, CustomerDTO> customerDtoMap =
                        customerDTOList.stream().collect(Collectors.toMap(CustomerDTO::getId, customerDTO -> customerDTO, (k1, k2) -> k1));
                // 服务正常，补充用户信息，并直接返回
                mainList.forEach(mainEvaluation -> {
                    CustomerDTO customerDTO = customerDtoMap.get(NumberUtils.toLong(mainEvaluation.getCustId()));
                    if (customerDTO == null) {
                        // 用户信息查询不到，使用默认展示
                        mainEvaluation.setNickname(FrontUtils.showNickHidden(null));
                        mainEvaluation.setHeadUrl(defaultIcon);
                        return;
                    }
                    mainEvaluation.setNickname(FrontUtils.showNickHidden(customerDTO.getNickname()));
                    mainEvaluation.setHeadUrl(customerDTO.getHeadUrl() == null ? defaultIcon : customerDTO.getHeadUrl());
                });
                return;
            }
        }
        // 服务异常，使用默认展示
        mainList.forEach(mainEvaluation -> {
            mainEvaluation.setNickname(FrontUtils.showNickHidden(null));
            mainEvaluation.setHeadUrl(defaultIcon);
        });
    }

    private void fillEvaluationReply(List<ItemEvaluationVO> mainList) {
        if (CollectionUtils.isEmpty(mainList)) {
            return;
        }
        // 获取评价的回复信息
        List<Long> orderIdList = mainList.stream().map(ItemEvaluationVO::getOrderId).collect(Collectors.toList());
        List<ItemEvaluationVO> replyList = searchAdaptor.queryReply(orderIdList);
        if (CollectionUtils.isEmpty(replyList)) {
            return;
        }
        // 按照订单ID分组
        Map<Long, List<ItemEvaluationVO>> replyMap = replyList.stream().collect(Collectors.groupingBy(ItemEvaluationVO::getOrderId));
        // 填充到主评价
        mainList.forEach(mainEvaluation -> mainEvaluation.setReplyList(replyMap.get(mainEvaluation.getOrderId())));
    }

    private void filterSkuPropValue(ItemDetailVO itemDetailVO) {
        if (CollectionUtils.isEmpty(itemDetailVO.getItemSkuVOList()) || CollectionUtils.isEmpty(itemDetailVO.getItemSkuPropVOList())) {
            return;
        }
        // convert to set
        Set<String> valueSet = itemDetailVO.getItemSkuVOList().stream().flatMap(prop -> prop.getSkuPropList().stream()).map(value -> value.getVid())
                .collect(Collectors.toSet());
        // filter value
        itemDetailVO.getItemSkuPropVOList().forEach(itemSkuPropVO -> {
            List<ItemSkuValueVO> itemSkuValueVOList =
                    itemSkuPropVO.getValueList().stream().filter(itemSkuValueVO -> valueSet.contains(itemSkuValueVO.getVid())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(itemSkuValueVOList)) {
                itemSkuPropVO.setValueList(itemSkuValueVOList);
            }
        });
    }

    @Override
    public ItemDetailVO queryItemSku(ItemSkuRestQuery itemSkuRestQuery) {
        // 1. 获取商品和sku信息
        ItemDetailVO itemDetailVO = itemAdaptor.getItemSku(itemSkuRestQuery.getItemId());
        // 2. 如有已选中的sku，返回skuId
        itemDetailVO.setSelectedSkuId(itemSkuRestQuery.getSkuId());
        // 3. 补充sku名称
        this.fillSkuName(itemDetailVO);
        // 4. 过滤无效的sku属性值
        this.filterSkuPropValue(itemDetailVO);
        // 5. 补充sku的图片信息
        this.fillSkuPic(itemDetailVO);

        return itemDetailVO;
    }

    private void fillSkuPic(ItemDetailVO itemDetailVO) {
        if (CollectionUtils.isEmpty(itemDetailVO.getItemBaseVO().getPictureList())) {
            // 商品没有图片，则无需为sku设置图片
            return;
        }
        String itemPicUrl = itemDetailVO.getItemBaseVO().getPictureList().get(0);
        itemDetailVO.getItemSkuVOList().forEach(itemSkuVO -> {
            SkuPropVO skuPropVOPic =
                    itemSkuVO.getSkuPropList().stream().filter(skuPropVO -> StringUtils.isNotBlank(skuPropVO.getPicUrl())).findFirst().orElse(null);
            itemSkuVO.setPicUrl(skuPropVOPic == null ? itemPicUrl : skuPropVOPic.getPicUrl());
        });
    }

    private void fillSkuName(ItemDetailVO itemDetailVO) {
        // 构建pid、vid为key，propName+valueName为value的双层map
        Map<Long/* pid */, Map<String/* vid */, String/* pName:vName */>> skuPropValueMap = itemDetailVO.getItemSkuPropVOList().stream()
                .collect(Collectors.toMap(ItemSkuPropVO::getPid, itemSkuPropVO -> itemSkuPropVO.getValueList().stream().collect(Collectors
                        .toMap(ItemSkuValueVO::getVid, itemSkuValueVO -> itemSkuPropVO.getName() + BizConst.SKU_PV_SPLIT + itemSkuValueVO.getValue()))));

        itemDetailVO.getItemSkuVOList().forEach(itemSkuVO -> {
            // 组装SKU名称
            StringBuilder skuName = new StringBuilder();
            itemSkuVO.getSkuPropList().forEach(skuPropVO -> {
                String propValueName = skuPropValueMap.get(skuPropVO.getPid()).get(skuPropVO.getVid());
                if (propValueName == null) {
                    return;
                }
                skuName.append(BizConst.SKU_PROP_SPLIT + propValueName);
            });
            // 删除第一个分隔符
            itemSkuVO.setSkuName(skuName.length() > 0 ? skuName.deleteCharAt(0).toString() : null);
            // 设置选中sku名称
            if (itemDetailVO.getSelectedSkuId() != null && itemDetailVO.getSelectedSkuId().equals(itemSkuVO.getId())) {
                itemDetailVO.setSelectedSkuName(itemSkuVO.getSkuName());
            }
        });
    }

    @Override
    public PageInfo<ItemEvaluationVO> queryEvaluationList(EvaluationRestQuery evaluationRestQuery) {
        PageInfo<ItemEvaluationVO> pageInfo = searchAdaptor.queryList(evaluationRestQuery.getItemId(), evaluationRestQuery.getPage().getPageNo());
        this.fillEvaluationData(pageInfo.getList());
        return pageInfo;
    }

    @Override
    public PageInfo<ItemEvaluationVO> queryHotEvaluationList(EvaluationRestQuery evaluationRestQuery) {
        PageInfo<ItemEvaluationVO> hotPage = searchAdaptor.queryHot(evaluationRestQuery.getItemId());
        this.fillEvaluationData(hotPage.getList());
        return hotPage;
    }

    @Override
    public ItemFreightVO queryItemFreight(ItemFreightRestQuery itemFreightRestQuery) {
        // 1. 查询商品
        ItemDetailVO itemDetailVO = itemAdaptor.getItemSkuWeak(itemFreightRestQuery.getItemId());
        if (null == itemDetailVO || ItemType.EVOUCHER.getType().equals(itemDetailVO.getItemBaseVO().getItemType())) {
            // 商品访问不到/电子凭证 不展示运费信息
            return null;
        }
        // 2. 确定SKU
        Long skuId = itemFreightRestQuery.getSkuId();
        if (skuId == null || skuId <= 0L) {
            // 默认使用商品的第一个SKU
            skuId = itemDetailVO.getItemSkuVOList().get(0).getId();
        }
        // 3. 获取收货地址
        AddressVO addressVO = this.queryAddress(itemFreightRestQuery);
        // 4. 计算运费
        Long freight = itemAdaptor.queryItemFreight(skuId, addressVO);
        return ItemFreightVO.builder().itemId(itemFreightRestQuery.getItemId()).skuId(skuId).addressVO(addressVO).freight(freight).build();
    }

    @Override
    public ItemDetailVO queryItemBase(ItemDetailRestQuery itemDetailRestQuery) {
        Long itemId = itemDetailRestQuery.getItemId();
        // 获取商品信息，含sku和类目
        ItemDetailVO itemDetailVO = getItemBase(itemId);
        itemDetailVO = itemConvertor.copy(itemDetailVO);

        // 获取协议价
        fillAgreementData(itemId, itemDetailRestQuery.getCustId(), itemDetailVO);

        // 获取优惠信息
        PromotionQueryReq detailPromotionQueryReq = this.buildPromotionQuery(itemDetailRestQuery, itemDetailVO);
        PromotionInfo detailDisplay = itemAdaptor.queryPromotion(detailPromotionQueryReq);
        this.fillPromotionData(itemDetailVO, detailDisplay);
        // 展示按钮控制
        // 判断是否奖品
        Boolean isAward;
        if (StringUtils.isBlank(itemDetailRestQuery.getPromotionSource())) {
            isAward = false;
        } else {
            isAward = itemDetailRestQuery.getPromotionSource().startsWith(AW_PREFIX);
        }
        itemDetailVO.setIsAward(isAward);
        ItemDetailButtonVO itemDetailButtonVO = ItemDetailButtonVO.builder().toBuy(true).addCart(addCart(itemDetailVO)).build();

        // 根据展示类型设置
        itemDetailButtonVO.setButtonValue(DetailShowType.find(itemDetailVO.getShowType()));
        // 禁止购买兜底,放在所有的按钮控制判断逻辑最后面
        if (itemDetailVO.getItemBaseVO().isDisableBuy()) {
            itemDetailButtonVO.setToBuy(false);
            itemDetailButtonVO.setAddCart(false);
        }

        itemDetailVO.setShowButtons(itemDetailButtonVO);

        return itemDetailVO;
    }

    @Override
    public ItemPriceVO queryItemPrice(ItemPriceRestQuery query) {

        ItemDetailVO itemDetailVO = getItemBase(query.getItemId());
        itemDetailVO = itemConvertor.copy(itemDetailVO);

        List<ItemSkuVO> itemSkuVOList = itemDetailVO.getItemSkuVOList();

        if (CollectionUtils.isEmpty(itemSkuVOList)) {
            return new ItemPriceVO(0L, "0.00");
        }

        // 获取协议价
        fillAgreementData(query.getItemId(), query.getCustId(), itemDetailVO);

        Map<Long, Long> skuMap = query.getSkuMap();

        long totalQuantity = 0, totalPrice = 0;

        for (ItemSkuVO sku : itemSkuVOList) {
            Long buyNum = skuMap.get(sku.getId());
            if (buyNum != null && buyNum > 0) {
                totalQuantity += buyNum;
                totalPrice += buyNum * sku.getPrice();
            }
        }

        B2bItemVO b2bItemVO = itemDetailVO.getB2bItem();
        if (b2bItemVO == null || CollectionUtils.isEmpty(b2bItemVO.getPriceList())) {
            return new ItemPriceVO(totalQuantity, ItemUtils.fen2Yuan(totalPrice));
        }

        Long price = b2bItemVO.getPrice(totalQuantity);
        if (price != null) {
            return new ItemPriceVO(totalQuantity, ItemUtils.fen2Yuan(totalQuantity * price));
        } else {
            return new ItemPriceVO(totalQuantity, ItemUtils.fen2Yuan(totalPrice));
        }

    }

    @Override
    public ItemSkuV2VO queryItemSkuV2(ItemSkuRestQuery req) {
        return itemAdaptor.queryItemSkuV2(req);
    }

    @Override
    public List<ItemSkuFeaturesVO> queryItemSkuFeatures(ItemSkuRestQuery query) {
        return itemAdaptor.queryItemSkuFeatures(query);
    }

    @Override
    public ItemDetailV2VO queryItemDetailV2(ItemDetailV2RestQuery query) {
        SkuOfferSearchReq req = new SkuOfferSearchReq();
        req.setSkuId(query.getSkuId());
        req.setCityCode(query.getCityCode());
        List<SkuQuoteListVO> skuQuoteListVos = getSearch(req, 1, 0);
        if (CollectionUtils.isEmpty(skuQuoteListVos)) {
            return null;
        }
        SkuQuoteListVO skuQuoteListVo = skuQuoteListVos.get(0);
        query.setItemId(skuQuoteListVo.getItemId());
        ItemDetailV2VO itemDetailV2Vo = itemAdaptor.queryItemDetailV2(query);
        setItemDesc(itemDetailV2Vo);
        // 查询活动
        setPromotionDetail(query, itemDetailV2Vo);
        return itemDetailV2Vo;
    }

    private void setPromotionDetail(ItemDetailV2RestQuery query, ItemDetailV2VO itemDetailV2Vo) {
        if (itemDetailV2Vo == null || itemDetailV2Vo.getItem() == null) {
            return;
        }
        CouponInfoReqQuery couponInfoQue = new CouponInfoReqQuery();
        ItemVO item = itemDetailV2Vo.getItem();
        log.info("[查询商品详情]item.id{}, skuId={}", item.getId());
        couponInfoQue.setSkuId(query.getSkuId());
        couponInfoQue.setItemId(item.getId());
        couponInfoQue.setCategoryId(item.getCategoryId());
        PromotionEnableSelectInfoVO promotionEnableSelectInfoVo = itemPromotionAdaptor.queryPromotionCouponInfo(couponInfoQue);
        log.info("[查询商品详情]查询优惠券信息入参={}, 出参={}", JSON.toJSONString(couponInfoQue), JSON.toJSONString(promotionEnableSelectInfoVo));
        if (Objects.isNull(promotionEnableSelectInfoVo) || CollectionUtils.isEmpty(promotionEnableSelectInfoVo.getPromotionDetailList())) {
            return;
        }

        PromotionDetailVO promotionDetailVo = promotionEnableSelectInfoVo.getPromotionDetailList().stream()
                .filter(x -> PromotionToolCodeEnum.YU_SHOU.getCode().equals(x.getPromotionToolCode())
                        || PromotionToolCodeEnum.MIAO_SHA.getCode().equals(x.getPromotionToolCode()))
                .findFirst().orElse(null);
        log.info("[查询商品详情]promotionDetailVo={}", JSON.toJSONString(promotionDetailVo));
        itemDetailV2Vo.setPromotionDetail(promotionDetailVo);
        if (promotionDetailVo != null && PromotionToolCodeEnum.MIAO_SHA.getCode().equals(promotionDetailVo.getPromotionToolCode())) {
            log.info("[查询商品详情]coming");
            // 秒杀商品查询商品可购买次数
            Long buyCnt = countBuyCnt2(itemDetailV2Vo);
            if (buyCnt != null && itemDetailV2Vo.getBuyLimitOrds() != null && buyCnt > itemDetailV2Vo.getBuyLimitOrds()) {
                itemDetailV2Vo.setDisableBuyCamp(true);
            }
            itemDetailV2Vo.setBuyLimitNum(itemDetailV2Vo.getBuyLimitNum());
            itemDetailV2Vo.setBuyLimitOrds(itemDetailV2Vo.getBuyLimitOrds());
        }

    }

    /**
     * 查询秒杀商品查询商品可购买次数
     * 
     * @param itemDetailV2Vo
     * @return
     */
    private Long countBuyCnt2(ItemDetailV2VO itemDetailV2Vo) {
        Long custId = Optional.ofNullable((CustDTO) UserHolder.getUser()).map(CustDTO::getCustId).orElse(null);
        if (custId == null) {
            return null;
        }
        PromotionDetailVO promotionDetail = itemDetailV2Vo.getPromotionDetail();
        Long campId = promotionDetail.getCampaignId();
        if (campId == null) {
            return null;
        }

        BuyOrderCntReq buyOrderCntReq = new BuyOrderCntReq();
        buyOrderCntReq.setCampId(campId);
        buyOrderCntReq.setCustId(custId);
        buyOrderCntReq.setItemId(itemDetailV2Vo.getId());
        RpcResponse<Long> rpcResp;
        try {
            rpcResp = promotionBuyOrderFacade.queryBuyOrdCnt(buyOrderCntReq);
            log.info("[查询商品详情]item.id={},可购买次数={}", itemDetailV2Vo.getItem().getId(), JSON.toJSONString(rpcResp));
        } catch (Exception e) {
            log.warn("[查询商品详情]queryBuyOrdCnt failed req: {} ", JSON.toJSONString(buyOrderCntReq), e);
            return null; // 降级不限, 下单时会限住
        }
        if (!rpcResp.isSuccess()) {
            log.warn("[查询商品详情]queryBuyOrdCnt failed req: {}, resp: {} ", JSON.toJSONString(buyOrderCntReq), JSON.toJSONString(rpcResp));
            return null; // 降级不限, 下单时会限住
        }
        return rpcResp.getData();
    }

    /**
     * 设置描述
     * 
     * @param itemDetailV2VO vo
     */
    private void setItemDesc(ItemDetailV2VO itemDetailV2VO) {
        if (itemDetailV2VO == null || itemDetailV2VO.getItem() == null) {
            return;
        }
        ItemVO item = itemDetailV2VO.getItem();
        String fileToText = fileAdaptor.getFileToText(item.getItemDesc());
        item.setItemDesc(fileToText);
    }

    @Override
    public ItemSkuPriceVO querySkuPrice(ItemSkuPriceRestQuery query) {
        SkuOfferSearchReq req = new SkuOfferSearchReq();
        req.setSkuId(query.getSkuId());
        req.setCityCode(query.getCityCode());
        List<SkuQuoteListVO> skuQuoteListVOS = getSearch(req, 50, 0);
        if (CollectionUtils.isEmpty(skuQuoteListVOS)) {
            return null;
        }
        SkuQuoteListVO skuQuoteListVO = skuQuoteListVOS.stream().min(Comparator.comparing(SkuQuoteListVO::getPrice)).get();
        ItemSkuPriceVO itemSkuPriceVO = new ItemSkuPriceVO();
        // itemSkuPriceVO.setPrice(skuQuoteListVO.getPrice());
        itemSkuPriceVO.setOriginPrice(skuQuoteListVO.getPrice());
        itemSkuPriceVO.setSellerSkuCode(skuQuoteListVO.getSellerSkuCode());
        itemSkuPriceVO.setSellerId(skuQuoteListVO.getSellerId());
        setPriceType(itemSkuPriceVO, skuQuoteListVO.getCityPriceInfo(), query.getCityCode());

        PromotionQueryReq promotionQueryReq = buildPromotionQueryReq(skuQuoteListVO);
        PromotionSummation promotionInfo = itemAdaptor.queryPromotionSummation(promotionQueryReq);
        setItemSkuPriceVOPrice(query.getSkuId(), itemSkuPriceVO, promotionInfo);
        return itemSkuPriceVO;
    }

    // 设置折扣价，折扣率
    private void setItemSkuPriceVOPrice(Long skuId, ItemSkuPriceVO itemSkuPriceVO, PromotionSummation promotionInfo) {
        if (promotionInfo == null) {
            return;
        }
        Map<Long, ItemDividePriceDTO> itemDivide = promotionInfo.getItemDivide();
        ItemDividePriceDTO itemDividePriceDTO = itemDivide.get(skuId);
        if (itemDividePriceDTO == null) {
            return;
        }
        itemDividePriceDTO.getSellerId();
        Long originPrice = itemSkuPriceVO.getOriginPrice();
        Long totalPromPrice = itemDividePriceDTO.getTotalPromPrice();
        if (totalPromPrice == null || totalPromPrice >= originPrice) {
            return;
        }
        BigDecimal divide = BigDecimal.valueOf(originPrice - totalPromPrice).divide(BigDecimal.valueOf(originPrice), 4, RoundingMode.DOWN);
        DecimalFormat decimalFormat = new DecimalFormat("0%");
        String format = decimalFormat.format(divide.doubleValue());
        // itemSkuPriceVO.setDiscountRate("-"+format);
        itemSkuPriceVO.setPromPrice(totalPromPrice);
    }

    private PromotionQueryReq buildPromotionQueryReq(SkuQuoteListVO skuQuoteVO) {
        PromotionQueryReq promotionQueryReq = new PromotionQueryReq();
        List<TargetItemCluster> itemClusters = new ArrayList<>();
        TargetItemCluster targetItemCluster = new TargetItemCluster();
        List<TargetItem> targetItems = new ArrayList<>();
        TargetItem targetItem = new TargetItem();
        targetItem.setItemId(skuQuoteVO.getItemId());
        targetItem.setSkuId(skuQuoteVO.getSkuId());
        targetItem.setItemQty(1);
        targetItem.setSellerId(skuQuoteVO.getSellerId());
        targetItem.setSkuOriginPrice(skuQuoteVO.getOriginPrice());
        targetItems.add(targetItem);
        targetItemCluster.setTargetItems(targetItems);
        itemClusters.add(targetItemCluster);
        promotionQueryReq.setItemClusters(itemClusters);
        return promotionQueryReq;
    }


    private void setPriceType(ItemSkuPriceVO itemSkuPriceVO, List<SkuQuoteCityPriceVO> cityPriceInfo, String cityCode) {
        if (CollectionUtils.isEmpty(cityPriceInfo)) {
            return;
        }
        List<SkuQuoteCityPriceVO> collect = cityPriceInfo.stream().filter(x -> x.getOnSale() == 1)
                .filter(x -> "all".equals(x.getCityCode()) || cityCode.equals(x.getCityCode())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(collect)) {
            return;
        }
        List<SkuQuoteCityPriceTemp> skuQuoteCityPriceTemps = new ArrayList<>();
        for (SkuQuoteCityPriceVO cityPriceVO : collect) {
            List<LoanPeriodDTO> priceList = cityPriceVO.getPriceList();
            LoanPeriodDTO minPrice = priceList.stream().min(Comparator.comparing(LoanPeriodDTO::getValue)).get();
            LoanPeriodDTO maxType = priceList.stream().max(Comparator.comparing(LoanPeriodDTO::getType)).get();
            SkuQuoteCityPriceTemp skuQuoteCityPriceTemp = new SkuQuoteCityPriceTemp();
            skuQuoteCityPriceTemp.setCityCode(cityPriceVO.getCityCode());
            skuQuoteCityPriceTemp.setMinPrice(minPrice.getValue());
            skuQuoteCityPriceTemp.setMinType(minPrice.getType());
            skuQuoteCityPriceTemp.setMaxType(maxType.getType());
            skuQuoteCityPriceTemp.setMaxPrice(maxType.getValue());
            skuQuoteCityPriceTemps.add(skuQuoteCityPriceTemp);
        }
        SkuQuoteCityPriceTemp skuQuoteCityPriceTemp = skuQuoteCityPriceTemps.stream().min(Comparator.comparing(SkuQuoteCityPriceTemp::getMinType)).get();
        itemSkuPriceVO.setMinType(skuQuoteCityPriceTemp.getMinType());
        itemSkuPriceVO.setMaxType(skuQuoteCityPriceTemp.getMaxType());
        Long maxPrice = skuQuoteCityPriceTemp.getMaxPrice();
        Integer maxType = skuQuoteCityPriceTemp.getMaxType();
        if (maxPrice != null && maxType != null) {
            long round = Math.round((double) maxPrice / maxType);
            itemSkuPriceVO.setMaxPrice(round);
        }
    }

    private List<SkuQuoteListVO> getSearch(SkuOfferSearchReq req, int size, int page) {
        SearchRequest searchRequest = new SearchRequest("sku_quote_" + env);
        SearchSourceBuilder ssBuilder = new SearchSourceBuilder();
        ssBuilder.size(size);
        ssBuilder.from(page);
        BoolQueryBuilder boolQuery = new BoolQueryBuilder();
        // skuId
        if (CollectionUtils.isNotEmpty(req.getSkuIdList())) {
            boolQuery.must(QueryBuilders.termQuery("sku_id", req.getSkuIdList()));
        }
        if (Objects.nonNull(req.getSkuId())) {
            boolQuery.must(QueryBuilders.termQuery("sku_id", req.getSkuId()));
        }
        ssBuilder.query(boolQuery);
        searchRequest.source(ssBuilder);
        SearchResponse result = searchClient.search(searchRequest);
        List<SkuQuoteListVO> list = Arrays.stream(result.getHits().getHits()).map(SearchHit::getSourceAsString).map(n -> itemComp.convert2SkuQuoteVO(n))
                .filter(vo -> Objects.nonNull(vo.getId())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list;
    }

    @Override
    public List<ItemDeadlineVO> queryItemDeadline(ItemDeadlineRestQuery query) {
        return itemAdaptor.queryItemDeadline(query);
    }

    @Override
    public List<ItemCharacteristicsVO> queryItemCharacteristics(ItemDetailV2RestQuery query) {
        return itemAdaptor.queryItemCharacteristics(query);
    }

    @Override
    public RestResponse<PageInfo<ItemMerchantInfoVO>> queryItemMerchantInfo(SkuOfferSearchReq req) {
        SearchRequest searchRequest = new SearchRequest("sku_quote_" + env);
        SearchSourceBuilder ssBuilder = new SearchSourceBuilder();
        PageParam pageParam = req.getPage();
        ssBuilder.size(pageParam.getPageSize());
        ssBuilder.from((pageParam.getPageNo() - 1) * pageParam.getPageSize());

        if (pageParam.getTotalNum() >= 10000) {
            return RestResponse.fail(ItemFrontResponseCode.PAGES_EXCEED);
        }

        BoolQueryBuilder boolQuery = new BoolQueryBuilder();
        // skuId
        if (Objects.nonNull(req.getSkuId())) {
            boolQuery.must(QueryBuilders.termQuery("sku_id", req.getSkuId()));
        }

        // 城市code
        // if(StringUtils.isNotBlank(req.getCityCode())){
        // NestedQueryBuilder nestedCityPriceQuery = QueryBuilders.nestedQuery(
        // "city_price_info",
        // QueryBuilders.boolQuery()
        // .should(QueryBuilders.boolQuery()
        // .must(QueryBuilders.termQuery("city_price_info.cityCode", req.getCityCode()))
        // .must(QueryBuilders.termQuery("city_price_info.onSale", 1))
        // )
        // .should(QueryBuilders.boolQuery()
        // .must(QueryBuilders.termQuery("city_price_info.cityCode", "all"))
        // .must(QueryBuilders.termQuery("city_price_info.onSale", 1))
        // .must(QueryBuilders.boolQuery()
        // .should(QueryBuilders.boolQuery()
        // .mustNot(QueryBuilders.termQuery("city_price_info.cityCode", req.getCityCode()))
        // )
        // .should(QueryBuilders.boolQuery()
        // .must(QueryBuilders.termQuery("city_price_info.cityCode", req.getCityCode()))
        // .must(QueryBuilders.termQuery("city_price_info.onSale", 1))
        // )
        // )
        // ),ScoreMode.None);
        // boolQuery.must(nestedCityPriceQuery);
        // }
        // 处理sku引用ID
        if (CollectionUtils.isNotEmpty(req.getSkuQuoteIdList())) {
            boolQuery.must(QueryBuilders.termsQuery("id", req.getSkuQuoteIdList()));
        }

        // 处理商品ID
        if (CollectionUtils.isNotEmpty(req.getItemIdList())) {
            boolQuery.must(QueryBuilders.termsQuery("item_id", req.getItemIdList()));
        }

        // 处理商品标题(模糊查询)
        if (StringUtils.isNotBlank(req.getTitle())) {
            // 构建嵌套查询
            NestedQueryBuilder titleNestedQuery = QueryBuilders.nestedQuery("sku_name", QueryBuilders.boolQuery() //
                    .must(QueryBuilders.termQuery("sku_name.langSet.lang", itemComp.getLang())) //
                    .must(QueryBuilders.matchPhraseQuery("sku_name.langSet.value", req.getTitle())), //
                    ScoreMode.Total);
            boolQuery.must(titleNestedQuery);
        }

        // 处理类目ID(可使用 filter)
        if (Objects.nonNull(req.getCatagoryId())) {
            boolQuery.filter(QueryBuilders.termQuery("category_id", req.getCatagoryId()));
        }

        // 处理审核状态(可使用 filter)
        if (Objects.nonNull(req.getMapStatus())) {
            boolQuery.filter(QueryBuilders.termQuery("sku_quote_map_status", req.getMapStatus()));
        }

        if (CollectionUtils.isNotEmpty(req.getOrders())) {
            req.getOrders().forEach(sorter -> {
                ssBuilder.sort(SortBuilders.fieldSort(sorter.getField()).order(ItemComponent.getSortOrder(sorter.getDirection())));
            });
        } else {
            ssBuilder.sort(SortBuilders.fieldSort("gmt_modified").order(SortOrder.DESC));
        }

        ssBuilder.query(boolQuery);
        searchRequest.source(ssBuilder);
        SearchResponse result = searchClient.search(searchRequest);
        List<SkuQuoteListVO> list = Arrays.stream(result.getHits().getHits()).map(SearchHit::getSourceAsString).map(n -> itemComp.convert2SkuQuoteVO(n))
                .filter(vo -> Objects.nonNull(vo.getId())).collect(Collectors.toList());
        PageInfo<ItemMerchantInfoVO> pageInfo = new PageInfo<>();
        if (CollectionUtils.isEmpty(list)) {
            return RestResponse.okWithoutMsg(pageInfo);
        }
        // 补充商品类目信息
        Long categoryId = list.get(0).getCategoryId();
        CategoryDTO categoryDTO = itemAdaptor.queryById(categoryId);

        for (SkuQuoteListVO skuQuote : list) {
            if (Objects.isNull(categoryDTO)) {
                continue;
            }
            CategoryDeliveryDTO categoryDelivery = categoryDTO.getCategoryDelivery();
            Integer deliveryType = categoryDelivery.getDeliveryType();

            // 首先判断分类是否配置了那种物流方式
            if (Objects.equals(1, deliveryType)) {

                // hm物流三种都支持
                skuQuote.setIsExtradite(Boolean.TRUE);
                skuQuote.setIsStockpile(Boolean.TRUE);
                skuQuote.setIsBill(Boolean.TRUE);
            } else {
                // 如果商家只支持自有物流的话就不展示三小时时效
                skuQuote.setIsExtradite(Boolean.TRUE);
                skuQuote.setIsStockpile(Boolean.FALSE);
                skuQuote.setIsBill(Boolean.FALSE);
            }
        }

        pageInfo.setTotal(Objects.requireNonNull(result.getHits().getTotalHits()).value);
        List<ItemMerchantInfoVO> infoVOS = itemConvertor.convertToItemMerchantInfoVOList(list);
        // 2. 获取优惠信息
        ItemDetailRestQuery query = new ItemDetailRestQuery();
        query.setChannel(query.getChannel());
        SkuQuoteListVO skuQuoteListVO = list.get(0);
        query.setItemId(skuQuoteListVO.getItemId());
        query.setCustId(req.getCustId());
        query.setPromotionSource(req.getPromotionSource());
        PromotionQueryReq detailPromotionQueryReq = buildPromotionQueryV2(query, skuQuoteListVO.getSkuId());
        PromotionInfo detailDisplay = itemAdaptor.queryPromotion(detailPromotionQueryReq);
        fillPromotionData(infoVOS, detailDisplay);
        pageInfo.setList(infoVOS);
        return RestResponse.okWithoutMsg(pageInfo);
    }

    private void fillPromotionData(List<ItemMerchantInfoVO> itemMerchantInfoVOList, PromotionInfo detailDisplay) {
        if (detailDisplay == null) {
            return;
        }
        itemMerchantInfoVOList.forEach(it -> {
            // 补充优惠券信息
            List<CouponInstanceDTO> coupons = PromotionFillUtils.parseAllCampaign(detailDisplay.getCoupons());
            if (CollectionUtils.isNotEmpty(coupons)) {
                List<ItemPromotionVO> couponList = itemConvertor.convertCoupon(coupons);
                couponList.stream().forEach(itemPromotionVO -> itemPromotionVO.setIsCoupon(true));
                it.setCoupons(couponList);
                buildItemPromotion(couponList);
            }
            // 补充优惠活动信息
            List<PromotionDetailDTO> campaigns = PromotionFillUtils.parseAllCampaign(detailDisplay.getCampaigns());
            if (CollectionUtils.isNotEmpty(campaigns)) {
                List<ItemPromotionVO> campList = itemConvertor.convertCampaigns(campaigns);
                it.setCampaigns(campList);
                buildItemPromotion(campList);
            }
            // 补充商品优惠价格信息
            if (CollectionUtils.isEmpty(detailDisplay.getItemPrices())) {
                return;
            }
            // 第一个商品的价格
            ItemPriceDTO itemPriceDTO = detailDisplay.getItemPrices().get(0);
            if (itemPriceDTO == null) {
                return;
            }
            // sku的价格展示
            fillSkuPromotionPrice(it, itemPriceDTO);
            // 设置秒杀信息
            fillMiaoshaData(it, itemPriceDTO);
            // 预售活动信息
            fillYushouData(it, itemPriceDTO);

        });

    }

    private void fillYushouData(ItemMerchantInfoVO itemMerchantInfoVO, ItemPriceDTO itemPriceDTO) {
        if (itemPriceDTO.getCamp() == null) {
            return;
        }
        // 预售活动信息
        if (!PromotionToolCodes.YUSHOU.equals(itemPriceDTO.getCamp().getPromotionToolCode())) {
            return;
        }
        itemMerchantInfoVO.setDepositType(2);

        // 设置sku定金价格
        LocalizedParams param = new LocalizedParams();
        param.setOrig(Long.parseLong(itemMerchantInfoVO.getPrice()));
        param.setSkuId(itemMerchantInfoVO.getSkuId());
        itemMerchantInfoVO.setDepositPrice(String.valueOf(itemPriceDTO.calculateVariablePrice(LocalizedFormula.DEPOSIT, param)));


        // 尾款时间
        ImmutablePair<Date, Date> pair = (ImmutablePair<Date, Date>) itemPriceDTO.getCamp().getExtras().get(PromotionExtrasKey.PRESALE_BALANCE_TIME);

        if (pair != null) {
            itemMerchantInfoVO.setDepositDayDisplay(
                    String.format(I18NMessageUtils.getMessage("balance.payment.time") + ":%s " + I18NMessageUtils.getMessage("to") + " %s",
                            DateTimeUtils.format(pair.getLeft()), // # "尾款支付时间:%s 至
                            DateTimeUtils.format(pair.getRight())));
        }
        // 是否可退
        itemMerchantInfoVO.setDepositCanRefused(false);
    }

    private void fillMiaoshaData(ItemMerchantInfoVO itemMerchantInfoVO, ItemPriceDTO itemPriceDTO) {
        if (itemPriceDTO.getCamp() == null) {
            return;
        }
        // 不是秒杀跳过
        if (!PromotionToolCodes.MIAOSHA.equals(itemPriceDTO.getCamp().getPromotionToolCode())) {
            return;
        }
        // 设置优惠活动信息
        ItemPromotionVO promotionVO = itemConvertor.convertCampaign(itemPriceDTO.getCamp());
        itemMerchantInfoVO.setPriceCampaign(promotionVO);
        itemMerchantInfoVO.setShowType(DetailShowType.MIAOSHA.getType());// I18NMessageUtils.getMessage("flash.sale"); //# 秒杀
        // 设置活动时间
        Date now = new Date();
        long left = promotionVO.getStartTime().getTime() - now.getTime();
        promotionVO.setLeftStartTime(left < 0 ? 0 : left);
    }

    private void fillSkuPromotionPrice(ItemMerchantInfoVO itemMerchantInfoVO, ItemPriceDTO itemPriceDTO) {
        // 目前一口价没有预热,不展示价格;秒杀有
        if (itemPriceDTO.isInPreheat() && !PromotionToolCodes.MIAOSHA.equals(itemPriceDTO.getCamp().getPromotionToolCode())) {
            return;
        }
        final Long[] maxPromPrice = {Long.MIN_VALUE};
        final Long[] minPromPrice = {Long.MAX_VALUE};
        // 补充SKU价格
        LocalizedParams param = new LocalizedParams();
        long price = Long.parseLong(itemMerchantInfoVO.getPrice());
        param.setOrig(price);
        param.setSkuId(itemMerchantInfoVO.getSkuId());
        Long promPrice = itemPriceDTO.calculateVariablePrice(param);

        // 优惠价保护，避免优惠价高于商品原价
        if (promPrice > price) {
            promPrice = price;
        }
        BigDecimal divide = BigDecimal.valueOf(promPrice).divide(BigDecimal.valueOf(price), 2, RoundingMode.DOWN);
        itemMerchantInfoVO.setDiscount(divide.doubleValue());
        if (maxPromPrice[0] < promPrice) {
            maxPromPrice[0] = promPrice;
        }
        if (minPromPrice[0] > promPrice) {
            minPromPrice[0] = promPrice;
        }
        // 商品区间价格优惠
        itemMerchantInfoVO.setPromPrice(ItemUtils.fen2YuanRange(minPromPrice[0], maxPromPrice[0]));
    }

    private PromotionQueryReq buildPromotionQueryV2(ItemDetailRestQuery itemDetailRestQuery, Long skuId) {
        PromotionQueryReq detailPromotionQueryReq = new PromotionQueryReq();
        // 设置用户
        TargetCust targetCust = new TargetCust();
        targetCust.setCustId(itemDetailRestQuery.getCustId());
        if (itemDetailRestQuery.getCustId() != null && isNewUser(itemDetailRestQuery.getCustId())) {
            targetCust.setIsNewUser(true);
            targetCust.setSpecificGroupId(SPECIFIC_GROUP_ID);
        }
        detailPromotionQueryReq.setEnableCache(true);
        detailPromotionQueryReq.setPromotionSource(itemDetailRestQuery.getPromotionSource());
        detailPromotionQueryReq.setCust(targetCust);
        detailPromotionQueryReq.setChannel(ChannelEnum.get(itemDetailRestQuery.getChannel()).getId());
        TargetItem targetItem = new TargetItem();
        targetItem.setItemId(itemDetailRestQuery.getItemId());
        targetItem.setItemQty(1);
        targetItem.setSkuId(skuId);
        detailPromotionQueryReq.setItem(targetItem);
        detailPromotionQueryReq.setWithAttainable(true);
        // 活动预热
        detailPromotionQueryReq.setWithPreheat(true);
        return detailPromotionQueryReq;
    }


    /**
     * 使用的优先级，从传递信息的详细到简化，有详细信息使用详细的
     *
     * @param itemFreightRestQuery 用户和地址信息
     * @return 收货地址
     */
    private AddressVO queryAddress(ItemFreightRestQuery itemFreightRestQuery) {
        // 1. 优先使用传递的地址,透传使用
        AddressVO addressVO = itemFreightRestQuery.getAddressVO();
        if (addressVO != null) {
            return addressVO;
        }
        Long custId = itemFreightRestQuery.getCustId();
        if (custId == null || custId <= 0L) {
            return this.getGmallDefault();
        }
        // 2. 有收货地址ID
        if (itemFreightRestQuery.getAddressId() != null) {
            addressVO = itemAdaptor.queryAddressById(custId, itemFreightRestQuery.getAddressId());
            if (addressVO != null) {
                return addressVO;
            }
        }
        // 3. 仅登录的情况，获取用户的默认收货地址
        addressVO = itemAdaptor.queryAddressDefault(custId);
        if (addressVO != null) {
            return addressVO;
        }
        // 4. 使用系统设置的默认地址
        return this.getGmallDefault();
    }

    /**
     * 系统默认收货地址
     *
     * @return
     */
    private AddressVO getGmallDefault() {
        AddressVO addressVO = new AddressVO();
        addressVO.setProvinceId(BizConst.ADDRESS_PROVINCE_ID);
        addressVO.setCityId(BizConst.ADDRESS_CITY_ID);
        addressVO.setAreaId(BizConst.ADDRESS_AREA_ID);
        addressVO.setProvince(BizConst.ADDRESS_PROVINCE);
        addressVO.setCity(BizConst.ADDRESS_CITY);
        addressVO.setArea(BizConst.ADDRESS_AREA);
        return addressVO;
    }

    private void fillAgreementData(Long itemId, Long custId, ItemDetailVO detail) {
        List<AgreementPriceDTO> agreements = itemAdaptor.queryItemAgreements(custId, itemId, detail.getSellerId());
        if (CollectionUtils.isEmpty(agreements)) {
            return;
        }

        Map<Long, AgreementPriceDTO> map = agreements.stream().collect(Collectors.toMap(AgreementPriceDTO::getItemId, Function.identity()));
        AgreementPriceDTO itemAgreement = map.get(itemId);
        AgreementPriceDTO userAgreement = map.get(0L);

        if (!AgreementUtils.isAvailable(new Date(), userAgreement, itemAgreement)) {
            return;
        }

        // 按数量
        if (AgreementPriceType.COUNT.getCode().equals(itemAgreement.getPriceType())) {
            B2bItemVO b2bItem = detail.getB2bItem();
            if (b2bItem != null) {
                b2bItem.setType(B2bItemDTO.TYPE_1);
                List<StepPrice> list = new ArrayList<>();
                Integer minCount = null;
                Long maxPrice = null;
                for (Entry<Integer, Long> en : itemAgreement.getStepPrice().entrySet()) {
                    if (en.getKey() == null || en.getValue() == null) {
                        continue;
                    }
                    StepPrice price = new StepPrice();
                    price.setNum(en.getKey());
                    price.setPrice(MoneyUtils.centToYuan(en.getValue()));
                    list.add(price);
                    if (minCount == null || minCount.intValue() > en.getKey().intValue()) {
                        minCount = en.getKey();
                    }
                    if (maxPrice == null || maxPrice.longValue() < en.getValue().longValue()) {
                        maxPrice = en.getValue();
                    }
                }
                list.sort(Comparator.comparing(StepPrice::getNum));
                b2bItem.setPriceList(list);
                b2bItem.setMinBuyNum(minCount);
                detail.setItemPrice(MoneyUtils.centToYuan(maxPrice));
                detail.setAgreementPrice(true);
            }
        }
        // 按规格
        else if (AgreementPriceType.SKU.getCode().equals(itemAgreement.getPriceType())) {
            B2bItemVO b2bItem = detail.getB2bItem();
            if (b2bItem != null) {
                b2bItem.setType(B2bItemDTO.TYPE_2);
                b2bItem.setPriceList(null);
            }
            Long minPrice = null;
            Long maxPrice = null;
            for (ItemSkuVO sku : detail.getItemSkuVOList()) {
                Long price = itemAgreement.getSkuPrice().get(sku.getId());
                if (price == null) {
                    continue;
                }
                sku.setPrice(price);
                if (minPrice == null || minPrice.longValue() > price.longValue()) {
                    minPrice = price;
                }
                if (maxPrice == null || maxPrice.longValue() < price.longValue()) {
                    maxPrice = price;
                }
            }
            if (minPrice.longValue() == maxPrice.longValue()) {
                detail.setItemPrice(MoneyUtils.centToYuan(maxPrice));
            } else {
                detail.setItemPrice(MoneyUtils.centToYuan(minPrice) + "-" + MoneyUtils.centToYuan(maxPrice));
            }
            detail.setAgreementPrice(true);
        }
    }

    /**
     * 秒杀信息
     * 
     * @param itemDetailVO
     * @param itemPriceDTO
     */
    public void fillMiaoshaData(ItemDetailVO itemDetailVO, ItemPriceDTO itemPriceDTO) {
        if (itemPriceDTO.getCamp() == null) {
            return;
        }
        // 不是秒杀跳过
        if (!PromotionToolCodes.MIAOSHA.equals(itemPriceDTO.getCamp().getPromotionToolCode())) {
            return;
        }
        // 设置优惠活动信息
        ItemPromotionVO promotionVO = itemConvertor.convertCampaign(itemPriceDTO.getCamp());
        itemDetailVO.setPriceCampaign(promotionVO);
        itemDetailVO.setShowType(DetailShowType.MIAOSHA.getType());// I18NMessageUtils.getMessage("flash.sale"); //# 秒杀
        // 设置活动时间
        Date now = new Date();
        long left = promotionVO.getStartTime().getTime() - now.getTime();
        promotionVO.setLeftStartTime(left < 0 ? 0 : left);
    }

    /**
     * 预售信息
     * 
     * @param itemDetailVO
     * @param itemPriceDTO
     */
    public void fillYushouData(ItemDetailVO itemDetailVO, ItemPriceDTO itemPriceDTO) {
        if (itemPriceDTO.getCamp() == null) {
            return;
        }
        // 预售活动信息
        if (!PromotionToolCodes.YUSHOU.equals(itemPriceDTO.getCamp().getPromotionToolCode())) {
            return;
        }
        itemDetailVO.setDepositType(2);

        // 设置sku定金价格
        itemDetailVO.getItemSkuVOList().forEach(x -> {
            LocalizedParams param = new LocalizedParams();
            param.setOrig(x.getPrice());
            param.setSkuId(x.getId());
            x.setDepositPrice(itemPriceDTO.calculateVariablePrice(LocalizedFormula.DEPOSIT, param));
        });

        // 设置商品定金价格
        LongSummaryStatistics minAndMax = itemDetailVO.getItemSkuVOList().stream().mapToLong(ItemSkuVO::getDepositPrice).summaryStatistics();
        itemDetailVO.setDepositPrice(ItemUtils.fen2YuanRange(minAndMax.getMin(), minAndMax.getMax()));

        // 尾款时间
        ImmutablePair<Date, Date> pair = (ImmutablePair<Date, Date>) itemPriceDTO.getCamp().getExtras().get(PromotionExtrasKey.PRESALE_BALANCE_TIME);

        if (pair != null) {
            itemDetailVO.setDepositDayDisplay(
                    String.format(I18NMessageUtils.getMessage("balance.payment.time") + ":%s " + I18NMessageUtils.getMessage("to") + " %s",
                            DateTimeUtils.format(pair.getLeft()), // # "尾款支付时间:%s 至
                            DateTimeUtils.format(pair.getRight())));
        }
        // 是否可退
        itemDetailVO.setDepositCanRefused(false);
    }

    @Override
    public List<CouponInstanceVO> queryCouponInfo(CouponInfoReqQuery query) {
        PromotionEnableSelectInfoVO promotionEnableSelectInfoVO = itemPromotionAdaptor.queryPromotionCouponInfo(query);
        if (null != promotionEnableSelectInfoVO) {
            return promotionEnableSelectInfoVO.getCouponInstanceList();
        }
        return Collections.emptyList();
    }

    @Override
    public List<SkuEvaluateInfoVO> querySkuEvaluateInfo(SkuEvaluateInfoQuery req) {
        EvaluationIdReq evaluationIdReq = new EvaluationIdReq();
        evaluationIdReq.setEvaluationId(req.getEvaluationId());
        RpcResponse<List<EvaluationDTO>> evaluationWithReplies = evaluationReadFacade.getEvaluationWithReplies(evaluationIdReq);
        List<EvaluationDTO> data = evaluationWithReplies.getData();
        if (CollectionUtils.isEmpty(data)) {
            return null;
        }
        List<SkuEvaluateInfoVO> skuEvaluateInfoVOS = BeanUtil.copyToList(data, SkuEvaluateInfoVO.class);
        return skuEvaluateInfoVOS;
    }

    @Override
    public PageInfo<SkuEvaluateInfoVO> querySkuEvaluateList(SkuEvaluateQuery req) {
        EvaluationQueryRpcReq evaluationQueryRpcReq = new EvaluationQueryRpcReq();
        evaluationQueryRpcReq.setItemId(req.getSkuId());
        evaluationQueryRpcReq.setPage(req.getPage());
        evaluationQueryRpcReq.setGradeClassify(req.getGradeClassify());
        RpcResponse<PageInfo<ItemEvaluationDTO>> pageInfoRpcResponse = evaluationReadFacade.queryEvaluation(evaluationQueryRpcReq);
        PageInfo<ItemEvaluationDTO> data = pageInfoRpcResponse.getData();
        PageInfo pageInfo = new PageInfo();
        if (data == null) {
            return pageInfo;
        }
        pageInfo.setTotal(data.getTotal());
        List<ItemEvaluationDTO> list = data.getList();
        if (CollectionUtils.isEmpty(list)) {
            return pageInfo;
        }
        List<EvaluationDTO> collect = list.stream().map(ItemEvaluationDTO::getItemEvaluation).collect(Collectors.toList());
        List<SkuEvaluateInfoVO> skuEvaluateInfoVOS = BeanUtil.copyToList(collect, SkuEvaluateInfoVO.class);
        pageInfo.setList(skuEvaluateInfoVOS);
        return pageInfo;
    }

    /**
     * 查询加车价格
     * 
     * @param itemSkuCartPriceRestQuery
     * @return
     */
    @Override
    public ItemSkuCartPriceVO calcItemAddCart(ItemSkuCartPriceRestQuery itemSkuCartPriceRestQuery) {
        ItemSkuCartPriceVO itemSkuCartPriceVO = new ItemSkuCartPriceVO();
        itemSkuCartPriceVO.setAddCart(Boolean.FALSE);
        // 原价
        itemSkuCartPriceVO.setItemPrice(itemSkuCartPriceRestQuery.getItemPrice() * itemSkuCartPriceRestQuery.getItemQty());
        // 营销价
        itemSkuCartPriceVO.setItemPromotionPrice(itemSkuCartPriceRestQuery.getItemPrice() * itemSkuCartPriceRestQuery.getItemQty());
        // step 0 查询商品信息 不能是虚拟商品 虚拟商品不能加车
        ItemDetailVO itemDetailVO = itemAdaptor.getItemDetail(itemSkuCartPriceRestQuery.getItemId());
        if (itemDetailVO == null || itemDetailVO.getItemBaseVO() == null || itemDetailVO.getItemBaseVO().getItemType() == null) {
            return itemSkuCartPriceVO;
        }
        if (PayModeEnum.LOAN.getScript().equals(itemSkuCartPriceRestQuery.getPayMode())) {
            // 输入loan，默认查询三期价格
            itemSkuCartPriceRestQuery.setPeriod(3);
        }
        if (PayModeEnum.EAPY.getScript().equals(itemSkuCartPriceRestQuery.getPayMode())) {
            // 输入epay，默认查询三期价格
            itemSkuCartPriceRestQuery.setPeriod(3);
        }
        // step1 查询索引 查询商品价格信息
        SearchRequest searchRequest = new SearchRequest("sku_quote_" + env);
        SearchSourceBuilder ssBuilder = new SearchSourceBuilder();
        ssBuilder.from(0);
        ssBuilder.size(10);
        BoolQueryBuilder boolQuery = new BoolQueryBuilder();
        // skuId
        boolQuery.must(QueryBuilders.termQuery("sku_id", itemSkuCartPriceRestQuery.getSkuId()));
        // 处理sku引用ID
        boolQuery.must(QueryBuilders.termQuery("id", itemSkuCartPriceRestQuery.getQuoteId()));
        ssBuilder.query(boolQuery);
        searchRequest.source(ssBuilder);
        SearchResponse result = searchClient.search(searchRequest);
        List<SkuQuoteListVO> list = Arrays.stream(result.getHits().getHits())
            .map(SearchHit::getSourceAsString)
            .map(n -> itemComp.convert2SkuQuoteVO(n))
            .filter(vo -> Objects.nonNull(vo.getId()))
            .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(list) || list.get(0) == null) {
            return itemSkuCartPriceVO;
        }
        // step2 获取城市+支付方式对应的价格
        SkuQuoteListVO skuQuoteListVO = list.get(0);
        if (CollectionUtils.isEmpty(skuQuoteListVO.getCityPriceInfo())) {
            return itemSkuCartPriceVO;
        }
        // 查找对应城市的价格
        // 城市价
        SkuQuoteCityPriceVO skuQuoteCityPriceVO = skuQuoteListVO
            .getCityPriceInfo()
            .stream()
            .filter(price -> price.getOnSale() == 1) // 过滤出 onSale为1的价格信息
            .filter(price -> itemSkuCartPriceRestQuery.getCityCode().equals(price.getCityCode()))
            .findFirst()
            .orElse(null);
        // 没有取全部
        if (skuQuoteCityPriceVO == null) {
            // 过滤出 onSale为1的价格信息
            skuQuoteCityPriceVO = skuQuoteListVO.getCityPriceInfo()
                .stream()
                .filter(price -> price.getOnSale() == 1)
                .filter(price -> "all".equals(price.getCityCode()))
                .findFirst()
                .orElse(null);
        }
        // 还是没有 结束 ---- 理论上不可能没有
        if (skuQuoteCityPriceVO == null || CollectionUtils.isEmpty(skuQuoteCityPriceVO.getPriceList())) {
            return itemSkuCartPriceVO;
        }
        // 根据支付方式取价格
        LoanPeriodDTO loanPeriodDTO = skuQuoteCityPriceVO.getPriceList().stream()
            .filter(loanPeriod -> itemSkuCartPriceRestQuery
            .getPeriod()
            .equals(loanPeriod.getType()))
            .findFirst()
            .orElse(null);
        // 没有价格 结束
        if (loanPeriodDTO == null || loanPeriodDTO.getValue() == null) {
            return itemSkuCartPriceVO;
        }
        // 查询营销价格
        PromotionQueryReq promotionQueryReq = new PromotionQueryReq();
        List<TargetItemCluster> itemClusters = new ArrayList<>();
        TargetItemCluster targetItemCluster = new TargetItemCluster();
        List<TargetItem> targetItems = new ArrayList<>();
        TargetItem targetItem = new TargetItem();
        targetItem.setItemId(itemSkuCartPriceRestQuery.getItemId());
        targetItem.setSkuId(itemSkuCartPriceRestQuery.getSkuId());
        targetItem.setItemQty(itemSkuCartPriceRestQuery.getItemQty());
        targetItem.setSellerId(itemSkuCartPriceRestQuery.getSellerId());
        targetItem.setSkuOriginPrice(loanPeriodDTO.getValue());
        targetItems.add(targetItem);
        targetItemCluster.setTargetItems(targetItems);
        itemClusters.add(targetItemCluster);
        promotionQueryReq.setItemClusters(itemClusters);
        if (itemSkuCartPriceRestQuery.getCustId() != null && itemSkuCartPriceRestQuery.getCustId() > 0L) {
            TargetCust targetCust = new TargetCust();
            targetCust.setCustId(itemSkuCartPriceRestQuery.getCustId());
            promotionQueryReq.setCust(targetCust);
        }
        log.info("calcItemAddCart queryPromotionSummation req : {} ", JSON.toJSONString(promotionQueryReq));
        PromotionSummation promotionInfo = itemAdaptor.queryPromotionSummation(promotionQueryReq);
        log.info("calcItemAddCart queryPromotionSummation result : {} ", JSON.toJSONString(promotionInfo));
        if (promotionInfo == null || promotionInfo.getItemDivide() == null) {
            return itemSkuCartPriceVO;
        }
        Map<Long, ItemDividePriceDTO> itemDivide = promotionInfo.getItemDivide();
        // 如果是秒杀， 预售 ， 虚拟产品等 不能加车
        ItemDividePriceDTO itemDividePriceDTO = itemDivide.get(itemSkuCartPriceRestQuery.getSkuId());
        if (itemDividePriceDTO == null) {
            return itemSkuCartPriceVO;
        }
        itemSkuCartPriceVO.setAddCart(Boolean.TRUE);
        itemSkuCartPriceVO.setItemPromotionPrice(itemDividePriceDTO.getTotalPromPrice());
        itemSkuCartPriceVO.setItemUnitPromotionPrice(itemDividePriceDTO.getUnitPromPrice());
        itemSkuCartPriceVO.setItemPrice(itemSkuCartPriceRestQuery.getItemQty() * loanPeriodDTO.getValue());
        if (CollectionUtils.isNotEmpty(itemDividePriceDTO.getDivides())) {
            for (PromDivideDTO promDivideDTO : itemDividePriceDTO.getDivides()) {
                // 预售 计算无意义 赠品不能加车
                if (PromotionToolCodeEnum.YU_SHOU.getCode().equals(promDivideDTO.getToolCode())) {
                    itemSkuCartPriceVO.setAddCart(Boolean.FALSE);
                    itemSkuCartPriceVO.setPromotionTool(PromotionToolCodeEnum.YU_SHOU.getCode());
                    //如果是预售，只能购买一件(校验)
                    if(itemSkuCartPriceRestQuery.getItemQty() > 1){
                        throw new GmallException(ItemFrontResponseCode.YU_SHOU_CAN_ONLY_BUY_ONE);
                    }
                    if (Objects.nonNull(promDivideDTO.getExtras()) && Objects.nonNull(promDivideDTO.getExtras().get("pred"))) {
                        String pred = promDivideDTO.getExtras().get("pred").toString();
                        long promPrice = Long.parseLong(pred);
                        itemSkuCartPriceVO.setFirstPromotionPrice(promPrice);
                        itemSkuCartPriceVO.setBalancePromotionPrice(itemDividePriceDTO.getTotalPromPrice() - promPrice);
                    }
                }
                // 秒杀 计算无意义 赠品不能加车
                if (PromotionToolCodeEnum.MIAO_SHA.getCode().equals(promDivideDTO.getToolCode())) {
                    itemSkuCartPriceVO.setPromotionTool(PromotionToolCodeEnum.MIAO_SHA.getCode());
                    itemSkuCartPriceVO.setAddCart(Boolean.FALSE);
                    // 超过可以购买的上限制
                    if (Objects.nonNull(itemDividePriceDTO.getBuyLimitNum()) && itemSkuCartPriceRestQuery.getItemQty() > itemDividePriceDTO.getBuyLimitNum()) {
                        throw new GmallException(ItemFrontResponseCode.FLASH_ITEM_LIMIT);
                    }
                }
                // # "积分发放判断表信息表查询失败"
                if (AssetsType.AWARD.equals(promDivideDTO.getAssetType())) {
                    itemSkuCartPriceVO.setAddCart(Boolean.FALSE);
                }
            }
        }
        return itemSkuCartPriceVO;
    }

    /**
     * 压缩短连接
     * @param itemDetailLinkRestQuery
     * @return
     */
    @Override
    public ItemDetailLinkVO itemLinkShort(ItemDetailLinkRestQuery itemDetailLinkRestQuery) {
        ItemDetailLinkVO itemDetailLinkVO = new ItemDetailLinkVO();
        itemDetailLinkVO.setShortLink(itemDetailLinkRestQuery.getLinkUrl());
        itemDetailLinkVO.setPreviewLink(itemDetailLinkRestQuery.getLinkUrl());
        GoogleShortLinkRpcReq googleShortLinkRpcReq = new GoogleShortLinkRpcReq();
        googleShortLinkRpcReq.setSkuId(itemDetailLinkRestQuery.getSkuId());
        RpcResponse<ShortLinkDto> shortLinkDtoRpcResponse = googleFacade.itemShortLink(googleShortLinkRpcReq);
        if(Objects.nonNull(shortLinkDtoRpcResponse) &&
            Boolean.TRUE.equals(shortLinkDtoRpcResponse.isSuccess()) &&
            Objects.nonNull(shortLinkDtoRpcResponse.getData())) {
            itemDetailLinkVO.setShortLink(shortLinkDtoRpcResponse.getData().getShortLink());
            itemDetailLinkVO.setPreviewLink(shortLinkDtoRpcResponse.getData().getPreviewLink());
        }
        return itemDetailLinkVO;
    }

    @Override
    public RestResponse<Integer> queryCategoryLevel(Long categoryId) {
        return ResponseUtils.copyToRest(categoryReadFacade.queryCategoryLevel(categoryId));
    }
}
