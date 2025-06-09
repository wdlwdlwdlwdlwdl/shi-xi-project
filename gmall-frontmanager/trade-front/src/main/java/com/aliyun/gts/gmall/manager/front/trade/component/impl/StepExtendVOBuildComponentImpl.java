package com.aliyun.gts.gmall.manager.front.trade.component.impl;

import com.aliyun.gts.gmall.manager.front.trade.annotation.VoOrderType;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.ConfirmOrderRestQuery;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.Result;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.PayPriceVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.extend.OrderExtendVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.extend.StepExtendVO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.confirm.ConfirmOrderDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.confirm.OrderGroupDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.confirm.PayPriceDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.MainOrderDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.MainOrderDetailDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.StepOrderDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.SubOrderDTO;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderTypeEnum;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.aliyun.gts.gmall.manager.front.common.util.ItemUtils.nullZero;

@Component
/**
 * OrderTypeEnum.MULTI_STEP_ORDER
 */
@VoOrderType(2)
public class StepExtendVOBuildComponentImpl extends NormalExtendVOBuildComponentImpl {

    @Override
    public StepExtendVO buildExtend(MainOrderDetailDTO mainOrderDetailDTO) {
        OrderExtendVO sup = super.buildExtend(mainOrderDetailDTO);
        StepExtendVO ext = buildStepExtendVO(sup);
        fillPrice(ext, mainOrderDetailDTO);
        ext.setStepNo(mainOrderDetailDTO.getCurrentStepNo());
        ext.setStepStatus(mainOrderDetailDTO.getCurrentStepOrder().getStatus());
        return ext;
    }

    @Override
    public OrderExtendVO buildExtend(SubOrderDTO subOrderDTO, MainOrderDetailDTO mainOrderDetailDTO) {
        OrderExtendVO sup = super.buildExtend(subOrderDTO, mainOrderDetailDTO);
        sup.setOrderType(OrderTypeEnum.MULTI_STEP_ORDER.getCode());
        return sup;
    }

    @Override
    public Result<OrderExtendVO> buildExtend(ConfirmOrderRestQuery query, ConfirmOrderDTO confirmOrderDTO) {
        Result<OrderExtendVO> sup = super.buildExtend(query, confirmOrderDTO);
        if (!sup.isSuccess()) {
            return sup;
        }
        StepExtendVO ext = buildStepExtendVO(sup.getData());
        fillPrice(ext, confirmOrderDTO);
        return Result.success(ext);
    }

    @Override
    public Map<Long, OrderExtendVO> buildExtendList(Collection<MainOrderDTO> orderList) {
        Map<Long, OrderExtendVO> map = new HashMap<>();
        for (MainOrderDTO main : orderList) {
            StepExtendVO ext = buildStepExtendVO(null);
            fillPrice(ext, main);
            ext.setStepNo(main.getCurrentStepNo());
            ext.setStepStatus(main.getCurrentStepOrder().getStatus());
            map.put(main.getOrderId(), ext);
        }
        return map;
    }

    private StepExtendVO buildStepExtendVO(OrderExtendVO normal) {
        StepExtendVO ext = new StepExtendVO();
        ext.setNormal(normal);
        ext.setOrderType(OrderTypeEnum.MULTI_STEP_ORDER.getCode());
        if (normal != null) {
            normal.setOrderType(OrderTypeEnum.MULTI_STEP_ORDER.getCode());
        }
        return ext;
    }

    private void fillPrice(StepExtendVO ext, ConfirmOrderDTO confirmOrderDTO) {
        PayPriceDTO firstPay = confirmOrderDTO.getStepExtend().getFirstPay();
        PayPriceVO firstPayVO = new PayPriceVO();
        BeanUtils.copyProperties(firstPay, firstPayVO);

        PayPriceDTO remainPay = confirmOrderDTO.getStepExtend().getRemainPay();
        PayPriceVO remainPayVO = new PayPriceVO();
        BeanUtils.copyProperties(remainPay, remainPayVO);

        ext.setFirstPay(firstPayVO);
        ext.setRemainPay(remainPayVO);

        List<Map<String, String>> contextPropsList = new ArrayList<>();
        for (OrderGroupDTO group : confirmOrderDTO.getOrderGroups()) {
            contextPropsList.add(group.getStepContextProps());
        }
        ext.setConfirmContextProps(contextPropsList);
    }

    private void fillPrice(StepExtendVO ext, MainOrderDTO main) {
        PayPriceVO firstPrice = new PayPriceVO();
        PayPriceVO remainPrice = new PayPriceVO();
        boolean first = true;
        for (StepOrderDTO step : main.getStepOrders()) {
            if (first) {
                add(step, firstPrice);
                first = false;
            } else {
                add(step, remainPrice);
            }
        }
        remainPrice.setFreightAmt(main.getPrice().getFreightAmt());
        ext.setFirstPay(firstPrice);
        ext.setRemainPay(remainPrice);
        ext.setContextProps(main.getStepContextProps());
    }

    private static void add(StepOrderDTO value, PayPriceVO sumResult) {
        sumResult.setPointAmt(nullZero(sumResult.getPointAmt()) + nullZero(value.getPointAmt()));
        sumResult.setPointCount(nullZero(sumResult.getPointCount()) + nullZero(value.getPointCount()));
        sumResult.setRealAmt(nullZero(sumResult.getRealAmt()) + nullZero(value.getRealAmt()));
    }
}
