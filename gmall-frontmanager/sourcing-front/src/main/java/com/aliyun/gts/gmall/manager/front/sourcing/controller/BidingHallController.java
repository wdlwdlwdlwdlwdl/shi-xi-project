package com.aliyun.gts.gmall.manager.front.sourcing.controller;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.gts.gcai.platform.sourcing.api.dto.model.BidingPriceDTO;
import com.aliyun.gts.gcai.platform.sourcing.api.dto.model.SourcingApplyDTO;
import com.aliyun.gts.gcai.platform.sourcing.common.input.query.BidingPriceQuery;
import com.aliyun.gts.gcai.platform.sourcing.common.input.query.SourcingApplyQuery;
import com.aliyun.gts.gcai.platform.sourcing.common.type.ApplyStatusType;
import com.aliyun.gts.gcai.platform.sourcing.common.type.ApplyType;
import com.aliyun.gts.gcai.platform.sourcing.common.type.BidingPriceStatus;
import com.aliyun.gts.gcai.platform.sourcing.common.type.SourcingStatus;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.dto.PageParam;
import com.aliyun.gts.gmall.framework.api.rest.dto.CommonByIdQuery;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import com.aliyun.gts.gmall.manager.front.b2bcomm.utils.ConvertUtils;
import com.aliyun.gts.gmall.manager.front.b2bcomm.utils.ParameterUtils;
import com.aliyun.gts.gmall.manager.front.sourcing.convert.BidingPriceConvert;
import com.aliyun.gts.gmall.manager.front.sourcing.facade.BidingPriceFacade;
import com.aliyun.gts.gmall.manager.front.sourcing.facade.SourcingApplyFacade;
import com.aliyun.gts.gmall.manager.front.sourcing.facade.SourcingFacade;
import com.aliyun.gts.gmall.manager.front.sourcing.service.impl.SourcingCheckOwnerService;
import com.aliyun.gts.gmall.manager.front.sourcing.utils.BidingPriceBuildUtils;
import com.aliyun.gts.gmall.manager.front.sourcing.utils.SourcingBuildUtils;
import com.aliyun.gts.gmall.manager.front.sourcing.vo.BidingPriceVo;
import com.aliyun.gts.gmall.manager.front.sourcing.vo.SourcingVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/6/9 16:54
 */
@RestController
@RequestMapping(value = "/bidingHall")
@Slf4j
public class BidingHallController {
    @Resource
    private SourcingFacade sourcingFacade;
    @Resource
    private BidingPriceFacade bidingPriceFacade;
    @Resource
    private BidingPriceConvert convert;
    @Resource
    private SourcingApplyFacade applyFacade;
    @Autowired
    private SourcingCheckOwnerService sourcingCheckOwnerService;

    @RequestMapping(value = "/home")
    public RestResponse<Map<String, Object>> home(@RequestBody JSONObject query) {
        Long sourcingId = query.getLong("sourcingId");
        ParamUtil.nonNull(sourcingId, "sourcingId"+ I18NMessageUtils.getMessage("cannot.be.empty"));  //# 不能为空"
        SourcingVo vo = sourcingFacade.queryById(sourcingId, false);
        sourcingCheckOwnerService.checkSourcingOwner(vo);

        SourcingBuildUtils.buildViewStatus(vo);
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("sourcingInfo", vo);
        return RestResponse.okWithoutMsg(result);
    }

    /**
     * 竞价大厅首页
     *
     * @param param
     * @return
     */
    @RequestMapping(value = "/rank")
    public RestResponse<List<BidingPriceVo>> rank(@RequestBody JSONObject param) {
        Long sourcingId = param.getLong("sourcingId");
        ParamUtil.nonNull(sourcingId,"sourcingId"+I18NMessageUtils.getMessage("cannot.be.empty"));  //# 不能为空"
        sourcingCheckOwnerService.checkSourcingOwner(sourcingId);

        Boolean includeApply = param.getBoolean("includeApply");
        Boolean includeRange = param.getBoolean("includeRange");
        //当前报价数据
        List<BidingPriceDTO> list = bidingPriceFacade.priceRank(sourcingId,includeRange);
        List<BidingPriceVo> priceVoList = ConvertUtils.converts(list,convert::dto2Vo);
        //排序+排名
        BidingPriceBuildUtils.rank(priceVoList);
        //第一次报价+和当前报价之差
        if(Boolean.TRUE.equals(includeRange)) {
            BidingPriceBuildUtils.computeRange(priceVoList);
        }
        //排名是否包含+已经报名列表
        if(Boolean.TRUE.equals(includeApply)) {
            List<SourcingApplyDTO> applyList = applyList(sourcingId);
            priceVoList = BidingPriceBuildUtils.merge(priceVoList, applyList);
        }
        return RestResponse.okWithoutMsg(priceVoList);
    }

    private List<SourcingApplyDTO> applyList(Long sourcingId) {
        SourcingApplyQuery query = new SourcingApplyQuery();
        query.setApplyType(ApplyType.APPLY.getValue());
        query.setSourcingId(sourcingId);
        query.setStatus(ApplyStatusType.pass_approve.getValue());
        query.setPage(new PageParam(1, 20));
        PageInfo<SourcingApplyDTO> pageInfo = applyFacade.pageInfo(query);
        return pageInfo.getList();
    }

    /**
     * 竞价大厅首页
     *
     * @param query
     * @return
     */
    @RequestMapping(value = "/history")
    public RestResponse<PageInfo<BidingPriceVo>> history(@RequestBody BidingPriceQuery query) {
        ParamUtil.nonNull(query.getSourcingId(),"sourcingId"+I18NMessageUtils.getMessage("cannot.be.empty"));  //# 不能为空"
        sourcingCheckOwnerService.checkSourcingOwner(query.getSourcingId());

        query.setPage(new PageParam(1, 500));
        PageInfo<BidingPriceDTO> page = bidingPriceFacade.page(query);
        //当前报价
        PageInfo<BidingPriceVo> result = ConvertUtils.convertPage(page, convert::dto2Vo);
        return RestResponse.okWithoutMsg(result);
    }

    /**
     * 预授标
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/preAwardBid")
    public RestResponse<Boolean> preAwardBid(@RequestBody JSONObject param) {
        List<Long> bidPriceIds = ParameterUtils.getIds(param,"bidPriceIds");
        ParamUtil.expectTrue(!CollectionUtils.isEmpty(bidPriceIds), "bidPriceIds");
        sourcingCheckOwnerService.checkBidOwner(bidPriceIds);
        return bidingPriceFacade.preAwardBiding(bidPriceIds, BidingPriceStatus.pre_award.getValue());
    }

    /**
     * 流标
     * @param
     * @return
     */
    @RequestMapping(value = "/missAwardBid")
    public RestResponse<Boolean> missAwardBid(@RequestBody CommonByIdQuery query) {
        ParamUtil.nonNull(query.getId(), "id"+I18NMessageUtils.getMessage("cannot.be.empty"));  //# 不能为空"
        sourcingCheckOwnerService.checkSourcingOwner(query.getId());
        SourcingVo vo = new SourcingVo();
        vo.setId(query.getId());
        vo.setStatus(SourcingStatus.miscarriage.getValue());
        return sourcingFacade.updateStatus(vo);
    }
}
