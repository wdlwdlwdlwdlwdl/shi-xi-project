package com.aliyun.gts.gmall.platform.trade.domain.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * <p>
 * 
 * </p>
 *
 * @author mybatis plus
 * @since 2021-02-04
 */
@TableName("tc_order_summary")
@Data
public class TcOrderSummaryDO implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 自增主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 卖家id
     */
    private Long sellerId;

    /**
     * 订单数量
     */
    private Long orderNum;

    /**
     * 取消单数量
     */
    private Long cancelOrderNum;

    /**
     * 统计时间
     */
    private String statisticDate;

    /**
     * 取消比例
     */
    private String cancelRate;

    /**
     * 创建时间（统计时间）
     */
    private Date createTime;

    private int isSend;

    /**
     * 更新时间
     */
    private Date updateTime;
}
