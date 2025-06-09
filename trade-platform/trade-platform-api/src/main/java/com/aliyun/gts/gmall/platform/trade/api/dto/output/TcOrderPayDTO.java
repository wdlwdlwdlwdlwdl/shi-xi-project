package com.aliyun.gts.gmall.platform.trade.api.dto.output;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author mybatis plus
 * @since 2021-01-29
 */
public class TcOrderPayDTO implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 自增主键
     */
    private Long id;

    /**
     * 主订单ID
     */
    private String primaryOrderId;

    /**
     * 订单ID、订单ID可能是子订单也可能是主订单ID
     */
    private String orderId;

    /**
     * 支付单编号
     */
    private String paymentId;

    /**
     * 购买客户的编码，分库分表健
     */
    private String custId;

    /**
     * 支付单状态：00-待支付，01-待确认，02-部分支付，03-已支付 
     */
    private String paymentStatus;

    /**
     * 支付类型：1-在线支付，2-用户资产支付
     */
    private Integer payType;

    /**
     * 支付选项，前端选择的具体支付渠道
     */
    private String payOption;

    /**
     * 支付阶段：0-普通支付，1-定金，2-中期款，3-尾款
     */
    private Integer paymentStage;

    /**
     * 订单合计费用：费用合计= Σ每行营销优惠后售价+Σ每行优惠后运费+Σ每行优惠后税费
     */
    private BigDecimal totalAmount;

    /**
     * 支付优惠金额
     */
    private BigDecimal paymentDiscountAmt;

    /**
     * 订单已支付费用合计，单位：分           本次订购已支付的所有费用合计            已支付费用合计= Σ支付流水费用
     */
    private BigDecimal paidAmount;

    /**
     * 订单取消费用合计
     */
    private BigDecimal cancelAmount;

    /**
     * 支付备注
     */
    private String remark;

    /**
     * 创建人
     */
    private String createId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新人
     */
    private String updateId;

    /**
     * 支付时间
     */
    private Date payTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 市场ID
     */
    private String marketId;

    /**
     * 支付单属性
     */
    private String payFeature;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPrimaryOrderId() {
        return primaryOrderId;
    }

    public void setPrimaryOrderId(String primaryOrderId) {
        this.primaryOrderId = primaryOrderId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }

    public String getPayOption() {
        return payOption;
    }

    public void setPayOption(String payOption) {
        this.payOption = payOption;
    }

    public Integer getPaymentStage() {
        return paymentStage;
    }

    public void setPaymentStage(Integer paymentStage) {
        this.paymentStage = paymentStage;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getPaymentDiscountAmt() {
        return paymentDiscountAmt;
    }

    public void setPaymentDiscountAmt(BigDecimal paymentDiscountAmt) {
        this.paymentDiscountAmt = paymentDiscountAmt;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public BigDecimal getCancelAmount() {
        return cancelAmount;
    }

    public void setCancelAmount(BigDecimal cancelAmount) {
        this.cancelAmount = cancelAmount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCreateId() {
        return createId;
    }

    public void setCreateId(String createId) {
        this.createId = createId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUpdateId() {
        return updateId;
    }

    public void setUpdateId(String updateId) {
        this.updateId = updateId;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getMarketId() {
        return marketId;
    }

    public void setMarketId(String marketId) {
        this.marketId = marketId;
    }

    public String getPayFeature() {
        return payFeature;
    }

    public void setPayFeature(String payFeature) {
        this.payFeature = payFeature;
    }

    @Override
    public String toString() {
        return "TcOrderPayDO{" +
        "id=" + id +
        ", primaryOrderId=" + primaryOrderId +
        ", orderId=" + orderId +
        ", paymentId=" + paymentId +
        ", custId=" + custId +
        ", paymentStatus=" + paymentStatus +
        ", payType=" + payType +
        ", payOption=" + payOption +
        ", paymentStage=" + paymentStage +
        ", totalAmount=" + totalAmount +
        ", paymentDiscountAmt=" + paymentDiscountAmt +
        ", paidAmount=" + paidAmount +
        ", cancelAmount=" + cancelAmount +
        ", remark=" + remark +
        ", createId=" + createId +
        ", createTime=" + createTime +
        ", updateId=" + updateId +
        ", payTime=" + payTime +
        ", updateTime=" + updateTime +
        ", marketId=" + marketId +
        ", payFeature=" + payFeature +
        "}";
    }
}
