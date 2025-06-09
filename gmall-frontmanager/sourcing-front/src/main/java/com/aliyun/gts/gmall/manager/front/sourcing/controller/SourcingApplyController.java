package com.aliyun.gts.gmall.manager.front.sourcing.controller;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gcai.platform.sourcing.api.dto.model.SourcingApplyDTO;
import com.aliyun.gts.gcai.platform.sourcing.api.facade.SourcingApplyReadFacade;
import com.aliyun.gts.gcai.platform.sourcing.api.facade.SourcingApplyWriteFacade;
import com.aliyun.gts.gcai.platform.sourcing.common.input.CommandRequest;
import com.aliyun.gts.gcai.platform.sourcing.common.input.StatusUpdateRequest;
import com.aliyun.gts.gcai.platform.sourcing.common.input.query.SourcingApplyQuery;
import com.aliyun.gts.gcai.platform.sourcing.common.type.ApplyStatusType;
import com.aliyun.gts.gcai.platform.sourcing.common.type.ApplyType;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rest.dto.CommonByIdQuery;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.manager.front.b2bcomm.controller.BaseRest;
import com.aliyun.gts.gmall.manager.front.b2bcomm.input.ByIdQueryRestReq;
import com.aliyun.gts.gmall.manager.front.b2bcomm.utils.ResponseUtils;
import com.aliyun.gts.gmall.manager.front.sourcing.convert.SourcingApplyConvert;
import com.aliyun.gts.gmall.manager.front.sourcing.input.ApproveReq;
import com.aliyun.gts.gmall.manager.front.sourcing.input.SourcingApplyListReq;
import com.aliyun.gts.gmall.manager.front.sourcing.input.SourcingApplyQueryReq;
import com.aliyun.gts.gmall.manager.front.sourcing.vo.SourcingApplyVo;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author haibin.xhb
 * @description: TODO 报名邀约接口
 * @date 2021/5/21 18:52
 */
@RestController
@RequestMapping(value = "/sourcingApply")
public class SourcingApplyController extends BaseRest {
    @Resource
    private SourcingApplyWriteFacade sourcingApplyWriteFacade;

    @Resource
    private SourcingApplyReadFacade sourcingApplyReadFacade;

    @Resource
    private SourcingApplyConvert convert;

    /**
     * 邀约列表
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/bookPage")
    public RestResponse<PageInfo<SourcingApplyVo>> bookPage(@RequestBody SourcingApplyQueryReq query) {
        query.setApplyType(ApplyType.BOOK.getValue());
        RpcResponse<PageInfo<SourcingApplyDTO>> response = sourcingApplyReadFacade.page(query.build());
        return ResponseUtils.convertVOPageResponse(response, convert::dto2Vo, false);
    }

    /**
     * 主动报名列表
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/applyPage")
    public RestResponse<PageInfo<SourcingApplyVo>> applyPage(@RequestBody SourcingApplyQueryReq query) {
        if(query.getStatus()!= null && query.getStatus() <= 0){
            query.setStatus(null);
        }
        query.setApplyType(ApplyType.APPLY.getValue());
        RpcResponse<PageInfo<SourcingApplyDTO>> response = sourcingApplyReadFacade.page(query.build());
        return ResponseUtils.convertVOPageResponse(response, convert::dto2Vo, false);
    }

    /**
     * 邀约列表
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/book")
    public RestResponse<Boolean> book(@RequestBody SourcingApplyListReq req) {
        try {
            List<SourcingApplyDTO> applyDTOS = req.getList();
            ParamUtil.nonNull(applyDTOS, I18NMessageUtils.getMessage("param.not.empty"));  //# "参数不为空"
            for (SourcingApplyDTO applyDTO : applyDTOS) {
                ParamUtil.nonNull(applyDTO.getSourcingId(), "sourcingId"+I18NMessageUtils.getMessage("not.empty"));  //# 不为空"
                ParamUtil.nonNull(applyDTO.getSupplierId(), "supplierId");
                applyDTO.setApplyType(ApplyType.BOOK.getValue());
            }
            RpcResponse<Boolean> response = sourcingApplyWriteFacade.book(CommandRequest.of(applyDTOS));
            return ResponseUtils.operateResult(response);
        } catch (Exception e) {
            return RestResponse.fail("1001", I18NMessageUtils.getMessage("invitation.fail")+"：" + e.getMessage());  //# "邀约失败
        }
    }

    /**
     * 邀约列表
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/deleteBook")
    public RestResponse<Boolean> deleteBook(@RequestBody ByIdQueryRestReq delete) {
        try {
            ParamUtil.nonNull(delete.getId(), "id"+I18NMessageUtils.getMessage("cannot.be.empty"));  //# 不能为空"
            RpcResponse<Boolean> response = sourcingApplyWriteFacade.delete(delete.convert());
            return ResponseUtils.operateResult(response);
        } catch (Exception e) {
            return RestResponse.fail("1001", I18NMessageUtils.getMessage("deletion.fail")+"：" + e.getMessage());  //# "删除失败
        }
    }

    /**
     * 审核
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/approve")
    public RestResponse<Boolean> approve(@RequestBody ApproveReq approveReq) {
        try {
            ParamUtil.nonNull(approveReq.getId(), "id"+I18NMessageUtils.getMessage("cannot.be.empty"));  //# 不能为空"
            StatusUpdateRequest request = new StatusUpdateRequest();
            request.setId(approveReq.getId());
            request.setRemark(approveReq.getRemark());
            if (approveReq.isPass()) {
                request.setStatus(ApplyStatusType.pass_approve.getValue());
            }
            if (approveReq.forbid()) {
                request.setStatus(ApplyStatusType.not_approve.getValue());
            }
            RpcResponse<Boolean> response = sourcingApplyWriteFacade.updateStatus(request);
            return ResponseUtils.operateResult(response);
        } catch (Exception e) {
            return RestResponse.fail("1001", I18NMessageUtils.getMessage("deletion.fail")+"：" + e.getMessage());  //# "删除失败
        }
    }
}
