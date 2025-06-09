package com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.TcOrderExtendDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@ApiModel("订单基础信息")
public class BaseOrderDTO extends AbstractOutputInfo {

    @ApiModelProperty("订单状态")
    Integer orderStatus;

    @ApiModelProperty("售后状态的订单, 记录发起售后前的订单状态")
    Integer reversalOrderStatus;

    @ApiModelProperty("商品数量")
    Integer itemQuantity;

    @ApiModelProperty("订单id")
    Long orderId;

    @ApiModelProperty("对外展示display_order_id")
    String displayOrderId;

    @ApiModelProperty("订单创建时间")
    Date createTime;

    @ApiModelProperty("订单上的金额相关信息")
    OrderDisplayPriceDTO price;

    @ApiModelProperty("OrderEvaluateEnum")
    Integer evaluate;

    @ApiModelProperty("自定义扩展内容 (orderAttr.extra)")
    Map<String, String> extras;

    @ApiModelProperty("订单扩展信息列表")
    List<TcOrderExtendDTO> orderExtends;

    @ApiModelProperty("订单自定义标, 进搜索 (orderAttr.tags)")
    List<String> tags;

    @ApiModelProperty("订单的版本号")
    Long version;

    @ApiModelProperty("订单阶段(售前/售中/售后) OrderStageENum")
    Integer orderStage;

}
