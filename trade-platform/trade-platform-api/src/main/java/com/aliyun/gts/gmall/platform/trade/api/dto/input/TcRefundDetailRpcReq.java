package com.aliyun.gts.gmall.platform.trade.api.dto.input;

import com.aliyun.gts.gmall.framework.api.rpc.dto.AbstractCommandRpcRequest;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;


/**
 *
 * @author mybatis plus
 * @since 2021-02-18
 */
@Data
public class TcRefundDetailRpcReq extends AbstractCommandRpcRequest {

    /**
     * 退款流水自增id
     */
    private Long id;

    /**
     * 售后单id
     */
    private Long primaryReversalId;

    /**
     * 退款单标识
     */
    private Long refundId;

    /**
     * 退款记录业务id
     */
    private Long refundDetailId;

    /**
     * 购买用户的id，分库分表健
     */
    private Long custId;

    /**
     * 退款用户姓名
     */
    private String custName;

    /**
     * 退款请求流水
     */
    private String refundTradeNo;

    /**
     * 退款流水状态:00-待退款，01-失败，02-成功，03-转预付款，04-对账超时
            
     */
    private Integer refundFlowStatus;

    /**
     * 退款类型：1-在线支付，2-用户资产
     */
    private Integer refundType;

    /**
     * 退款方式
     */
    private String refundMethod;

    /**
     * 支付类型
     */
    private Integer payType;

    /**
     * 支付方式
     */
    private String payMethod;

    /**
     * 支付渠道
     */
    private String payChannel;

    /**
     * 退款金额，单位：分
     */
    private BigDecimal refundFlowAmount;

    /**
     * 退款时间
     */
    private Date refundFlowTime;

    /**
     * 支付流水，此支付流水是订购过程中支付记录的流水
     */
    private String paymentFlowId;

    /**
     * 外部支付流水
     */
    private String payOutFlowNo;

    /**
     * 商家编号
     */
    private Long sellerId;

    /**
     * 客户退款银行名称
     */
    private String bankName;

    /**
     * 客户退款银行卡号
     */
    private String bankCardNo;

    /**
     * 退款银行账户名称
     */
    private String bankAccountName;

    /**
     * 退款流水记录创建时间
     */
    private Date gmtCreate;

    /**
     * 最后更新时间
     */
    private Date gmtModified;

    /**
     * 备注
     */
    private String remark;

    /**
     * 自动退款标识  0-非自动退款 ；1-自动退款
     */
    private Boolean isAutoRefund;
}
