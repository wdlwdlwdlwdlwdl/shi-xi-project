package com.aliyun.gts.gmall.manager.front.item.dto.output;

import java.util.List;

import com.aliyun.gts.gmall.platform.item.common.enums.ItemStatus;
import com.aliyun.gts.gmall.platform.item.common.enums.SkuStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

@Data
@ApiModel(value = "组合商品信息")
public class CombineItemVO {

    @ApiModelProperty(value = "被组合的sku信息")
    private List<CombineSkuVO> skuList;

    @ApiModelProperty("组合商品的价格,元")
    private String price;

    @ApiModelProperty("被组合商品的sku状态是否正常")
    public boolean isEnableStatus() {
        if (CollectionUtils.isEmpty(skuList)) {
            return false;
        }

        return skuList.stream().allMatch(x ->
            SkuStatus.ENABLE.getStatus().equals(x.getStatus()) &&
                ItemStatus.ENABLE.getStatus().equals(x.getItemStatus())
        );
    }

    @ApiModelProperty("组合商品的库存。取能组合出来的最大库存")
    public long getQuantity() {

        if (CollectionUtils.isEmpty(skuList)) {
            return 0;
        }

        Long quantity = null;
        for (CombineSkuVO combineSkuVO : skuList) {

            long skuQuantity = combineSkuVO.getQuantity() / combineSkuVO.getPerNum();

            if (quantity == null || skuQuantity < quantity) {
                quantity = skuQuantity;
            }
        }
        return quantity == null ? 0 : quantity;
    }
}
