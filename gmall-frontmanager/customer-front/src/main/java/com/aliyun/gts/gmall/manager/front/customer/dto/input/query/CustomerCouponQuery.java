package com.aliyun.gts.gmall.manager.front.customer.dto.input.query;

import com.aliyun.gts.gmall.manager.front.common.dto.PageLoginRestQuery;
import lombok.Data;

/**
 * 买家券查询
 *
 * @author GTS
 * @date 2021/03/12
 */
@Data
public class CustomerCouponQuery extends PageLoginRestQuery {
    /**
     * AssetsStatus 可选
     */
    public Integer status;
}