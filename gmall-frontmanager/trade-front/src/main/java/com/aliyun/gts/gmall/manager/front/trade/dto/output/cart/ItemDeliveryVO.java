package com.aliyun.gts.gmall.manager.front.trade.dto.output.cart;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 商品物流对象信息
 */
@Data
public class ItemDeliveryVO extends AbstractOutputInfo {

    // 物流方式
    @ApiModelProperty("物流方式")
    private Integer deliveryType;

    // 物流方式名称
    @ApiModelProperty("物流方式名称")
    private String deliveryTypeName;

    // 配送时间
    @ApiModelProperty("配送时间，单位小时")
    private Integer deliverHours;

    // 配送时间
    @ApiModelProperty("配送时间，单位日期")
    private Date deliverTime;

    // 运费
    @ApiModelProperty("运费")
    private Long freightAmt;

    // 发货城市code
    @ApiModelProperty("发货城市code")
    private String fromCityCode;

    // 收货城市
    @ApiModelProperty("收货城市")
    private String receiverCityCode;


    @ApiModelProperty("是否三小时内到达")
    public boolean isHoursLimit3() {
        return 0 < this.deliverHours && this.deliverHours <= 3;
    }

    @ApiModelProperty("是否一天内到达")
    public boolean isDayLimit1() {
        return 0 < this.deliverHours && this.deliverHours <= 24;
    }

    @ApiModelProperty("是否两天内到达")
    public boolean isDayLimit2() {
        return 24 < this.deliverHours && this.deliverHours <= 48;
    }
    @ApiModelProperty("是否三天内到达")
    public boolean isDayLimit3() {
        return 48 < this.deliverHours && this.deliverHours <= 64;
    }

}