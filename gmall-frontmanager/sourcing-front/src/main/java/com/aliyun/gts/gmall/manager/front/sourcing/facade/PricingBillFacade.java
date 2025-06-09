package com.aliyun.gts.gmall.manager.front.sourcing.facade;

import com.aliyun.gts.gcai.platform.sourcing.api.dto.model.PricingBillDTO;
import com.aliyun.gts.gcai.platform.sourcing.api.facade.PricingBillReadFacade;
import com.aliyun.gts.gcai.platform.sourcing.api.facade.PricingBillWriteFacade;
import com.aliyun.gts.gcai.platform.sourcing.common.input.CommandRequest;
import com.aliyun.gts.gcai.platform.sourcing.common.input.StatusUpdateRequest;
import com.aliyun.gts.gcai.platform.sourcing.common.input.query.PricingBillQuery;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rest.dto.CommonByIdQuery;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.manager.front.b2bcomm.utils.ResponseUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/5/19 19:27
 */
@Component
public class  PricingBillFacade {
    @Resource
    private PricingBillWriteFacade writeFacade;

    @Resource
    private PricingBillReadFacade readFacade;
    /**
     *
     * @return
     */
    public RpcResponse<Long> createBill(PricingBillDTO billDTO){
        return writeFacade.create(CommandRequest.of(billDTO));
    }
    /**
     *
     * @return
     */
    public RpcResponse<Boolean> updateBill(PricingBillDTO billDTO){
        return writeFacade.update(CommandRequest.of(billDTO));
    }
    /**
     *
     * @return
     */
    public PricingBillDTO queryById(Long id){
        RpcResponse<PricingBillDTO> response = readFacade.queryById(CommonByIdQuery.of(id));
        return response.getData();
    }
    /**
     *
     * @return
     */
    public PricingBillDTO queryBySourcingId(Long id){
        RpcResponse<PricingBillDTO> response = readFacade.queryBySourcingId(CommonByIdQuery.of(id));
        return response.getData();
    }

    /**
     * 分页查询
     * @param query
     * @return
     */
    public PageInfo<PricingBillDTO> page(PricingBillQuery query){
        RpcResponse<PageInfo<PricingBillDTO>> response = readFacade.page(query);
        return  response.getData();
    }

    /**
     * @param request
     * @return
     */
    public RestResponse<Boolean> approve(StatusUpdateRequest request){
        RpcResponse<Boolean> response = writeFacade.approve(request);
        return ResponseUtils.operateResult(response);
    }

    public RestResponse<Boolean> delete(CommonByIdQuery query){
        RpcResponse<Boolean> response = writeFacade.delete(query);
        return ResponseUtils.operateResult(response);
    }
}
