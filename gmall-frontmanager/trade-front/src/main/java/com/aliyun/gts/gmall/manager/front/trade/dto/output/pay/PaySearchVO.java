package com.aliyun.gts.gmall.manager.front.trade.dto.output.pay;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *
 */
@ApiModel("支付单搜索结果信息")
@Data
public class PaySearchVO {
    @ApiModelProperty("支付单id")
    String paymentId;
    @ApiModelProperty("主订单id")
    Long primaryOrderId;
    @ApiModelProperty("买家名称")
    String custName;

    Long custId;
    @ApiModelProperty("金额")
    Double payAmt;
    @ApiModelProperty("支付方式")
    String payChannel;


    Map<String,String> featureMap;

    String paymentStatusDisplay;
    String payChannelDisplay;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date gmtCreate;

    String sellerName;

    Long sellerId;
    @ApiModelProperty("是否逾期")
    Boolean timeout;

    Map<String , Boolean> buttons = new HashMap<>();

}
