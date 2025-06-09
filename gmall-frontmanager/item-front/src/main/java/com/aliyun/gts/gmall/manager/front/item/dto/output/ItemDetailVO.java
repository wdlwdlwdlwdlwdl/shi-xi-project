package com.aliyun.gts.gmall.manager.front.item.dto.output;

import java.util.List;

import javax.validation.constraints.NotNull;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import com.aliyun.gts.gmall.manager.front.item.dto.output.sku.ItemSkuPropVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.sku.ItemSkuVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 商品详情页面信息
 *
 * @author tiansong
 */
@Data
@ApiModel("商品详情页面信息")
public class ItemDetailVO extends AbstractOutputInfo {

    @ApiModelProperty("选中的SKU ID")
    private Long selectedSkuId;
    @ApiModelProperty("选中的SKU名称")
    private String selectedSkuName;
    @ApiModelProperty("商品原价-区间价格")
    private String itemPrice;

    @ApiModelProperty("商品优惠价-区间价格")
    private String promPrice;

    @ApiModelProperty("定金尾款:1，预售活动:2")
    private Integer depositType;

    @ApiModelProperty("商品定金-定金配置")
    private DepositConfigVO depositConfigVO;

    @ApiModelProperty("商品定金-区间价格,根据定金配置+优惠价格算出")
    private String depositPrice;

    @ApiModelProperty("商品定金-尾款天数文案")
    private String depositDayDisplay;

    @ApiModelProperty("商品定金-定金是否可退")
    private boolean depositCanRefused;

    @ApiModelProperty("商品库存")
    private Long itemQuantity;
    @ApiModelProperty("近30天销量")
    private Long sellCount = 0L;
    @NotNull(message="seller.id.not.null")
    @ApiModelProperty("卖家id")
    private Long sellerId;
    @ApiModelProperty("购物车数量")
    private Integer cartQuantity;
    @ApiModelProperty("商品基础信息")
    private ItemBaseVO itemBaseVO;
    @ApiModelProperty("商品SKU属性信息")
    private List<ItemSkuPropVO> itemSkuPropVOList;
    @ApiModelProperty("商品SKU信息")
    private List<ItemSkuVO> itemSkuVOList;
    @ApiModelProperty("商品类目信息")
    private List<ItemCategoryVO> itemCategoryVOList;
    @ApiModelProperty("商品类目属性信息")
    private List<ItemCatPropVO> catPropInfo;
    @ApiModelProperty("券列表")
    private List<ItemPromotionVO> coupons;
    @ApiModelProperty("活动列表")
    private List<ItemPromotionVO> campaigns;
    @ApiModelProperty("一口价营销优惠活动,秒杀")
    private ItemPromotionVO priceCampaign;

    @ApiModelProperty("商品评价总量")
    private Long totalEvaluation;
    @ApiModelProperty("商品热门评价")
    private List<ItemEvaluationVO> hotEvaluationList;
    @ApiModelProperty("商品业务标签")
    private List<String> tagList;
    @ApiModelProperty("商详按钮展示控制")
    private ItemDetailButtonVO showButtons;
    @ApiModelProperty("评价分")
    private String evaluationScore;

    @ApiModelProperty("电子凭证展示信息")
    private String eVoucherDisplay;

    @ApiModelProperty("商贸商品信息")
    private B2bItemVO b2bItem;

    @ApiModelProperty("是否为协议价")
    private boolean agreementPrice = false;

    @ApiModelProperty("商品展示类型: 1表示秒杀")
    private Integer showType;

    @ApiModelProperty("组合商品信息")
    private CombineItemVO combineItem;

    @ApiModelProperty("是否奖品")
    private Boolean isAward;

    @ApiModelProperty("积分商品名称")
    private String title;



}
