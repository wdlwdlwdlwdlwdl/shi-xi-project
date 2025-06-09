package com.aliyun.gts.gmall.platform.trade.domain.entity.point;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointExchange {
    public static final long POINT_UNIT = 1000L;
    public static final long DEDUCT_UNIT = 1000L;

    @ApiModelProperty("是否允许积分抵扣")
    private Boolean supportPoint;

    @ApiModelProperty("1原子积分 = ?人民币分 / 1000")
    private Integer exchangeRate;

    @ApiModelProperty("最大抵扣比例, 千分之x")
    private Integer maxDeductRate;

    @JSONField(serialize = false)
    public boolean isSupportPoint() {
        return supportPoint != null && supportPoint.booleanValue()
                && exchangeRate != null && exchangeRate.intValue() > 0;
    }

    /**
     * 金额(分) --> 原子积分数, 向上取整 （业务上确保可以整除, 配置侧做了控制）
     */
    public long exAmtToCount(long amt) {
        // amt * UNIT / ex;
        return BigDecimal.valueOf(amt)
                .multiply(BigDecimal.valueOf(POINT_UNIT))
                .divide(BigDecimal.valueOf(getExchangeRate()), RoundingMode.UP)
                .longValue();
    }

    /**
     * 原子积分数 --> 金额(分), 向下取整 (业务上存在除不尽)
     */
    public long exCountToAmt(long count) {
        // count * ex / UNIT;
        return BigDecimal.valueOf(count)
                .multiply(BigDecimal.valueOf(getExchangeRate()))
                .divide(BigDecimal.valueOf(POINT_UNIT), RoundingMode.DOWN)
                .longValue();
    }

    /**
     * 原子积分数 --> 金额(分), 向上取整 (业务上存在除不尽)
     */
    public long exCountToAmtUpper(long count) {
        // count * ex / UNIT;
        return BigDecimal.valueOf(count)
                .multiply(BigDecimal.valueOf(getExchangeRate()))
                .divide(BigDecimal.valueOf(POINT_UNIT), RoundingMode.UP)
                .longValue();
    }

    /**
     * 金额(分) --> 最大可抵扣的金额(分)
     */
    public long calcMaxDeductAmt(long amt) {
        return BigDecimal.valueOf(amt)
                .multiply(BigDecimal.valueOf(getMaxDeductRate()))
                .divide(BigDecimal.valueOf(DEDUCT_UNIT), RoundingMode.FLOOR)
                .longValue();
    }
}
