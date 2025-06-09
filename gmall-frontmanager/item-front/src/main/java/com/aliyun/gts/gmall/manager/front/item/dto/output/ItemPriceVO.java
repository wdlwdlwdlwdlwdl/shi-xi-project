package com.aliyun.gts.gmall.manager.front.item.dto.output;

import com.aliyun.gts.gmall.manager.front.common.util.ItemUtils;
import com.google.common.base.Strings;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品阶梯价
 * author linyi
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemPriceVO {

    /**
     * 库存总数
     */
    private Long quantity;

    /**
     * 商品价格总和,不包括优惠
     */
    private String price;

    /**
     * 商品单价
     */
    private String getItemPrice() {

        if (quantity == null || Strings.isNullOrEmpty(price)) {
            return null;
        }

        if (quantity == 0) {
            return "0.00";
        }

        long itemPrice = Long.valueOf(price) / quantity;

        return String.valueOf(itemPrice);

    }

}
