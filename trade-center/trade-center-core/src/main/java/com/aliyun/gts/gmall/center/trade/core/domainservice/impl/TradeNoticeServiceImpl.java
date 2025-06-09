package com.aliyun.gts.gmall.center.trade.core.domainservice.impl;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.center.trade.core.domainservice.TradeNoticeService;
import com.aliyun.gts.gmall.center.trade.domain.entity.notice.NoticeMessage;
import com.aliyun.gts.gmall.center.trade.domain.repositiry.NoticeRepository;
import com.aliyun.gts.gmall.center.user.api.dto.constants.CustomerConstant;
import com.aliyun.gts.gmall.center.user.api.dto.input.NewCustomerMarkedReq;
import com.aliyun.gts.gmall.center.user.api.facade.CustomerReadExtFacade;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.framework.server.util.DateTimeUtils;
import com.aliyun.gts.gmall.platform.pay.api.dto.message.PaySuccessMessage;
import com.aliyun.gts.gmall.platform.trade.api.constant.PayErrorCode;
import com.aliyun.gts.gmall.platform.trade.common.util.MoneyUtils;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderQueryAbility;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.PayQueryService;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.ReversalQueryService;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSku;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSkuId;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderQueryOption;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.pay.OrderPay;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.MainReversal;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.ReversalDetailOption;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.SubReversal;
import com.aliyun.gts.gmall.platform.trade.domain.repository.ItemRepository;
import com.aliyun.gts.gmall.platform.trade.domain.repository.OrderPayRepository;
import com.aliyun.gts.gmall.platform.trade.domain.util.StepOrderUtils;
import com.aliyun.gts.gmall.platform.user.api.dto.input.CustomerByIdQuery;
import com.aliyun.gts.gmall.platform.user.api.dto.input.CustomerQueryOption;
import com.aliyun.gts.gmall.platform.user.api.dto.input.SaveFeatureCommand;
import com.aliyun.gts.gmall.platform.user.api.dto.output.CustomerDTO;
import com.aliyun.gts.gmall.platform.user.api.facade.CustomerReadFacade;
import com.aliyun.gts.gmall.platform.user.api.facade.CustomerWriteFacade;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TradeNoticeServiceImpl implements TradeNoticeService {

    @Autowired
    private NoticeRepository noticeRepository;
    @Autowired
    private OrderQueryAbility orderQueryAbility;
    @Autowired
    private ReversalQueryService reversalQueryService;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private PayQueryService payQueryService;
    @Autowired
    private CustomerReadFacade customerReadFacade;
    @Autowired
    private CustomerWriteFacade customerWriteFacade;
    @Autowired
    private CustomerReadExtFacade customerReadExtFacade;

    // 库存不足通知 -- 库存小于等于多少开始通知, 默认0
    @Value("${trade.notice.inventoryCount:0}")
    private int noticeInventoryCount;

    // 库存不足通知 -- 同一商品重复通知间隔, 默认10分钟
    @Value("${trade.notice.inventoryInterval:600000}")
    private int noticeInventoryInterval;

    @Override
    public void onOrderCreate(MainOrder mainOrder) {
        List<ItemSkuId> idList = mainOrder.getSubOrders().stream()
            .map(SubOrder::getItemSku).map(ItemSku::getItemSkuId)
            .collect(Collectors.toList());
        List<ItemSku> itemList = itemRepository.queryItemsFromCache(idList);

        for (ItemSku item : itemList) {
            //临时注释
           /* if (item.getSkuQty().intValue() <= noticeInventoryCount) {
                // 通知库存不足
                noticeInventoryLack(item);
            }*/
        }
    }

    private void noticeInventoryLack(ItemSku item) {
        long timeSuffix = System.currentTimeMillis() / noticeInventoryInterval;

        Map<String, Object> args = new HashMap<>();
        args.put("itemId", item.getItemId());
        args.put("itemTitle", item.getItemTitle());

        String code = "trade_inventory_lack";
        NoticeMessage message = new NoticeMessage();
        message.setBizId(code + "_" + item.getItemId() + "_" + timeSuffix);
        message.setSellerId(item.getSeller().getSellerId());
        message.setTemplateCode(code);
        message.setTemplateArgs(args);
        noticeRepository.publish(message);
    }

    @Override
    public void onOrderPaid(PaySuccessMessage message) {
        long primaryOrderId = Long.parseLong(message.getPrimaryOrderId());
        MainOrder mainOrder = orderQueryAbility.getMainOrder(
            primaryOrderId,
            OrderQueryOption.builder().build()
        );
        if (mainOrder == null) {
            return;
        }

        Set<String> titles = new LinkedHashSet<>();
        for (SubOrder subOrder : mainOrder.getSubOrders()) {
            if (titles.size() >= 3) {
                titles.add("..."+ I18NMessageUtils.getMessage("items")+" " + mainOrder.getSubOrders().size() + " "+I18NMessageUtils.getMessage("num.items"));  //# 等 " + mainOrder.getSubOrders().size() + " 个商品"
                break;
            }
            titles.add(subOrder.getItemSku().getItemTitle());
        }

        Long custId = mainOrder.getCustomer().getCustId();
        Integer stepNo = message.getStepNo();
        OrderPay orderPay = payQueryService.queryByOrder(primaryOrderId, custId, stepNo);
        if (orderPay == null) {
            throw new GmallException(PayErrorCode.PAY_ORDER_NOT_EXISTS);
        }
        String payTime = DateTimeUtils.format(orderPay.getPayTime());
        String totalAmtYuan = String.valueOf(mainOrder.getOrderPrice().getOrderTotalAmt());

        Map<String, Object> args = new HashMap<>();
        args.put("primaryOrderId", primaryOrderId);
        args.put("itemTitle", StringUtils.join(titles, " ; "));
        args.put("payTime", payTime);
        args.put("totalAmtYuan", totalAmtYuan);

        String code = "trade_order_pay";
        NoticeMessage notice = new NoticeMessage();
        notice.setBizId(code + "_" + primaryOrderId);
        notice.setSellerId(mainOrder.getSeller().getSellerId());
        notice.setTemplateCode(code);
        notice.setTemplateArgs(args);
        noticeRepository.publish(notice);

        // 给用户打标
        this.markOldCustomer(custId);
    }

    @Override
    public void onReversalCreate(long primaryReversalId) {
        MainReversal mainReversal = reversalQueryService.queryReversal(
            primaryReversalId,
            ReversalDetailOption.builder().includeOrderInfo(true).build()
        );
        if (mainReversal == null) {
            return;
        }
        Set<String> titles = new LinkedHashSet<>();
        for (SubReversal subReversal : mainReversal.getSubReversals()) {
            if (titles.size() >= 3) {
                titles.add("..."+ I18NMessageUtils.getMessage("items")+" " + mainReversal.getSubReversals().size() + " "+I18NMessageUtils.getMessage("num.items"));  //# 等 " + mainReversal.getSubReversals().size() + " 个商品"
                break;
            }
            titles.add(subReversal.getSubOrder().getItemSku().getItemTitle());
        }
        String reversalTime = DateTimeUtils.format(mainReversal.getGmtCreate());
        String cancelAmtYuan = String.valueOf(mainReversal.getCancelAmt());

        Map<String, Object> args = new HashMap<>();
        args.put("primaryOrderId", mainReversal.getMainOrder().getPrimaryOrderId());
        args.put("itemTitle", StringUtils.join(titles, " ; "));
        args.put("reversalTime", reversalTime);
        args.put("cancelAmtYuan", cancelAmtYuan);

        String code = "trade_reversal_create";
        NoticeMessage message = new NoticeMessage();
        message.setBizId(code + "_" + primaryReversalId);
        message.setSellerId(mainReversal.getSellerId());
        message.setTemplateCode(code);
        message.setTemplateArgs(args);
        noticeRepository.publish(message);
    }

    private void markOldCustomer(Long custId) {
        CustomerDTO customerDTO = customerReadFacade.query(CustomerByIdQuery.of(custId, CustomerQueryOption.builder().build())).getData();
        Map<String, String> feature = customerDTO.getFeatures();
        if (feature == null) {
            feature = new HashMap<>();
        }
        if (feature.containsKey(CustomerConstant.OLD)) {
            return;
        }
        feature.put(CustomerConstant.OLD, "Y");
        customerWriteFacade.saveFeature(SaveFeatureCommand.builder().id(custId).addMap(feature).build());
        // 处理缓存
        NewCustomerMarkedReq req = new NewCustomerMarkedReq();
        req.setId(custId);
        req.setIsNew(Boolean.FALSE);
        customerReadExtFacade.markedOldCustomer(req);
    }
}
