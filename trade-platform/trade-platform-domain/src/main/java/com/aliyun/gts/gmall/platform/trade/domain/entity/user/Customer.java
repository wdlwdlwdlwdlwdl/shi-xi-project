package com.aliyun.gts.gmall.platform.trade.domain.entity.user;

import com.aliyun.gts.gmall.platform.trade.common.annotation.SearchMapping;
import com.aliyun.gts.gmall.platform.trade.domain.entity.AbstractBusinessEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class Customer extends AbstractBusinessEntity {

    @ApiModelProperty("买家id")
    @SearchMapping("cust_id")
    private Long custId;
    @ApiModelProperty("买家name")
    @SearchMapping("cust_name")
    private String custName;
    @ApiModelProperty("iin 身份证号")
    private String iin;
    private String uid;
    @ApiModelProperty("first_name")
    @SearchMapping("first_name")
    private String firstName;
    @SearchMapping("last_name")
    @ApiModelProperty("last_name")
    private String lastName;
    @ApiModelProperty("email")
    private String email;
    @ApiModelProperty("phone")
    private String phone;
    @ApiModelProperty("countryCode")
    private String countryCode;
    @ApiModelProperty("生日")
    private Date birthDay;
    @ApiModelProperty("语言")
    private String language;
}
