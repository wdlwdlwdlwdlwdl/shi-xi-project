package com.aliyun.gts.gmall.manager.front.sourcing.controller;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gcai.platform.sourcing.api.dto.model.AwardQuoteDetailDTO;
import com.aliyun.gts.gcai.platform.sourcing.api.dto.model.PricingBillDTO;
import com.aliyun.gts.gcai.platform.sourcing.api.dto.model.QuoteDTO;
import com.aliyun.gts.gcai.platform.sourcing.api.dto.model.QuoteDetailDTO;
import com.aliyun.gts.gcai.platform.sourcing.api.facade.PricingBillWriteFacade;
import com.aliyun.gts.gcai.platform.sourcing.common.constant.QuoteDetailExtras;
import com.aliyun.gts.gcai.platform.sourcing.common.input.CommandRequest;
import com.aliyun.gts.gcai.platform.sourcing.common.input.StatusUpdateRequest;
import com.aliyun.gts.gcai.platform.sourcing.common.model.inner.QuoteDetailFeature;
import com.aliyun.gts.gcai.platform.sourcing.common.type.PricingBillStatus;
import com.aliyun.gts.gmall.center.misc.api.dto.input.flow.AppInfo;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.manager.front.b2bcomm.component.OAurlComponent;
import com.aliyun.gts.gmall.manager.front.b2bcomm.controller.BaseRest;
import com.aliyun.gts.gmall.manager.front.b2bcomm.flow.PurchaseFlowComponent;
import com.aliyun.gts.gmall.manager.front.b2bcomm.input.ByIdLoginRestReq;
import com.aliyun.gts.gmall.manager.front.b2bcomm.utils.ConvertUtils;
import com.aliyun.gts.gmall.manager.front.b2bcomm.utils.ErrorCodeUtils;
import com.aliyun.gts.gmall.manager.front.b2bcomm.utils.ResponseUtils;
import com.aliyun.gts.gmall.manager.front.common.exception.FrontCommonResponseCode;
import com.aliyun.gts.gmall.manager.front.sourcing.convert.PricingBillConvert;
import com.aliyun.gts.gmall.manager.front.sourcing.facade.PricingBillFacade;
import com.aliyun.gts.gmall.manager.front.sourcing.facade.QuotePriceFacade;
import com.aliyun.gts.gmall.manager.front.sourcing.facade.SourcingFacade;
import com.aliyun.gts.gmall.manager.front.sourcing.input.ApproveReq;
import com.aliyun.gts.gmall.manager.front.sourcing.input.PricingBillCreateReq;
import com.aliyun.gts.gmall.manager.front.sourcing.input.PricingBillQueryReq;
import com.aliyun.gts.gmall.manager.front.sourcing.input.PricingBillReq;
import com.aliyun.gts.gmall.manager.front.sourcing.service.PricingBillService;
import com.aliyun.gts.gmall.manager.front.sourcing.service.impl.SourcingCheckOwnerService;
import com.aliyun.gts.gmall.manager.front.sourcing.utils.PricingBillBuildUtils;
import com.aliyun.gts.gmall.manager.front.sourcing.vo.PricingBillListVo;
import com.aliyun.gts.gmall.manager.front.sourcing.vo.PricingBillVo;
import com.aliyun.gts.gmall.manager.front.sourcing.vo.PricingToOrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/5/19 19:58
 */
@RestController
@RequestMapping(value = "/pricing")
@Slf4j
public class PricingBillController extends BaseRest {
    @Resource
    private PricingBillService pricingBillService;
    @Resource
    private PricingBillConvert convert;
    @Resource
    private PricingBillFacade facade;
    @Resource
    private SourcingFacade sourcingFacade;
    @Resource
    private PricingBillWriteFacade billWriteFacade;
    @Autowired
    PurchaseFlowComponent purchaseFlowComponent;
    @Autowired
    OAurlComponent oAurlComponent;
    @Autowired
    private SourcingCheckOwnerService sourcingCheckOwnerService;
    @Autowired
    private QuotePriceFacade quotePriceFacade;

