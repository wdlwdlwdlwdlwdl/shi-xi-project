package com.aliyun.gts.gmall.manager.front.sourcing.controller;

import com.aliyun.gts.gcai.platform.sourcing.api.dto.model.PurchaseRequestDTO;
import com.aliyun.gts.gcai.platform.sourcing.api.dto.model.PurchaseRequestStatusDTO;
import com.aliyun.gts.gcai.platform.sourcing.api.facade.PurchaseRequestFacade;
import com.aliyun.gts.gcai.platform.sourcing.common.input.query.PurchaseRequestQuery;
import com.aliyun.gts.gcai.platform.sourcing.common.model.BaseDTO;
import com.aliyun.gts.gcai.platform.sourcing.common.type.PurchaseRequestStatus;
import com.aliyun.gts.gmall.center.misc.api.dto.input.flow.AppInfo;
import com.aliyun.gts.gmall.framework.api.consts.CommonResponseCode;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.manager.front.b2bcomm.component.OAurlComponent;
import com.aliyun.gts.gmall.manager.front.b2bcomm.controller.BaseRest;
import com.aliyun.gts.gmall.manager.front.b2bcomm.flow.PurchaseFlowComponent;
import com.aliyun.gts.gmall.manager.front.b2bcomm.input.ByIdQueryRestReq;
import com.aliyun.gts.gmall.manager.front.b2bcomm.output.CategoryVO;
import com.aliyun.gts.gmall.manager.front.b2bcomm.service.CategoryService;
import com.aliyun.gts.gmall.manager.front.sourcing.convert.PurchaseRequestConvert;
import com.aliyun.gts.gmall.manager.front.sourcing.input.pr.PurchaseRequestReq;
import com.aliyun.gts.gmall.manager.front.sourcing.service.impl.SourcingCheckOwnerService;
import com.aliyun.gts.gmall.manager.front.sourcing.vo.SourcingMaterialVo;
import com.aliyun.gts.gmall.manager.front.sourcing.vo.pr.PurchaseRequestBaseVO;
import com.aliyun.gts.gmall.manager.front.sourcing.vo.pr.PurchaseRequestDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping(value = "/purchase_request")
public class PurchaseRequestController extends BaseRest {
    @Autowired
    PurchaseRequestConvert purchaseRequestConvert;
    @Autowired
    PurchaseRequestFacade purchaseRequestFacade;
    @Autowired
    PurchaseFlowComponent purchaseFlowComponent;

    @Resource
    private CategoryService categoryService;

    @Autowired
    OAurlComponent oAurlComponent;

    @Autowired
    private SourcingCheckOwnerService sourcingCheckOwnerService;


    @RequestMapping("/save")
    public RestResponse createPurchaseRequest(@RequestBody PurchaseRequestDetailVO purchaseRequestVO){
        sourcingCheckOwnerService.checkPurchaseOwner(purchaseRequestVO.getId());
        PurchaseRequestDTO purchaseRequestDTO = purchaseRequestConvert.convert(purchaseRequestVO);
        purchaseRequestDTO.setOperatorId(getLogin().getOperatorId());
        purchaseRequestDTO.setOperatorName(getLogin().getUsername());

        RpcResponse<Long>  response = purchaseRequestFacade.savePurchaseRequest(purchaseRequestDTO);
        if(response.isSuccess()){
            purchaseFlowComponent.startFlow(response.getData() , getLogin() ,
                oAurlComponent.buildOAUrl(response.getData(), "#/prList?prDetail=id%253D") , AppInfo.SOURCING_PR);
            return RestResponse.okWithoutMsg(null);
        }else{
            return RestResponse.fail(response.getFail().getCode() , response.getFail().getMessage());
        }
    }

    @RequestMapping("/get")
    public RestResponse<PurchaseRequestDetailVO> getPurchaseRequest(@RequestBody ByIdQueryRestReq req){
        sourcingCheckOwnerService.checkPurchaseOwner(req.getId());
        BaseDTO baseDTO = new BaseDTO();
        baseDTO.setId(req.getId());
        RpcResponse<PurchaseRequestDTO>  response = purchaseRequestFacade.getPurchaseRequest(baseDTO);
        if(response.isSuccess()){
            PurchaseRequestDetailVO detailVO = purchaseRequestConvert.convert2Detail(response.getData());
            detailVO.getMaterialList().forEach(m->fillCategory(m));
            return RestResponse.okWithoutMsg(detailVO);
        }else{
            return RestResponse.fail(response.getFail().getCode() , response.getFail().getMessage());
        }
    }

    @RequestMapping("/query")
    public RestResponse<PageInfo<PurchaseRequestBaseVO>> queryPurchaseRequests(@RequestBody PurchaseRequestReq req){
        sourcingCheckOwnerService.checkPurchaseOwner(0L);

        PurchaseRequestQuery query = purchaseRequestConvert.convert(req);
        RpcResponse<PageInfo<PurchaseRequestDTO>>  response = purchaseRequestFacade.queryPurchaseRequests(query);
        if(response.isSuccess()){
            List<PurchaseRequestBaseVO> list = purchaseRequestConvert.convert2BaseList(response.getData().getList());
            PageInfo<PurchaseRequestBaseVO> result = new PageInfo<PurchaseRequestBaseVO>();
            result.setList(list);
            result.setTotal(response.getData().getTotal());
            return RestResponse.okWithoutMsg(result);
        }else{
            return RestResponse.fail(response.getFail().getCode() , response.getFail().getMessage());
        }
    }

    @RequestMapping("/cancel")
    public RestResponse cancel(@RequestBody ByIdQueryRestReq req){
        sourcingCheckOwnerService.checkPurchaseOwner(req.getId());

        PurchaseRequestStatusDTO purchaseRequestStatusDTO = new PurchaseRequestStatusDTO();
        purchaseRequestStatusDTO.setStatus(PurchaseRequestStatus.Draft);
        purchaseRequestStatusDTO.setId(req.getId());
        RpcResponse  response = purchaseRequestFacade.updateStatus(purchaseRequestStatusDTO);
        if(response.isSuccess()){
//            purchaseFlowComponent.cancelFlow(req.getId() , getLogin() ,  AppInfo.SOURCING_PR);
            return RestResponse.ok(null);
        }else{
            return RestResponse.fail(response.getFail().getCode() , response.getFail().getMessage());
        }
    }

    @RequestMapping("/delete")
    public RestResponse delete(@RequestBody ByIdQueryRestReq req){
        sourcingCheckOwnerService.checkPurchaseOwner(req.getId());

        BaseDTO baseDTO = new BaseDTO();
        baseDTO.setId(req.getId());
        RpcResponse  response = purchaseRequestFacade.deletePurchaseRequest(baseDTO);
        if(response.isSuccess()){
            return RestResponse.ok(null);
        }else{
            return RestResponse.fail(response.getFail().getCode() , response.getFail().getMessage());
        }
    }

    /**
     * 类目
     * @param materialVO
     * @return
     */
    private void fillCategory(SourcingMaterialVo materialVO) {
        if (materialVO.getCategoryId() == null) {
            return;
        }
        List<CategoryVO> list = categoryService.queryCategoryPathById(materialVO.getCategoryId());
        materialVO.setCategoryList(list);
    }

}
