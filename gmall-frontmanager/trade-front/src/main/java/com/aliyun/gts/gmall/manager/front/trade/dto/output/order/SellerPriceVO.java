package com.aliyun.gts.gmall.manager.front.trade.dto.output.order;

import java.util.List;

import com.aliyun.gts.gmall.manager.front.common.util.ItemUtils;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.item.ItemPriceVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 俊贤
 * @date 2021/03/05
 */
@Data
public class SellerPriceVO {
    @ApiModelProperty("卖家ID")
    private Long sellerId;
    @ApiModelProperty("优惠选项")
    private List<PromotionOptionVO> options;
    @ApiModelProperty("营销返回的最终价格")
    private Long promotionPrice;
    @ApiModelProperty("商品明细")
    private List<ItemPriceVO> items;

    public String getPromotionPriceYuan() {
        return String.valueOf(this.getPromotionPrice());
    }
    
}