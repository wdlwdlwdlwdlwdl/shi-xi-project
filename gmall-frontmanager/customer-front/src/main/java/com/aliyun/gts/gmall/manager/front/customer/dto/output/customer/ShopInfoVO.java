package com.aliyun.gts.gmall.manager.front.customer.dto.output.customer;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class ShopInfoVO extends AbstractOutputInfo {

    @ApiModelProperty("卖家ID")
    private Long   sellerId;
    @ApiModelProperty("店铺名字")
    private String name;
    @ApiModelProperty("描述")
    private String desc;
    @ApiModelProperty("logo")
    private String logoUrl;
    @ApiModelProperty("域名")
    private String domainName;
    @ApiModelProperty("ICP备案号")
    private String icpNo;
    @ApiModelProperty("店铺联系地址")
    private String address;
    @ApiModelProperty("联系电话")
    private String contactPhone;
    @ApiModelProperty("是否自营店铺")
    private Boolean selfRun;
    @ApiModelProperty("综合评分")
    private String totalScore;
    @ApiModelProperty(value = "销量")
    private String saleCountAll;
    @ApiModelProperty(value = "关注数量")
    private String followerCount;
    @ApiModelProperty(value = "商品信息")
    private List<ShopItemInfoVO> itemInfoVOS;
}
