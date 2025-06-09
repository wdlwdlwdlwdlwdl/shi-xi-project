package com.aliyun.gts.gmall.center.trade.core.domainservice.impl;

import com.aliyun.gts.gmall.center.pay.api.enums.PayChannelEnum;
import com.aliyun.gts.gmall.center.trade.api.dto.enums.PayCloseTypeEnum;
import com.aliyun.gts.gmall.center.trade.api.dto.input.PaymentIdRpcReq;
import com.aliyun.gts.gmall.center.trade.api.dto.input.PaymentSellerReceivedRpcReq;
import com.aliyun.gts.gmall.center.trade.common.constants.CATPayFeatureKeys;
import com.aliyun.gts.gmall.center.trade.core.domainservice.PayCATService;
import com.aliyun.gts.gmall.center.trade.domain.entity.pay.PayExtendModify;
import com.aliyun.gts.gmall.center.trade.domain.repositiry.PayExtendRepository;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.constant.PayErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.operate.PrimaryOrderRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.pay.ConfirmPayCheckRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.reversal.ReversalBuyerConfirmReq;
import com.aliyun.gts.gmall.platform.trade.api.facade.order.OrderWriteFacade;
import com.aliyun.gts.gmall.platform.trade.api.facade.reversal.ReversalWriteFacade;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcReversalDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.OrderAttrDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.pay.OrderPay;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcOrderRepository;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcReversalRepository;
import com.egzosn.pay.common.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PayCATServiceImpl implements PayCATService {

    @Autowired
    PayExtendRepository payExtendRepository;

    @Autowired
    OrderWriteFacade orderWriteFacade;

    @Autowired
    TcOrderRepository tcOrderRepository;

    @Autowired
    ReversalWriteFacade reversalWriteFacade;

    @Autowired
    private TcReversalRepository tcReversalRepository;

    @Override
    public void sellerConfirmReveived(PaymentSellerReceivedRpcReq req) {
        OrderPay orderPay = payExtendRepository.queryByPayId(req.getPaymentId());
        if (orderPay == null) {
            throw new GmallException(PayErrorCode.PAY_ORDER_NOT_EXISTS);
        }

        Map<String, String> feature = new HashMap<>();
        List<String> addTags = new ArrayList<>();
        feature.put(CATPayFeatureKeys.SellerConfirmReceived, req.getReceived() + "");
        feature.put(CATPayFeatureKeys.Memo, req.getMemo());
        Date current = new Date();
        feature.put(CATPayFeatureKeys.RealPayTime, DateUtils.format(current));
        feature.put(CATPayFeatureKeys.SellerConfirmTime, DateUtils.format(current));
        if (req.getReceived().equals(1)) {
            feature.put(CATPayFeatureKeys.CATFlowId, req.getFlowId());
            addTags.add(CATPayFeatureKeys.TAG_SELLER_RECEIVED);
        } else {
            feature.put(CATPayFeatureKeys.ColseType, req.getCloseType() + "");
            feature.put(CATPayFeatureKeys.Closed, Boolean.TRUE.toString());
            addTags.add(CATPayFeatureKeys.TAG_SELLER_CLOSED);
            // 对公支付的逾期  就要关闭支付单
            // 修改: 通过feature Closed 判断了
        }

        //对公支付 专有逻辑
        if (PayChannelEnum.CAT.getCode().equals(orderPay.getPayChannel())) {
            updateOrderStatus(req);
        }

        PayExtendModify modify = new PayExtendModify();
        modify.setPayId(req.getPaymentId());
        modify.setPutBizFeature(feature);
        modify.setAddBizTags(addTags);
        payExtendRepository.updatePayExtend(modify);
    }

    @Override
    public void buyerConfirmTransfer(PaymentIdRpcReq req) {
        OrderPay orderPay = payExtendRepository.queryByPayId(req.getPaymentId());
        if (orderPay == null) {
            throw new GmallException(PayErrorCode.PAY_ORDER_NOT_EXISTS);
        }

        if (org.apache.commons.lang3.StringUtils.isNotBlank(req.getPaymentId())) {
            Map<String, String> feature = new HashMap<>();
            if (req.getPaid()) {
                feature.put(CATPayFeatureKeys.BuyerConfirmTransfered, "1");
                feature.put(CATPayFeatureKeys.RealPayTime, DateUtils.format(new Date()));
                if (!CollectionUtils.isEmpty(req.getExtra()) && !StringUtils.isEmpty(req.getExtra().get(CATPayFeatureKeys.VOUCHERS))) {
                    feature.put(CATPayFeatureKeys.VOUCHERS, req.getExtra().get(CATPayFeatureKeys.VOUCHERS));
                }
            } else {
                feature.put(CATPayFeatureKeys.BuyerConfirmTransfered, "0");
                feature.put(CATPayFeatureKeys.Closed, Boolean.TRUE.toString());
            }
            PayExtendModify modify = new PayExtendModify();
            modify.setPayId(req.getPaymentId());
            modify.setPutBizFeature(feature);
            payExtendRepository.updatePayExtend(modify);
        }


        if (req.getPaid()) {
            //对公支付才需要更新订单状态  账期支付不用修改
            if (PayChannelEnum.CAT.getCode().equals(orderPay.getPayChannel())) {
                tcOrderRepository.updateStatusAndStageByPrimaryId(req.getPrimaryOrderId(),
                        OrderStatusEnum.WAIT_SELLER_RECEIVED.getCode(),
                        null, OrderStatusEnum.WAIT_BUYER_TRANSFERED.getCode());
            }

        } else {
            if (PayChannelEnum.CAT.getCode().equals(orderPay.getPayChannel())
                    || PayChannelEnum.ACCOUNT_PERIOD.getCode().equals(orderPay.getPayChannel())) {
                TcOrderDO tcOrderDO = tcOrderRepository.queryPrimaryByOrderId(req.getPrimaryOrderId());
                if (tcOrderDO.getPrimaryOrderStatus().equals(OrderStatusEnum.WAIT_BUYER_TRANSFERED.getCode())
                        || tcOrderDO.getPrimaryOrderStatus().equals(OrderStatusEnum.SELLER_CANCELING.getCode())) {
                    //由于关闭订单依赖于订单状态为待付款，所以先修改状态为待付款
                    tcOrderRepository.updateStatusAndStageByPrimaryId(req.getPrimaryOrderId(),
                            OrderStatusEnum.ORDER_WAIT_PAY.getCode(), null, tcOrderDO.getPrimaryOrderStatus());
                    PrimaryOrderRpcReq primaryOrderRpcReq = new PrimaryOrderRpcReq();
                    primaryOrderRpcReq.setPrimaryOrderId(req.getPrimaryOrderId());
                    orderWriteFacade.cancelOrder(primaryOrderRpcReq);
                } else if (tcOrderDO.getPrimaryOrderStatus().equals(OrderStatusEnum.SELLER_AGREE_CANCEL.getCode())) {
                    //若为商家同意取消，则调用退款成功接口
                    List<TcReversalDO> reversalList = tcReversalRepository.queryByPrimaryOrderId(req.getPrimaryOrderId());
                    log.info("调用买家确认收款接口：{}",reversalList.toString());
                    for(TcReversalDO reversal : reversalList){
                        ReversalBuyerConfirmReq reversalBuyerConfirmReq = new ReversalBuyerConfirmReq();
                        reversalBuyerConfirmReq.setPrimaryReversalId(reversal.getPrimaryReversalId());
                        reversalBuyerConfirmReq.setCustId(reversal.getCustId());
                        reversalWriteFacade.buyerConfirmRefund(reversalBuyerConfirmReq);
                    }
                }
            }
        }
    }

    private void updateOrderStatus(PaymentSellerReceivedRpcReq req) {
        TcOrderDO tcOrderDO = tcOrderRepository.queryPrimaryByOrderId(req.getPrimaryOrderId());
        if(req.getReceived().equals(1)){
            if(tcOrderDO.getPrimaryOrderStatus().equals(OrderStatusEnum.WAIT_SELLER_RECEIVED.getCode()) ||
                    tcOrderDO.getPrimaryOrderStatus().equals(OrderStatusEnum.WAIT_BUYER_TRANSFERED.getCode())){
                //确认收款后再发货
                tcOrderRepository.updateStatusAndStageByPrimaryId(req.getPrimaryOrderId(),
                        OrderStatusEnum.ORDER_WAIT_DELIVERY.getCode(),null,tcOrderDO.getPrimaryOrderStatus());
            }
        } else {
            if (tcOrderDO.getPrimaryOrderStatus().equals(OrderStatusEnum.WAIT_SELLER_RECEIVED.getCode()) ||
                    tcOrderDO.getPrimaryOrderStatus().equals(OrderStatusEnum.WAIT_BUYER_TRANSFERED.getCode())) {
                //由于关闭订单依赖于订单状态为待付款，所以先修改状态为待付款
                tcOrderRepository.updateStatusAndStageByPrimaryId(req.getPrimaryOrderId(),
                        OrderStatusEnum.ORDER_WAIT_PAY.getCode(), null, tcOrderDO.getPrimaryOrderStatus());

                //卖家关闭订单
                PrimaryOrderRpcReq primaryOrderRpcReq = new PrimaryOrderRpcReq();
                primaryOrderRpcReq.setPrimaryOrderId(req.getPrimaryOrderId());
                orderWriteFacade.closeOrder(primaryOrderRpcReq);
            }


            //填写原因
            TcOrderDO dbOrderDO = tcOrderRepository.queryPrimaryByOrderId(req.getPrimaryOrderId());

            OrderAttrDO orderAttrDO = dbOrderDO.getOrderAttr();
            if (orderAttrDO == null) {
                orderAttrDO = new OrderAttrDO();
            }
            orderAttrDO.setScr(PayCloseTypeEnum.codeOf(req.getCloseType()).getName() + " - " + req.getMemo());
            updateOrderAttr(orderAttrDO, req.getPrimaryOrderId(), dbOrderDO.getVersion());
        }
    }

    private void updateOrderAttr(OrderAttrDO orderAttrDO, Long primaryOrderId, Long version) {
        TcOrderDO tcOrderDO = new TcOrderDO();
        tcOrderDO.setOrderAttr(orderAttrDO);
        tcOrderDO.setPrimaryOrderId(primaryOrderId);
        tcOrderDO.setOrderId(primaryOrderId);
        tcOrderDO.setVersion(version);
        boolean b = tcOrderRepository.updateByOrderIdVersion(tcOrderDO);
        if (!b) {
            throw new GmallException(CommonErrorCode.CONCURRENT_UPDATE_FAIL);
        }
    }

    @Override
    public List<Map<String, String>> queryPayFlowByCartId(ConfirmPayCheckRpcReq req) {
        List<Map<String, String>> list = payExtendRepository.queryPayFlowByCartId(req.getCustId(), req.getCartId());
        return list;
    }

}
