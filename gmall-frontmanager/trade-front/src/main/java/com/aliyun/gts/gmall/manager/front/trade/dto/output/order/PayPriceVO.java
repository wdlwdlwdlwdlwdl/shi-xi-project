package com.aliyun.gts.gmall.manager.front.trade.dto.output.order;

import com.aliyun.gts.gmall.manager.front.common.util.ItemUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import static com.aliyun.gts.gmall.manager.front.common.util.ItemUtils.nullZero;


@Data
public class PayPriceVO {

    @ApiModelProperty("实付现金")
    private Long realAmt;
    @ApiModelProperty("积分金额")
    private Long pointAmt;
    @ApiModelProperty("积分个数")
    private Long pointCount;
    @ApiModelProperty("最大可用积分个数")
    private Long maxAvailablePoint;

    @ApiModelProperty("运费金额")
    private Long freightAmt;

    public Long getTotalAmt() {
        return nullZero(realAmt) + nullZero(pointAmt);
    }

    public String getRealAmtYuan() {
        return String.valueOf(this.getRealAmt());
    }

    public String getPointAmtYuan() {
        return String.valueOf(this.getPointAmt());
    }

    public String getPointCountUnit() {
        return ItemUtils.pointDisplay(pointCount);
    }

    public String getMaxAvailablePointUnit() {
        return ItemUtils.pointDisplay(maxAvailablePoint);
    }


    public String getTotalAmtYuan() {
        return String.valueOf(this.getTotalAmt());
    }

    public String getFreightAmtYuan() {
        return String.valueOf(this.getFreightAmt());
    }

    public String getPromotionAmtYuan() {
        return String.valueOf(this.getTotalAmt() - nullZero(this.getFreightAmt()));
    }
}
