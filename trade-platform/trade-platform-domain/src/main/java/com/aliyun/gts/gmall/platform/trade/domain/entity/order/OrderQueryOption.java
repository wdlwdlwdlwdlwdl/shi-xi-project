package com.aliyun.gts.gmall.platform.trade.domain.entity.order;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Setter
@Getter
public class OrderQueryOption {

    /**
     * 是否包含逆向信息
     */
    boolean includeReversalInfo;

    /**
     * 是否包含订单扩展结构
     */
    boolean includeExtends;

    /**
     * 多阶段订单同时查出阶段信息
     */
    @Builder.Default
    boolean includeStepOrder = true;


    public static void main(String[] args) {
        OrderQueryOption build = OrderQueryOption.builder().build();
        System.out.println(build);
    }
}
