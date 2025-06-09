package com.aliyun.gts.gmall.platform.trade.api.dto.input.logistics;

import com.aliyun.gts.gmall.framework.api.rpc.dto.AbstractCommandRpcRequest;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import com.aliyun.gts.gmall.platform.trade.common.constants.LogisticsCompanyTypeEnum;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class LogisticsInfoRpcReq extends AbstractCommandRpcRequest {

    /**
     * 物流公司类型
     * @see LogisticsCompanyTypeEnum
     */
    @NotNull
    @Min(1)
    @Max(9999)
    private Integer companyType;

    /**
     * 物流公司返回的物流编号
     */
    @NotNull
    @Size(max=100)
    private String logisticsId;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.nonNull(companyType, I18NMessageUtils.getMessage("logistics.type.empty"));  //# "物流公司类型不能为空"
        ParamUtil.nonNull(logisticsId, I18NMessageUtils.getMessage("logistics.num.empty"));  //# "物流单号不能为空"
    }
}
