package com.aliyun.gts.gmall.manager.front.trade.adaptor;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.manager.biz.output.CustDTO;
import com.aliyun.gts.gmall.manager.front.common.util.UserHolder;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.EPayQueryReq;
import com.aliyun.gts.gmall.platform.open.customized.api.dto.input.EPayRefundRpcReq;
import com.aliyun.gts.gmall.platform.open.customized.api.dto.input.EPayTokenRpcReq;
import com.aliyun.gts.gmall.platform.open.customized.api.dto.output.EpayTransactionStatusDTO;
import com.aliyun.gts.gmall.platform.open.customized.api.facade.EPaymentFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 交易依赖
 *
 * @author tiansong
 */
@Slf4j
@Service
public class EPayAdapter {



    @Autowired
    private EPaymentFacade ePaymentFacade;



    public String getEpayAccessToken(EPayQueryReq EPayQueryReq) {
        EPayTokenRpcReq EPayTokenRpcReq = new EPayTokenRpcReq();
        BeanUtils.copyProperties(EPayQueryReq, EPayTokenRpcReq);
        RpcResponse<String> rpcResponse = ePaymentFacade.getEpayAccessToken(EPayTokenRpcReq);
        return rpcResponse.getData();
    }


    public String getEpayCards(EPayQueryReq EPayQueryReq) {
        EPayTokenRpcReq EPayTokenRpcReq = new EPayTokenRpcReq();
        BeanUtils.copyProperties(EPayQueryReq, EPayTokenRpcReq);
        RpcResponse<String> rpcResponse = ePaymentFacade.getEpayCards(EPayTokenRpcReq);
        return rpcResponse.getData();
    }

    public String getEpayClients(EPayQueryReq EPayQueryReq) {
        EPayTokenRpcReq EPayTokenRpcReq = new EPayTokenRpcReq();
        BeanUtils.copyProperties(EPayQueryReq, EPayTokenRpcReq);
        EPayTokenRpcReq.setScope("payment");
        RpcResponse<String> rpcResponse = ePaymentFacade.getEpayClients(EPayTokenRpcReq);
        return rpcResponse.getData();
    }

    public String getInvoiceIdStatu(EPayQueryReq EPayQueryReq) {
        EPayTokenRpcReq EPayTokenRpcReq = new EPayTokenRpcReq();
        BeanUtils.copyProperties(EPayQueryReq, EPayTokenRpcReq);
        RpcResponse<EpayTransactionStatusDTO> rpcResponse = ePaymentFacade.getInvoiceIdStatu(EPayTokenRpcReq);
        return JSONObject.toJSONString(rpcResponse.getData());
    }

    public Boolean refund(EPayQueryReq EPayQueryReq) {
        // 仅模拟 测试使用
        EPayRefundRpcReq ePayRefundRpcReq = new EPayRefundRpcReq();
        ePayRefundRpcReq.setAmount(EPayQueryReq.getAmount());
        ePayRefundRpcReq.setInvoiceID(EPayQueryReq.getInvoiceID());
        ePayRefundRpcReq.setId(EPayQueryReq.getId());
        ePayRefundRpcReq.setIsCancel(EPayQueryReq.getIsCancel());
        ePayRefundRpcReq.setCustId(EPayQueryReq.getCustId().toString());//must
        RpcResponse<Boolean> rpcResponse = ePaymentFacade.refund(ePayRefundRpcReq);
        return rpcResponse.getData();
    }
}