package com.aliyun.gts.gmall.platform.trade.api.dto.output.reversal;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "售后单统计结果")
public class ReversalDTO extends AbstractOutputInfo{
    private static final long serialVersionUID = 1L;
    /** 商家id */
    private Long sellerId;
    /** 订单总数 */
    private int orderCount;
    /** 退单订单总数 */
    private int reversalCount;
}
