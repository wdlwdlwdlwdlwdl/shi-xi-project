package com.aliyun.gts.gmall.platform.trade.api.dto.message;

import com.aliyun.gts.gmall.framework.api.dto.Transferable;
import lombok.Data;

/**
 * 说明： TODO
 *
 * @author yangl
 * @version 1.0
 * @date 2024/11/18 19:07
 */
@Data
public class EvaluateMessageDTO implements Transferable {

    private Long sellerId;

    private Long primaryOrderId;
    /**
     * 买家id
     */
    private Long custId;
    /**
     * 评论分数1-5
     */
    private Integer rateScore;

    /**
     * 评价内容
     */
    private String rateDesc;
}
