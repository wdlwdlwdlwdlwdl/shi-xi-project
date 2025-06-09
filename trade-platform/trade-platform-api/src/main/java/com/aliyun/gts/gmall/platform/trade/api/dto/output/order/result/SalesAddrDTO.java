package com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 说明： TODO
 *
 * @author yangl
 * @version 1.0
 * @date 2024/12/19 14:10
 */
@Data
public class SalesAddrDTO extends AbstractOutputInfo {

    @ApiModelProperty(value = "详细收货地址, 含省市区信息")
    private String deliveryAddr;

    @ApiModelProperty(value = "发件人姓名")
    private String name;

    @ApiModelProperty(value = "发件人联系电话")
    private String phone;

    @ApiModelProperty("市")
    private String city;

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

    @ApiModelProperty(value = "是否DC")
    private String isDc;


}