    @RequestMapping(value = "/detail")
    public RestResponse<PricingBillVo> detail(@RequestBody PricingBillReq req) {
        PricingBillVo pricingBillVo = null;
        if (req.getBillId() == null) {
            ParamUtil.nonNull(req.getQuoteIds(), I18NMessageUtils.getMessage("not.empty"));  //# "不为空"
            pricingBillVo = pricingBillService.queryBill(req.getSourcingId(), req.getQuoteIds());
        } else {
            pricingBillVo = pricingBillService.queryBillById(req.getBillId());
        }
        sourcingCheckOwnerService.checkPricingOwner(pricingBillVo);
        try {
            pricingBillService.fillToOrder(pricingBillVo, req);
        } catch (GmallException e) {
            // 弱依赖
            PricingToOrderVO fail = new PricingToOrderVO();
            fail.setFailInfo(ErrorCodeUtils.getFailInfo(e.getFrontendCare()));
            pricingBillVo.setToOrderInfo(fail);
        }
        return RestResponse.okWithoutMsg(pricingBillVo);
    }

    @RequestMapping(value = "/queryBillBySourcing")
    public RestResponse<PricingBillDTO> queryBillBySourcing(@RequestBody ByIdLoginRestReq req) {
        sourcingCheckOwnerService.checkSourcingOwner(req.getId());
        PricingBillDTO pricingBillVo = facade.queryBySourcingId(req.getId());
        if (pricingBillVo != null && !Objects.equals(pricingBillVo.getPurchaserId(), req.getCustId())) {
            throw new GmallException(FrontCommonResponseCode.DATA_NO_PERMISSION);
        }
        return RestResponse.okWithoutMsg(pricingBillVo);
    }

    @RequestMapping(value = "/createBill")
    public RestResponse<Boolean> createBill(@RequestBody PricingBillCreateReq req) {
        sourcingCheckOwnerService.checkSourcingOwner(req.getSourcingId());
        check(req);
        PricingBillDTO dto = convert.re2Dto(req);
        dto.setCreateId(getLogin().getOperatorId() + "");
        dto.setCreateName(getLogin().getUsername());
        dto.setPurchaserId(getLogin().getPurchaserId());

        RestResponse<Boolean> response = null;
        //更新
        if(req.getId() != null){
            response =  pricingBillService.updateBill(dto);
        }else{
            response = pricingBillService.createBillAndUpdate(dto);
        }
        if(response.isSuccess()){
            if(dto.getStatus().equals(PricingBillStatus.wait_approve.getValue())) {
                String context = buildOAurl(dto);
                purchaseFlowComponent.startFlow(dto.getId(), getLogin(), context, AppInfo.SOURCING_BID);
            }
        }
        return response;
    }

    @RequestMapping(value = "/deleteBill")
    public RestResponse<Boolean> deleteBill(@RequestBody ByIdLoginRestReq req) {
        sourcingCheckOwnerService.checkPricingOwner(req.getId());
        return facade.delete(req.convert());
    }

    @RequestMapping(value = "/billPage")
    public RestResponse<PageInfo<PricingBillListVo>> billPage(@RequestBody PricingBillQueryReq req) {
        if(req.getSourcingType() != null && req.getSourcingType() <= 0){
            req.setSourcingType(null);
        }
        if (req.getStatus() != null && req.getStatus() < 0) {
            req.setStatus(null);
        }
        PageInfo<PricingBillDTO> pageInfo = facade.page(req.build());
        PageInfo<PricingBillListVo> result = ConvertUtils.convertPage(pageInfo,convert::dto2Vo);
        fillAuditUrl(result.getList());
        return RestResponse.okWithoutMsg(result);
    }

