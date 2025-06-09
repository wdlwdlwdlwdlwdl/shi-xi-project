package com.aliyun.gts.gmall.manager.front.trade.dto.input.param;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.dto.AbstractInputParam;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 物流信息
 *
 * @author tiansong
 */
@Data
@ApiModel("物流信息")
public class LogisticsInfo extends AbstractInputParam {
    @ApiModelProperty(value = "物流公司类型", required = true)
    private Integer companyType;
    @ApiModelProperty(value = "物流单号", required = true)
    private String logisticsId;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.nonNull(this.companyType, I18NMessageUtils.getMessage("logistics.company.type.required"));  //# "物流公司类型不能为空"
        ParamUtil.nonNull(this.logisticsId, I18NMessageUtils.getMessage("logistics.number.required"));  //# "物流单号不能为空"
    }
}
