package com.aliyun.gts.gmall.manager.front.customer.dto.input.query;

import com.aliyun.gts.gmall.manager.front.common.dto.PageLoginRestQuery;
import lombok.Data;

/**
 * 用户积分日志查询
 *
 * @author GTS
 * @date 2021/03/12
 */
@Data
public class AccountBookRecordRestQuery extends PageLoginRestQuery {
    /**
     * 变动方向： 1-赋予 积分收入，2-使用 积分支出
     */
    private Integer changeDirection;
    /**
     * ChangeTypeEnum 积分变化类型，比如失效积分:invalidate
     */
    private Integer changeType;
}