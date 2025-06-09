package com.aliyun.gts.gmall.platform.trade.domain.entity.user;

import com.aliyun.gts.gmall.platform.trade.common.annotation.SearchMapping;
import com.aliyun.gts.gmall.platform.trade.domain.entity.AbstractBusinessEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

@Data
public class Seller extends AbstractBusinessEntity {


    @ApiModelProperty("卖家id")
    @SearchMapping("seller_id")
    private Long sellerId;

    @ApiModelProperty("卖家名称, 优先为店铺名称")
    @SearchMapping("seller_name")
    private String sellerName;

    @ApiModelProperty("卖家BIN/IIN")
    private String sellerBin;

    @ApiModelProperty("卖家支付账号")
    private SellerAccountInfo sellerAccountInfo;

    @ApiModelProperty("卖家状态")
    private String sellerStatus;

    @ApiModelProperty("卖家扩展字段")
    private Map<String, String> features;

    @ApiModelProperty("卖家扩展结构")
    private Map<String, Map<String, String>> sellerExtends;

    @ApiModelProperty("卖家BIN/IIN")
    private String binOrIin;

    @ApiModelProperty("手机号码")
    private String phone;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("地址")
    private String address;

    @ApiModelProperty("logo")
    private String headUrl;

    private String shopName;

}
