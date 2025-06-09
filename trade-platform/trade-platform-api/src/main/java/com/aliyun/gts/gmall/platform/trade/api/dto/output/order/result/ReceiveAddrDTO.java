package com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 说明： TODO
 *
 * @author yangl
 * @version 1.0
 * @date 2024/12/19 14:12
 */
@Data
public class ReceiveAddrDTO extends AbstractOutputInfo {
    @ApiModelProperty(value = "收件地址ID")
    private Long receiverId;

    @ApiModelProperty(value = "省编码")
    private String provinceCode;

    @ApiModelProperty(value = "市编码")
    private String cityCode;

    @ApiModelProperty(value = "区县编码")
    private String districtCode;

    @ApiModelProperty(value = "街道编码")
    private String streetCode;

    @ApiModelProperty(value = "详细收货地址, 含省市区信息")
    private String deliveryAddr;

    @ApiModelProperty(value = "收件人姓名")
    private String receiverName;

    @ApiModelProperty(value = "收件人联系电话")
    private String phone;

    @ApiModelProperty("省")
    private String province;

    @ApiModelProperty("市")
    private String city;

    @ApiModelProperty("区")
    private String area;

    @ApiModelProperty("邮编")
    private String postCode;

    @ApiModelProperty("首名称")
    private String firstName;

    @ApiModelProperty("最后名称")
    private String lastName;

    @ApiModelProperty("中间名称")
    private String middleName;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("经度")
    private String longitude;

    @ApiModelProperty("纬度")
    private String latitude;

    @ApiModelProperty("公司名称")
    private String companyName;

    @ApiModelProperty("邮箱地址")
    private String email;

    private String countryCode;
}
