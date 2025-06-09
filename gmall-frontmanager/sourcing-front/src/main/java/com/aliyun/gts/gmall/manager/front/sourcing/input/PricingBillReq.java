package com.aliyun.gts.gmall.manager.front.sourcing.input;

import com.aliyun.gts.gmall.framework.api.dto.AbstractRequest;
import com.aliyun.gts.gmall.framework.api.rest.dto.AbstractQueryRestRequest;
import lombok.Data;

import java.util.List;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/5/20 17:42
 */
@Data
public class PricingBillReq extends AbstractQueryRestRequest {
    private Long sourcingId;
    /**
     * 比价单ID
     */
    private Long billId;
    /**
     * 报价ID
     */
    private List<Long> quoteIds;

    /**
     * 单个报价ID
     */
    private Long quoteId;

    @Override
    public boolean isWrite() {
        return false;
    }
}
