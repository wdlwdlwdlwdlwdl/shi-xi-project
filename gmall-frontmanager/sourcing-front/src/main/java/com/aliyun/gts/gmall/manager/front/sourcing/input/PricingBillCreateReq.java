package com.aliyun.gts.gmall.manager.front.sourcing.input;

import com.aliyun.gts.gcai.platform.sourcing.api.dto.model.AwardQuoteDetailDTO;
import com.aliyun.gts.gmall.framework.api.dto.AbstractRequest;
import com.aliyun.gts.gmall.framework.api.rest.dto.AbstractCommandRestRequest;
import com.aliyun.gts.gmall.framework.api.rest.dto.AbstractQueryRestRequest;
import lombok.Data;

import java.util.List;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/5/20 17:42
 */
@Data
public class PricingBillCreateReq extends AbstractCommandRestRequest {
    private Long id;
    /**
     * 寻源单ID
     */
    private Long sourcingId;
    /**
     * 报价ID
     */
    private List<Long> quoteIds;
    /**
     * 状态 com.aliyun.gts.gcai.platform.sourcing.common.type.PricingBillStatus
     */
    private Integer status;
    /**
     * 选中的报价
     */
    private List<AwardQuoteDetailDTO> awardQuote;
    @Override
    public boolean isWrite() {
        return false;
    }
}
