package com.aliyun.gts.gmall.manager.front.item.dto.input.query;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import java.util.Arrays;

import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestQuery;
import com.aliyun.gts.gmall.manager.front.item.dto.output.AddressVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 商详物流及运费请求
 *
 * @author tiansong
 */
@Data
@ApiModel("商详运费请求")
public class ItemFreightRestQuery extends LoginRestQuery {
    @ApiModelProperty(value = "商品ID", required = true)
    private Long      itemId;
    @ApiModelProperty("SkuId")
    private Long      skuId;
    @ApiModelProperty("收货地址ID")
    private Long      addressId;
    @ApiModelProperty("收货地址详情（仅透传使用）")
    private AddressVO addressVO;

    @Override
    public void checkInput() {
        // 只有传递用户的收货地址ID时，才进行校验用户的登录情况
        if (addressId != null) {
            super.checkInput();
        }
        ParamUtil.nonNull(itemId, I18NMessageUtils.getMessage("product")+" [ID] "+I18NMessageUtils.getMessage("cannot.be.empty"));  //# "商品ID不能为空"
        if (addressVO != null) {
            ParamUtil.nonNull(addressVO.getProvinceId(), I18NMessageUtils.getMessage("address.province")+" [ID] "+I18NMessageUtils.getMessage("cannot.be.empty"));  //# "地址省份ID不能为空"
            ParamUtil.nonNull(addressVO.getCityId(), I18NMessageUtils.getMessage("address.city")+" [ID] "+I18NMessageUtils.getMessage("cannot.be.empty"));  //# "地址城市ID不能为空"
            ParamUtil.nonNull(addressVO.getAreaId(), I18NMessageUtils.getMessage("address.district")+" [ID] "+I18NMessageUtils.getMessage("cannot.be.empty"));  //# "地址区ID不能为空"
            ParamUtil.nonBlankElements(Arrays.asList(addressVO.getProvince(), addressVO.getCity(), addressVO.getArea()),
                I18NMessageUtils.getMessage("address.province.city.district.name.required"));  //# "地址省市区名称不能为空"
        }
    }
}
