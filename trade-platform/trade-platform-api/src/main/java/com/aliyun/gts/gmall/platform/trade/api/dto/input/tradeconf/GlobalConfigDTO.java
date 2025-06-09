package com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf;

import com.aliyun.gts.gmall.framework.api.rpc.dto.AbstractCommandRpcRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

@Data
public class GlobalConfigDTO extends AbstractCommandRpcRequest {

    @ApiModelProperty("减库存方式, 枚举:InventoryReduceType")
    private Integer inventoryReduceType;

    @ApiModelProperty("超卖处理方式 ,枚举:OversellProcessType")
    private Integer oversellProcessType;

    // === 正向交易时间 ===

    @ApiModelProperty("拍下未付款自动关闭订单时间（秒）")
    private Integer autoCloseOrderTimeInSec;

    @ApiModelProperty("拍下未付款自动释放库存时间（秒）, 仅对付款减库存有效")
    private Integer autoUnlockInventoryTimeInSec;

    @ApiModelProperty("自动确认收货时间（秒）")
    private Integer autoConfirmReceiveTimeInSec;

    @ApiModelProperty("自动评价的时间 (秒)")
    private Integer autoEvaluateTimeInSec;

    // === 逆向交易时间 ===

    @ApiModelProperty("可申请售后服务的最长天数（从确认收货后计算）")
    private Integer createReversalMaxDays;

    @ApiModelProperty("自动同意逆向请求时间（秒）")
    private Integer autoAgreeReversalTimeInSec;

    @ApiModelProperty("逆向退货自动确认收货时间（秒）")
    private Integer autoReceiveReversalTimeInSec;

    @ApiModelProperty("其他扩展配置项")
    private Map<String, String> extendConfigs;
}
