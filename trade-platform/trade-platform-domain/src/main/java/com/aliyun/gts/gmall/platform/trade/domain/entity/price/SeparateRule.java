package com.aliyun.gts.gmall.platform.trade.domain.entity.price;

import com.aliyun.gts.gmall.framework.domain.extend.ExtendComponent;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

/**
 * 分账规则
 */
@Data
public class SeparateRule extends ExtendComponent {
    public static final String ROLE_SELLER = "seller";     // 分账角色：卖家
    public static final String ROLE_PLATFORM = "platform";  // 分账角色：平台佣金

    @ApiModelProperty("各角色分账比例, 例如 {seller:95, platform:5} 代表平台抽佣 5%")
    private Map<String, Integer> roles;
}
