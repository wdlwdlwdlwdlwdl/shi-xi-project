package com.aliyun.gts.gmall.platform.trade.api.dto.output;

import com.aliyun.gts.gmall.platform.trade.common.constants.LogisticsCompanyTypeEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.LogisticsTypeEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 *
 */
@Data
public class TcLogisticsDTO implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 自增主键
     */
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

    private Date gmtCreate;

    private Date gmtModified;

    /**
     * 收货人信息 实物类就是姓名 电话  地址
     */
    private String receiverInfo;

    /**
     * 扩展信息
     */
    private Map logisticsAttr;

    private String logisticsUrl;

    private String otpCode;

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
     * 1实物物流 2 虚拟物流 3自提
     * @see LogisticsTypeEnum
     */
    private Integer type;

    private Integer typeName;

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
        ", receiverInfo=" + receiverInfo +
        ", logisticsAttr=" + logisticsAttr +
        ", type=" + type +
        "}";
    }
}
