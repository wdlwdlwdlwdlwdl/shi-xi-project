package com.aliyun.gts.gmall.platform.trade.core.task.biz;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.platform.trade.api.dto.common.ReceiverDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.reversal.ReversalAgreeRpcReq;
import com.aliyun.gts.gmall.platform.trade.common.constants.ReversalStatusEnum;
import com.aliyun.gts.gmall.platform.trade.core.ability.reversal.ReversalProcessAbility;
import com.aliyun.gts.gmall.platform.trade.core.task.AbstractScheduledTask;
import com.aliyun.gts.gmall.platform.trade.core.task.param.ReversalTaskParam;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcReversalDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.ReceiveAddr;
import com.aliyun.gts.gmall.platform.trade.domain.entity.task.ScheduleTask;
import com.aliyun.gts.gmall.platform.trade.domain.entity.user.SellerTradeConfig;
import com.aliyun.gts.gmall.platform.trade.domain.repository.ReceiverRepository;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcReversalRepository;
import com.aliyun.gts.gmall.platform.trade.persistence.rpc.converter.ReceiverRpcConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 售后待卖家同意超时，自动同意
 */
@Component
@Slf4j
public class ReversalAgreeTask extends AbstractScheduledTask<ReversalTaskParam> {

    @Autowired
    private ReversalProcessAbility reversalProcessAbility;

    @Autowired
    private ReceiverRepository receiverRepository;

    @Autowired
    private TcReversalRepository tcReversalRepository;

    @Autowired
    private ReceiverRpcConverter receiverRpcConverter;

    @Override
    protected ScheduleTask doBuildTask(ReversalTaskParam param) {
        SellerTradeConfig cfg = orderConfigService.getSellerConfig(param.getSellerId());
        Date scheduleTime = new Date(System.currentTimeMillis() + 1000L * cfg.getAutoAgreeReversalTimeInSec());
        ScheduleTask scheduleTask = new ScheduleTask();
        scheduleTask.setScheduleTime(scheduleTime);
        scheduleTask.setType(getTaskType());
        scheduleTask.setPrimaryOrderId(param.getPrimaryOrderId());
        scheduleTask.putParam("primaryReversalId", param.getPrimaryReversalId());
        return scheduleTask;
    }

    @Override
    protected ExecuteResult doExecute(ScheduleTask task) {
        long primaryReversalId = task.getParamLong("primaryReversalId");
        TcReversalDO reversal = tcReversalRepository.queryByReversalId(primaryReversalId);
        if (reversal == null) {
            return ExecuteResult.success("reversal not exist");
        }
        if (reversal.getReversalStatus().intValue() != ReversalStatusEnum.WAIT_SELLER_AGREE.getCode()) {
            return ExecuteResult.success("reversal not WAIT_SELLER_AGREE");
        }
        ReceiverDTO receiver = null;    // 需要卖家收货地址的, 取默认地址
        if (reversal.getReversalFeatures().getNeedLogistics() != null &&
            reversal.getReversalFeatures().getNeedLogistics().booleanValue()) {
            ReceiveAddr addr = receiverRepository.getSellerDefaultReceiver(reversal.getSellerId());
            if (addr == null) {
                return ExecuteResult.fail(I18NMessageUtils.getMessage("seller.no.default.return.address"));  //# "卖家无默认退货地址"
            }
            receiver = receiverRpcConverter.toReceiverDTO(addr);
        }
        ReversalAgreeRpcReq req = new ReversalAgreeRpcReq();
        req.setPrimaryReversalId(primaryReversalId);
        req.setSellerId(reversal.getSellerId());
        req.setReceiver(receiver);
        req.setSystemOperate(true);
        reversalProcessAbility.sellerAgree(req);
        return ExecuteResult.success("ok");
    }
}
