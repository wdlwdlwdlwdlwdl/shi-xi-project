package com.aliyun.gts.gmall.platform.trade.api.dto.output.order.confirm;

import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 
 * @Title: LoanPeriodDTO.java
 * @Description:
 * @author zhao.qi
 * @date 2024年10月11日 13:06:37
 * @version V1.0
 */
@Getter
@Setter
public class LoanPeriodDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer type;
    private Long value;

    public void checkInput() {
        ParamUtil.nonNull(type,
                I18NMessageUtils.getMessage("prod") + ":sku.quote.installmentPriceList.priceList.type" + I18NMessageUtils.getMessage("not.empty"));
        ParamUtil.nonNull(type,
                I18NMessageUtils.getMessage("prod") + ":sku.quote.installmentPriceList.priceList.value" + I18NMessageUtils.getMessage("not.empty"));
    }
}
