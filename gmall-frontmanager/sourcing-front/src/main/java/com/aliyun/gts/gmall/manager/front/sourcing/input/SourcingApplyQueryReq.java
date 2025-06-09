package com.aliyun.gts.gmall.manager.front.sourcing.input;

import com.aliyun.gts.gcai.platform.sourcing.common.input.query.SourcingApplyQuery;
import com.aliyun.gts.gmall.framework.api.rest.dto.AbstractPageQueryRestRequest;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class SourcingApplyQueryReq extends AbstractPageQueryRestRequest {
    private Long sourcingId;
    private Long supplierId;
    private String supplierName;
    private Integer applyType;
    private Integer status;
    private Integer sourcingType;

    public SourcingApplyQuery build() {
        SourcingApplyQuery q = new SourcingApplyQuery();
        BeanUtils.copyProperties(this, q);
        return q;
    }
}
