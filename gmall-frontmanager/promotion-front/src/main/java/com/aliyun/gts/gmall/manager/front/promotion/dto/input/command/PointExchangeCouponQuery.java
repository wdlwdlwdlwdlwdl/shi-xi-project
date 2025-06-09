package com.aliyun.gts.gmall.manager.front.promotion.dto.input.command;

import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestCommand;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotNull;

@ApiModel("积分兑换优惠券")
@Data
public class PointExchangeCouponQuery extends LoginRestCommand {

    /**
     * 消耗积分
     */
    private Long assets;

    /**
     * 营销类型
     */
    private Integer changeType;

    /**
     * 这个是放到消息中用来查询用的
     */
    private Long loginCustId;

    /**
     * 券ID领券；必填
     */
    public  String  couponCode;
    /**
     * 唯一幂等ID;领券幂等；必填
     */
    public  String  bizId;

    /**
     * 积分优惠券id
     */
    public  Long  couponId;

    /**
     * 应用来源
     */
    @NotNull(message="points.coupon.is.benefit")
    private Integer app = 3;

}
