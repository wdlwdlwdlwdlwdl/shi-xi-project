package com.aliyun.gts.gmall.manager.front.sourcing.controller;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gcai.platform.sourcing.api.dto.model.*;
import com.aliyun.gts.gcai.platform.sourcing.api.facade.*;
import com.aliyun.gts.gcai.platform.sourcing.common.input.CommandRequest;
import com.aliyun.gts.gcai.platform.sourcing.common.input.query.ChosingConfigQuery;
import com.aliyun.gts.gcai.platform.sourcing.common.input.query.CommonIdQuery;
import com.aliyun.gts.gcai.platform.sourcing.common.input.query.ExpertScoringQuery;
import com.aliyun.gts.gcai.platform.sourcing.common.model.BaseDTO;
import com.aliyun.gts.gcai.platform.sourcing.common.model.FileDO;
import com.aliyun.gts.gcai.platform.sourcing.common.model.ListParam;
import com.aliyun.gts.gcai.platform.sourcing.common.model.UserSignInDisplayDO;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rest.dto.CommonByIdQuery;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.manager.front.b2bcomm.constants.AccountTypeEnum;
import com.aliyun.gts.gmall.manager.front.b2bcomm.controller.BaseRest;
import com.aliyun.gts.gmall.manager.front.b2bcomm.input.ByIdQueryRestReq;
import com.aliyun.gts.gmall.manager.front.b2bcomm.input.ByStatusQueryRestReq;
import com.aliyun.gts.gmall.manager.front.b2bcomm.model.OperatorDO;
import com.aliyun.gts.gmall.manager.front.b2bcomm.utils.ErrorCodeUtils;
import com.aliyun.gts.gmall.manager.front.sourcing.convert.BidChosingConvert;
import com.aliyun.gts.gmall.manager.front.sourcing.service.impl.SourcingCheckOwnerService;
import com.aliyun.gts.gmall.manager.front.sourcing.utils.ChosingConfigUtils;
import com.aliyun.gts.gmall.manager.front.sourcing.vo.bc.*;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/bid_chosing")
public class BidChosingController extends BaseRest {

    @Autowired
    BidChosingConvert bidChosingConvert;

    @Autowired
    BidChosingFacade bidChosingFacade;

    @Resource
    QuotePriceReadFacade quotePriceReadFacade;

    @Autowired
    SourcingWriteFacade sourcingWriteFacade;

    @Autowired
    SourcingReadFacade sourcingReadFacade;

    @Autowired
    QuotePriceWriteFacade quotePriceWriteFacade;

    @Autowired
    private SourcingCheckOwnerService sourcingCheckOwnerService;


    @RequestMapping("/save_config")
    public RestResponse<Long> saveConfig(@RequestBody BidChosingConfigVO configVO){
        if(configVO.getUserGroup().stream().filter(u->u.getType() == 1).findFirst().get().getPeopleList().size() < 2){
            return RestResponse.fail("" , I18NMessageUtils.getMessage("experts.number.gt")+"2"+I18NMessageUtils.getMessage("people"));  //# "专家人数必须大于等于2人"
        }
        sourcingCheckOwnerService.checkBidConfigOwner(configVO.getSourcingId(), configVO.getSourcingId());
        BidChosingConfigDTO bidChosingConfigDTO = bidChosingConvert.convert(configVO);
        RpcResponse<Long> response = bidChosingFacade.saveConfig(bidChosingConfigDTO);
        if(response.isSuccess()){
            return RestResponse.ok(response.getData());
        }
        return RestResponse.fail(response.getFail().getCode() , response.getFail().getMessage());
    }

    @RequestMapping("/query_config")
    public RestResponse<PageInfo<BidChosingConfigRowVO>> queryConfig(@RequestBody ChosingConfigQuery query){
        ParamUtil.nonNull(query.getSourcingId(), "sourcingId"+I18NMessageUtils.getMessage("cannot.be.empty"));  //# 不能为空"
        sourcingCheckOwnerService.checkSourcingOwner(query.getSourcingId());
        RpcResponse<PageInfo<BidChosingConfigDTO>>  response = bidChosingFacade.queryConfig(query);
        if(response.isSuccess()){
            List<BidChosingConfigRowVO> list = response.getData().getList().stream().
                map(c->bidChosingConvert.convertRow(c,getLogin())).collect(Collectors.toList());
            PageInfo<BidChosingConfigRowVO> pageInfo = new PageInfo<BidChosingConfigRowVO>();
            pageInfo.setList(list);
            pageInfo.setTotal(response.getData().getTotal());
            return RestResponse.okWithoutMsg(pageInfo);
        }else{
            return RestResponse.fail(response.getFail().getCode() , response.getFail().getMessage());
        }
    }

    @RequestMapping("/query_readonly")
    public RestResponse<Map> queryReadonly(@RequestBody ByIdQueryRestReq query){
        sourcingCheckOwnerService.checkSourcingOwner(query.getId());
        BaseDTO baseDTO = new BaseDTO();
        baseDTO.setId(query.getId());
        RpcResponse<BidChosingConfigDTO>  response = bidChosingFacade.getConfigBySourcing(baseDTO);
        Map<String , Boolean> map = ChosingConfigUtils.buttons(response.getData() , getLogin());
        return RestResponse.okWithoutMsg(map);
    }



