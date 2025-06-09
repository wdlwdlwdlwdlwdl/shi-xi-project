package com.aliyun.gts.gmall.platform.trade.api.dto.output;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author mybatis plus
 * @since 2021-02-01
 */
public class TcPayFlowDTO implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 支付流水自增id
     */
    private Long id;

    /**
     * 主订单编号
     */
    private String primaryOrderId;

    /**
     * 支付单业务id
     */
    private String paymentId;

    /**
     * 支付流水业务id
     */
    private String paymentFlowId;

    /**
     * 支付交易请求流水，调用第三方平台支付的流水
     */
    private String payTradeNo;

    /**
     * 分期类型：01-不分期，02-分3期，03-分6期，04-分12期
     */
    private String installmentType;

    /**
     * 外部支付流水号，合并支付的时候多个支付流水记录的外部支付流水一样
     */
    private String outFlowNo;

    /**
     * 支付类型：1-在线支付，2-用户资产 
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
     * 购买用户的id，分库分表健
     */
    private String custId;

    /**
     * 支付时间
     */
    private Date paymentFlowTime;

    /**
     * 支付流水费用，单位：分
     */
    private BigDecimal payAmount;

    /**
     * 附加值，如支付方式为积分，则此字段为应付积分的数值
     */
    private Long extraValue;

    /**
     * 支付流水状态:0-待确认，1-未支付，2-支付成功，3-支付失败，4-支付取消，5-已经退款
     */
    private Boolean paymentFlowStatus;

    /**
     * 对账状态：0-未对账，1-已对账
     */
    private Boolean accountChkFlag;

    /**
     * 购买方账户id
     */
    private String custAccountNo;

    /**
     * 支付银行编码
     */
    private String bankCode;

    /**
     * 卡有效期
     */
    private String cardValidDate;

    /**
     * 创建人
     */
    private String createId;

    /**
     * 流水创建时间
     */
    private Date createTime;

    /**
     * 更新人
     */
    private String updateId;

    /**
     * 最新修改时间
     */
    private Date updateTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 市场ID
     */
    private String marketId;

    /**
     * 支付流水扩展属性
     */
    private String paymentFeature;

    /**
     * 支付来源,同订单表的下单渠道。0-App，1-小程序，2-H5，3-PC，4-IPAD
     */
    private String paySource;

    /**
     * 支付商户ID
     */
    private String merId;

    /**
     * 支付请求报文
     */
    private String outReq;

    /**
     * 支付返回报文
     */
    private String outSp;


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

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getPaymentFlowId() {
        return paymentFlowId;
    }

    public void setPaymentFlowId(String paymentFlowId) {
        this.paymentFlowId = paymentFlowId;
    }

    public String getPayTradeNo() {
        return payTradeNo;
    }

    public void setPayTradeNo(String payTradeNo) {
        this.payTradeNo = payTradeNo;
    }

    public String getInstallmentType() {
        return installmentType;
    }

    public void setInstallmentType(String installmentType) {
        this.installmentType = installmentType;
    }

    public String getOutFlowNo() {
        return outFlowNo;
    }

    public void setOutFlowNo(String outFlowNo) {
        this.outFlowNo = outFlowNo;
    }

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }

    public String getPayMethod() {
        return payMethod;
    }

    public void setPayMethod(String payMethod) {
        this.payMethod = payMethod;
    }

    public String getPayChannel() {
        return payChannel;
    }

    public void setPayChannel(String payChannel) {
        this.payChannel = payChannel;
    }

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public Date getPaymentFlowTime() {
        return paymentFlowTime;
    }

    public void setPaymentFlowTime(Date paymentFlowTime) {
        this.paymentFlowTime = paymentFlowTime;
    }

    public BigDecimal getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(BigDecimal payAmount) {
        this.payAmount = payAmount;
    }

    public Long getExtraValue() {
        return extraValue;
    }

    public void setExtraValue(Long extraValue) {
        this.extraValue = extraValue;
    }

    public Boolean getPaymentFlowStatus() {
        return paymentFlowStatus;
    }

    public void setPaymentFlowStatus(Boolean paymentFlowStatus) {
        this.paymentFlowStatus = paymentFlowStatus;
    }

    public Boolean getAccountChkFlag() {
        return accountChkFlag;
    }

    public void setAccountChkFlag(Boolean accountChkFlag) {
        this.accountChkFlag = accountChkFlag;
    }

    public String getCustAccountNo() {
        return custAccountNo;
    }

    public void setCustAccountNo(String custAccountNo) {
        this.custAccountNo = custAccountNo;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getCardValidDate() {
        return cardValidDate;
    }

    public void setCardValidDate(String cardValidDate) {
        this.cardValidDate = cardValidDate;
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

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getMarketId() {
        return marketId;
    }

    public void setMarketId(String marketId) {
        this.marketId = marketId;
    }

    public String getPaymentFeature() {
        return paymentFeature;
    }

    public void setPaymentFeature(String paymentFeature) {
        this.paymentFeature = paymentFeature;
    }

    public String getPaySource() {
        return paySource;
    }

    public void setPaySource(String paySource) {
        this.paySource = paySource;
    }

    public String getMerId() {
        return merId;
    }

    public void setMerId(String merId) {
        this.merId = merId;
    }

    public String getOutReq() {
        return outReq;
    }

    public void setOutReq(String outReq) {
        this.outReq = outReq;
    }

    public String getOutSp() {
        return outSp;
    }

    public void setOutSp(String outSp) {
        this.outSp = outSp;
    }

    @Override
    public String toString() {
        return "TcPayFlowDO{" +
        "id=" + id +
        ", primaryOrderId=" + primaryOrderId +
        ", paymentId=" + paymentId +
        ", paymentFlowId=" + paymentFlowId +
        ", payTradeNo=" + payTradeNo +
        ", installmentType=" + installmentType +
        ", outFlowNo=" + outFlowNo +
        ", payType=" + payType +
        ", payMethod=" + payMethod +
        ", payChannel=" + payChannel +
        ", custId=" + custId +
        ", paymentFlowTime=" + paymentFlowTime +
        ", payAmount=" + payAmount +
        ", extraValue=" + extraValue +
        ", paymentFlowStatus=" + paymentFlowStatus +
        ", accountChkFlag=" + accountChkFlag +
        ", custAccountNo=" + custAccountNo +
        ", bankCode=" + bankCode +
        ", cardValidDate=" + cardValidDate +
        ", createId=" + createId +
        ", createTime=" + createTime +
        ", updateId=" + updateId +
        ", updateTime=" + updateTime +
        ", remark=" + remark +
        ", marketId=" + marketId +
        ", paymentFeature=" + paymentFeature +
        ", paySource=" + paySource +
        ", merId=" + merId +
        ", outReq=" + outReq +
        ", outSp=" + outSp +
        "}";
    }
}
