package com.aliyun.gts.gmall.manager.front.login.dto.input.query;


import com.aliyun.gts.gmall.framework.api.rest.dto.AbstractQueryRestRequest;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(description = "接收登录时下发积分详情配置")
public class GrantIntegralConfig extends AbstractQueryRestRequest {

    /**
     * 免登录送积分
     */
    private Boolean tradeNoLoginPoint;

    /**
     * 免登录送积分
     */
    private Long noLoginPointValue;


    /**
     * 注册送积分
     */
    private Boolean tradeRegisterPoint;

    /**
     * 注册送积分
     */
    private Long registerPointValue;

    /**
     * 注册送积分风控积分总量
     */
    private Long registerGrantTotalCount;



    /**
     * 积分失效类型 1是长期; 2是年; 3是月;
     */
    private Integer invalidType;
    /**
     * 积分失效年
     */
    private Integer invalidYear;
    /**
     * 积分失效月分
     */
    private Integer invalidMonth;



}