    @RequestMapping("/get_config_by_sourcing")
    public RestResponse<BidChosingConfigVO> getConfigBySourcing(@RequestBody ByIdQueryRestReq req){
        BaseDTO baseDTO = new BaseDTO();
        baseDTO.setId(req.getId());
        RpcResponse<BidChosingConfigDTO> response = bidChosingFacade.getConfigBySourcing(baseDTO);
        if (!response.isSuccess()) {
            throw new GmallException(ErrorCodeUtils.mapCode(response.getFail()));
        }
        sourcingCheckOwnerService.checkBidConfigOwner(response.getData());
        BidChosingConfigDTO configDTO = response.getData();
        BidChosingConfigVO configVO = bidChosingConvert.convert(configDTO);
        return RestResponse.okWithoutMsg(configVO);
    }

    @RequestMapping("/get_expert_scoring")
    public RestResponse<List<BidChosingDetailVO>> getExpertScoring(/**souringId**/@RequestBody ByIdQueryRestReq req){
        sourcingCheckOwnerService.checkSourcingOwner(req.getId());
        ExpertScoringQuery query = new ExpertScoringQuery();
        Long expertId = getLogin().getOperatorId();
        query.setExpertId(expertId);
        query.setSourcingId(req.getId());
        RpcResponse<List<BidChosingDetailDTO>> response = bidChosingFacade.queryExpertScoringDetails(query);
        RpcResponse<List<BidExpertOpinionDTO>> opinionResponse = bidChosingFacade.queryExpertOpinions(query);
        /**以没有标段概念来处理以下逻辑**/
        List<BidChosingDetailDTO> detailDTOS = response.getData();

        if(detailDTOS == null || detailDTOS.size() == 0){
            BaseDTO baseDTO = new BaseDTO();
            baseDTO.setId(req.getId());
            RpcResponse<BidChosingConfigDTO> configResponse = bidChosingFacade.getConfigBySourcing(baseDTO);
            CommonByIdQuery idQuery = new CommonByIdQuery();
            idQuery.setId(req.getId());
            RpcResponse<List<QuoteDTO>> quoteRespose = quotePriceReadFacade.queryBySourcing(idQuery);
            return RestResponse.okWithoutMsg(bidChosingConvert.newBCDetailVOS(configResponse.getData() , quoteRespose.getData()));
        }else{
            List<BidExpertOpinionDTO> opinions = opinionResponse.getData();
            return RestResponse.okWithoutMsg(bidChosingConvert.convertBCDetailVOS(detailDTOS , opinions));
        }
    }

    @RequestMapping("/expert_scoring")
    public RestResponse expertScoring(@RequestBody List<BidChosingDetailVO> list){
        for (BidChosingDetailVO vo : list) {
            if (CollectionUtils.isNotEmpty(vo.getDetailList())) {
                for (SectionDetailVO sub : vo.getDetailList()) {
                    sourcingCheckOwnerService.checkSourcingOwner(sub.getSourcingId());
                }
            }
        }

        OperatorDO operatorDO = getLogin();
        List<BidChosingDetailDTO> detailDTOS = bidChosingConvert.convert2DetailDTOs(list , operatorDO);
        List<BidExpertOpinionDTO> opinions = bidChosingConvert.convert2Opinions(list , operatorDO);
        ListParam<BidChosingDetailDTO> detailDTOListParam = new ListParam<>();
        detailDTOListParam.setList(detailDTOS);
        ListParam<BidExpertOpinionDTO> bidExpertChosingDTOListParam = new ListParam<>();
        bidExpertChosingDTOListParam.setList(opinions);
        RpcResponse response = bidChosingFacade.expertScoring(detailDTOListParam, bidExpertChosingDTOListParam);
        if(response.isSuccess()){
            return RestResponse.ok(null);
        }else{
            return RestResponse.fail(response.getFail().getCode() , response.getFail().getMessage());
        }
    }

    @RequestMapping("/query_summary")
    public RestResponse<List<BidChosingSummaryVO>> querySummary(@RequestBody ByIdQueryRestReq req ,
                                                                HttpServletResponse servletResponse){
        if(getLogin().getType().equals(AccountTypeEnum.EXPERT_ACCOUNT.getCode())){
            RestResponse result =  RestResponse.okWithoutMsg(null);
            Map<String, String> extra = new HashMap<>();
            extra.put("httpCode" , "302");
            extra.put("url" , String.format("/sourcing/home.html#/court?id=%s&type=1",req.getId()));
            result.setExtra(extra);
            result.setCode("302");
            return result;
        }
        sourcingCheckOwnerService.checkSourcingOwner(req.getId());
        ExpertScoringQuery query = new ExpertScoringQuery();
        query.setSourcingId(req.getId());
        query.setStatus(1);
        RpcResponse<List<BidChosingDetailDTO>> response = bidChosingFacade.queryExpertScoringDetails(query);
        List<BidChosingDetailDTO> list = response.getData();
        CommonByIdQuery idQuery = new CommonByIdQuery();
        idQuery.setId(req.getId());
        RpcResponse<List<QuoteDTO>> quoteRespose = quotePriceReadFacade.queryBySourcing(idQuery);
        List<QuoteDTO> quoteDTOList = quoteRespose.getData();
        List<BidChosingSummaryVO>  result = bidChosingConvert.convert2SummaryList(list,quoteDTOList);
        return RestResponse.okWithoutMsg(result);
    }

