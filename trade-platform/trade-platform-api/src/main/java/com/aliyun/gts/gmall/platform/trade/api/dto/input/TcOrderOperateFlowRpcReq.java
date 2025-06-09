package com.aliyun.gts.gmall.platform.trade.api.dto.input;

import com.aliyun.gts.gmall.framework.api.rpc.dto.AbstractCommandRpcRequest;
import io.swagger.annotations.ApiModel;

import java.util.Date;

/**
 *
 * @author mybatis plus
 * @since 2021-03-23
 */
@ApiModel("订单操作记录查询参数")
public class TcOrderOperateFlowRpcReq extends AbstractCommandRpcRequest {

    /**
     * 主键
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
     * 变更前订单状态
     */
    private Integer fromOrderStatus;

    /**
     * 变更后订单状态
     */
    private Integer toOrderStatus;

    /**
     * 1:customer, 0:seller
     */
    private Integer operatorType;

    /**
     * 操作名称
     */
    private String opName;

    /**
     * 操作者名称
     */
    private String operator;

    /**
     * 操作时间
     */
    private Date gmtCreate;

    /**
     * 更新时间
     */
    private Date gmtModified;

    /**
     * 扩展
     */
    private String features;


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

    public Integer getFromOrderStatus() {
        return fromOrderStatus;
    }

    public void setFromOrderStatus(Integer fromOrderStatus) {
        this.fromOrderStatus = fromOrderStatus;
    }

    public Integer getToOrderStatus() {
        return toOrderStatus;
    }

    public void setToOrderStatus(Integer toOrderStatus) {
        this.toOrderStatus = toOrderStatus;
    }

    public Integer getOperatorType() {
        return operatorType;
    }

    public void setOperatorType(Integer operatorType) {
        this.operatorType = operatorType;
    }

    public String getOpName() {
        return opName;
    }

    public void setOpName(String opName) {
        this.opName = opName;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
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

    public String getFeatures() {
        return features;
    }

    public void setFeatures(String features) {
        this.features = features;
    }

    @Override
    public String toString() {
        return "TcOrderOperateFlowDO{" +
        "id=" + id +
        ", primaryOrderId=" + primaryOrderId +
        ", orderId=" + orderId +
        ", fromOrderStatus=" + fromOrderStatus +
        ", toOrderStatus=" + toOrderStatus +
        ", operatorType=" + operatorType +
        ", opName=" + opName +
        ", operator=" + operator +
        ", gmtCreate=" + gmtCreate +
        ", gmtModified=" + gmtModified +
        ", features=" + features +
        "}";
    }
}
