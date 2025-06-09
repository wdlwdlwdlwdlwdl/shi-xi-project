package com.aliyun.gts.gmall.manager.front.customer.dto.input.query;

import com.aliyun.gts.gmall.framework.api.rest.dto.AbstractQueryRestRequest;
import lombok.Data;

/**
 * 根据code查询券
 *
 * @author GTS
 * @date 2021/03/12
 */
@Data
public class ByCodeCouponQuery extends AbstractQueryRestRequest {
    /**
     * 券code
     */
    private String code;
}