    @RequestMapping("/sign_in")
    public RestResponse signIn(@RequestBody ByIdQueryRestReq req){
        Long sourcingId = req.getId();
        sourcingCheckOwnerService.checkSourcingOwner(sourcingId);
        Long userId = getLogin().getOperatorId();
        UserSignInDTO signInDTO = new UserSignInDTO();
        signInDTO.setSourcingId(sourcingId);
        signInDTO.setUserId(userId);
        bidChosingFacade.signIn(signInDTO);
        return RestResponse.okWithoutMsg(null);
    }

    @RequestMapping("/query_sign_info")
    public RestResponse<List<UserSignInDisplayDO>> querySignInfo(@RequestBody ByIdQueryRestReq req){
        Long sourcingId = req.getId();
        sourcingCheckOwnerService.checkSourcingOwner(sourcingId);
        BaseDTO baseDTO = new BaseDTO();
        baseDTO.setId(sourcingId);
        RpcResponse<List<UserSignInDisplayDO>> response = bidChosingFacade.queryUserSignInfo(baseDTO);
        return RestResponse.okWithoutMsg(response.getData());
    }

    @RequestMapping("/upload_files")
    public RestResponse uploadChosingFiles(@RequestBody ChosingFileDTO fileDTO){
        sourcingCheckOwnerService.checkSourcingOwner(fileDTO.getSourcingId());
        fileDTO.setUserId(getLogin().getOperatorId());
        fileDTO.setUserName(getLogin().getUsername());
        RpcResponse response = sourcingWriteFacade.uploadChosingFiles(fileDTO);
        if(response.isSuccess()){
            return RestResponse.ok(null);
        }else{
            return RestResponse.fail(response.getFail().getCode() , response.getFail().getMessage());
        }
    }

    @RequestMapping("/get_files")
    public RestResponse<List<FileDO>> getChosingFiles(@RequestBody ByIdQueryRestReq req){
        CommonIdQuery query = new CommonIdQuery();
        query.setId(req.getId());
        query.setIncludeDetail(false);
        RpcResponse<SourcingDTO>  response = sourcingReadFacade.queryById(query);
        sourcingCheckOwnerService.checkSourcingOwner(response.getData());

        List<FileDO> files = response.getData().getFeature().getChosingFiles();

        if(response.isSuccess()){
            return RestResponse.okWithoutMsg(files);
        }else{
            return RestResponse.fail(response.getFail().getCode() , response.getFail().getMessage());
        }
    }

    /**
     * 选为候选或者撤销候选
     * @param req
     * @return
     */
    @RequestMapping("/choose_candidate")
    public RestResponse chosingCandidate(/**quoteId**/@RequestBody ByStatusQueryRestReq req){
        sourcingCheckOwnerService.checkQuoteOwner(req.getId());
        CommandRequest<QuoteDTO> request = new CommandRequest<QuoteDTO>();
        QuoteDTO quoteDTO = new QuoteDTO();
        quoteDTO.setId(req.getId());
        quoteDTO.setStatus(req.getStatus());
        request.setData(quoteDTO);
        RpcResponse<Boolean> response = quotePriceWriteFacade.updateStatus(request);
        if(response.isSuccess()){
            return RestResponse.ok(null);
        }else{
            return RestResponse.fail(response.getFail().getCode() , response.getFail().getMessage());
        }
    }

    @RequestMapping("/query_opinions")
    public RestResponse<List<BidExpertOpinionVO>> queryOpinions(/**sourcingId**/@RequestBody ByStatusQueryRestReq req){
        sourcingCheckOwnerService.checkSourcingOwner(req.getId());
        ExpertScoringQuery query = new ExpertScoringQuery();
        query.setSourcingId(req.getId());
        query.setStatus(1);
        RpcResponse<List<BidExpertOpinionDTO>> opinionResponse = bidChosingFacade.queryExpertOpinions(query);
        BaseDTO baseDTO = new BaseDTO();
        baseDTO.setId(req.getId());
        RpcResponse<BidChosingConfigDTO> configResponse = bidChosingFacade.getConfigBySourcing(baseDTO);
        if(opinionResponse.isSuccess()){
            List<BidExpertOpinionDTO> opinionDTOS = opinionResponse.getData();
            List<BidExpertOpinionVO> result = bidChosingConvert.convert(opinionDTOS,configResponse.getData());
            return RestResponse.okWithoutMsg(result);
        }else{
            return RestResponse.fail(opinionResponse.getFail().getCode() , opinionResponse.getFail().getMessage());
        }
    }

}
