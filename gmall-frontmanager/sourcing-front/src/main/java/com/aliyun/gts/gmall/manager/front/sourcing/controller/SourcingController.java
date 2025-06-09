package com.aliyun.gts.gmall.manager.front.sourcing.controller;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.gts.gcai.platform.sourcing.api.dto.model.QuoteDTO;
import com.aliyun.gts.gcai.platform.sourcing.common.model.inner.SourcingFeature;
import com.aliyun.gts.gcai.platform.sourcing.common.type.ApproveStatus;
import com.aliyun.gts.gcai.platform.sourcing.common.type.QuoteStatusType;
import com.aliyun.gts.gcai.platform.sourcing.common.type.SourcingStatus;
import com.aliyun.gts.gcai.platform.sourcing.common.type.SourcingType;
import com.aliyun.gts.gmall.center.misc.api.dto.input.flow.AppInfo;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.manager.front.b2bcomm.component.OAurlComponent;
import com.aliyun.gts.gmall.manager.front.b2bcomm.constants.B2bFrontResponseCode;
import com.aliyun.gts.gmall.manager.front.b2bcomm.controller.BaseRest;
import com.aliyun.gts.gmall.manager.front.b2bcomm.flow.PurchaseFlowComponent;
import com.aliyun.gts.gmall.manager.front.b2bcomm.input.ByIdLoginRestReq;
import com.aliyun.gts.gmall.manager.front.b2bcomm.output.CategoryVO;
import com.aliyun.gts.gmall.manager.front.b2bcomm.service.CategoryService;
import com.aliyun.gts.gmall.manager.front.b2bcomm.utils.ConvertUtils;
import com.aliyun.gts.gmall.manager.front.common.util.UserHolder;
import com.aliyun.gts.gmall.manager.front.customer.adaptor.CustomerAdapter;
import com.aliyun.gts.gmall.manager.front.sourcing.constants.SourcingConstants;
import com.aliyun.gts.gmall.manager.front.sourcing.facade.QuotePriceFacade;
import com.aliyun.gts.gmall.manager.front.sourcing.facade.SourcingFacade;
import com.aliyun.gts.gmall.manager.front.sourcing.input.ApproveReq;
import com.aliyun.gts.gmall.manager.front.sourcing.input.SourcingQueryReq;
import com.aliyun.gts.gmall.manager.front.sourcing.service.ItemSourcingService;
import com.aliyun.gts.gmall.manager.front.sourcing.service.impl.SourcingCheckOwnerService;
import com.aliyun.gts.gmall.manager.front.sourcing.utils.SourcingBuildUtils;
import com.aliyun.gts.gmall.manager.front.sourcing.utils.SourcingCheckUtils;
import com.aliyun.gts.gmall.manager.front.sourcing.vo.SourcingMaterialVo;
import com.aliyun.gts.gmall.manager.front.sourcing.vo.SourcingVo;
import com.aliyun.gts.gmall.platform.user.api.dto.common.CustomerApplyExtendDTO;
import com.aliyun.gts.gmall.platform.user.api.dto.contants.CustomerStatusEnum;
import com.aliyun.gts.gmall.platform.user.api.dto.output.CustomerDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.K;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/5/13 16:12
 */
@Slf4j
public abstract class SourcingController extends BaseRest {

    @Resource
    private SourcingFacade sourcingFacade;

    @Resource
    private CategoryService categoryService;

    @Autowired
    PurchaseFlowComponent purchaseFlowComponent;

    @Autowired
    OAurlComponent oAurlComponent;

    @Autowired
    QuotePriceFacade quotePriceFacade;

    @Autowired
    private ItemSourcingService itemSourcingService;

    @Autowired
    private SourcingCheckOwnerService sourcingCheckOwnerService;

    @Autowired
    private CustomerAdapter customerAdapter;


    @RequestMapping(value = "/create")
    public RestResponse<Boolean> create(@RequestBody SourcingVo sourcingVo) {
        checkB2bUser();
        sourcingVo.setOperatorId(getLogin().getOperatorId());
        sourcingVo.setPurchaserName(getLogin().getUsername());
        sourcingVo.setPurchaserId(getLogin().getPurchaserId());
        fillCompanyParam(sourcingVo);
        //参数校验
        initParam(sourcingVo);
        checkParam(sourcingVo);
        sourcingVo.setSourcingType(getSourcingType().getValue());

        if (sourcingVo.getId() != null) {
            RestResponse<Boolean> updateResponse = sourcingFacade.update(sourcingVo);
            if (updateResponse.isSuccess()) {
                purchaseFlowComponent.startFlow(sourcingVo.getId(), getLogin(), buildOAurl(sourcingVo), AppInfo.SOURCING_BR);
            }
            return updateResponse;
        }
        sourcingVo.setCreateId(getLogin().getOperatorId() + "");
        sourcingVo.setCreateName(getLogin().getUsername() + "");
        RestResponse<Boolean> createResponse = sourcingFacade.create(sourcingVo);
        if (createResponse.isSuccess()) {
            purchaseFlowComponent.startFlow(sourcingVo.getId(), getLogin(), buildOAurl(sourcingVo), AppInfo.SOURCING_BR);
        }
        return createResponse;
    }

