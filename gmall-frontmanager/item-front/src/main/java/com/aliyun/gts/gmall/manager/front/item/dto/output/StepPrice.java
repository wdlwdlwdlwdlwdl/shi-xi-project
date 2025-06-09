package com.aliyun.gts.gmall.manager.front.item.dto.output;

import lombok.Data;

/**
 * 商品阶梯价
 * author linyi
 */
@Data
public class StepPrice {

    /**
     * 最小购买数量
     */
    private Integer num;

    /**
     * 符合数量时的购买价格(元)
     */
    private String price;

}
