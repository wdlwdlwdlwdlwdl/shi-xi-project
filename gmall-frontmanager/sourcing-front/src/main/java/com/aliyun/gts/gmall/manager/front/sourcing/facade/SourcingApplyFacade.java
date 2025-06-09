package com.aliyun.gts.gmall.manager.front.sourcing.facade;

import com.aliyun.gts.gcai.platform.sourcing.api.dto.model.SourcingApplyDTO;
import com.aliyun.gts.gcai.platform.sourcing.api.facade.SourcingApplyReadFacade;
import com.aliyun.gts.gcai.platform.sourcing.common.input.query.SourcingApplyQuery;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/6/9 18:34
 */
@Component
public class SourcingApplyFacade {
    @Resource
    private SourcingApplyReadFacade readFacade;

    public PageInfo<SourcingApplyDTO> pageInfo(SourcingApplyQuery query) {
        RpcResponse<PageInfo<SourcingApplyDTO>> response = readFacade.page(query);
        return response.getData();
    }
}