    private void fillCompanyParam(SourcingVo sourcingVo) {
        if (sourcingVo.getFeature() == null) {
            sourcingVo.setFeature(new SourcingFeature());
        }
        Map<String, String> extras = sourcingVo.getFeature().extras();
        extras.remove(SourcingConstants.SOURCING_EXT_KEY_COMPANY);

        Long custId = UserHolder.getUser().getCustId();
        Map<String, String> map = customerAdapter.queryExtend(custId, CustomerApplyExtendDTO.EXTEND_TYPE);
        if (map == null) {
            return;
        }
        String applyInfo = map.get(CustomerApplyExtendDTO.EXTEND_KEY);
        if (StringUtils.isBlank(applyInfo)) {
            return;
        }
        JSONObject applyJson = JSON.parseObject(applyInfo);
        String companyName = applyJson.getString("companyName");
        extras.put(SourcingConstants.SOURCING_EXT_KEY_COMPANY, companyName);
    }

    private void checkB2bUser() {
        Long custId = UserHolder.getUser().getCustId();
        CustomerDTO customer = customerAdapter.queryById(custId);
        if (customer == null || !customer.isB2b()
                || !CustomerStatusEnum.NORMAL.getCode().toString().equals(customer.getStatus())) {
            throw new GmallException(B2bFrontResponseCode.NOT_B2B_CUST);
        }
    }

    @RequestMapping(value = "/page")
    public RestResponse<PageInfo<SourcingVo>> page(@RequestBody SourcingQueryReq query) {
        query.setSourcingType(getSourcingType().getValue());
        if (query.getStatus() != null && query.getStatus() < 0) {
            query.setStatus(null);
        }
        RestResponse<PageInfo<SourcingVo>> response = sourcingFacade.page(query.build());
        if (response.isSuccess() && response.getData() != null) {
            sourcingFacade.fillDetail(response.getData().getList());
        }
        //设置viewStatus
        ConvertUtils.convertPage(response.getData(), SourcingBuildUtils::buildViewStatus);
        fillAuditUrl(response.getData().getList());
        return response;
    }

    @RequestMapping(value = "/detail")
    public RestResponse<SourcingVo> detail(@RequestBody ByIdLoginRestReq query) {
        SourcingVo vo = sourcingFacade.queryById(query.getId(), true);
        sourcingCheckOwnerService.checkSourcingOwner(vo);

        //填充类目信息
        ConvertUtils.converts(vo.getMaterials(), this::fillCategory);
        return RestResponse.okWithoutMsg(vo);
    }

    @RequestMapping(value = "/fromItem")
    public RestResponse<SourcingVo> fromItem(@RequestBody ByIdLoginRestReq query) {
        checkB2bUser();
        SourcingVo s = itemSourcingService.fromItem(query.getId());
        return RestResponse.okWithoutMsg(s);
    }

    @RequestMapping(value = "/cancel")
    public RestResponse<Boolean> cancel(@RequestBody ApproveReq req) {
        sourcingCheckOwnerService.checkSourcingOwner(req.getId());
        SourcingVo vo = new SourcingVo();
        vo.setId(req.getId());
        vo.setApproveStatus(ApproveStatus.CANCEL.getValue());
        vo.setStatus(SourcingStatus.draft.getValue());
        RestResponse<Boolean> updateResponse = sourcingFacade.updateStatus(vo);
//        if (updateResponse.isSuccess()) {
//            purchaseFlowComponent.cancelFlow(req.getId(), getLogin(), AppInfo.SOURCING_BR);
//        }
        return updateResponse;
    }

