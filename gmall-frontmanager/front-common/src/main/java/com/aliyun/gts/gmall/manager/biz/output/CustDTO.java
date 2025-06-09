package com.aliyun.gts.gmall.manager.biz.output;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/11/10 17:26
 */
@Data
public class CustDTO implements Serializable {
    @ApiModelProperty("自增主键")
    private Long custId;
    @ApiModelProperty("昵称")
    private String nick;
    @ApiModelProperty("phone")
    private String phone;
    @ApiModelProperty("头像")
    private String head_url;
    @ApiModelProperty("用户唯一标识符字段")
    private String custPrimary;
    @ApiModelProperty("身份标识符")
    private String iin;
    @ApiModelProperty("鉴权token")
    private String token;
    @ApiModelProperty("名字")
    private String firstName;
    @ApiModelProperty("名字")
    private String middleName;
    @ApiModelProperty("名字")
    private String lastName;
    @ApiModelProperty("认证token")
    private String casLoginToken;
    @ApiModelProperty("生日日期")
    private Date birthDay;
    @ApiModelProperty("halyk id")
    private String accountId;
    @ApiModelProperty("casKey")
    private String casKey;
    @ApiModelProperty("language")
    private String language;
}