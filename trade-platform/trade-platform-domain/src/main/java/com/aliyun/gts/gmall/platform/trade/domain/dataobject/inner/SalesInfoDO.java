package com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner;

import com.aliyun.gts.gmall.framework.domain.extend.ExtendComponent;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SalesInfoDO extends ExtendComponent {

    @ApiModelProperty(value = "详细收货地址, 含省市区信息")
    private String deliveryAddr;

    @ApiModelProperty(value = "发件人姓名")
    private String name;

    @ApiModelProperty(value = "发件人联系电话")
    private String phone;

    @ApiModelProperty("市")
    private String city;

    @ApiModelProperty("市")
    private String cityCode;

    @ApiModelProperty("country")
    private String country;

    @ApiModelProperty("邮编")
    private String postCode;

    @ApiModelProperty("经度")
    private String longitude;

    @ApiModelProperty("纬度")
    private String latitude;

    @ApiModelProperty(value = "数量")
    private int seatNum;

    @ApiModelProperty("是否DC")
    private String isDc;
}
