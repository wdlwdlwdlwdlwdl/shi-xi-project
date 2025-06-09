package com.aliyun.gts.gmall.platform.trade.core.ability.reversal;

import com.aliyun.gts.gmall.framework.api.consts.CommonResponseCode;
import com.aliyun.gts.gmall.framework.extensionengine.ext.aop.Ability;
import com.aliyun.gts.gmall.framework.extensionengine.ext.model.BaseAbility;
import com.aliyun.gts.gmall.framework.extensionengine.ext.util.reducers.Reducers;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.api.constant.ReversalErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.reversal.*;
import com.aliyun.gts.gmall.platform.trade.core.bizcode.BizCodeEntity;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.ReversalQueryService;
import com.aliyun.gts.gmall.platform.trade.core.extension.reversal.ReversalProcessExt;
import com.aliyun.gts.gmall.platform.trade.core.extension.reversal.defaultimpl.DefaultReversalProcessExt;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.MainReversal;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.ReversalDetailOption;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.function.Consumer;

@Slf4j
@Component
@Ability(code = "com.aliyun.gts.gmall.platform.trade.core.ability.reversal.ReversalProcessAbility",
        fallback = DefaultReversalProcessExt.class,
        description = "售后处理能力")
public class ReversalProcessAbility extends BaseAbility<BizCodeEntity, ReversalProcessExt> {

    @Autowired
    private ReversalQueryService reversalQueryService;

    @ApiOperation("卖家同意售后")
    public void sellerAgree(ReversalAgreeRpcReq req) {
        // 查询退单 必须存在
        MainReversal reversal = getReversal(req.getPrimaryReversalId());
        // 获取订单的bizcode
        BizCodeEntity bizCode = getBizCode(reversal);
        // 执行同意方法
        executeVoid(bizCode, x -> x.sellerAgree(req, reversal));
    }

    @ApiOperation("卖家不同意售后,并关闭")
    public void sellerRefuse(ReversalRefuseRpcReq req) {
        // 查询退单 必须存在
        MainReversal reversal = getReversal(req.getPrimaryReversalId());
        // 获取订单的bizcode
        BizCodeEntity bizCode = getBizCode(reversal);
        // 执行拒绝方法
        executeVoid(bizCode, x -> x.sellerRefuse(req, reversal));
    }

    @ApiOperation("顾客取消售后")
    public void customerCancel(ReversalModifyRpcReq req) {
        MainReversal reversal = getReversal(req.getPrimaryReversalId());
        BizCodeEntity bizCode = getBizCode(reversal);
        executeVoid(bizCode, x -> x.customerCancel(req, reversal));
    }

    @ApiOperation("顾客发货")
    public void customerSendDeliver(ReversalDeliverRpcReq req) {
        MainReversal reversal = getReversal(req.getPrimaryReversalId());
        BizCodeEntity bizCode = getBizCode(reversal);
        executeVoid(bizCode, x -> x.customerSendDeliver(req, reversal));
    }

    @ApiOperation("卖家确认收货")
    public void sellerConfirmDeliver(ReversalModifyRpcReq req) {
        // 查询退单 必须存在
        MainReversal reversal = getReversal(req.getPrimaryReversalId());
        // 获取订单的bizcode
        BizCodeEntity bizCode = getBizCode(reversal);
        // 执行确认收货 退货完成
        executeVoid(bizCode, x -> x.sellerConfirmDeliver(req, reversal));
    }

    @ApiOperation("卖家拒绝收货,并关闭")
    public void sellerRefuseDeliver(ReversalRefuseRpcReq req) {
        MainReversal reversal = getReversal(req.getPrimaryReversalId());
        BizCodeEntity bizCode = getBizCode(reversal);
        executeVoid(bizCode, x -> x.sellerRefuseDeliver(req, reversal));
    }

    @ApiOperation("退款成功后，处理售后单、订单的状态")
    public void refundSuccess(MainReversal reversal, Integer stepNo) {
        //MainReversal reversal = getReversal(primaryReversalId);
        BizCodeEntity bizCode = getBizCode(reversal);
        executeVoid(bizCode, x -> x.refundSuccess(reversal, stepNo));
    }

    @ApiOperation("买家确认收到退款")
    public void buyerConfirmRefund(ReversalBuyerConfirmReq req) {
        MainReversal mainReversal = getReversal(req.getPrimaryReversalId());
        if (!req.getCustId().equals(mainReversal.getCustId())) {
            throw new GmallException(CommonResponseCode.NoPermission, req.getCustId(),
                    mainReversal.getPrimaryReversalId());
        }
        mainReversal.setBcrMemo(req.getBcrMemo());
        mainReversal.setBcrNumber(req.getBcrNumber());
        BizCodeEntity bizCode = getBizCode(mainReversal);

        executeVoid(bizCode, x -> x.buyerConfirmRefund(mainReversal));
    }

    // ============== private =============
    private MainReversal getReversal(Long primaryReversalId) {
        MainReversal reversal = reversalQueryService.queryReversal(
            primaryReversalId,
            ReversalDetailOption.builder().includeOrderInfo(true).build()
        );
        if (reversal == null) {
            throw new GmallException(ReversalErrorCode.REVERSAL_NOT_EXIST);
        }
        return reversal;
    }

    private BizCodeEntity getBizCode(MainReversal reversal) {
        return BizCodeEntity.buildWithDefaultBizCode(reversal.getMainOrder());
    }

    private void executeVoid(BizCodeEntity bizCode, Consumer<ReversalProcessExt> c) {
        executeExt(bizCode,
            extension -> {
                c.accept(extension);
                return null;
            },
            ReversalProcessExt.class,
            Reducers.firstOf(Objects::nonNull)
        );
    }

    @ApiOperation("运营同意售后")
    public void agreeByOperation(ReversalAgreeRpcReq req) {
        // 查询退单 必须存在
        MainReversal reversal = getReversal(req.getPrimaryReversalId());
        // 获取订单的bizcode
        BizCodeEntity bizCode = getBizCode(reversal);
        // 运营同意售后
        executeVoid(bizCode, x -> x.agreeByOperation(req, reversal));
    }

    @ApiOperation("系统自动取消售后")
    public void cancelBySystem(ReversalModifyRpcReq req) {
        // 查询退单 必须存在
        MainReversal reversal = getReversal(req.getPrimaryReversalId());
        // 获取订单的bizcode
        BizCodeEntity bizCode = getBizCode(reversal);
        // 自动退货
        executeVoid(bizCode, x -> x.cancelBySystem(req, reversal));
    }
}
