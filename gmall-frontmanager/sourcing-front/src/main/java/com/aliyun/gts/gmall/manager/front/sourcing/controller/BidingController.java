package com.aliyun.gts.gmall.manager.front.sourcing.controller;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gcai.platform.sourcing.api.dto.model.BidingPriceDTO;
import com.aliyun.gts.gcai.platform.sourcing.api.facade.BidingPriceWriteFacade;
import com.aliyun.gts.gcai.platform.sourcing.common.input.StatusUpdateRequest;
import com.aliyun.gts.gcai.platform.sourcing.common.input.query.BidingPriceQuery;
import com.aliyun.gts.gcai.platform.sourcing.common.type.SourcingType;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.manager.front.b2bcomm.utils.ConvertUtils;
import com.aliyun.gts.gmall.manager.front.b2bcomm.utils.DateUtils;
import com.aliyun.gts.gmall.manager.front.b2bcomm.utils.ResponseUtils;
import com.aliyun.gts.gmall.manager.front.sourcing.convert.BidingPriceConvert;
import com.aliyun.gts.gmall.manager.front.sourcing.facade.BidingPriceFacade;
import com.aliyun.gts.gmall.manager.front.sourcing.service.impl.SourcingCheckOwnerService;
import com.aliyun.gts.gmall.manager.front.sourcing.utils.BidingPriceBuildUtils;
import com.aliyun.gts.gmall.manager.front.sourcing.vo.BidingPriceVo;
import com.aliyun.gts.gmall.manager.front.sourcing.vo.SourcingVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/5/13 16:12
 */
@RestController
@RequestMapping(value = "/biding")
public class BidingController extends SourcingController {

    @Resource
    private BidingPriceFacade bidingPriceFacade;
    @Resource
    private BidingPriceWriteFacade priceWriteFacade;
    @Resource
    private BidingPriceConvert convert;
    @Autowired
    private SourcingCheckOwnerService sourcingCheckOwnerService;

    @Override
    public SourcingType getSourcingType() {
        return SourcingType.BidPrice;
    }

    @RequestMapping(value = "/bidingPriceList")
    public RestResponse<List<BidingPriceVo>> bidingPriceList(@RequestBody BidingPriceQuery query) {
        ParamUtil.nonNull(query.getSourcingId(), "sourcingId"+I18NMessageUtils.getMessage("not.empty"));  //# 不为空"
        sourcingCheckOwnerService.checkSourcingOwner(query.getSourcingId());
        List<BidingPriceDTO> response = bidingPriceFacade.priceRank(query.getSourcingId(), true);
        List<BidingPriceVo> priceVoList = ConvertUtils.converts(response, convert::dto2Vo);
        BidingPriceBuildUtils.computeRange(priceVoList);
        return RestResponse.okWithoutMsg(priceVoList);
    }

    /**
     * 授标
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/awardBiding")
    public RestResponse<Boolean> awardBiding(@RequestBody StatusUpdateRequest request) {
        ParamUtil.nonNull(request.getId(), "sourcingId"+I18NMessageUtils.getMessage("not.empty"));  //# 不为空"
        sourcingCheckOwnerService.checkBidOwner(request.getId());
        RpcResponse<Boolean> response = priceWriteFacade.awardBiding(request);
        return ResponseUtils.operateResult(response);
    }

    @Override
    protected void initParam(SourcingVo sourcingVo){
        ParamUtil.nonNull(sourcingVo.getStartTime(),I18NMessageUtils.getMessage("bidding.start.time.required"));  //# "竞价开始时间不能为空"
        ParamUtil.nonNull(sourcingVo.getFeature().getBidTime(),I18NMessageUtils.getMessage("bidding.duration.required"));  //# "竞价长不能为空"
        Date end = DateUtils.addMinute(sourcingVo.getStartTime(),sourcingVo.getFeature().getBidTime());
        sourcingVo.setEndTime(end);
        if(sourcingVo.getApplyStartTime() == null){
            sourcingVo.setApplyStartTime(new Date());
        }
    }
    @Override
    public void checkParam(SourcingVo sourcingVo) {
        ParamUtil.nonNull(sourcingVo.getApplyEndTime(),I18NMessageUtils.getMessage("enrollment.time.required"));  //# "报名时间不能为空"
        super.checkParam(sourcingVo);
    }
}
