package com.aliyun.gts.gmall.platform.trade.common.domain.pay;

import com.aliyun.gts.gmall.framework.api.dto.AbstractInputParam;
import lombok.Data;

/**
 * 币种
 */
@Data
public class CurrencyType extends AbstractInputParam {
    /**
     * 币种类型
     */
    private String type;

    /**
     * 币种名称
     */
    private String name;
}
