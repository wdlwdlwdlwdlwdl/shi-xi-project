package com.aliyun.gts.gmall.platform.trade.api.dto.common.promotion;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@ApiModel
public class PromotionExtendDTO extends AbstractOutputInfo {

    /**
     * 附加优惠结果。
     */
    private List<Map<String, Object>> extraPromotions;

}
