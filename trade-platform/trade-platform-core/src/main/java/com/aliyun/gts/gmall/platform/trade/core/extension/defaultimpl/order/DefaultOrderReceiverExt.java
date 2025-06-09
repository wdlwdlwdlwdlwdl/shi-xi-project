package com.aliyun.gts.gmall.platform.trade.core.extension.defaultimpl.order;

import com.aliyun.gts.gmall.framework.api.rpc.dto.ByIdCommandRequest;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.item.api.dto.output.warehouse.WarehouseDTO;
import com.aliyun.gts.gmall.platform.item.api.facade.warehouse.WarehouseReadFacade;
import com.aliyun.gts.gmall.platform.open.customized.api.constant.DeliveryConstants;
import com.aliyun.gts.gmall.platform.open.customized.api.dto.input.DeliveryPickupAddressReq;
import com.aliyun.gts.gmall.platform.open.customized.api.dto.output.PointResp;
import com.aliyun.gts.gmall.platform.open.customized.api.facade.DeliveryFacade;
import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.common.constants.LogisticsTypeEnum;
import com.aliyun.gts.gmall.platform.trade.core.extension.order.create.OrderReceiverExt;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.ReceiveAddr;
import com.aliyun.gts.gmall.platform.trade.domain.repository.ReceiverRepository;
import com.aliyun.gts.gmall.platform.user.api.enums.DeliveryMethodEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
public class DefaultOrderReceiverExt implements OrderReceiverExt {

    @Autowired
    private ReceiverRepository receiverRepository;

    @Autowired
    private WarehouseReadFacade warehouseReadFacade;

    @Autowired
    private DeliveryFacade deliveryFacade;

    /**
     * 收货地址校验
     * @param custId
     * @param addr
     * @return
     */
    @Override
    public TradeBizResult<ReceiveAddr> checkOnConfirmOrder(Long custId, ReceiveAddr addr) {
        ReceiveAddr result = null;
        if(Objects.nonNull(addr)) {
            if (addr.getReceiverId() != null) {
                // 取ID指定的收货地址
                result = receiverRepository.getReceiverById(custId, addr.getReceiverId());
                if (Objects.nonNull(result)) {
                    Long refAddressId = result.getRefAddressId();
                    if (Objects.nonNull(refAddressId) && Objects.nonNull(result.getDeliveryMethod())) {
                        buildReferAddressInfo(result, refAddressId, result.getDeliveryMethod());

                    }
                }
            } else {
                // 取传过来的地址
                result = new ReceiveAddr();
                BeanUtils.copyProperties(addr, result);
            }
        }
        //确认订单时候，不再校验地址，全部在chekout页面校验，也就是在创建订单时候校验
//        if (result == null) {
//            return TradeBizResult.fail(OrderErrorCode.RECEIVER_NOT_EXISTS);
//        }
//        if (!result.checkProvinceCityDistrict()) {
//            return TradeBizResult.fail(OrderErrorCode.RECEIVER_NOT_EXISTS);
//        }
        return TradeBizResult.ok(result);
    }

    /**
     * 不同的物流方式获取关联的物流信息
     * @param receiveAddr 用户地址
     * @param refAddressId 关联的地址id
     * @param deliveryMethod 关联的物流方式
     */
    private void buildReferAddressInfo(ReceiveAddr receiveAddr, Long refAddressId, String deliveryMethod) {
        DeliveryMethodEnum deliveryMethodEnum = DeliveryMethodEnum.find(deliveryMethod);
        if(Objects.isNull(deliveryMethodEnum))
        {
            log.warn("no DeliveryMethodEnum  support deliveryMethod:{}", deliveryMethod);
            return;
        }
        switch(deliveryMethodEnum) {
            case PICK_UP:
                ByIdCommandRequest var1 = new ByIdCommandRequest();
                var1.setId(refAddressId);
                RpcResponse<WarehouseDTO> resultRes = warehouseReadFacade.queryById(var1);
                if (Objects.nonNull(resultRes) && Objects.nonNull(resultRes.getData())) {
                    receiveAddr.setWarehouseBusinessHoursDTOList(resultRes.getData().getWarehouseBusinessHoursList());
                    receiveAddr.setSchedule(resultRes.getData().getBusinessHours());
                }
                return;
            case PVZ:
                DeliveryPickupAddressReq req = new DeliveryPickupAddressReq();
                req.setPickUpPointId(String.valueOf(refAddressId));
                req.setCityCode(receiveAddr.getCityCode());
                req.setType(DeliveryConstants.PVZ);
                RpcResponse<PointResp> resultResPVZ = deliveryFacade.queryDeliveryAddressByPickUpPointId(req);
                if (resultResPVZ.isSuccess() && Objects.nonNull(resultResPVZ.getData())) {
                    receiveAddr.setSchedule(resultResPVZ.getData().getSchedule());
                }
                return;
            case POSAMAT:
                DeliveryPickupAddressReq deliveryPickupAddressReq = new DeliveryPickupAddressReq();
                deliveryPickupAddressReq.setPickUpPointId(String.valueOf(refAddressId));
                deliveryPickupAddressReq.setCityCode(receiveAddr.getCityCode());
                deliveryPickupAddressReq.setType(DeliveryConstants.POSTAMAT);
                deliveryPickupAddressReq.setLockerSize("L");
                RpcResponse<PointResp> resultPostamat = deliveryFacade.queryDeliveryAddressByPickUpPointId(deliveryPickupAddressReq);
                if (resultPostamat.isSuccess() && Objects.nonNull(resultPostamat.getData())) {
                    receiveAddr.setSchedule(resultPostamat.getData().getSchedule());
                }
            default:
                log.warn("DeliveryMethodEnum not support:{}", deliveryMethodEnum);
        }


    }

    /**
     * 收货地址校验
     * @param custId
     * @param addr
     * @return
     */
    @Override
    public TradeBizResult<ReceiveAddr> checkOnCreateOrder(Long custId, ReceiveAddr addr) {
        if (addr == null) {
            return TradeBizResult.fail(OrderErrorCode.RECEIVER_NOT_EXISTS);
        }
        if (addr.getReceiverId() == null || addr.getReceiverId().longValue() <= 0) {
            return TradeBizResult.fail(OrderErrorCode.RECEIVER_NOT_EXISTS);
        }
        ReceiveAddr result = receiverRepository.getReceiverById(custId, addr.getReceiverId());
        if (result == null) {
            return TradeBizResult.fail(OrderErrorCode.RECEIVER_NOT_EXISTS);
        }
        if (!result.checkAll()) {
            TradeBizResult.fail(OrderErrorCode.RECEIVER_NOT_EXISTS);
        }
        return TradeBizResult.ok(result);
    }

    @Override
    public void fillLogisticsInfo(CreatingOrder order) {
        for (MainOrder mainOrder : order.getMainOrders()) {
            mainOrder.orderAttr().setLogisticsType(LogisticsTypeEnum.REALITY.getCode());
        }
    }
}
