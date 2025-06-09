package com.aliyun.gts.gmall.manager.front.sourcing.service.impl;

import com.aliyun.gts.gcai.platform.sourcing.api.dto.model.*;
import com.aliyun.gts.gcai.platform.sourcing.api.facade.BidChosingFacade;
import com.aliyun.gts.gcai.platform.sourcing.common.model.BaseDTO;
import com.aliyun.gts.gmall.framework.api.consts.CommonResponseCode;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.manager.biz.output.CustDTO;
import com.aliyun.gts.gmall.manager.front.b2bcomm.utils.ErrorCodeUtils;
import com.aliyun.gts.gmall.manager.front.common.exception.FrontCommonResponseCode;
import com.aliyun.gts.gmall.manager.front.common.util.UserHolder;
import com.aliyun.gts.gmall.manager.front.sourcing.facade.BidingPriceFacade;
import com.aliyun.gts.gmall.manager.front.sourcing.facade.PricingBillFacade;
import com.aliyun.gts.gmall.manager.front.sourcing.facade.QuotePriceFacade;
import com.aliyun.gts.gmall.manager.front.sourcing.facade.SourcingFacade;
import com.aliyun.gts.gmall.manager.front.sourcing.vo.PricingBillVo;
import com.aliyun.gts.gmall.manager.front.sourcing.vo.QuoteVo;
import com.aliyun.gts.gmall.manager.front.sourcing.vo.SourcingVo;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SourcingCheckOwnerService {

    @Autowired
    private SourcingFacade sourcingFacade;
    @Autowired
    private PricingBillFacade pricingBillFacade;
    @Autowired
    private BidingPriceFacade bidingPriceFacade;
    @Autowired
    private BidChosingFacade bidChosingFacade;
    @Autowired
    private QuotePriceFacade quotePriceFacade;

    public void checkSourcingOwner(SourcingVo vo) {
        CustDTO user = UserHolder.getUser();
        if (user == null) {
            throw new GmallException(CommonResponseCode.NotLogin);
        }
        if (vo != null && !Objects.equals(user.getCustId(), vo.getPurchaserId())) {
            throw new GmallException(FrontCommonResponseCode.DATA_NO_PERMISSION);
        }
    }

    public void checkSourcingOwner(SourcingDTO dto) {
        CustDTO user = UserHolder.getUser();
        if (user == null) {
            throw new GmallException(CommonResponseCode.NotLogin);
        }
        if (dto != null && !Objects.equals(user.getCustId(), dto.getPurchaserId())) {
            throw new GmallException(FrontCommonResponseCode.DATA_NO_PERMISSION);
        }
    }

    public void checkSourcingOwner(Long id) {
        CustDTO user = UserHolder.getUser();
        if (user == null) {
            throw new GmallException(CommonResponseCode.NotLogin);
        }
        SourcingVo vo = sourcingFacade.queryById(id, false);
        if (vo != null && !Objects.equals(user.getCustId(), vo.getPurchaserId())) {
            throw new GmallException(FrontCommonResponseCode.DATA_NO_PERMISSION);
        }
    }

    public void checkPricingOwner(PricingBillVo vo) {
        CustDTO user = UserHolder.getUser();
        if (user == null) {
            throw new GmallException(CommonResponseCode.NotLogin);
        }
        if (vo == null) {
            return;
        }
        if (vo.getBillInfo() != null && !Objects.equals(vo.getBillInfo().getPurchaserId(), user.getCustId())) {
            throw new GmallException(FrontCommonResponseCode.DATA_NO_PERMISSION);
        }
        if (vo.getSourcingInfo() != null && !Objects.equals(vo.getSourcingInfo().getPurchaserId(), user.getCustId())) {
            throw new GmallException(FrontCommonResponseCode.DATA_NO_PERMISSION);
        }
    }

    public void checkPricingOwner(Long id) {
        CustDTO user = UserHolder.getUser();
        if (user == null) {
            throw new GmallException(CommonResponseCode.NotLogin);
        }
        PricingBillDTO dto = pricingBillFacade.queryById(id);
        if (dto != null && !Objects.equals(dto.getPurchaserId(), user.getCustId())) {
            throw new GmallException(FrontCommonResponseCode.DATA_NO_PERMISSION);
        }
    }

    public void checkQuoteOwner(QuoteVo vo) {
        CustDTO user = UserHolder.getUser();
        if (user == null) {
            throw new GmallException(CommonResponseCode.NotLogin);
        }
        if (vo != null && !Objects.equals(vo.getPurchaserId(), user.getCustId())) {
            throw new GmallException(FrontCommonResponseCode.DATA_NO_PERMISSION);
        }
    }

    public void checkQuoteOwner(Long id) {
        CustDTO user = UserHolder.getUser();
        if (user == null) {
            throw new GmallException(CommonResponseCode.NotLogin);
        }
        QuoteDTO dto = quotePriceFacade.queryQuote(id);
        if (dto != null && !Objects.equals(dto.getPurchaserId(), user.getCustId())) {
            throw new GmallException(FrontCommonResponseCode.DATA_NO_PERMISSION);
        }
    }

    public void checkBidOwner(List<Long> ids) {
        CustDTO user = UserHolder.getUser();
        if (user == null) {
            throw new GmallException(CommonResponseCode.NotLogin);
        }
        List<BidingPriceDTO> list = bidingPriceFacade.queryByIds(ids);
        Set<Long> sourcingIds = list.stream()
                .map(BidingPriceDTO::getSourcingId).collect(Collectors.toSet());
        for (Long sid : sourcingIds) {
            checkSourcingOwner(sid);
        }
    }

    public void checkBidOwner(Long id) {
        checkBidOwner(Lists.newArrayList(id));
    }

    public void checkBidConfigOwner(Long id, Long sourcingId) {
        CustDTO user = UserHolder.getUser();
        if (user == null) {
            throw new GmallException(CommonResponseCode.NotLogin);
        }
        if (id != null) {
            BaseDTO b = new BaseDTO();
            b.setId(id);
            RpcResponse<BidChosingConfigDTO> resp = bidChosingFacade.getConfig(b);
            if (!resp.isSuccess()) {
                throw new GmallException(ErrorCodeUtils.mapCode(resp.getFail()));
            }
            BidChosingConfigDTO cf = resp.getData();
            if (cf != null && !Objects.equals(cf.getSourcingId(), sourcingId)) {
                checkSourcingOwner(cf.getSourcingId());
            }
        }
        checkSourcingOwner(sourcingId);
    }

    public void checkBidConfigOwner(BidChosingConfigDTO dto) {
        CustDTO user = UserHolder.getUser();
        if (user == null) {
            throw new GmallException(CommonResponseCode.NotLogin);
        }
        if (dto != null) {
            checkSourcingOwner(dto.getSourcingId());
        }
    }

    public void checkPurchaseOwner(Long id) {
        throw new GmallException(CommonResponseCode.IllegalArgument, "Not Support");
    }
}
