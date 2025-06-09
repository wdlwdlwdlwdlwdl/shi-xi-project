package com.aliyun.gts.gmall.manager.front.trade.facade;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.command.CreateReversalRestCommand;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.command.ModifyReversalRestCommand;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.command.ReversalBuyerConfirmRestCommand;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.command.ReversalDeliverRestCommand;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.ReversalCheckRestQuery;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.ReversalDetailRestQuery;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.ReversalRestQuery;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.reversal.ReversalDetailVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.reversal.ReversalOrderDetailVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.reversal.ReversalOrderVO;

/**
 * 售后相关接口
 *
 * @author tiansong
 */
public interface ReversalFacade {
    /**
     * 售后申请表单读取
     *
     * @param reversalCheckRestQuery
     * @return
     */
    ReversalOrderDetailVO queryForCreate(ReversalCheckRestQuery reversalCheckRestQuery);

    /**
     * 创建售后申请
     *
     * @param createReversalRestCommand
     * @return 返回售后单ID
     */
    Long create(CreateReversalRestCommand createReversalRestCommand);

    /**
     * 取消售后申请
     *
     * @param modifyReversalRestCommand
     * @return
     */
    Boolean cancel(ModifyReversalRestCommand modifyReversalRestCommand);

    /**
     * 售后单详情
     *
     * @param reversalDetailRestQuery
     * @return
     */
    ReversalDetailVO queryDetail(ReversalDetailRestQuery reversalDetailRestQuery);

    /**
     * 售后单列表
     *
     * @param reversalRestQuery
     * @return
     */
    PageInfo<ReversalOrderVO> queryList(ReversalRestQuery reversalRestQuery);

    /**
     * 售后申请邮寄确认
     *
     * @param reversalDeliverRestCommand
     * @return
     */
    Boolean sendDeliver(ReversalDeliverRestCommand reversalDeliverRestCommand);

    Boolean buyerConfirmRefund(ReversalBuyerConfirmRestCommand command);
}