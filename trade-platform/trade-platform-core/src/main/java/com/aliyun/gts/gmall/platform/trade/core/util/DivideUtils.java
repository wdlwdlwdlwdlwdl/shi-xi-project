package com.aliyun.gts.gmall.platform.trade.core.util;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;


public class DivideUtils {

    /**
     * 分摊计算, 将 amt 按 weights 分摊
     */
    public static List<Long> divide(Long amt, List<Long> weights) {
        long sum = 0;
        for (Long weight : weights) {
            sum += weight;
        }

        Divider d = new Divider(amt, sum, weights.size());
        return weights.stream().map(w -> d.next(w)).collect(Collectors.toList());
    }

    /**
     * 按比例分摊
     */
    public static class Divider {
        private final Long amt;
        private final Long factorSum;
        private final Integer factorCount;
        private int i;
        private long divided;

        /**
         * @param amt         被分摊的总额
         * @param factorSum   分摊依据总值
         * @param factorCount 分摊依据个数
         */
        public Divider(Long amt, Long factorSum, Integer factorCount) {
            this.amt = amt;
            this.factorSum = factorSum;
            this.factorCount = factorCount;
        }

        public Long next(long factor) {
            return next(factor, BigDecimal.ROUND_HALF_UP);
        }

        public Long next(long factor, int roundingMode) {
            i++;
            if (i > factorCount) {
                throw new RuntimeException();
            }
            if (amt == 0L) {
                return 0L;
            }
            if (i == factorCount) {
                return amt - divided;
            }
            long divideAmt = BigDecimal.valueOf(amt)
                    .multiply(BigDecimal.valueOf(factor))
                    .divide(BigDecimal.valueOf(factorSum), roundingMode)
                    .longValue();
            divided += divideAmt;
            return divideAmt;
        }
    }

    /**
     * 先到先得的分摊策略
     */
    public static class FifoDivider {
        private long remain;

        public FifoDivider(long amt) {
            this.remain = amt;
        }

        public long next(long expect) {
            if (remain >= expect) {
                remain -= expect;
                return expect;
            } else {
                long ret = remain;
                remain = 0L;
                return ret;
            }
        }
    }

}
