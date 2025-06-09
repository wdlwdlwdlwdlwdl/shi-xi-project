package com.aliyun.gts.gmall.platform.trade.server.facade.impl;

import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.GlobalConfigDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.GlobalConfigRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.SellerConfigDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.SellerIdRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.facade.tradeconf.TradeConfigFacade;
import com.aliyun.gts.gmall.platform.trade.core.convertor.TradeConfigConverter;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderConfigService;
import com.aliyun.gts.gmall.platform.trade.domain.entity.user.SellerTradeConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TradeConfigFacadeImpl implements TradeConfigFacade {

    @Autowired
    private OrderConfigService orderConfigService;
    @Autowired
    private TradeConfigConverter tradeConfigConverter;

    @Override
    public RpcResponse<SellerConfigDTO> getSellerConfig(SellerIdRpcReq req) {
        SellerTradeConfig conf = orderConfigService.getSellerConfigNoCache(req.getSellerId());
        SellerConfigDTO dto = tradeConfigConverter.toSellerConfigDTO(conf, req.getSellerId());
        return RpcResponse.ok(dto);
    }

    @Override
    public RpcResponse saveSellerConfig(SellerConfigDTO req) {
        SellerTradeConfig conf = tradeConfigConverter.toSellerTradeConfig(req);
        orderConfigService.saveSellerConfig(req.getSellerId(), conf);
        return RpcResponse.ok(null);
    }

    @Override
    public RpcResponse<GlobalConfigDTO> getDefaultConfig(GlobalConfigRpcReq req) {
        SellerTradeConfig conf = orderConfigService.getSellerConfigNoCache(0L);
        GlobalConfigDTO dto = tradeConfigConverter.toGlobalConfigDTO(conf);
        return RpcResponse.ok(dto);
    }

    @Override
    public RpcResponse saveDefaultConfig(GlobalConfigDTO req) {
        SellerTradeConfig conf = tradeConfigConverter.toSellerTradeConfig(req);
        orderConfigService.saveSellerConfig(0L, conf);
        return RpcResponse.ok(null);
    }
}
