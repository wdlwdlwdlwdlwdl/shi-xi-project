package com.aliyun.gts.gmall.center.trade.server.flow.handler.orderConfirm;

import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.item.api.dto.output.item.LoanPeriodDTO;
import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.confirm.ConfirmOrderInfoRpcReq;
import com.aliyun.gts.gmall.platform.trade.common.constants.LoanTypeEnum;
import com.aliyun.gts.gmall.platform.trade.core.ability.order.create.OrderBizCodeAbility;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.ItemService;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderCreateService;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSku;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSkuId;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderConfirm;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import com.aliyun.gts.gmall.platform.trade.server.utils.BizCodeHandlerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class ConfirmOrderItemHandlerCenter extends AdapterHandler<TOrderConfirm> {

    @Autowired
    private OrderCreateService orderCreateService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private OrderBizCodeAbility orderBizCodeAbility;

    @Override
    public void handle(TOrderConfirm inbound) {
        ConfirmOrderInfoRpcReq req = inbound.getReq();
        CreatingOrder order = inbound.getDomain();
        order.setPayMode(req.getPayMode());
        order.setCartId(req.getCartId());
        // 查询商品信息
        Map<ItemSkuId, ItemSku> itemMap = queryItems(inbound);

        //如果是代客下单需要对代客下单价进行处理
//        if(CreatingOrderParamUtils.isHelpOrder(req.getParams())) {
//            Map<ItemSkuId, Long> item2HelpOrderPrice = req.getOrderItems().stream()
//                    .filter(item-> Objects.nonNull(item.getHelpOrderPrice())&&item.getHelpOrderPrice()>=0)
//                    .collect(Collectors.toMap(item -> new ItemSkuId(item.getItemId(), item.getSkuId()), ConfirmItemInfo::getHelpOrderPrice));
//
//            if(item2HelpOrderPrice.size()>0) {
//               for(Map.Entry<ItemSkuId, ItemSku> entry: itemMap.entrySet()) {
//                   ItemSkuId itemSkuId = entry.getKey();
//                   Long helpOrderPrice = item2HelpOrderPrice.get(itemSkuId);
//                   entry.getValue().getItemPrice().setHelpOrderPrice(helpOrderPrice);
//                   //将代客下单价写入order.params供创单时使用
//                   order.addParam(CreatingOrderParamConstants.HELP_ORDER_PREFIX+itemSkuId.getItemId()+"-"+itemSkuId.getSkuId(), helpOrderPrice);
//               }
//            }
//        }

        // 按卖家拆单
        List<MainOrder> mainOrders = orderCreateService.splitOrder(itemMap.values());

        order.setMainOrders(mainOrders);
        //根据ItemSku中的installment和loan分期取交集，只允许都有的分期才行
        order.setInstallment(itemMap.values().stream().map(ItemSku::getInstallment).filter(Objects::nonNull) .filter(set -> !set.isEmpty()) // 过滤掉空集合
            .reduce((a, b) -> {
                //用 retainAll 来保留两个集合的交集，并将结果传递给下一个迭代。
                List<Integer> aTemp = new ArrayList<>(a);
                List<Integer> bTemp = new ArrayList<>(b);
                aTemp.retainAll(bTemp);
                return aTemp;
            }).orElse(new ArrayList<Integer>())); // 如果没有元素或都为空，则返回一个空集合;
        List<LoanPeriodDTO> allPriceList = new ArrayList<>();
        mainOrders.forEach(e->
                e.getSubOrders().forEach(g->
                        allPriceList.addAll(g.getItemSku().getPriceList())
                )

        );
        List<LoanPeriodDTO> sumPriceList = new ArrayList<>();
        Long temLoanPrice=0L;
        for(Integer type: order.getInstallment() ){
            LoanPeriodDTO loanPeriodDTO = new LoanPeriodDTO();
            Long value = allPriceList.stream().filter(p-> Objects.equals(p.getType(), type)).mapToLong(LoanPeriodDTO::getValue).sum();
            if(type==3){
                temLoanPrice =value;
            }
            loanPeriodDTO.setType(type);
            loanPeriodDTO.setValue(value/type);
            sumPriceList.add(loanPeriodDTO);
        }
        order.setSumPriceList(sumPriceList);
        List<LoanPeriodDTO> sumLoanPriceList = new ArrayList<>();
        for(LoanTypeEnum loanTypeEnum : LoanTypeEnum.values()){
            LoanPeriodDTO loanPeriodDTO = new LoanPeriodDTO();
            loanPeriodDTO.setType(loanTypeEnum.getCode());
            loanPeriodDTO.setValue(temLoanPrice/loanTypeEnum.getCode());
            sumLoanPriceList.add(loanPeriodDTO);

        }
        order.setSumLoanPriceList(sumLoanPriceList);
        /*order.setLoan(itemMap.values().stream().map(ItemSku::getLoan).filter(Objects::nonNull) .filter(set -> !set.isEmpty()) // 过滤掉空集合
            .reduce((a, b) -> {
                //用 retainAll 来保留两个集合的交集，并将结果传递给下一个迭代。
                a.retainAll(b);
                return a;
            }).orElse(new ArrayList<Integer>())); // 如果没有元素或都为空，则返回一个空集合;*/

        // 商品业务身份
        BizCodeHandlerUtils.fillBizCode(inbound, orderBizCodeAbility::getBizCodesFromItem);
    }

    /**
     * 商品信息查询
     * @param inbound
     * @return
     */
    protected Map<ItemSkuId, ItemSku> queryItems(TOrderConfirm inbound) {
        ConfirmOrderInfoRpcReq req = inbound.getReq();
        List<ItemSkuId> idList = req.getOrderItems()
            .stream()
            .map(item -> new ItemSkuId(
                item.getItemId(),
                item.getSkuId(),
                item.getSellerId(),
                item.getSkuQuoteId(),
                item.getCityCode(),
                item.getDeliveryType())
            )
            .collect(Collectors.toList());
        Map<ItemSkuId, ItemSku> itemMap = itemService.queryItemsRequired(idList);
        for (ItemSku item : itemMap.values()) {
            if (!item.isEnabled()) {
                throw new GmallException(OrderErrorCode.ITEM_NOT_ENABLE);
            }
        }
        return itemMap;
    }
}
