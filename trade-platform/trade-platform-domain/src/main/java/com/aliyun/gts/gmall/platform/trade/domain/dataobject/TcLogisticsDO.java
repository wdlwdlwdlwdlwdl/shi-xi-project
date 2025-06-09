package com.aliyun.gts.gmall.platform.trade.domain.dataobject;

import com.aliyun.gts.gmall.platform.trade.common.constants.LogisticsCompanyTypeEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.LogisticsTypeEnum;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

/**
 * <p>
 * 
 * </p>
 *
 * @author mybatis plus
 * @since 2021-02-04
 */
@TableName("tc_logistics")
@Data
public class TcLogisticsDO implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 自增主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 主订单ID
     */
    private Long primaryOrderId;

    /**
     * 订单ID、订单ID可能是子订单也可能是主订单ID
     */
    private Long orderId;

    /**
     * 买家id
     */
    private Long custId;

    /**
     * 卖家id
     */
    private Long sellerId;

    /**
     * 物流公司类型
     * @see LogisticsCompanyTypeEnum
     */
    private Integer companyType;

    private String companyName;

    /**
     * 物流公司返回的物流编号
     */
    private String logisticsId;

    /**
     * 物流状态
     */
    private Integer logisticsStatus;

    /**
     * 售后单主id
     */
    private Long primaryReversalId;

    /**
     * 售后单子id
     */
    private Long reversalId;

    @TableField(fill = FieldFill.INSERT)
    private Date gmtCreate;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date gmtModified;

    /**
     * 收货人姓名
     */
    private String receiverName;

    /**
     * 收货人电话
     */
    private String receiverPhone;

    /**
     * 收货人详细地址
     */
    private String receiverAddr;

    /**
     * 扩展信息
     */
    private HashMap logisticsAttr;

    /**
     * 1实物物流 2 虚拟物流 3自提
     * @see LogisticsTypeEnum
     */
    private Integer type;

    private String otpCode;

    private String returnCode;

    private String logisticsUrl;
    @Override
    public String toString() {
        return "TcLogisticsDO{" +
        "id=" + id +
        ", primaryOrderId=" + primaryOrderId +
        ", orderId=" + orderId +
        ", custId=" + custId +
        ", sellerId=" + sellerId +
        ", companyType=" + companyType +
        ", logisticsId=" + logisticsId +
        ", logisticsStatus=" + logisticsStatus +
        ", primaryReversalId=" + primaryReversalId +
        ", reversalId=" + reversalId +
        ", gmtCreate=" + gmtCreate +
        ", gmtModified=" + gmtModified +
        ", receiverName=" + receiverName +
        ", receiverPhone=" + receiverPhone +
        ", receiverAddr=" + receiverAddr +
        ", logisticsAttr=" + logisticsAttr + ", logisticsUrl=" + logisticsUrl +
        ", type=" + type +
        "}";
    }
}
