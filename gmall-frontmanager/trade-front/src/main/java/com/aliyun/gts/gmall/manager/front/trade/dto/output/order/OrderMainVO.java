package com.aliyun.gts.gmall.manager.front.trade.dto.output.order;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.extend.InvoiceDetailVO;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 主订单信息
 *
 * @author tiansong
 */
@Data
@ApiModel("主订单信息")
public class OrderMainVO extends OrderBaseVO {
    @ApiModelProperty("收货人信息")
    String  receiverInfo;

    @ApiModelProperty("收货人姓名")
    String  receiverName;

    @ApiModelProperty("收货人电话")
    String  receiverPhone;

    @ApiModelProperty("收货人地址")
    String  receiverAddr;

    //增加 receiveId 字段
    @ApiModelProperty(value = "收件地址ID")
    private Long receiverId;

    @ApiModelProperty("买家名称")

    String custName;

    @ApiModelProperty("买家id")
    Long  custId;

    @ApiModelProperty("卖家名称")
    String sellerName;

    @ApiModelProperty("卖家id")
    Long sellerId;

    @ApiModelProperty("卖家电话")
    String  sellerPhone;

    @ApiModelProperty("卖家logo")
    String sellerLogo;

    @ApiModelProperty("配送方式")
    Integer logisticsType;

    @ApiModelProperty("下单渠道")
    String orderChannel;

    @ApiModelProperty("付款渠道")
    String payChannel;

    @ApiModelProperty("付款渠道")
    String payTypeStr;

    @ApiModelProperty("付款卡号")
    String bankCardNbr;

    @ApiModelProperty("买家留言")
    String customerMemo;

    @ApiModelProperty("付款时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date payTime;

    @ApiModelProperty("发货时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date sendTime;

    @ApiModelProperty("确认收货时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date receivedTime;

    @ApiModelProperty("是否已评价, OrderEvaluateEnum")
    Integer evaluate;

    @ApiModelProperty("订单类型 1实物  10电子凭证")
    Integer orderType;

    @ApiModelProperty("主单中的商品数量（将子单中的商品数量合并）")
    Integer totalItemQuantity;

    @ApiModelProperty("子订单列表")
    List<OrderSubVO> subOrderList;

    @ApiModelProperty("阶段订单")
    List<StepOrderVO> stepOrders;

    @ApiModelProperty("按钮展示")
    OrderButtonsVO orderButtons;

    @ApiModelProperty("业务身份")
    List<String> bizCodes;

    @ApiModelProperty("参与的活动列表目前只有满赠")
    List<PromotionOptionVO> campaignList;

    @ApiModelProperty("代客下单信息")
    private HelpOrderVO helpOrderInfo;

    @ApiModelProperty("发票详情")
    private InvoiceDetailVO invoiceDetailVO;

    @ApiModelProperty("系统自动确认收货 剩余时间, 毫秒")
    private Long autoConfirmReceiveTaskMillis;

    @ApiModelProperty("未支付自动关单 剩余时间, 毫秒")
    private Long autoCloseUnpaidTaskMillis;

    @ApiModelProperty("是否可售后")
    private boolean apply = true;

    @ApiModelProperty(value = "预售 首付金额")
    private Long stepFirstAmt;

    @ApiModelProperty(value = "预售 尾款金额")
    private Long stepLastAmt;

    @ApiModelProperty(value = "营销活动优惠金额")
    private Long campReduceAmt;

    @ApiModelProperty("参与的活动列表目前只有满赠")
    private List<PromotionOptionVO> couponList;

    @ApiModelProperty("备注")
    private String remark;
}
