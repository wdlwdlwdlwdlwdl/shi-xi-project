package com.aliyun.gts.gmall.manager.front.customer.dto.output;

import com.aliyun.gts.gmall.platform.promotion.api.dto.model.DiscountRuleDTO;
import lombok.Data;

@Data
public class DiscountRuleVO extends DiscountRuleDTO {

    private Double priceYuan;

    private Double manYuan;

    private Double jianYuan;
}