    @RequestMapping(value = "/approve")
    public RestResponse<Boolean> approve(@RequestBody ApproveReq req) {
        sourcingCheckOwnerService.checkPricingOwner(req.getId());
        ParamUtil.nonNull(req.getApproveStatus(), I18NMessageUtils.getMessage("audit.status.not.empty"));  //# "审核状态不为空"
        StatusUpdateRequest dto = new StatusUpdateRequest();
        dto.setId(req.getId());
        if (req.getApproveStatus().equals(1)) {
            dto.setStatus(PricingBillStatus.pass_approve.getValue());
        } else {
            dto.setStatus(PricingBillStatus.not_approve.getValue());
        }
        return facade.approve(dto);
    }

    @RequestMapping(value = "/cancel")
    public RestResponse<Boolean> cancel(@RequestBody ByIdLoginRestReq req) {
        sourcingCheckOwnerService.checkPricingOwner(req.getId());
        PricingBillDTO billDTO = new PricingBillDTO();
        billDTO.setId(req.getId());
        billDTO.setStatus(PricingBillStatus.init.getValue());
        RpcResponse<Boolean> response = billWriteFacade.updateStatus(CommandRequest.of(billDTO));
//            if (Boolean.TRUE.equals(response.getData())) {
//                purchaseFlowComponent.cancelFlow(req.getId(), getLogin(), AppInfo.SOURCING_BID);
//            }
        return ResponseUtils.operateResult(response);
    }

    /**
     * 比价单校验
     * @param req
     */
    private void check(PricingBillCreateReq req){
        ParamUtil.notEmpty(req.getAwardQuote(), I18NMessageUtils.getMessage("select.award.qty"));  //# "请选择授标数量"
        Set<Long> quoteIds = req.getAwardQuote().stream()
                .map(AwardQuoteDetailDTO::getQuoteId).collect(Collectors.toSet());
        req.setQuoteIds(new ArrayList<>(quoteIds));

        // 校验授标数量 >= 报价数量
        Map<Long, QuoteDetailDTO> qdMap = new HashMap<>();
        for (Long quoteId : quoteIds) {
            QuoteDTO quote = quotePriceFacade.queryQuote(quoteId);
            quote.getDetails().stream().forEach(x -> qdMap.put(x.getId(), x));
        }
        for (AwardQuoteDetailDTO award : req.getAwardQuote()) {
            QuoteDetailDTO qd = qdMap.get(award.getQuoteDetailId());
            ParamUtil.nonNull(qd, I18NMessageUtils.getMessage("award.entry.not.exist") + award.getQuoteDetailId());  //# "授标条目不存在"
            ParamUtil.expectTrue(award.getAwardNum() != null
                    && award.getAwardNum() >= qd.getNum(), I18NMessageUtils.getMessage("award.qty.not.less"));  //# "授标数量不能小于报价数量"
            int maxLimit = Optional.ofNullable(qd.getQuoteFeature())
                    .map(QuoteDetailFeature::getExtend)
                    .map(ex -> ex.getInteger(QuoteDetailExtras.MAX_NUM_LIMIT))
                    .orElse(0);
            ParamUtil.expectTrue(maxLimit <= 0
                    || award.getAwardNum() <= maxLimit, I18NMessageUtils.getMessage("award.qty.gt"));  //# "授标数量超过报价最大数量"
        }
    }

    private List<PricingBillListVo> fillAuditUrl(List<PricingBillListVo> list) {
        if (CollectionUtils.isEmpty(list)) {
            return list;
        }
//        List<Long> ids = list.stream().map(PricingBillListVo::getId).collect(Collectors.toList());
//        Map<String, String> map = purchaseFlowComponent.queryInstanceId(AppInfo.SOURCING_BID, ids);
//        for (PricingBillListVo sourcingVo : list) {
//            if (PricingBillStatus.wait_approve.eq(sourcingVo.getStatus())) {
//                String url = purchaseFlowComponent.getAuditFlowUrl(getLogin().getToken(), map.get(sourcingVo.getId() + ""));
//                sourcingVo.setAuditUrl(url);
//            }
//        }
        return list;
    }

    private String buildOAurl(PricingBillDTO billDTO) {
        return oAurlComponent.buildPath("#/pricing?billId="+billDTO.getId());
    }
}
