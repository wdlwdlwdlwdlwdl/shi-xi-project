package com.aliyun.gts.gmall.platform.trade.domain.entity.order;

import com.aliyun.gts.gmall.framework.api.rpc.dto.AbstractPageQueryRpcRequest;
import lombok.Data;

/**
 * 说明： TODO
 *
 * @author yangl
 * @version 1.0
 * @date 2024/8/26 19:41
 */
@Data
public class TcSumOrder extends AbstractPageQueryRpcRequest {

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

    private Integer isSend;

}
