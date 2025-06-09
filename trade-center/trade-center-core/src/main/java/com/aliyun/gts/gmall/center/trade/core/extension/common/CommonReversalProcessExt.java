package com.aliyun.gts.gmall.center.trade.core.extension.common;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.reversal.ReversalAgreeRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.reversal.ReversalModifyRpcReq;
import com.aliyun.gts.gmall.platform.trade.core.extension.reversal.defaultimpl.DefaultReversalProcessExt;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.MainReversal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

/**
 * description: 售后处理公共扩展策略
 *
 * @author hu.zhiyong
 * @date 2022/10/10 15:48
 **/
@Slf4j
public class CommonReversalProcessExt extends DefaultReversalProcessExt {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sellerAgree(ReversalAgreeRpcReq req, MainReversal reversal) {
        //审核同意
        super.sellerAgree(req, reversal);
    }

    @Override
    public void sellerConfirmDeliver(ReversalModifyRpcReq req, MainReversal reversal) {
        super.sellerConfirmDeliver(req,reversal);
    }
}
