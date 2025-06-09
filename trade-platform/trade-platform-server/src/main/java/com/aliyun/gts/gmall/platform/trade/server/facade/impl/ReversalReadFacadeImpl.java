package com.aliyun.gts.gmall.platform.trade.server.facade.impl;

import com.alibaba.fastjson.JSON;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.convert.MultiLangConverter;
import com.aliyun.gts.gmall.framework.i18n.LangText;
import com.aliyun.gts.gmall.framework.processengine.WorkflowEngine;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.item.api.dto.output.item.SkuPropDTO;
import com.aliyun.gts.gmall.platform.trade.api.constant.ReversalErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.reversal.*;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.reversal.*;
import com.aliyun.gts.gmall.platform.trade.api.facade.reversal.ReversalReadFacade;
import com.aliyun.gts.gmall.platform.trade.core.bizcode.BizCodeEntity;
import com.aliyun.gts.gmall.platform.trade.core.config.WorkflowProperties;
import com.aliyun.gts.gmall.platform.trade.core.convertor.ReversalConverter;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.ReversalQueryService;
import com.aliyun.gts.gmall.platform.trade.core.util.RpcServerUtils;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.OrderItemFeatureDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.MainReversal;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.ReversalDetailOption;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.ReversalSearchQuery;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.ReversalSearchResult;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TReversalCheck;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class ReversalReadFacadeImpl implements ReversalReadFacade {

    @Autowired
    private WorkflowEngine workflowEngine;

    @Autowired
    private ReversalQueryService reversalQueryService;

    @Autowired
    private ReversalConverter reversalConverter;

    @Autowired
    private WorkflowProperties workflowProperties;

    @Autowired
    protected MultiLangConverter multiLangConverter;

    @Override
    public RpcResponse<ReversalCheckDTO> checkReversal(CheckReversalRpcReq req) {
        return RpcServerUtils.invoke(() -> {
            TReversalCheck check = new TReversalCheck();
            check.setReq(req);
            check.setDomain(new MainReversal());
            workflowEngine.invokeAndGetResult(workflowProperties.getReversalCheck(), check.toWorkflowContext());
            return check.getResponse();
        }, "ReversalReadFacadeImpl.checkOrderReversal");
    }

    @Override
    public RpcResponse<List<ReversalReasonDTO>> queryReversalReasons(GetReasonRpcReq req) {
        return RpcServerUtils.invoke(() -> {
            List<ReversalReasonDTO> reasons = reversalQueryService.queryReason(req.getReversalType());
            return RpcResponse.ok(reasons);
        }, "ReversalReadFacadeImpl.getReversalReasons");
    }
    //
    @Override
    public RpcResponse<PageInfo<MainReversalDTO>> queryReversalList(ReversalQueryRpcReq req) {
        return RpcServerUtils.invoke(() -> {
            ReversalSearchQuery query = reversalConverter.toReversalSearchQuery(req);
            query.setSellerBin(req.getSellerBIN());
            ReversalSearchResult result = reversalQueryService.searchReversal(query);
            if (req.isIncludeOrderInfo()) {
                reversalQueryService.fillOrderInfo(result.getList().stream().filter(Objects::nonNull).toList());
            }
            result.setList(result.getList().stream().filter(Objects::nonNull).toList());
            return RpcResponse.ok(reversalConverter.toMainReversalPages(result));
        }, "", BizCodeEntity.buildByReq(req));
    }

    @Override
    public RpcResponse<MainReversalDTO> queryReversalDetail(GetDetailRpcReq req) {
        return RpcServerUtils.invoke(() -> {
            ReversalDetailOption option = ReversalDetailOption
                .builder()
                .includeFlows(req.isIncludeFlows())
                .includeReason(req.isIncludeReason()).build();
            MainReversal reversal = reversalQueryService.queryReversal(req.getPrimaryReversalId(), option);
            if (reversal == null) {
                throw new GmallException(ReversalErrorCode.REVERSAL_NOT_EXIST);
            }
            if (req.isIncludeOrderInfo()) {
                reversalQueryService.fillOrderInfo(List.of(reversal));
            }
            MainReversalDTO dto = reversalConverter.toMainReversalDTO(reversal);
            List<SubOrder> subOrders= reversal.getMainOrder().getSubOrders();
            for(SubReversalDTO subReversalDTO :dto.getSubReversals()){
                Optional<OrderItemFeatureDO> itemFeatureOp = subOrders.stream()
                    .filter(p->p.getOrderId().equals(subReversalDTO.getOrderId()))
                    .map(SubOrder::getItemFeature)
                    .findAny();
                itemFeatureOp.ifPresent(orderItemFeatureDO -> subReversalDTO
                    .getOrderInfo()
                    .setCategoryName(orderItemFeatureDO.getCategoryName()));
                String title = subReversalDTO.getOrderInfo().getItemTitle();
                String skuDesc = subReversalDTO.getOrderInfo().getSkuDesc();
                subReversalDTO.getOrderInfo().setItemTitle(multiLangConverter.mText_to_str(multiLangConverter.to_multiLangText(title)));
                subReversalDTO.getOrderInfo().setSkuDesc(multiLangConverter.mText_to_str(multiLangConverter.to_multiLangText(skuDesc)));
            }
            return RpcResponse.ok(dto);
        }, "ReversalReadFacadeImpl.getReversalDetail");
    }

    public String convertSkuDesc(String skuDesc) {
        List<SkuPropDTO> list = JSON.parseArray(skuDesc, SkuPropDTO.class);
        StringBuilder sku = new StringBuilder();
        for(SkuPropDTO skuPropDTO:list){
            Optional<LangText> nameText = skuPropDTO.getName().getLangTextByLang(LocaleContextHolder.getLocale().getLanguage());
            String name = "";
            String value = "";
            if (nameText.isPresent()) {
                name = nameText.get().getValue();
            }
            Optional<LangText> valueText = skuPropDTO.getValue().getLangTextByLang(LocaleContextHolder.getLocale().getLanguage());
            if (nameText.isPresent()) {
                value = valueText.get().getValue();
            }
            sku.append(name).append(':').append(value).append(';');
        }
        if (sku.length() > 0) {
            sku.setLength(sku.length() - 1);
        }
        return sku.toString();
    }

    @Override
    public RpcResponse<List<MainReversalDTO>> queryRefundMerchant(ReversalRpcReq req) {
        return RpcServerUtils.invoke(() -> {
            return RpcResponse.ok(reversalQueryService.queryRefundMerchant(req));
        }, "OrderReadFacadeImpl.queryRefundMerchant");
    }

    @Override
    public RpcResponse<ReversalDTO> statisticsReversal(ReversalRpcReq req) {
        return RpcServerUtils.invoke(() -> {
            return RpcResponse.ok(reversalQueryService.statisticsReversal(req));
        }, "OrderReadFacadeImpl.statisticsReversal");
    }
}
