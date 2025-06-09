package com.aliyun.gts.gmall.manager.front.b2bcomm.output;

import lombok.Data;

import java.io.Serializable;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/4/25 16:12
 */
@Data
public class OperatorVO implements Serializable {
    /**
     * 当前登陆的Operator的id
     */
    private Long     operatorId;
    /**
     * 登陆的商家id
     */
    private Long     purchaserId;
    /**
     * token信息
     */
    private String token;
    /**
     * 账户类型
     * com.aliyun.gts.gmall.platform.user.api.dto.contants.OperatorTypeEnum
     */
    private Integer type;
}
