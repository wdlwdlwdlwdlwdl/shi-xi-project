package com.aliyun.gts.gmall.manager.front.promotion.dto.output;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import lombok.Data;

@Data
public class PromotionConfigVO extends AbstractOutputInfo {

    private Boolean tradeNoLoginPoint;

    private Long noLoginPointValue;

    private Boolean tradeRegisterPoint;

    private Long registerPointValue;

    private Long registerGrantTotalCount;

    private Integer invalidType;

    private Integer invalidYear;

    private Integer invalidMonth;

    private Boolean evaluationPointSwitch;

    private Long evaluationPointCount;


}
