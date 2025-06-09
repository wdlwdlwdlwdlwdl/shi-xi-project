package com.aliyun.gts.gmall.manager.front.item.dto.output;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 商品基础信息
 *
 * @author tiansong
 */

@Data
@ApiModel("商品基础信息")
public class ItemBaseVO extends AbstractOutputInfo {
    @NotNull
    @ApiModelProperty("商品id")
    private Long id;
    @NotNull
    @ApiModelProperty("卖家id")
    private Long sellerId;
    @ApiModelProperty("商品类型")
    private Integer itemType;
    @NotBlank
    @ApiModelProperty("商品标题")
    private String title;
    @NotBlank
    @ApiModelProperty("商品描述,oss地址")
    private String itemDesc;
    @ApiModelProperty("商品图片,已按order排好序")
    private List<String> pictureList;
    @NotNull
    @ApiModelProperty("商品分类id")
    private Long categoryId;
    @ApiModelProperty("品牌id")
    private Long brandId;
    @ApiModelProperty("品牌名")
    private String brandName;
    @ApiModelProperty("品牌logo")
    private String brandLogo;
    @NotNull
    @ApiModelProperty("商品状态")
    private Integer status;

    @ApiModelProperty(value = "限购数量")
    private Long buyLimit;

    @ApiModelProperty(value = "营销活动的限购数量")
    private Long buyLimitCamp;

    @ApiModelProperty(value = "定时上架时间")
    private Date onlineTime;

    @ApiModelProperty(value = "是否组合商品")
    private boolean combine = false;

    @ApiModelProperty(value = "禁止购买")
    private boolean disableBuy = false;

    @ApiModelProperty(value = "禁止购买")
    private boolean disableBuyCamp = false;

    @ApiModelProperty(value = "距离上架毫秒数,0代表已上架,普通上架商品一直是0")
    public long getRemainTime() {

        if (onlineTime == null) {
            return 0;
        }
        long time = onlineTime.getTime() - System.currentTimeMillis();

        return time > 0 ? time : 0;
    }

    // 取商品和营销限购中, 小的一个, 前端只认这一个值
    public Long getBuyLimit() {
        if (buyLimitCamp != null && buyLimit != null) {
            return Math.min(buyLimitCamp, buyLimit);
        }
        if (buyLimit != null) {
            return buyLimit;
        }
        if (buyLimitCamp != null) {
            return buyLimitCamp;
        }
        return null;
    }

    public boolean isDisableBuy() {
        return disableBuy || disableBuyCamp;
    }
}
