package com.aliyun.gts.gmall.manager.front.trade.component.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.aliyun.gts.gmall.center.item.api.dto.output.EvoucherPeriodDTO;
import com.aliyun.gts.gmall.center.item.common.consts.ItemExtendConstant;
import com.aliyun.gts.gmall.center.trade.api.dto.input.EvoucherQueryRpcReq;
import com.aliyun.gts.gmall.center.trade.api.dto.output.EvoucherDTO;
import com.aliyun.gts.gmall.center.trade.api.facade.EvoucherFacade;
import com.aliyun.gts.gmall.center.trade.common.constants.ExtOrderType;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.manager.front.trade.adaptor.TradeAdapter;
import com.aliyun.gts.gmall.manager.front.trade.annotation.VoOrderType;
import com.aliyun.gts.gmall.manager.front.trade.component.OrderExtendVOBuildComponent;
import com.aliyun.gts.gmall.manager.front.trade.convertor.TradeResponseConvertor;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.ConfirmOrderRestQuery;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.Result;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.OrderSubVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.extend.EvoucherInfoVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.extend.EvoucherTimeVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.extend.EvoucherVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.extend.OrderExtendVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.extend.SkuExtendVO;
import com.aliyun.gts.gmall.platform.item.api.dto.output.item.ItemDTO;
import com.aliyun.gts.gmall.platform.item.api.utils.ItemExtendUtil;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.confirm.ConfirmOrderDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.MainOrderDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.MainOrderDetailDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.SubOrderDTO;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
/**
 * ExtOrderType.EVOUCHER
 */
@VoOrderType(10)
public class EvoucherVOBuildComponentImpl implements OrderExtendVOBuildComponent {

    @Autowired
    EvoucherFacade evoucherFacade;

    @Autowired
    TradeResponseConvertor tradeResponseConvertor;

    @Autowired
    TradeAdapter tradeAdapter;

    @Override
    public OrderExtendVO buildExtend(MainOrderDetailDTO mainOrderDetailDTO) {
        return null;
    }

    @Override
    public OrderExtendVO buildExtend(SubOrderDTO subOrderDTO , MainOrderDetailDTO mainOrderDetailDTO) {
        EvoucherVO evoucherVO = new EvoucherVO();
        EvoucherQueryRpcReq req = new EvoucherQueryRpcReq();
        req.setOrderId(subOrderDTO.getOrderId());

        RpcResponse<List<EvoucherDTO>> response = evoucherFacade.queryEvouchers(req);
        if(response.isSuccess()){
            List<EvoucherDTO> evoucherDTOS = response.getData();
            List<EvoucherInfoVO> list = evoucherDTOS.stream().map(e->{
                EvoucherInfoVO evoucherInfoVO = tradeResponseConvertor.convertEvoucher(e);
                evoucherInfoVO.calcStatus();
                return evoucherInfoVO;
            }).collect(Collectors.toList());
            evoucherVO.setEvouchers(list);
        }
        evoucherVO.setOrderType(ExtOrderType.EVOUCHER.getCode());

        return evoucherVO;
    }

    @Override
    public Result<OrderExtendVO> buildExtend(ConfirmOrderRestQuery query, ConfirmOrderDTO confirmOrderDTO) {
        Long itemId = query.getOrderItems().get(0).getItemId();
        List<ItemDTO> itemList = tradeAdapter.queryItemDTOByIds(Sets.newHashSet(itemId));
        EvoucherPeriodDTO dto = ItemExtendUtil.getExtendObject(ItemExtendConstant.EVOUCHER_PERIOD, itemList.get(0), EvoucherPeriodDTO.class);
        if (Objects.nonNull(dto)) {
            EvoucherTimeVO evoucherTimeVO = new EvoucherTimeVO(dto);
            evoucherTimeVO.setOrderType(ExtOrderType.EVOUCHER.getCode());
            Result<OrderExtendVO> result = Result.success(evoucherTimeVO);
            return result;
        }
        EvoucherTimeVO evoucherTimeVO = new EvoucherTimeVO();
        evoucherTimeVO.setOrderType(ExtOrderType.EVOUCHER.getCode());
        return Result.success(evoucherTimeVO);
    }

    @Override
    public Map<Long, OrderExtendVO> buildExtendList(Collection<MainOrderDTO> orderList) {
        return null;
    }
}
