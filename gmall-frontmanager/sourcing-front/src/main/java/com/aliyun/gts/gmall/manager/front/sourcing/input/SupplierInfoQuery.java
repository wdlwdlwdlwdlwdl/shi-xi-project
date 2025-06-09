package com.aliyun.gts.gmall.manager.front.sourcing.input;

import com.aliyun.gts.gmall.framework.api.rest.dto.AbstractPageQueryRestRequest;
import lombok.Data;

@Data
public class SupplierInfoQuery extends AbstractPageQueryRestRequest {
    private String name;
    private Integer status;
    private Integer auditStatus;
}
