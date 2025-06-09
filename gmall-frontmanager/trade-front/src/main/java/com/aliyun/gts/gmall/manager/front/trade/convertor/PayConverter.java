package com.aliyun.gts.gmall.manager.front.trade.convertor;

import com.aliyun.gts.gmall.center.pay.api.dto.input.settle.OrderPayQueryRpcReq;
import com.aliyun.gts.gmall.center.pay.api.dto.output.settle.OrderPayRowRpcResp;
import com.aliyun.gts.gmall.center.pay.api.enums.PayChannelEnum;
import com.aliyun.gts.gmall.center.trade.api.dto.enums.PayCloseTypeEnum;
import com.aliyun.gts.gmall.center.trade.api.dto.input.PaymentIdRpcReq;
import com.aliyun.gts.gmall.center.trade.common.constants.CATPayFeatureKeys;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.manager.front.trade.constants.ButtonNames;
import com.aliyun.gts.gmall.manager.front.trade.constants.CATPayStatusEnum;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.command.BuyerConfirmPayRestCommand;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.PayQueryReq;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.pay.PaySearchVO;
import com.aliyun.gts.gmall.platform.pay.api.enums.PayStatusEnum;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring",imports = {PayChannelEnum.class,StringUtils.class})
@Slf4j
public abstract class PayConverter {

    @Mapping(source = "req.page.pageNo" , target = "currentPage")
    @Mapping(source = "req.page.pageSize" , target = "pageSize")
    @Mapping(expression = "java(convert(req.getPaymentStatus()))" , target = "statusList")
    @Mapping(expression = "java(convert2Map(req.getPaymentStatus()))" , target = "map")
    @Mapping(expression = "java(StringUtils.startsWith(req.getPaymentORorderId(),\"P\")?req.getPaymentORorderId():null)"
        , target = "paymentId")
    @Mapping(expression = "java(StringUtils.isNumeric(req.getPaymentORorderId())?Long.valueOf(req.getPaymentORorderId()):null)"
        , target = "primaryOrderId")
    @Mapping(target = "paymentStatus", ignore = true)
    public abstract OrderPayQueryRpcReq convert(PayQueryReq req);

    public abstract PageInfo<PaySearchVO> convert(PageInfo<OrderPayRowRpcResp> resp);

    public abstract PaymentIdRpcReq convert(BuyerConfirmPayRestCommand command);

    @Mapping(target = "featureMap" , source = "resp.bizFeature")
    @Mapping(target = "paymentId" , source = "resp.payId")
    @Mapping(target = "payAmt" , expression = "java(resp.getPayAmt()/100.0d)")
    @Mapping(target = "paymentStatusDisplay" , expression = "java(convertStatusDisplay(resp.getPaymentStatus(),"
        + "resp.getBizFeature()))")
    @Mapping(target = "payChannelDisplay" , expression = "java(PayChannelEnum.codeOf(resp.getPayChannel()).getMessage())")
    @Mapping(target = "buttons" , expression = "java(convertButtons(resp.getPaymentStatus(),"
        + "resp.getBizFeature(),resp.getPayChannel()))")
    @Mapping(target = "timeout" , expression = "java(isTimeout(resp.getBizFeature()))")
    public abstract PaySearchVO convert(OrderPayRowRpcResp resp);

    protected List<String> convert(CATPayStatusEnum statusEnum){
        if(statusEnum == null){
            return null;
        }
        if(statusEnum.equals(CATPayStatusEnum.BUYER_PAID)){
            return Lists.newArrayList(PayStatusEnum.PAID.getCode());
        }
        //这里的待付款，通过下面的map.put("transfered" , 0); 来判断
        if(statusEnum.equals(CATPayStatusEnum.TO_BE_PAID)){
            return Lists.newArrayList(PayStatusEnum.PAID.getCode() , PayStatusEnum.TO_BE_PAID.getCode());
        }
        //对公支付的支付单   创建后就变成了已支付状态  已支付的状态  不会变成05的关闭状态   对公支付的取消状态  用pay_closed true表示
//        if(statusEnum.equals(CATPayStatusEnum.PAY_CANCELED)){
//            return Lists.newArrayList(PayStatusEnum.PAY_CANCELED.getCode());
//        }
        return null;
    }

