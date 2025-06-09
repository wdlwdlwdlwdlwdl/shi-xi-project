package com.aliyun.gts.gmall.manager.front.item.dto.output;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@ApiModel(value = "商品商家信息返回对象")
public class ItemMerchantInfoVO extends AbstractOutputInfo {

    @ApiModelProperty(value = "quoteId")
    private Long quoteId;
    @ApiModelProperty(value = "商户id")
    private Long sellerId;
    @ApiModelProperty(value = "支付方式")
    private String payMode;
    @ApiModelProperty(value = "分期")
    private String period;
    @ApiModelProperty(value = "是否官方分销商")
    private Boolean isOfficialDistributor;
    @ApiModelProperty(value = "是否展示点赞")
    private Boolean showPraise;

    @ApiModelProperty(value = "分期标题")
    private String title;

    @ApiModelProperty(value = "分期颜色")
    private String titleColor;
    @ApiModelProperty(value = "商户名称")
    private String sellerName;

    @ApiModelProperty(value = "评分")
    private Double star;


    @ApiModelProperty(value = "是否展示3小时免费送")
    private Boolean isThreeFee;
    @ApiModelProperty(value = "是否在引渡点")
    private Boolean isExtradite;
    @ApiModelProperty(value = "是否展示提货")
    private Boolean isBill;
    @ApiModelProperty(value = "是否展示储存柜")
    private Boolean isStockpile;
    @ApiModelProperty(value = "原价")
    private String originPrice;
    @ApiModelProperty(value = "现价")
    private String price;
    @ApiModelProperty(value = "促销价格")
    private String PromPrice;
    @ApiModelProperty(value = "折扣")
    private Double discount;
    @ApiModelProperty(value = "分期价格")
    private String amortizePrice;
    @ApiModelProperty(value = "skuId")
    private Long skuId;
    @ApiModelProperty(value = "商品主键")
    private Long itemId;
    @ApiModelProperty(value = "库存")
    private Boolean stock;
    @ApiModelProperty(value = "城市")
    private CityVO city;

    @ApiModelProperty("券列表")
    private List<ItemPromotionVO> coupons;
    @ApiModelProperty("活动列表")
    private List<ItemPromotionVO> campaigns;
    @ApiModelProperty("一口价营销优惠活动,秒杀")
    private ItemPromotionVO priceCampaign;
    @ApiModelProperty("商品展示类型: 1表示秒杀")
    private Integer showType;
    @ApiModelProperty("定金尾款:1，预售活动:2")
    private Integer depositType;
    @ApiModelProperty("商品定金-区间价格,根据定金配置+优惠价格算出")
    private String depositPrice;
    @ApiModelProperty("商品定金-尾款天数文案")
    private String depositDayDisplay;
    @ApiModelProperty("商品定金-定金是否可退")
    private boolean depositCanRefused;
    @ApiModelProperty("商品活动类型")
    private Integer activityType;
    @ApiModelProperty("商家是否可靠的")
    private Boolean reliable;

}
