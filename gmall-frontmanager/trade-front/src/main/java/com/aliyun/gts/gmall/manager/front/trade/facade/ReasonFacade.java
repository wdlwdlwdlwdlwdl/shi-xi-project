package com.aliyun.gts.gmall.manager.front.trade.facade;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.command.*;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.*;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.*;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.reversal.ReversalOrderVO;

import java.util.List;

/**
 * 取消原因接口
 *
 * @author yangl
 */
public interface ReasonFacade {

    PageInfo<CancelReasonVO> queryCancelReasonList(CancelReasonQueryReq req);
}