    protected Map<String,Object> convert2Map(CATPayStatusEnum statusEnum){
        Map<String,Object> map = new HashMap<>() ;
        if(statusEnum != null) {
            if (statusEnum.equals(CATPayStatusEnum.BUYER_PAID)) {
                map.put("pay_transfered", 1);
            }
            if (statusEnum.equals(CATPayStatusEnum.TO_BE_PAID)) {
                map.put("pay_transfered", 0);
            }
            if(statusEnum.equals(CATPayStatusEnum.PAID)){
                map.put("pay_received", 1);
            }
            if(statusEnum.equals(CATPayStatusEnum.TIMEOUT)){
                map.put("pay_received", 0);
                map.put("closeType", 1);
            }
            if(statusEnum.equals(CATPayStatusEnum.PAY_CANCELED)){
                map.put("pay_closed", "true");
            }
        }
        return map;
    }

    protected String convertStatusDisplay(String paymentStatus, Map<String, String> feature){
        if (feature == null) {
            feature = Collections.EMPTY_MAP;
        }

        if(paymentStatus.equals(PayStatusEnum.PAY_CANCELED.getCode())){
            return CATPayStatusEnum.PAY_CANCELED.getName();
        }
        if(paymentStatus.equals(PayStatusEnum.PENDING_REFUND.getCode())){
            return CATPayStatusEnum.PENDING_REFUND.getName();
        }
        if(paymentStatus.equals(PayStatusEnum.SELLER_REFUNDED.getCode())){
            return CATPayStatusEnum.SELLER_REFUNDED.getName();
        }
        String closed = feature.get(CATPayFeatureKeys.Closed);
        if ("true".equals(closed)) {
            return CATPayStatusEnum.PAY_CANCELED.getName();
        }

        String received = feature.get("received");
        if(received != null){
            //卖家确认收款  表示付款成功
            if("1".equals(received)){
                return CATPayStatusEnum.PAID.getName();
            }
        }

        String closeType = feature.get("closeType");
        if(closeType != null){
            //有逾期标签  卖家received字段不为1  表示逾期未支付
            if((PayCloseTypeEnum.TIMEOUT.getCode()+"").equals(closeType)){
                return CATPayStatusEnum.TIMEOUT.getName();
            }
        }



        String transfered = feature.get("transfered");
        if(transfered != null){
            if("1".equals(transfered)){
                //支付单状态为支付  买家打款标记位1   状态为买家表示已支付
                if(paymentStatus.equals(PayStatusEnum.PAID.getCode())){
                    return CATPayStatusEnum.BUYER_PAID.getName();
                }
            }else{
                //支付单状态为支付或待支付  买家打款标记位0   状态为 待支付
                if(paymentStatus.equals(PayStatusEnum.PAID.getCode()) ||
                    paymentStatus.equals(PayStatusEnum.TO_BE_PAID.getCode())){
                    return CATPayStatusEnum.TO_BE_PAID.getName();
                }
            }
        }
        return null;
    }

    protected Boolean isTimeout(Map<String, String> feature){
        if (feature == null) {
            feature = Collections.EMPTY_MAP;
        }
        String closeType = feature.get("closeType");
        if(closeType != null){
            if((PayCloseTypeEnum.TIMEOUT.getCode()+"").equals(closeType)){
                return true;
            }
        }

        return false;
    }

    protected Map<String , Boolean> convertButtons(String paymentStatus, Map<String, String> feature , String payChannel){
        if (feature == null) {
            feature = Collections.EMPTY_MAP;
        }
        Map<String , Boolean> buttons = new HashMap<>();
        if(paymentStatus.equals(PayStatusEnum.PAY_CANCELED.getCode())){
            return buttons;
        }
        if(paymentStatus.equals(PayStatusEnum.SELLER_REFUNDED.getCode())){
            buttons.put(ButtonNames.buyerRefundReceived , true);
            return buttons;
        }
        String closed = feature.get(CATPayFeatureKeys.Closed);
        if ("true".equals(closed)) {
            return buttons;
        }

        String received = feature.get("received");
        if(received != null){
            //卖家确认收款  表示付款成功
            if("1".equals(received)){
                return buttons;
            }
        }
        String closeType = feature.get("closeType");
        if(closeType != null){
            //有逾期标签  卖家received字段不为1  表示逾期未支付
            if((PayCloseTypeEnum.TIMEOUT.getCode()+"").equals(closeType)){
                buttons.put(ButtonNames.buyerConfirmPaid , true);
                buttons.put(ButtonNames.buyerCancelPaid , true);
                return buttons;
            }
        }



        String transfered = feature.get("transfered");
        if(transfered != null){
            if("0".equals(transfered)) {
                buttons.put(ButtonNames.buyerConfirmPaid, true);
                buttons.put(ButtonNames.buyerCancelPaid, true);
            }
            return buttons;
        }

        return buttons;

    }

}
