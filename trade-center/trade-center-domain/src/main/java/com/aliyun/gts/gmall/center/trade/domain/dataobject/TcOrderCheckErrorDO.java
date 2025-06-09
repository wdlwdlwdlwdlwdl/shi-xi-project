package com.aliyun.gts.gmall.center.trade.domain.dataobject;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;


/**
 * <p>
 * 交易对账错误表、记录错误信息、供后续人工或定时程序补救
 * </p>
 *
 * @author mybatis plus
 * @since 2021-06-01
 */
@TableName("tc_order_check_error")
public class TcOrderCheckErrorDO implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 对账主订单
     */
    private Long primaryOrderId;

    /**
     * 对账售后主单、如无售后、为空
     */
    private Long primaryReversalId;

    /**
     * 对账类型
     */
    private Integer checkType;

    /**
     * 对账类型描述
     */
    private String checkTypeDescribe;

    /**
     * 对账出错明细说明
     */
    private String checkErrorDetail;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date gmtCreate;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date gmtModified;

    /**
     * 扩展字段
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

    public Long getPrimaryReversalId() {
        return primaryReversalId;
    }

    public void setPrimaryReversalId(Long primaryReversalId) {
        this.primaryReversalId = primaryReversalId;
    }

    public Integer getCheckType() {
        return checkType;
    }

    public void setCheckType(Integer checkType) {
        this.checkType = checkType;
    }

    public String getCheckTypeDescribe() {
        return checkTypeDescribe;
    }

    public void setCheckTypeDescribe(String checkTypeDescribe) {
        this.checkTypeDescribe = checkTypeDescribe;
    }

    public String getCheckErrorDetail() {
        return checkErrorDetail;
    }

    public void setCheckErrorDetail(String checkErrorDetail) {
        this.checkErrorDetail = checkErrorDetail;
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
        return "TcOrderCheckErrorDO{" +
                "id=" + id +
                ", primaryOrderId=" + primaryOrderId +
                ", primaryReversalId=" + primaryReversalId +
                ", checkType=" + checkType +
                ", checkTypeDescribe=" + checkTypeDescribe +
                ", checkErrorDetail=" + checkErrorDetail +
                ", gmtCreate=" + gmtCreate +
                ", gmtModified=" + gmtModified +
                ", features=" + features +
                "}";
    }
}
