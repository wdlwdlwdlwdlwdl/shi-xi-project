package com.aliyun.gts.gmall.manager.front.sourcing.service.impl;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gcai.platform.sourcing.api.dto.model.AwardQuoteDetailDTO;
import com.aliyun.gts.gcai.platform.sourcing.api.dto.model.PricingBillDTO;
import com.aliyun.gts.gcai.platform.sourcing.api.dto.model.QuoteDTO;
import com.aliyun.gts.gcai.platform.sourcing.common.constant.QuoteDetailExtras;
import com.aliyun.gts.gmall.center.trade.api.dto.input.ConfirmOrderSplitReq;
import com.aliyun.gts.gmall.center.trade.api.dto.output.ConfirmOrderSplitDTO;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.manager.front.b2bcomm.constants.B2bFrontResponseCode;
import com.aliyun.gts.gmall.manager.front.b2bcomm.utils.ConvertUtils;
import com.aliyun.gts.gmall.manager.front.b2bcomm.utils.ResponseUtils;
import com.aliyun.gts.gmall.manager.front.common.util.UserHolder;
import com.aliyun.gts.gmall.manager.front.sourcing.convert.QuoteConvert;
import com.aliyun.gts.gmall.manager.front.sourcing.convert.SourcingConvert;
import com.aliyun.gts.gmall.manager.front.sourcing.facade.PricingBillFacade;
import com.aliyun.gts.gmall.manager.front.sourcing.facade.QuotePriceFacade;
import com.aliyun.gts.gmall.manager.front.sourcing.facade.SourcingFacade;
import com.aliyun.gts.gmall.manager.front.sourcing.input.PricingBillReq;
import com.aliyun.gts.gmall.manager.front.sourcing.service.PricingBillService;
import com.aliyun.gts.gmall.manager.front.sourcing.service.SupplierService;
import com.aliyun.gts.gmall.manager.front.sourcing.utils.PricingBillBuildUtils;
import com.aliyun.gts.gmall.manager.front.sourcing.vo.*;
import com.aliyun.gts.gmall.manager.front.trade.adaptor.OrderAdapter;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.confirm.ConfirmItemInfo;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderChannelEnum;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.testng.collections.Lists;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/5/19 19:26
 */
@Service
public class PricingBillServiceImpl implements PricingBillService {
    @Resource
    private PricingBillFacade pricingBillFacade;

    @Resource
    private QuotePriceFacade quotePriceFacade;

    @Resource
    private SupplierService supplierService;

    @Resource
    private SourcingFacade sourcingFacade;

    @Resource
    private SourcingConvert sourcingConvert;

    @Resource
    private QuoteConvert quoteConvert;

    @Resource
    private OrderAdapter orderAdapter;

    @Override
    public PricingBillVo queryBillById(Long billId) {
        PricingBillDTO dto = pricingBillFacade.queryById(billId);
        PricingBillVo billVo = combineBill(dto.getSourcingId(),dto.getQuoteIds(), false);
        PricingBillBuildUtils.buildSelectQuote(dto,billVo);
        billVo.setBillInfo(dto);
        return billVo;
    }
    @Override
    public PricingBillVo queryBill(Long sourcingId, List<Long> quoteId) {
        PricingBillDTO dto = pricingBillFacade.queryBySourcingId(sourcingId);
        if (dto != null) {
            List<Long> ids = PricingBillBuildUtils.combine(dto.getQuoteIds(),quoteId);
            dto.setQuoteIds(ids);
            PricingBillVo billVo = combineBill(dto.getSourcingId(), ids, false);
            PricingBillBuildUtils.buildSelectQuote(dto,billVo);
            billVo.setBillInfo(dto);
            return billVo;
        }
        return combineBill(sourcingId, quoteId, true);
    }

    @NotNull
    private PricingBillVo combineBill(Long sourcingId, List<Long> quoteId,
                                      boolean fillDefaultAwardNum) {    // 填默认授标数量
        ParamUtil.expectTrue(!CollectionUtils.isEmpty(quoteId),I18NMessageUtils.getMessage("cannot.be.empty"));  //# "不能为空"
        PricingBillVo billVo = new PricingBillVo();
        //寻源信息
        SourcingVo sourcingVo = sourcingFacade.queryById(sourcingId,true);
        billVo.setSourcingInfo(sourcingVo);
        //报价信息每个商品的报价
        List<QuoteDTO> quoteDTOS = quotePriceFacade.list(quoteId);
        List<QuoteVo> quoteVos = ConvertUtils.converts(quoteDTOS,quoteConvert::dto2Vo);
        PricingBillBuildUtils.rank(quoteVos);
        List<PricingBillVo.MaterialQuoteVo> materialQuoteVos = new ArrayList<PricingBillVo.MaterialQuoteVo>();
        for(SourcingMaterialVo vo : sourcingVo.getMaterials()){
            PricingBillVo.MaterialQuoteVo singleQuote = new PricingBillVo.MaterialQuoteVo();
            //设置每个物料对应的报名
            List<QuoteDetailVo> list = PricingBillBuildUtils.filterQuote( vo, quoteVos, fillDefaultAwardNum);
            //排序
            singleQuote.setQuoteDetails(list);
            singleQuote.setMaterial(vo);
            materialQuoteVos.add(singleQuote);
        }
        billVo.setMaterialQuotes(materialQuoteVos);
        billVo.setQuoteVos(quoteVos);
        //查询供应商信息
        buildSupplierInfo(billVo, quoteVos);
        return billVo;
    }

