package com.aliyun.gts.gmall.manager.front.sourcing.input;

import com.aliyun.gts.gcai.platform.sourcing.api.dto.model.SourcingApplyDTO;
import com.aliyun.gts.gmall.framework.api.rest.dto.AbstractQueryRestRequest;
import lombok.Data;

import java.util.List;

@Data
public class SourcingApplyListReq extends AbstractQueryRestRequest {

    private List<SourcingApplyDTO> list;
}
