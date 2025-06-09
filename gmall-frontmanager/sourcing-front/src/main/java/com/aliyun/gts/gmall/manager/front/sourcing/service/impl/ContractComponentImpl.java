package com.aliyun.gts.gmall.manager.front.sourcing.service.impl;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.alibaba.fastjson.JSON;

import com.aliyun.gts.gcai.platform.contract.api.dto.model.ContractDTO;
import com.aliyun.gts.gcai.platform.contract.api.dto.model.ContractSignStatusDTO;
import com.aliyun.gts.gcai.platform.contract.api.facade.ContractFacade;
import com.aliyun.gts.gcai.platform.contract.api.query.ContractSignPageQuery;
import com.aliyun.gts.gcai.platform.contract.common.model.inner.EsignInfo;
import com.aliyun.gts.gcai.platform.contract.common.type.BuyerJiaYiType;
import com.aliyun.gts.gcai.platform.contract.common.type.ContractStatus;
import com.aliyun.gts.gcai.platform.contract.common.type.SignStatus;
import com.aliyun.gts.gmall.framework.api.consts.CommonResponseCode;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.framework.api.rpc.dto.ByIdQueryRequest;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.manager.biz.output.CustDTO;
import com.aliyun.gts.gmall.manager.front.common.util.UserHolder;
import com.aliyun.gts.gmall.manager.front.customer.adaptor.CustomerAdapter;
import com.aliyun.gts.gmall.manager.front.customer.adaptor.SellerOperatorAdapter;
import com.aliyun.gts.gmall.manager.front.sourcing.service.ContractCompnent;
import com.aliyun.gts.gmall.manager.front.sourcing.convert.ContractConvert;
import com.aliyun.gts.gmall.manager.front.sourcing.vo.contract.ContractSignStatusVO;
import com.aliyun.gts.gmall.manager.front.sourcing.vo.contract.ContractVO;
import com.aliyun.gts.gmall.platform.user.api.dto.common.CustomerApplyExtendDTO;
import com.aliyun.gts.gmall.platform.user.api.dto.output.CustomerDTO;
import com.aliyun.gts.gmall.platform.user.api.dto.output.ESignInfoConfigDTO;
import com.aliyun.gts.gmall.platform.user.api.dto.output.SellerDTO;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class ContractComponentImpl implements ContractCompnent {

/*    @Autowired
    private ContractFacade contractFacade;
    @Autowired
    private ContractConvert contractConvert;
*//*    @Autowired
    private B2BContractFacade b2BContractFacade;*//*
    @Autowired
    private CustomerAdapter customerAdapter;
    @Autowired
    private SellerOperatorAdapter sellerOperatorAdapter;

    @Value("${esign.callback.url:}")
    String eSignCallBack;

    String salt = "df^dUDmdf#$-d132.@+=}sd1licm";

    @Override
    public RestResponse signStatus(ContractSignStatusVO signStatusVO) {

        SignStatus status = SignStatus.valueOf(signStatusVO.getSignStatus());
        ContractSignStatusDTO statusDTO = new ContractSignStatusDTO();
        statusDTO.setId(signStatusVO.getId());
        statusDTO.setSignStatus(status);
        RpcResponse response = contractFacade.changeSignStatus(statusDTO);
        if(!response.isSuccess()){
            return RestResponse.fail(response.getFail().getCode() , response.getFail().getMessage());
        }else{
            return RestResponse.okWithoutMsg(null);
        }
    }

    @Override
    public RestResponse<String> signPage(Long contractId) {

        ContractDTO contractDTO = getContract(contractId);
        Long eContractId = contractDTO.getFeature().getEContractId();
        String cellPhone = contractDTO.getFeature().getYiEsignInfo().getCellPhone();
        ContractSignPageQuery signPageQuery = new ContractSignPageQuery();
        signPageQuery.setEContractId(eContractId);
        signPageQuery.setCellPhone(cellPhone);
        signPageQuery.setCallBackUrl(eSignCallBack + contractDTO.getId());
        String signature = signature(contractId);
        String callback = String.format(eSignCallBack, contractDTO.getId() , signature);
        signPageQuery.setCallBackUrl(callback);
        RpcResponse<String>  pageResponse = contractFacade.signPage(signPageQuery);

        if(pageResponse.isSuccess()){
            return RestResponse.okWithoutMsg(pageResponse.getData());
        }else{
            return RestResponse.fail(pageResponse.getFail().getCode() , pageResponse.getFail().getMessage());
        }
    }

    @Override
    public boolean esignCallback(Long contractId, String sign) {

        String signature = signature(contractId);
        if(sign.equals(signature)){
            ContractDTO contractDTO = getContract(contractId);
            SignStatus status = SignStatus.PurchaseSigned;
            if( contractDTO.getSignStatus() == null ||  (contractDTO.getSignStatus() & status.getValue()) != status.getValue()){
                ContractSignStatusDTO statusDTO = new ContractSignStatusDTO();
                statusDTO.setId(contractId);
                statusDTO.setSignStatus(status);
                RpcResponse response = contractFacade.changeSignStatus(statusDTO);
                if(response.isSuccess()){
                    return true;
                }else {
                    return false;
                }
            }
        }

        return false;
    }

    private ContractDTO getContract(Long contractId){
        ByIdQueryRequest idQueryRequest = new ByIdQueryRequest();
        idQueryRequest.setId(contractId);

        RpcResponse<ContractDTO>  response = contractFacade.getContract(idQueryRequest);
        ContractDTO contractDTO = response.getData();
        return contractDTO;
    }

    private String signature(Long contractId){
        String md5 = "";
        String data = contractId + "@" + salt;
        try {
            md5 = DigestUtils.md5Hex(data.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return md5;
    }

    @Override
    public RestResponse createContract(ContractVO contractVO) {
        checkInput(contractVO);
        B2BContractDTO contractDTO = contractConvert.convert(contractVO);
//        contractDTO.setBuyerJiaYiType(BuyerJiaYiType.BUYER_IS_JIA);

        RestResponse fillResponse = fillUser(contractDTO, UserHolder.getUser());
        if(!fillResponse.isSuccess()){
            return fillResponse;
        }
        if(contractDTO.getTemplateId() != null){
            Map<String, String> avariables = new HashMap<>();
            avariables.put(I18NMessageUtils.getMessage("buyer.company.name") , contractDTO.getBuyerFeature().getCompanyName());  //# "买方企业名称"
            avariables.put(I18NMessageUtils.getMessage("seller.company.name") , contractDTO.getSellerFeature().getCompanyName());  //# "卖方企业名称"
            contractDTO.setTemplateVariables(avariables);
        }
        if (ContractStatus.Approving.getValue().equals(contractVO.getStatus())) {
            contractDTO.setStatus(ContractStatus.Signing.getValue());
        }
        RpcResponse<Long> response = b2BContractFacade.saveContract(contractDTO);
        if (!response.isSuccess()) {
            return RestResponse.fail(response.getFail().getCode(), response.getFail().getMessage());
        }
        //电子合同先要发起后,买家才能签约
        if (contractDTO.getFeature().getSignType().equals(1)
                && contractDTO.getStatus().equals(ContractStatus.Signing.getValue())) {
            RpcResponse sendResponse = contractFacade.sendContract(ByIdQueryRequest.of(response.getData()));
            if (!sendResponse.isSuccess()) {
                if (contractVO.getId() == null) {
                    //第一次创建时,如果就送合同失败后,物理删除自身系统里的合同,以便再次保存
                    contractFacade.delete(ByIdQueryRequest.of(response.getData()));
                }
                return RestResponse.fail(sendResponse.getFail().getCode(), sendResponse.getFail().getMessage());
            }
        }
        return RestResponse.ok(null);
    }

    private RestResponse fillUser(B2BContractDTO contractDTO , CustDTO cust){
        if (cust == null) {
            throw new GmallException(CommonResponseCode.NotLogin);
        }
        Map<String, String> custExtend = customerAdapter.queryExtend(cust.getCustId(), CustomerApplyExtendDTO.EXTEND_TYPE);
        String applyInfo = Optional.ofNullable(custExtend).map(m -> m.get(CustomerApplyExtendDTO.EXTEND_KEY)).orElse(null);
        if (StringUtils.isBlank(applyInfo)) {
            return RestResponse.fail("" , I18NMessageUtils.getMessage("buyer.no.company.info"));  //# "买方没有登记公司信息"
        }
        CustomerApplyExtendDTO custApply = JSON.parseObject(applyInfo, CustomerApplyExtendDTO.class);
        ESignInfoConfigDTO sellerEsign = sellerOperatorAdapter.queryESign(contractDTO.getSellerId());
        CustomerDTO customer = customerAdapter.queryById(cust.getCustId());
        SellerDTO seller = sellerOperatorAdapter.querySellerById(contractDTO.getSellerId());

        ContractUserFeature sellerFeature = new ContractUserFeature();
        sellerFeature.setCompanyName(sellerEsign.getCompany());
        contractDTO.setSellerFeature(sellerFeature);
        ContractUserFeature custFeature = new ContractUserFeature();
        custFeature.setCompanyName(custApply.getCompanyName());
        contractDTO.setBuyerFeature(custFeature);
        contractDTO.setBuyerId(cust.getCustId());
        contractDTO.setBuyerName(customer.getUsername());
        contractDTO.setSellerId(seller.getId());
        contractDTO.setSellerName(seller.getUsername());

        if(contractDTO.getFeature().getMediumType() == 1 || contractDTO.getFeature().getSignType() == 1){
            EsignInfo jiaInfo = new EsignInfo();
            jiaInfo.setCompany(custApply.getCompanyName());
            jiaInfo.setReceiver(custApply.getContactName());
            jiaInfo.setCellPhone(custApply.getPhone());

            EsignInfo yiInfo = new EsignInfo();
            yiInfo.setCompany(sellerEsign.getCompany());
            yiInfo.setReceiver(sellerEsign.getReceiver());
            yiInfo.setCellPhone(sellerEsign.getPhone());

            contractDTO.getFeature().setJiaEsignInfo(jiaInfo);
            contractDTO.getFeature().setYiEsignInfo(yiInfo);
        }
        return RestResponse.ok(contractDTO);
    }

    private void checkInput(ContractVO contractVO) {
        ParamUtil.isNull(contractVO.getId(), I18NMessageUtils.getMessage("cannot.edit"));  //# "不能编辑"
        ParamUtil.nonNull(contractVO.getCode(), I18NMessageUtils.getMessage("contract.number.required"));  //# "合同号不能为空"
        ParamUtil.nonNull(contractVO.getName(), I18NMessageUtils.getMessage("contract.name.required"));  //# "合同名称不能为空"
        ParamUtil.nonNull(contractVO.getFeature(), I18NMessageUtils.getMessage("signature.method.required"));  //# "签署方式不能为空"
        ParamUtil.nonNull(contractVO.getFeature().getSignType(), I18NMessageUtils.getMessage("signature.method.required"));  //# "签署方式不能为空"
        ParamUtil.nonNull(contractVO.getFeature().getMediumType(), I18NMessageUtils.getMessage("contract.type.required"));  //# "合同类型不能为空"
        ParamUtil.nonNull(contractVO.getFeature().getSignOrder(), I18NMessageUtils.getMessage("signature.order.required"));  //# "签署顺序不能为空"

        ParamUtil.nonNull(contractVO.getValidTime(), I18NMessageUtils.getMessage("expected.effective.time.required"));  //# "预计生效时间不能为空"
        ParamUtil.expectTrue(contractVO.getValidTime().length == 2, I18NMessageUtils.getMessage("expected.effective.time.invalid"));  //# "预计生效时间不正确"
        ParamUtil.nonNull(contractVO.getValidTime()[0], I18NMessageUtils.getMessage("expected.effective.time.required"));  //# "预计生效时间不能为空"
        ParamUtil.nonNull(contractVO.getValidTime()[1], I18NMessageUtils.getMessage("expected.effective.time.required"));  //# "预计生效时间不能为空"
        contractVO.getFeature().setStartTime(contractVO.getValidTime()[0]);
        contractVO.getFeature().setEndTime(contractVO.getValidTime()[1]);

        if(contractVO.getFeature().getMediumType() == 2) {
            ParamUtil.nonNull(contractVO.getDocument(), I18NMessageUtils.getMessage("contract.document.required"));  //# "合同文档不能为空"
            contractVO.setTemplateId(null);
        }
    }*/
}
