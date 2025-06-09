package com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result;

import com.aliyun.gts.gmall.platform.trade.common.constants.OrderTypeEnum;
import com.aliyun.gts.gmall.platform.trade.common.domain.step.StepTemplate;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.*;

@Data
public class MainOrderDTO extends BaseOrderDTO{

    @ApiModelProperty("收货人信息")
    String receiverInfo;

    @ApiModelProperty("收货人姓名")
    String receiverName;

    @ApiModelProperty("收货人电话")
    String receiverPhone;

    @ApiModelProperty("收货人地址")
    String receiverAddr;

    @ApiModelProperty("city")
    String city;

    @ApiModelProperty("cityCode")
    String cityCode;

    @ApiModelProperty("买家名称")
    String custName;

    @ApiModelProperty("买家id")
    Long custId;

    @ApiModelProperty("买家电话")
    String phone;

    @ApiModelProperty(value = "买家姓氏")
    private String lastName;

    @ApiModelProperty(value = "买家名字")
    private String firstName;

    @ApiModelProperty("卖家名称")
    String sellerName;

    @ApiModelProperty("卖家电话")
    String sellerPhone;

    @ApiModelProperty("卖家logo")
    String sellerLogo;

    @ApiModelProperty("卖家id")
    Long sellerId;

    @ApiModelProperty("卖家BIN/IIN")
    String sellerBin;

    @ApiModelProperty(value = "数量")
    private int seatNum;

    @ApiModelProperty(value = "物流公司名称")
    private String deliveryCompanyName;

    @ApiModelProperty(value = "物流公司编号")
    private String logisticsId;

    @ApiModelProperty("配送方式")
    Integer logisticsType;

    @ApiModelProperty("配送方式")
    String logisticsName;

    @ApiModelProperty("下单渠道")
    String orderChannel;

    @ApiModelProperty("卖家备注")
    String sellerMemo;

    @ApiModelProperty("买家留言")
    String customerMemo;

    @ApiModelProperty("付款方式")
    Integer payType;

    @ApiModelProperty("付款渠道")
    String payChannel;

    @ApiModelProperty("付款渠道")
    String payTypeStr;

    @ApiModelProperty("付款卡号")
    String bankCardNbr;

    @ApiModelProperty("订单类型")
    Integer orderType;

    @ApiModelProperty("子订单列表")
    List<SubOrderDTO> subOrderList = new ArrayList<>();

    @ApiModelProperty("确认收货时间 (废弃, 用 MainOrderDetailDTO.receivedTime)")
    @Deprecated
    Date confirmReceiveTime;

    @ApiModelProperty("发货时间")
    Date sendTime;

    @ApiModelProperty("合并下单的主订单ID列表, 非合并下单该字段为null")
    List<Long> mergeOrderIds;

    @ApiModelProperty("阶段订单")
    List<StepOrderDTO> stepOrders;

    @ApiModelProperty("多阶段模版")
    StepTemplate stepTemplate;

    @ApiModelProperty("当前阶段号")
    Integer currentStepNo;

    @ApiModelProperty("阶段上下文")
    Map<String, String> stepContextProps;

    @ApiModelProperty("业务身份")
    List<String> bizCodes;

    @ApiModelProperty("账期支付备注")
    String accountPeriodMemo;

    @ApiModelProperty("是否超卖 orderAttr.overSale")
    Boolean overSale;

    @ApiModelProperty("未分摊到子订单的运费, orderAttr.freightPrice")
    OrderDisplayPriceDTO additFreightPrice;

    @ApiModelProperty("贷款周期")
    Integer loanCycle;

    @ApiModelProperty("贷款状态")
    Integer loanStatus;

    @ApiModelProperty("发货地址")
    SalesAddrDTO sales;

    @ApiModelProperty("收货地址")
    ReceiveAddrDTO receiver;

    public StepOrderDTO getCurrentStepOrder() {
        if (OrderTypeEnum.codeOf(orderType) != OrderTypeEnum.MULTI_STEP_ORDER
                || stepOrders == null || currentStepNo == null) {
            return null;
        }
        return stepOrders.stream()
                .filter(step -> Objects.equals(step.getStepNo(), currentStepNo))
                .findFirst().orElse(null);
    }
}
