package com.aliyun.gts.gmall.manager.front.trade.dto.input.command;

import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestCommand;
import com.aliyun.gts.gmall.platform.open.customized.api.dto.input.MerchantDeliveryReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: yangl
 * @Date: 20224/8/9 10:37
 * @Desc: 物流时效Request
 */
@Data
public class DeliveryAccessQuery extends LoginRestCommand {

    @ApiModelProperty(value = "城市code", required = true)
    private String cityCode;
    @ApiModelProperty(value = "经度", required = true)
    private String latitude;
    @ApiModelProperty(value = "纬度", required = true)
    private String longitude;

    @Override
    public void checkInput() {
        ParamUtil.nonNull(this.cityCode, I18NMessageUtils.getMessage("city.code.required"));
        ParamUtil.nonNull(this.latitude, I18NMessageUtils.getMessage("city.latitude.required"));
        ParamUtil.nonNull(this.longitude, I18NMessageUtils.getMessage("city.longitude.required"));
    }
}
