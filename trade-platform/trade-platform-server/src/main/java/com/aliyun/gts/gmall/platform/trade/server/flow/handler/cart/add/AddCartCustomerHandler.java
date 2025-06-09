package com.aliyun.gts.gmall.platform.trade.server.flow.handler.cart.add;

import com.aliyun.gts.gmall.platform.trade.api.constant.CartErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.cart.AddCartRpcReq;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.CartItem;
import com.aliyun.gts.gmall.platform.trade.domain.entity.user.Customer;
import com.aliyun.gts.gmall.platform.trade.domain.repository.UserRepository;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TCartAdd;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Objects;

/**
 * 加车查询会员
 * 做会员ID和年龄check
 */
@Component
public class AddCartCustomerHandler extends TradeFlowHandler.AdapterHandler<TCartAdd> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void handle(TCartAdd inbound) {
        CartItem cart = inbound.getDomain();
        // 获取入参
        AddCartRpcReq addCartRpcReq =  inbound.getReq();
        // 查询用户信息
        Customer customer = userRepository.getCustomerRequired(addCartRpcReq.getCustId());
        if (customer == null) {
            inbound.setError(CartErrorCode.CART_USER_NOT_EXISTS);
            return;
        }
        // 获取用户年龄 理论上年龄一定存在
        if (Objects.nonNull(customer.getBirthDay())){
            // 计算年龄
            // 获取当前日期
            LocalDate today = LocalDate.now();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(customer.getBirthDay());
            // 计算年龄
            Integer age = today.getYear() - calendar.get(Calendar.YEAR);
            cart.setAge(age);
        }
    }
}
