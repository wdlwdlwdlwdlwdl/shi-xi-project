package com.aliyun.gts.gmall.platform.trade.core.config;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class TradeLimitConfiguration {
    
    //购物车install金额上线
    @NacosValue(value = "${trade.cart.loan-limit:}", autoRefreshed = true)
    private Long loanLimit;

    //购物车loan金额上线
    @NacosValue(value = "${trade.cart.installment-limit:}", autoRefreshed = true)
    private Long installmentLimit;

    //是否通过新接口结算运费0，老接口，1:商品提供新接口
    @NacosValue(value = "${trade.deliveryFee.calSwitch:0}", autoRefreshed = true)
    private Integer calSwitch;

}
