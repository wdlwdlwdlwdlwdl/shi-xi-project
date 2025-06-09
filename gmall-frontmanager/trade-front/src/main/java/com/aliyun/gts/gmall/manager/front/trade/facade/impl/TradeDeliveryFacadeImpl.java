package com.aliyun.gts.gmall.manager.front.trade.facade.impl;


import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.manager.front.trade.convertor.DeliveryConvertor;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.command.DeliveryAccessQuery;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.command.DeliveryAddressQuery;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.command.DeliveryTimeQuery;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.DeliveryAddressVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.DeliveryTimeVO;
import com.aliyun.gts.gmall.manager.front.trade.facade.TradeDeliveryFacade;
import com.aliyun.gts.gmall.platform.item.api.facade.commercial.CommercialReadFacade;
import com.aliyun.gts.gmall.platform.open.customized.api.dto.input.DeliveryAddressReq;
import com.aliyun.gts.gmall.platform.open.customized.api.dto.input.DeliveryTimeReq;
import com.aliyun.gts.gmall.platform.open.customized.api.dto.output.DeliveryAddressResp;
import com.aliyun.gts.gmall.platform.open.customized.api.dto.output.DeliveryTimeResp;
import com.aliyun.gts.gmall.platform.open.customized.api.facade.DeliveryFacade;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CityRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.DeliveryAccessDTO;
import com.aliyun.gts.gmall.platform.trade.api.facade.tradeconf.CityFacade;
import lombok.extern.slf4j.Slf4j;


/**
 * 物流操作接口
 *
 * @author yangl
 */
@Slf4j
@Service
public class TradeDeliveryFacadeImpl implements TradeDeliveryFacade {
    @Autowired
    private DeliveryConvertor deliveryConvertor;
    @Autowired
    private DeliveryFacade deliveryFacade;

    @Autowired
    private CommercialReadFacade commercialReadFacade;

    @Autowired
    private CityFacade cityFacade;

    @Override
    public DeliveryTimeVO queryDeliveryTime(DeliveryTimeQuery req) {
        DeliveryTimeReq rpc = new DeliveryTimeReq();
        BeanUtils.copyProperties(req, rpc);
        RpcResponse<DeliveryTimeResp> resp = deliveryFacade.queryDeliveryTime(rpc);
        return deliveryConvertor.convert(resp.getData());
    }

    @Override
    public DeliveryAddressVO queryDeliveryAddress(DeliveryAddressQuery req) {
        DeliveryAddressReq rpc = new DeliveryAddressReq();
        BeanUtils.copyProperties(req, rpc);
        RpcResponse<DeliveryAddressResp> resp = deliveryFacade.queryDeliveryAddress(rpc);
        return deliveryConvertor.convert(resp.getData());
    }

//    @Override
//    public DeliveryTypeDetailDTO queryDeliveryType(DeliveryTypeQueryReq req) {
//        //TODO 替换新方法
//        //RpcResponse<DeliveryTypeDTO> result = commercialReadFacade.queryDeliveryType(req);
//        return null; //result.getData();
//    }

    @Override
    public DeliveryAccessDTO queryDeliveryAccess(DeliveryAccessQuery req) {
        CityRpcReq rpc = new CityRpcReq();
        rpc.setCityCode(req.getCityCode());
        rpc.setLatitude(req.getLatitude());
        rpc.setLongitude(req.getLongitude());
        RpcResponse<DeliveryAccessDTO> rpcResponse = cityFacade.queryDeliveryAccess(rpc);
        return rpcResponse.getData();
    }
}