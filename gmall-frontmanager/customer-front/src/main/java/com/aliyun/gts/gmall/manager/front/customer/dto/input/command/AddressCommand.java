package com.aliyun.gts.gmall.manager.front.customer.dto.input.command;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.manager.front.common.consts.BizConst;
import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestCommand;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 收货地址的新增和修改
 *
 * @author tiansong
 */
@ApiModel(description = "收货地址的新增和修改")
@Data
public class AddressCommand extends LoginRestCommand {
    @ApiModelProperty(value = "收货地址ID")
    private Long    id;
    @ApiModelProperty(value = "收件人名称")
    private String  name;
    @ApiModelProperty(value = "收件人手机")
    private String  phone;
    @ApiModelProperty(value = "省code")
    private String  provinceId;
    @ApiModelProperty(value = "市code", required = true)
    private String  cityId;
    @ApiModelProperty(value = "区code")
    private String  areaId;
    @ApiModelProperty(value = "街道code")
    private String  streetId;
    @ApiModelProperty(value = "地址详情", required = true)
    private String  addressDetail;
    @ApiModelProperty(value = "省", required = true)
    private String  province;
    @ApiModelProperty(value = "市")
    private String  city;
    @ApiModelProperty(value = "区")
    private String  area;
    @ApiModelProperty(value = "街道")
    private String  street;
    @ApiModelProperty(value = "是否默认收货地址")
    private Boolean defaultYn;
    @ApiModelProperty(value = "地址备注")
    private String  remark;

    @ApiModelProperty(value = "物流方式(HOME_DELIVERY:送货上门, PVZ:银行网点，POSAMAT:快递柜, PUCK_UP:卖家自提")
    private String deliveryMethod;

    @ApiModelProperty(value = "第三方地址ID")
    private Long refAddressId;

    @ApiModelProperty(value = "公寓或者办公室号")
    private String  apartmentNo;

    @ApiModelProperty(value = "经度")
    private BigDecimal longitude;

    @ApiModelProperty(value = "纬度")
    private BigDecimal latitude;





    @Override
    public void checkInput() {
        super.checkInput();
//        ParamUtil.nonNull(name, I18NMessageUtils.getMessage("receiver.name.required"));  //# "收件人名称不能为空"
//        ParamUtil.expectInRange(name.length(), 1, 128, "recipient.name.length");
//        ParamUtil.nonNull(phone, I18NMessageUtils.getMessage("receiver.phone.required"));  //# "收件人手机不能为空"
        ParamUtil.expectTrue(phone.matches(BizConst.REGEX_MOBILE), I18NMessageUtils.getMessage("phone.format.invalid"));  //# "手机号码格式不正确"
//        ParamUtil.nonNull(provinceId, I18NMessageUtils.getMessage("province")+"code"+I18NMessageUtils.getMessage("cannot.be.empty"));  //# "省份code不能为空"
        ParamUtil.nonNull(cityId, I18NMessageUtils.getMessage("city")+"code"+I18NMessageUtils.getMessage("cannot.be.empty"));  //# "市code不能为空"
//        ParamUtil.nonNull(areaId, I18NMessageUtils.getMessage("district")+"code"+I18NMessageUtils.getMessage("cannot.be.empty"));  //# "区code不能为空"
//        ParamUtil.nonNull(province, I18NMessageUtils.getMessage("province.required"));  //# "省份不能为空"
        ParamUtil.nonNull(city, I18NMessageUtils.getMessage("city.required"));  //# "市不能为空"
//        ParamUtil.nonNull(area, I18NMessageUtils.getMessage("district.required"));  //# "区不能为空"
        ParamUtil.nonNull(addressDetail, I18NMessageUtils.getMessage("address.details.required"));  //# "地址详情不能为空"
        ParamUtil.expectInRange(addressDetail.length(), 1, 256, I18NMessageUtils.getMessage("address.details.len.exceed")+"256");  //# "地址详情不要超过
    }
}