    /**
     * 构建供应商信息
     * @param billVo
     * @param quoteDTOS
     */
    private void buildSupplierInfo(PricingBillVo billVo, List<QuoteVo> quoteDTOS) {
        List<Long> supplierId = quoteDTOS.stream().map(QuoteVo::getSupplierId).collect(Collectors.toList());
        Map<Long,SupplierVo> supplierVos = supplierService.queryWithDetail(supplierId);
        billVo.setSuppliers(supplierVos.values());
    }

    @Override
    public RestResponse<Boolean> createBillAndUpdate(PricingBillDTO billDTO) {
        buildSourcingInfo(billDTO);
        PricingBillDTO dto = pricingBillFacade.queryBySourcingId(billDTO.getSourcingId());
        if(dto != null){
            billDTO.setId(dto.getId());
            RpcResponse<Boolean> response = pricingBillFacade.updateBill(billDTO);
            return ResponseUtils.operateResult(response);
        }
        RpcResponse<Long> response = pricingBillFacade.createBill(billDTO);
        billDTO.setId(response.getData());
        return ResponseUtils.toBoolean(response);
    }

    @Override
    public RestResponse<Boolean> updateBill(PricingBillDTO billDTO) {
        PricingBillDTO dto = pricingBillFacade.queryBySourcingId(billDTO.getSourcingId());
        ParamUtil.nonNull(dto,I18NMessageUtils.getMessage("quote.sheet")+","+I18NMessageUtils.getMessage("not.exist"));  //# "比价单,不存在"
        buildSourcingInfo(billDTO);
        RpcResponse<Boolean> response = pricingBillFacade.updateBill(billDTO);
        return ResponseUtils.operateResult(response);
    }
    /**
     * 参数拼装
     * @param billDTO
     */
    private void buildSourcingInfo(PricingBillDTO billDTO){
        SourcingVo sourcingDTO = sourcingFacade.queryById(billDTO.getSourcingId(),false);
        billDTO.setSourcingName(sourcingDTO.getTitle());
        billDTO.setSourcingType(sourcingDTO.getSourcingType());
    }

    @Override
    public void fillToOrder(PricingBillVo bill, PricingBillReq req) {
        if (bill.getBillInfo() == null ||
                CollectionUtils.isEmpty(bill.getBillInfo().getAwardQuote())) {
            return;
        }

        // 报价
        Map<Long, QuoteDetailVo> qdMap = new HashMap<>();
        for (QuoteVo quote : bill.getQuoteVos()) {
            for (QuoteDetailVo detail : quote.getDetails()) {
                qdMap.put(detail.getId(), detail);
            }
        }

        List<ConfirmItemInfo> orderItems = new ArrayList<>();
        Set<Long> skuIdDup = new HashSet<>();

        // 授标数量
        for (AwardQuoteDetailDTO award : bill.getBillInfo().getAwardQuote()) {
            if (award.getPrimaryOrderId() != null) {
                continue;   // 已下单
            }
            Integer qty = award.getAwardNum();
            QuoteDetailVo qd = qdMap.get(award.getQuoteDetailId());
            if (qty == null || qd == null || qd.getQuoteFeature() == null) {
                continue;
            }
            Long skuId = QuoteDetailExtras.getSkuId(qd.getQuoteFeature().getExtend());
            Long itemId = QuoteDetailExtras.getItemId(qd.getQuoteFeature().getExtend());
            if (itemId == null || skuId == null) {
                continue;
            }
            // 只查询指定 quoteId 的
            if (req.getQuoteId() != null && !Objects.equals(req.getQuoteId(), award.getQuoteId())) {
                continue;
            }

            if (!skuIdDup.add(skuId)) {
                // 重复SKU无法下单, 相同sku不同价格等问题, 所以报价市直接不允许, 这里再校验一下
                throw new GmallException(B2bFrontResponseCode.DUPLICATED_SKU_ID, skuId);
            }
            ConfirmItemInfo item = new ConfirmItemInfo();
            item.setItemId(itemId);
            item.setSkuId(skuId);
            item.setItemQty(qty);
            orderItems.add(item);
        }
        if (orderItems.isEmpty()) {
            return;
        }

        // 拆分下单
        ConfirmOrderSplitReq chk = new ConfirmOrderSplitReq();
        chk.setOrderItems(orderItems);
        chk.setCustId(UserHolder.getUser().getCustId());
        chk.setOrderChannel(OrderChannelEnum.H5.getCode());
        Map<String, Object> extMap = new HashMap<>();
        extMap.put("pricingBillId", bill.getBillInfo().getId());
        chk.setParams(extMap);
        ConfirmOrderSplitDTO split = orderAdapter.checkSplit(chk);
        PricingToOrderVO result = quoteConvert.convert(split);
        bill.setToOrderInfo(result);
    }
}
