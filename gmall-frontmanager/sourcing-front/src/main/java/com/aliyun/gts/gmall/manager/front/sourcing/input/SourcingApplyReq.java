package com.aliyun.gts.gmall.manager.front.sourcing.input;

import com.aliyun.gts.gcai.platform.sourcing.api.dto.model.SourcingApplyDTO;
import com.aliyun.gts.gmall.framework.api.dto.AbstractRequest;
import lombok.Data;

import java.util.List;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/5/25 11:29
 */
@Data
public class SourcingApplyReq extends AbstractRequest {
    private Long sourcingId;
    /**
     * 供应商ID
     */
    private List<SourcingApplyDTO> supplier;

    @Override
    public boolean isWrite() {
        return false;
    }
}
