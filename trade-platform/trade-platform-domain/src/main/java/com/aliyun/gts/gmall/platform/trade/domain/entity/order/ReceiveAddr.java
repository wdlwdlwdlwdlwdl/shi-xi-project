package com.aliyun.gts.gmall.platform.trade.domain.entity.order;

import com.aliyun.gts.gmall.platform.item.api.dto.output.warehouse.WarehouseBusinessHoursDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;

/**
 * 收货地址
 */
@Data
public class ReceiveAddr implements Serializable {

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

    @ApiModelProperty("关联的自提点原有ID")
    private Long refAddressId;

    @ApiModelProperty("关联的物流方式")
    private String deliveryMethod;

    @ApiModelProperty("门牌号")
    private String apartmentNo;

    @ApiModelProperty("街道信息")
    private String addressDetail;

    @ApiModelProperty("仓库时间信息")
    List<WarehouseBusinessHoursDTO> warehouseBusinessHoursDTOList;

    @ApiModelProperty("营业时间")
    private String schedule;

    public boolean checkProvinceCityDistrict() {
        return StringUtils.isNumeric(provinceCode)
                && StringUtils.isNumeric(cityCode)
                && StringUtils.isNumeric(districtCode);
    }

    public boolean checkAll() {
        return receiverId != null &&
            StringUtils.isNumeric(cityCode) &&
            StringUtils.isNotBlank(deliveryAddr) &&
            StringUtils.isNotBlank(receiverName) &&
            StringUtils.isNotBlank(phone);
    }
}
