package com.aliyun.gts.gmall.platform.trade.domain.entity.logistics;

import java.util.Date;

import com.aliyun.gts.gmall.platform.trade.domain.entity.AbstractBusinessEntity;
import lombok.Data;

@Data
public class Logistics extends AbstractBusinessEntity {

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
     * 买家id
     */
    private Long custId;

    /**
     * 卖家id
     */
    private Long sellerId;

    /**
     * 物流公司类型
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
    private String extend;

    /**
     * 1实物物流 2 虚拟物流 3自提
     */
    private Integer type;

}
