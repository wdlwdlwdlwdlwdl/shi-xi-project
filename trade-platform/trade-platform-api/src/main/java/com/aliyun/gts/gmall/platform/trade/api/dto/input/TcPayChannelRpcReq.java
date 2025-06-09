package com.aliyun.gts.gmall.platform.trade.api.dto.input;

import com.aliyun.gts.gmall.framework.api.rpc.dto.AbstractCommandRpcRequest;

import java.util.Date;


/**
 *
 * @author mybatis plus
 * @since 2021-01-29
 */
public class TcPayChannelRpcReq extends AbstractCommandRpcRequest {

    /**
     * 自增id
     */
    private Long id;

    /**
     * 展示支付渠道编码
     */
    private String channelCode;

    /**
     * 渠道显示名称
     */
    private String channelName;

    /**
     * 支付方式编码
     */
    private String methodCode;

    /**
     * 支付方式名称
     */
    private String methodName;

    /**
     * 支付类型编码
     */
    private String typeCode;

    /**
     * 支付类型名称
     */
    private String typeName;

    /**
     * 支持的下单渠道编码|分隔
     */
    private String orderChannelCode;

    /**
     * 支持的退款渠道编码|分隔
     */
    private String refundChannelCode;

    /**
     * 支付渠道显示顺序
     */
    private Integer paySequence;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 更新时间
     */
    private Date gmtModified;

    /**
     * 删除标识：0-有效，1-失效
     */
    private Boolean deleted;

    /**
     * 市场ID
     */
    private String marketId;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getMethodCode() {
        return methodCode;
    }

    public void setMethodCode(String methodCode) {
        this.methodCode = methodCode;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getOrderChannelCode() {
        return orderChannelCode;
    }

    public void setOrderChannelCode(String orderChannelCode) {
        this.orderChannelCode = orderChannelCode;
    }

    public String getRefundChannelCode() {
        return refundChannelCode;
    }

    public void setRefundChannelCode(String refundChannelCode) {
        this.refundChannelCode = refundChannelCode;
    }

    public Integer getPaySequence() {
        return paySequence;
    }

    public void setPaySequence(Integer paySequence) {
        this.paySequence = paySequence;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public String getMarketId() {
        return marketId;
    }

    public void setMarketId(String marketId) {
        this.marketId = marketId;
    }

    @Override
    public String toString() {
        return "TcPayChannelDO{" +
        "id=" + id +
        ", channelCode=" + channelCode +
        ", channelName=" + channelName +
        ", methodCode=" + methodCode +
        ", methodName=" + methodName +
        ", typeCode=" + typeCode +
        ", typeName=" + typeName +
        ", orderChannelCode=" + orderChannelCode +
        ", refundChannelCode=" + refundChannelCode +
        ", paySequence=" + paySequence +
        ", remark=" + remark +
        ", gmtCreate=" + gmtCreate +
        ", gmtModified=" + gmtModified +
        ", deleted=" + deleted +
        ", marketId=" + marketId +
        "}";
    }
}
