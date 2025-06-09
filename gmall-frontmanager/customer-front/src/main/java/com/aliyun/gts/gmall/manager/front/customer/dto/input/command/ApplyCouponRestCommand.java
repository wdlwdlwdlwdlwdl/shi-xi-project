package com.aliyun.gts.gmall.manager.front.customer.dto.input.command;

import javax.validation.constraints.NotNull;

import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestCommand;
import lombok.Data;

/**
 * @author GTS
 * @date 2021/03/12
 */
@Data
public class ApplyCouponRestCommand extends LoginRestCommand {
    /**
     * 券ID领券；必填
     */
    @NotNull(message="coupon.code.required")
    public  String  couponCode;
    /**
     * 卖家id;尽可能传过来
     */
    @NotNull(message="seller.id.required")
    public  Long    sellerId;
    /**
     * 唯一幂等ID;领券幂等；必填
     */
    @NotNull(message="idempotent.id.required")
    public  String  bizId;
    /**
     * 应用来源
     */
    @NotNull(message="app.required")
    private Integer app = 1;

    @Override
    public void checkInput() {
        super.checkInput();
    }
}