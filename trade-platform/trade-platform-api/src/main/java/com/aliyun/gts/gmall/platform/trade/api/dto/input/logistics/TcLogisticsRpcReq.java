package com.aliyun.gts.gmall.platform.trade.api.dto.input.logistics;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.TradeCommandRpcRequest;
import com.aliyun.gts.gmall.platform.trade.common.constants.LogisticsTypeEnum;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * @author mybatis plus
 * @since 2021-02-04
 */
@Data
public class TcLogisticsRpcReq extends TradeCommandRpcRequest {

    /**
     * 自增主键
     */
    private Long id;

    /**
     * 主订单ID
     */
    @NotNull
    private Long primaryOrderId;

    /**
     * 订单ID、订单ID可能是子订单也可能是主订单ID
     */
    private Long orderId;

    /**
     * 买家id
     */
    @NotNull
    private Long custId;

    /**
     * 卖家id
     */
    @NotNull
    private Long sellerId;

    /**
     * 售后单主id
     */
    private Long primaryReversalId;

    /**
     * 售后单子id
     */
    private Long reversalId;

    private Date gmtCreate;

    private Date gmtModified;

    /**
     * 收货人姓名
     */
    @NotNull
    private String receiverName;

    /**
     * 收货人电话
     */
    @NotNull
    private String receiverPhone;

    /**
     * 收货人详细地址
     */
    @NotNull
    private String receiverAddr;

    /**
     * 扩展信息
     */
    private Map logisticsAttr;

    /**
     * 1实物物流 2 虚拟物流 3自提
     * @see LogisticsTypeEnum
     */
    @NotNull
    private Integer type;

    private String otpCode;

    private String returnCode;

    private String logisticsUrl;

    private String logisticsId;

    private String deliveryServiceName;

    /**
     * 物流单信息
     */
    @NotNull
    @Valid
    List<LogisticsInfoRpcReq> infoList;

    @Override
    public String toString() {
        return "TcLogisticsDO{" +
        "id=" + id +
        ", primaryOrderId=" + primaryOrderId +
        ", orderId=" + orderId +
        ", custId=" + custId +
        ", sellerId=" + sellerId +
        ", primaryReversalId=" + primaryReversalId +
        ", reversalId=" + reversalId +
        ", gmtCreate=" + gmtCreate +
        ", gmtModified=" + gmtModified +
        ", receiverName=" + receiverName +
        ", receiverPhone=" + receiverPhone +
        ", receiverAddr=" + receiverAddr +
        ", logisticsAttr=" + logisticsAttr +
        ", type=" + type + ", otpCode=" + otpCode +
        "}";
    }
}
