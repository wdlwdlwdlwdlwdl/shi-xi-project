package com.aliyun.gts.gmall.manager.front.sourcing.controller;

import com.aliyun.gts.gmall.manager.front.b2bcomm.controller.BaseRest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 */
@RestController
@RequestMapping(value = "/contract_template")
public class ContractTemplateController extends BaseRest {
//
//    @Autowired
//    ContractTemplateConvert contractTemplateConvert;
//
//    @Autowired
//    ContractTemplateFacade contractTemplateFacade;
//
//    @RequestMapping("/create")
//    public RestResponse createTemplate(@RequestBody ContractTemplateVO templateVO){
//        OperatorDO operatorDO = getLogin();
//        ContractTemplateDTO templateDTO = contractTemplateConvert.convert(templateVO);
//        templateDTO.setUploaderName(operatorDO.getNickname());
//        templateDTO.setUploaderId(operatorDO.getOperatorId());
//        RpcResponse<Long> response = contractTemplateFacade.uploadTemplate(templateDTO);
//        if(response.isSuccess()){
//            return RestResponse.okWithoutMsg(null);
//        }else{
//            return RestResponse.fail(response.getFail().getCode() , response.getFail().getMessage());
//        }
//    }
//
//    @RequestMapping("/query")
//    public RestResponse<PageInfo<ContractTemplateVO>> query(@RequestBody ContractTemplateReq contractTemplateReq){
//
//        ContractTemplateQuery query = contractTemplateConvert.convert(contractTemplateReq);
//        RpcResponse<PageInfo<ContractTemplateDTO>> response = contractTemplateFacade.queryTemplate(query);
//        if(response.isSuccess()){
//            PageInfo<ContractTemplateDTO> result = response.getData();
//            List<ContractTemplateVO> list = result.getList().stream().map(t->{
//                return contractTemplateConvert.convert(t);
//            }).collect(Collectors.toList());
//            PageInfo<ContractTemplateVO> pageResult = new PageInfo<ContractTemplateVO>();
//            pageResult.setTotal(result.getTotal());
//            pageResult.setList(list);
//            return RestResponse.okWithoutMsg(pageResult);
//        }else{
//            return RestResponse.fail(response.getFail().getCode() , response.getFail().getMessage());
//        }
//    }
//
//    @RequestMapping("/delete")
//    public RestResponse delete(@RequestBody ByIdQueryRestReq req){
//        ByIdCommandRequest baseDTO = new ByIdCommandRequest();
//        baseDTO.setId(req.getId());
//        RpcResponse response = contractTemplateFacade.deleteTemplate(baseDTO);
//        if(response.isSuccess()){
//            return RestResponse.okWithoutMsg(null);
//        }else{
//            return RestResponse.fail(response.getFail().getCode() , response.getFail().getMessage());
//        }
//    }
//
//    @RequestMapping("/get")
//    public RestResponse<ContractTemplateVO> get(@RequestBody ByIdQueryRestReq req){
//        ByIdQueryRequest baseDTO = new ByIdQueryRequest();
//        baseDTO.setId(req.getId());
//        RpcResponse<ContractTemplateDTO>  response = contractTemplateFacade.getTemplate(baseDTO);
//        if(response.isSuccess()){
//            ContractTemplateVO contractTemplateVO = contractTemplateConvert.convert(response.getData());
//            return RestResponse.okWithoutMsg(contractTemplateVO);
//        }else{
//            return RestResponse.fail(response.getFail().getCode() , response.getFail().getMessage());
//        }
//    }
//
//    @RequestMapping("/getByEtemplateId")
//    public RestResponse<ContractTemplateVO> getByEtemplateId(@RequestBody SingleParamReq<String> req){
//        String eTemplateId = req.getParam();
//        ByIdQueryRequest baseDTO = new ByIdQueryRequest();
//        baseDTO.setId(Long.valueOf(eTemplateId));
//        RpcResponse<ContractTemplateDTO>  response = contractTemplateFacade.getTemplateByEtemplateId(baseDTO);
//        if(response.isSuccess()){
//            ContractTemplateVO contractTemplateVO = contractTemplateConvert.convert(response.getData());
//            return RestResponse.okWithoutMsg(contractTemplateVO);
//        }else{
//            return RestResponse.fail(response.getFail().getCode() , response.getFail().getMessage());
//        }
//    }
//
//    @RequestMapping("/preview")
//    public RestResponse<String> preview(@RequestBody ByIdQueryRestReq req){
//        ByIdQueryRequest baseDTO = new ByIdQueryRequest();
//        baseDTO.setId(req.getId());
//        RpcResponse<String>  response = contractTemplateFacade.previewTemplate(baseDTO);
//        if(response.isSuccess()){
//
//            return RestResponse.okWithoutMsg(response.getData());
//        }else{
//            return RestResponse.fail(response.getFail().getCode() , response.getFail().getMessage());
//        }
//    }
//
//    @RequestMapping("/update")
//    public RestResponse updateTemplate(@RequestBody ContractTemplateVO templateVO){
//        ContractTemplateUpdateDTO templateDTO = contractTemplateConvert.convert2Update(templateVO);
//        RpcResponse<Long> response = contractTemplateFacade.updateTemplate(templateDTO);
//        if(response.isSuccess()){
//            return RestResponse.okWithoutMsg(null);
//        }else{
//            return RestResponse.fail(response.getFail().getCode() , response.getFail().getMessage());
//        }
//    }
//
//    @RequestMapping("/sourcing")
//    public RestResponse<String> test(@RequestBody SourcingVo sourcingVo){
//        return RestResponse.ok("dsfdsfdsfdsfds");
//    }


}
