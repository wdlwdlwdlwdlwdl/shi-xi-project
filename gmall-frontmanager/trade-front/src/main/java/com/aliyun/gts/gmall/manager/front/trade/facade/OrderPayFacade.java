package com.aliyun.gts.gmall.manager.front.trade.facade;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.command.AddCardRestCommand;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.command.OrderPayRestCommand;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.command.PrimaryOrderRestCommand;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.EPayQueryReq;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.PayCheckRestQuery;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.PrimaryOrderRestQuery;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.pay.OrderPayVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.pay.PayRenderVO;

import java.util.List;

/**
 * 订单支付
 *
 * @author tiansong
 */
public interface OrderPayFacade {
    /**
     * 收银台渲染
     *
     * @param primaryOrderRestQuery
     * @return
     */
    public PayRenderVO payRender(PrimaryOrderRestQuery primaryOrderRestQuery);

    /**
     * 支付成功检查
     *
     * @param payCheckRestQuery
     * @return
     */
    public Boolean payCheck(PayCheckRestQuery payCheckRestQuery);

    /**
     * 支付
     *
     * @param orderPayRestCommand
     * @return
     */
    public OrderPayVO toPay(OrderPayRestCommand orderPayRestCommand);

    /**
     * 加卡配置信息获取
     * @return
     */
    public JSONObject addPay();


    Boolean cancelOrder(PrimaryOrderRestCommand primaryOrderRestCommand);

    String payment(OrderPayRestCommand orderPayRestCommand, OrderPayVO orderPayVO);

    void addCard(AddCardRestCommand addCardRestCommand);

    /**
     * 获取用户的卡信息
     * @param ePayQueryReq
     * @return
     */
    List<JSONObject> getCardIds(EPayQueryReq ePayQueryReq);

    /**
     * 获取用户积分总额接口
     * @param ePayQueryReqd
     * @return
     */
    Long getBonuses(EPayQueryReq ePayQueryReqd);

    /**
     * 删除卡
     * @param ePayQueryReqd
     * @return
     */
    Boolean removeCard(EPayQueryReq ePayQueryReqd);


    List<JSONObject> getBankCards(String accountId);

    /**
     * 支付接口
     * @param orderPayRestCommand
     * @return
     */
    RestResponse<Boolean> payOrder(OrderPayRestCommand orderPayRestCommand);

}
