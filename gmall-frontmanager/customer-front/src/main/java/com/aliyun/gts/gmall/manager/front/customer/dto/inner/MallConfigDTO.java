package com.aliyun.gts.gmall.manager.front.customer.dto.inner;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 商城设置
 */
@Data
public class MallConfigDTO {

    public static final String DICT_KEY = "mall_config";


    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("描述")
    private String desc;

    @ApiModelProperty("logo")
    private String logoUrl;

    @ApiModelProperty("域名")
    private String domainName;

    @ApiModelProperty("ICP备案号")
    private String icpNo;

    @ApiModelProperty("开启店铺会员")
    private Boolean shopCVipEnabled;

    @ApiModelProperty("开启店铺大客户")
    private Boolean shopBVipEnabled;
}
