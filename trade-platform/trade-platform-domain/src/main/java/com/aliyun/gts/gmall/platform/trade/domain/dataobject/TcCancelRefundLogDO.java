package com.aliyun.gts.gmall.platform.trade.domain.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("tc_cancel_refund_log")
public class TcCancelRefundLogDO implements Serializable {

    /**
     * 自增id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 主订单id
     */
    private Long primaryOrderId;

    /**
     * 订单id
     */
    private Long orderId;

    /**
     * 售后单id
     */
    private Long primaryReversalId;

    /**
     * 退款请求信息
     */
    private String refundMsg;

    /**
     * 退款返回信息
     */
    private String returnMsg;

    /**
     * 取消来源（'取消来源 0 系统取消 1客户取消 2商家取消'）
     */
    private String cancelSource;

    /**
     * 取消原因
     */
    private String cancelReason;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 更新时间
     */
    private Date gmtModified;

    /**
     * 备注
     */
    private String remark;

}
