package com.aliyun.gts.gmall.manager.front.b2bcomm.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/2/3 18:44
 */
@ToString
@Data
public class OperatorDO implements Serializable {
    /**
     * token值
     */
    private String token;
    /**
     * 当前登陆的Operator的id
     */
    private Long     operatorId;
    /**
     * 登陆用户昵称
     */
    private String   nickname;
    /**
     * 用户名
     */
    private String   username;
    /**
     * 登陆的商家id
     */
    private Long purchaserId;
    /**
     * 头像
     */
    private String   headUrl;
    /**
     * 卖家账户;没有数据
     */
//    private PurchaserDO purchaserDO;
    /**
     * 登陆时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date loginTime;
    /**
     * 手机号
     */
    private String phone;
    /**
     * AccountTypeEnum.java
     */
    private Integer type;

    private boolean main;
    private Long mainAccountId;
}
