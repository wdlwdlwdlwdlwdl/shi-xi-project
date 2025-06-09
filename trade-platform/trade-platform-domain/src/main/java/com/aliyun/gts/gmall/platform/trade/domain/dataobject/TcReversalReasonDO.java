package com.aliyun.gts.gmall.platform.trade.domain.dataobject;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("tc_reversal_reason")
public class TcReversalReasonDO {

    /**
     * 主键
     */
    private Long id;

    /**
     * 1-仅退款, 2-退货退款
     * ReversalTypeEnum
     */
    private Integer reversalType;

    /**
     * 售后原因编码
     */
    private Integer reasonCode;

    /**
     * 售后原因内容
     */
    private String reasonMessage;
}