    @RequestMapping(value = "/stop")
    public RestResponse<Boolean> stop(@RequestBody ByIdLoginRestReq query) {
        sourcingCheckOwnerService.checkSourcingOwner(query.getId());
        return sourcingFacade.stop(query.convert());
    }
    @RequestMapping(value = "/approve")
    public RestResponse<Boolean> approve(@RequestBody ApproveReq req) {
        sourcingCheckOwnerService.checkSourcingOwner(req.getId());
        ParamUtil.nonNull(req.getApproveStatus(), I18NMessageUtils.getMessage("audit.status.not.empty"));  //# "审核状态不为空"
        SourcingVo vo = new SourcingVo();
        vo.setId(req.getId());
        if (req.isPass()) {
            vo.setStatus(SourcingStatus.pass_approve.getValue());
            vo.setApproveStatus(ApproveStatus.PASS_APPROVED.getValue());
        } else {
            vo.setStatus(SourcingStatus.forbid_approve.getValue());
            vo.setApproveStatus(ApproveStatus.FORBID_APPROVED.getValue());
        }
        return sourcingFacade.updateStatus(vo);
    }

    /**
     * admin管理页面
     *
     * @param query
     * @return
     */
    @RequestMapping(value = "/admin")
    public RestResponse<Map<String, Object>> admin(@RequestBody ByIdLoginRestReq query) {
        sourcingCheckOwnerService.checkSourcingOwner(query.getId());
        SourcingVo sourcingVo = sourcingFacade.queryById(query.getId(), false);
        Map<String, Object> result = new HashMap<>();
        result.put("sourcingInfo", sourcingVo);
        result.put("stepVo", SourcingBuildUtils.buildAdminStep(sourcingVo));
        return RestResponse.okWithoutMsg(result);
    }

    @RequestMapping(value = "/declare")
    public RestResponse declare(@RequestBody  ByIdLoginRestReq query){
        sourcingCheckOwnerService.checkSourcingOwner(query.getId());
        SourcingVo vo = new SourcingVo();
        vo.setId(query.getId());
        vo.setStatus(SourcingStatus.declare.getValue());
        RestResponse<Boolean> updateResponse = sourcingFacade.updateStatus(vo);

        List<QuoteDTO> quoteList = quotePriceFacade.list(query.getId());
        quoteList.forEach(q->{
            if(!q.getStatus().equals(QuoteStatusType.award.getValue())){
                q.setStatus(QuoteStatusType.not_award.getValue());
                quotePriceFacade.updateQuoteStatus(q);
            }
        });

        RestResponse response = RestResponse.ok(null);
        response.setMessage(I18NMessageUtils.getMessage("publish.success"));  //# "公示成功"
        return response;
    }

    public abstract SourcingType getSourcingType();

    protected void initParam(SourcingVo sourcingVo) {
    }

    public void checkParam(SourcingVo sourcingVo) {
        SourcingCheckUtils.baseCheck(sourcingVo);
    }

    /**
     * 类目
     *
     * @param materialVO
     * @return
     */
    private SourcingMaterialVo fillCategory(SourcingMaterialVo materialVO) {
        if (materialVO.getCategoryId() == null) {
            return materialVO;
        }
        List<CategoryVO> list = categoryService.queryCategoryPathById(materialVO.getCategoryId());
        materialVO.setCategoryList(list);
        return materialVO;
    }

    private String buildOAurl(SourcingVo sourcingVo) {
        if (getSourcingType().equals(SourcingType.Zhao)) {
            return oAurlComponent.buildOAUrl(sourcingVo.getId(), "#/tenderingList?tenderDetail=sourcingId%253D");
        }
        if (getSourcingType().equals(SourcingType.BidPrice)) {
            return oAurlComponent.buildOAUrl(sourcingVo.getId(), "#/bidingList?bidingDetail=sourcingId%253D");
        }
        if (getSourcingType().equals(SourcingType.InquiryPrice)) {
            return oAurlComponent.buildOAUrl(sourcingVo.getId(), "#/inquiryList?inquiryDetail=sourcingId%253D");
        }
        return "todo "+I18NMessageUtils.getMessage("screenshot");  //# 截图"
    }

    /**
     * 审核地址
     */
    private void fillAuditUrl(List<SourcingVo> list) {
//        if (CollectionUtils.isEmpty(list)) {
//            return;
//        }
//        List<Long> ids = list.stream().map(SourcingVo::getId).collect(Collectors.toList());
//        Map<String, String> map = purchaseFlowComponent.queryInstanceId(AppInfo.SOURCING_BR, ids);
//        for (SourcingVo sourcingVo : list) {
//            if (SourcingStatus.submit_approve.eq(sourcingVo.getStatus())) {
//                String url = purchaseFlowComponent.getAuditFlowUrl(getLogin().getToken(), map.get(sourcingVo.getId() + ""));
//                sourcingVo.setAuditUrl(url);
//            }
//        }
    }
}