package com.aliyun.gts.gmall.manager.front.item.dto.output;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(value = "优惠券信息")
public class CouponInfoVO extends AbstractOutputInfo {

    /**
     * 优惠券code
     */
    private String couponCode;
    /**
     * 券面值
     */
    private String couponValue;

    /**
     * 过期时间
     */
    private Long expirationDate;
    /**
     * 使用条件
     */
    private String conditions;
    /**
     * 门店名称
     */
    private String storeName;
    /**
     * 是否KA
     */
    private Integer ka;
    /**
     * 领取状态:true 是
     */
    private Boolean owner;
}
