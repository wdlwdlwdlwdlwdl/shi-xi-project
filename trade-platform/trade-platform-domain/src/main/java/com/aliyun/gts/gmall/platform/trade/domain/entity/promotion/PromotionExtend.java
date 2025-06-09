package com.aliyun.gts.gmall.platform.trade.domain.entity.promotion;

import com.aliyun.gts.gmall.platform.trade.domain.entity.AbstractBusinessEntity;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Deprecated
public class PromotionExtend extends AbstractBusinessEntity {

    /**
     * 附加优惠结果。
     */
    private List<Map<String, Object>> extraPromotions;
}
