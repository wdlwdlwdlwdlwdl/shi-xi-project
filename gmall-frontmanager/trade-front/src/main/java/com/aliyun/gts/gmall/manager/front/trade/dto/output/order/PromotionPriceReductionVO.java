package com.aliyun.gts.gmall.manager.front.trade.dto.output.order;

import com.aliyun.gts.gmall.manager.front.common.util.ItemUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * 活动优惠
 *
 * @author GTS
 * @date 2021/03/08
 */
@ToString
@Data
public class PromotionPriceReductionVO {
    @ApiModelProperty("类型")
    private String type;
    @ApiModelProperty("优惠扣减金额")
    private Long   reduceFee;

    public static PromotionPriceReductionVO of(String type) {
        PromotionPriceReductionVO vo = new PromotionPriceReductionVO();
        vo.setType(type);
        vo.setReduceFee(0L);
        return vo;
    }

    public void incr(Long incrReduceFee) {
        if (reduceFee == null) {
            reduceFee = 0L;
        }
        reduceFee += incrReduceFee;
    }

    public String getReduceFeeYuan() {
        return String.valueOf(this.getReduceFee());
    }
}