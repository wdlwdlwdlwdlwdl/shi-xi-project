package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.confirmv2;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.confirm.ConfirmOrderInfoRpcReq;
import com.aliyun.gts.gmall.platform.trade.common.constants.DeliveryTypeEnum;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.UserPickLogisticsService;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.user.Customer;
import com.aliyun.gts.gmall.platform.trade.domain.repository.UserRepository;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderConfirm;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Objects;

/**
 * 订单确认 step2
 *    下单用户check
 *    查询下单用户 必须存在
 * @anthor shifeng
 * @version 1.0.1
 * 2024-12-11 16:54:10
 */
@Component
public class ConfirmOrderNewCustomerHandler extends AdapterHandler<TOrderConfirm> {

    // 年龄
    @NacosValue(value = "${trade.cart.age-limit:21}", autoRefreshed = true)
    private Integer age;

    // 用户
    @Autowired
    private UserRepository userRepository;

    // 物流方式
    @Autowired
    private UserPickLogisticsService userPickLogisticsService;

    @Override
    public void handle(TOrderConfirm inbound) {
        // 入参
        ConfirmOrderInfoRpcReq confirmOrderInfoRpcReq = inbound.getReq();
        // 整体对象
        CreatingOrder creatingOrder = inbound.getDomain();
        Customer customer = userRepository.getCustomerRequired(confirmOrderInfoRpcReq.getCustId());
        // 没查到用户，直接报错
        if (Objects.isNull(customer) || Objects.isNull(customer.getCustId())) {
            throw new GmallException(OrderErrorCode.USER_NOT_EXISTS);
        }
        creatingOrder.setCustId(customer.getCustId());
        // 获取用户年龄
        if (Objects.nonNull(customer.getBirthDay())){
            // 计算年龄
            // 获取当前日期
            LocalDate today = LocalDate.now();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(customer.getBirthDay());
            // 计算年龄 没有年龄不可以买
            Integer age = today.getYear() - calendar.get(Calendar.YEAR);
            creatingOrder.setAge(age);
        }
        // 物流pick
        creatingOrder.setPvzPick(userPickLogisticsService.existUserPick(customer.getCustId(), DeliveryTypeEnum.PVZ.getScript()));
        creatingOrder.setPostamatPick(userPickLogisticsService.existUserPick(customer.getCustId(), DeliveryTypeEnum.POSTAMAT.getScript()));
        for (MainOrder mainOrder : creatingOrder.getMainOrders()) {
            mainOrder.setCustomer(customer);
            mainOrder.setFirstName(customer.getFirstName());
            mainOrder.setLastName(customer.getLastName());
            if (CollectionUtils.isNotEmpty(mainOrder.getSubOrders())) {
                for (SubOrder subOrder : mainOrder.getSubOrders()) {
                    subOrder.setFirstName(customer.getFirstName());
                    subOrder.setLastName(customer.getLastName());
                    // 未满21岁的不能卖21禁商品
                    if (Boolean.TRUE.equals(subOrder.getItemSku().getAgeCategory()) &&
                        creatingOrder.getAge() < age) {
                        throw new GmallException(OrderErrorCode.USER_AGE_ILLEGAL);
                    }
                }
            }
        }
    }
}
