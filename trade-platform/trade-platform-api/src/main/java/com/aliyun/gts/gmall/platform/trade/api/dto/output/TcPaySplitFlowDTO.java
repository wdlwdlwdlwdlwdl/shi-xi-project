package com.aliyun.gts.gmall.platform.trade.api.dto.output;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author mybatis plus
 * @since 2021-04-22
 */
public class TcPaySplitFlowDTO implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * ID主键
     */
    private Long id;

    /**
     * 主订单编号
     */
    private Long primaryOrderId;

    /**
     * 买家ID
     */
    private Long custId;

    /**
     * 分账关联的支付流水、可对应正向或者退款流水
     */
    private String relationFlowId;

    /**
     * 分账流水ID
     */
    private String splitFlowId;

    /**
     * 分账流水类型：1正向支付 2逆向退款
     */
    private Integer splitFlowType;

    /**
     * 收款账户类型：分润账户、商家商户等
     */
    private Integer payAccountType;

    /**
     * 付款账户
     */
    private String payerId;

    /**
     * 收款账户
     */
    private String payeeId;

    /**
     * 分账金额
     */
    private BigDecimal splitAmount;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 修改时间
     */
    private Date gmtModified;

    /**
     * 执行成功时间
     */
    private Date successTime;

    /**
     * 是否有效
     */
    private Integer valid;

    /**
     * 扩展信息
     */
    private String features;

    /**
     * 请求报文信息
     */
    private String reqMsg;

    /**
     * 响应报文信息
     */
    private String respMsg;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPrimaryOrderId() {
        return primaryOrderId;
    }

    public void setPrimaryOrderId(Long primaryOrderId) {
        this.primaryOrderId = primaryOrderId;
    }

    public Long getCustId() {
        return custId;
    }

    public void setCustId(Long custId) {
        this.custId = custId;
    }

    public String getRelationFlowId() {
        return relationFlowId;
    }

    public void setRelationFlowId(String relationFlowId) {
        this.relationFlowId = relationFlowId;
    }

    public String getSplitFlowId() {
        return splitFlowId;
    }

    public void setSplitFlowId(String splitFlowId) {
        this.splitFlowId = splitFlowId;
    }

    public Integer getSplitFlowType() {
        return splitFlowType;
    }

    public void setSplitFlowType(Integer splitFlowType) {
        this.splitFlowType = splitFlowType;
    }

    public Integer getPayAccountType() {
        return payAccountType;
    }

    public void setPayAccountType(Integer payAccountType) {
        this.payAccountType = payAccountType;
    }

    public String getPayerId() {
        return payerId;
    }

    public void setPayerId(String payerId) {
        this.payerId = payerId;
    }

    public String getPayeeId() {
        return payeeId;
    }

    public void setPayeeId(String payeeId) {
        this.payeeId = payeeId;
    }

    public BigDecimal getSplitAmount() {
        return splitAmount;
    }

    public void setSplitAmount(BigDecimal splitAmount) {
        this.splitAmount = splitAmount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public Date getSuccessTime() {
        return successTime;
    }

    public void setSuccessTime(Date successTime) {
        this.successTime = successTime;
    }

    public Integer getValid() {
        return valid;
    }

    public void setValid(Integer valid) {
        this.valid = valid;
    }

    public String getFeatures() {
        return features;
    }

    public void setFeatures(String features) {
        this.features = features;
    }

    public String getReqMsg() {
        return reqMsg;
    }

    public void setReqMsg(String reqMsg) {
        this.reqMsg = reqMsg;
    }

    public String getRespMsg() {
        return respMsg;
    }

    public void setRespMsg(String respMsg) {
        this.respMsg = respMsg;
    }

    @Override
    public String toString() {
        return "TcPaySplitFlowDO{" +
        "id=" + id +
        ", primaryOrderId=" + primaryOrderId +
        ", custId=" + custId +
        ", relationFlowId=" + relationFlowId +
        ", splitFlowId=" + splitFlowId +
        ", splitFlowType=" + splitFlowType +
        ", payAccountType=" + payAccountType +
        ", payerId=" + payerId +
        ", payeeId=" + payeeId +
        ", splitAmount=" + splitAmount +
        ", status=" + status +
        ", gmtCreate=" + gmtCreate +
        ", gmtModified=" + gmtModified +
        ", successTime=" + successTime +
        ", valid=" + valid +
        ", features=" + features +
        ", reqMsg=" + reqMsg +
        ", respMsg=" + respMsg +
        "}";
    }
}
