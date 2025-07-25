package com.aliyun.gts.gmall.platform.trade.api.dto.input;

import com.aliyun.gts.gmall.framework.api.rpc.dto.AbstractCommandRpcRequest;

import java.util.Date;


/**
 *
 * @author mybatis plus
 * @since 2021-03-19
 */
public class TcOrderExtendRpcReq extends AbstractCommandRpcRequest {

    /**
     * 自增主键
     */
    private Long id;

    /**
     * 主订单ID
     */
    private Long primaryOrderId;

    /**
     * 子订单ID
     */
    private Long orderId;

    /**
     * 买家ID
     */
    private Long custId;

    /**
     * 扩展属性类型
     */
    private String extendType;

    /**
     * 扩展属性key
     */
    private String extendKey;

    /**
     * 扩展属性value
     */
    private String extendValue;

    /**
     * 扩展属性名称
     */
    private String extendName;

    /**
     * 是否有效
     */
    private Integer valid;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 更新时间
     */
    private Date gmtModified;


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

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getCustId() {
        return custId;
    }

    public void setCustId(Long custId) {
        this.custId = custId;
    }

    public String getExtendType() {
        return extendType;
    }

    public void setExtendType(String extendType) {
        this.extendType = extendType;
    }

    public String getExtendKey() {
        return extendKey;
    }

    public void setExtendKey(String extendKey) {
        this.extendKey = extendKey;
    }

    public String getExtendValue() {
        return extendValue;
    }

    public void setExtendValue(String extendValue) {
        this.extendValue = extendValue;
    }

    public String getExtendName() {
        return extendName;
    }

    public void setExtendName(String extendName) {
        this.extendName = extendName;
    }

    public Integer getValid() {
        return valid;
    }

    public void setValid(Integer valid) {
        this.valid = valid;
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

    @Override
    public String toString() {
        return "TcOrderExtendDO{" +
        "id=" + id +
        ", primaryOrderId=" + primaryOrderId +
        ", orderId=" + orderId +
        ", custId=" + custId +
        ", extendType=" + extendType +
        ", extendKey=" + extendKey +
        ", extendValue=" + extendValue +
        ", extendName=" + extendName +
        ", valid=" + valid +
        ", gmtCreate=" + gmtCreate +
        ", gmtModified=" + gmtModified +
        "}";
    }
}
