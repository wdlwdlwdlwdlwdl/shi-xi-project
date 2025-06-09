package com.aliyun.gts.gmall.manager.front.sourcing.controller;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;


import com.aliyun.gts.gcai.platform.contract.api.facade.ContractFacade;
import com.aliyun.gts.gcai.platform.contract.common.type.ContractStatus;
import com.aliyun.gts.gcai.platform.contract.common.type.SignStatus;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.manager.front.sourcing.service.ContractCompnent;
import com.aliyun.gts.gmall.manager.front.sourcing.convert.ContractConvert;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.command.EmptyCommand;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.CommonPageReq;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.IdQuery;
import com.aliyun.gts.gmall.manager.front.sourcing.vo.contract.ContractSignStatusVO;
import com.aliyun.gts.gmall.manager.front.sourcing.vo.contract.ContractVO;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 */
@RestController
public class ContractController {

/*    @Autowired
    ContractCompnent contractCompnent;
    @Autowired
    ContractConvert contractConvert;
    @Autowired
    B2BContractFacade b2BContractFacade;
    @Autowired
    ContractFacade contractFacade;


    @PostMapping(name = "queryContract", value = "/api/trade/contract/query")
    public RestResponse<PageInfo<ContractVO>> query(@RequestBody CommonPageReq contractReq){

        B2BContractQuery query = new B2BContractQuery();
        query.setBuyerId(contractReq.getCustId());
        query.setStatusArr(Lists.newArrayList(ContractStatus.Signing.getValue() , ContractStatus.Deal.getValue()));
        query.setPage(contractReq.getPage());
        RpcResponse<PageInfo<B2BContractDTO>> response = b2BContractFacade.queryContract(query);
        if(response.isSuccess()){
            PageInfo<B2BContractDTO> result = response.getData();
            List<ContractVO> list = result.getList().stream().map(t->{
                return contractConvert.convert(t);
            }).collect(Collectors.toList());
            PageInfo<ContractVO> pageResult = new PageInfo<ContractVO>();
            pageResult.setTotal(result.getTotal());
            pageResult.setList(list);
            return RestResponse.okWithoutMsg(pageResult);
        }else{
            return RestResponse.fail(response.getFail().getCode() , response.getFail().getMessage());
        }
    }


    @PostMapping(name = "signPage", value = "/api/trade/contract/signPage")
    public RestResponse signPage(@RequestBody IdQuery req){
        return contractCompnent.signPage(req.getId());
    }

    @PostMapping(name = "signStatus", value = "/api/trade/contract/signStatus")
    public RestResponse signStatus(@RequestBody ContractSignStatusVO signStatusVO){
        Long signStatus = signStatusVO.getSignStatus();
        if(SignStatus.PurchaseReceived.getValue().equals(signStatus) ||
            SignStatus.PurchaseSigned.getValue().equals(signStatus) ||
            SignStatus.PurchaseSended.getValue().equals(signStatus) ){
            return contractCompnent.signStatus(signStatusVO);
        }else{
            return RestResponse.fail("" , I18NMessageUtils.getMessage("signature.status.error"));  //# "签署状态错误"
        }
    }


    @RequestMapping(value = "/api/trade/contract/esignCallback")
    @ResponseBody
    public Map callBack(EmptyCommand useless , @RequestParam("id") Long contractId ,
        @RequestParam("sign") String sign , HttpServletResponse response) throws Exception {
        Map map = new HashMap();
        boolean success = contractCompnent.esignCallback(contractId , sign);
        if(success){
            map.put("code" , 0);
            response.sendRedirect("/login/index.html#/signSuccess");
        }else{
            map.put("code" , 500);
        }
        return map;
    }

    @RequestMapping("/api/trade/contract/save")
    public RestResponse saveContract(@RequestBody ContractVO contractVO) {
        return contractCompnent.createContract(contractVO);
    }*/
}
