package com.aliyun.gts.gmall.manager.front.sourcing.input;

import com.aliyun.gts.gcai.platform.sourcing.common.input.query.PricingBillQuery;
import com.aliyun.gts.gmall.framework.api.dto.PageParam;
import com.aliyun.gts.gmall.framework.api.rest.dto.AbstractQueryRestRequest;
import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestQuery;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class PricingBillQueryReq extends LoginRestQuery {

    private Long id;
    private Long sourcingId;
    private Integer status;
    private String sourcingName;
    private PageParam page;
    private Long purchaserId;
    private Integer sourcingType;

    public PricingBillQuery build() {
        PricingBillQuery t = new PricingBillQuery();
        BeanUtils.copyProperties(this, t);
        t.setPurchaserId(getCustId());
        return t;
    }
}
