package com.aliyun.gts.gmall.manager.front.item.dto.output;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * 收货地址
 *
 * @author tiansong
 */
@Data
@ApiModel("收货地址")
public class AddressVO {
    @ApiModelProperty("自增ID")
    private Long id;
    @ApiModelProperty("买家ID")
    private Long custId;
    @ApiModelProperty("省")
    private String province;
    @ApiModelProperty("市")
    private String city;
    @ApiModelProperty("区")
    private String area;
    @ApiModelProperty("省ID")
    private Long provinceId;
    @ApiModelProperty("市ID")
    private Long cityId;
    @ApiModelProperty("区ID")
    private Long areaId;
    @ApiModelProperty("详细地址信息")
    private String addressDetail;
    @ApiModelProperty("完整地址")
    private String completeAddr;
    @ApiModelProperty("邮编")
    private String postCode;
    @ApiModelProperty("联系人姓名")
    private String name;
    @ApiModelProperty("联系人手机")
    private String phone;
    @ApiModelProperty("是否默认地址")
    private Boolean defaultYn;

    public String getAddressSimple() {
        if (StringUtils.isAnyBlank(province, city)) {
            return null;
        }
        return province.equals(city) ? (city + area) : (province + city + area);
    }
}