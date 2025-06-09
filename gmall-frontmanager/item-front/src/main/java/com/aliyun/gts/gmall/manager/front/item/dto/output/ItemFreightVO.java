package com.aliyun.gts.gmall.manager.front.item.dto.output;

import javax.validation.constraints.NotNull;

import com.aliyun.gts.gmall.manager.front.common.util.ItemUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * 商详运费信息
 *
 * @author tiansong
 */
@Data
@Builder
@ApiModel("商详运费信息")
public class ItemFreightVO {
    @NotNull
    @ApiModelProperty("商品ID")
    private Long      itemId;
    @NotNull
    @ApiModelProperty("SKU ID")
    private Long      skuId;
    @NotNull
    @ApiModelProperty("用户地址信息")
    private AddressVO addressVO;
    @ApiModelProperty("运费（分）")
    private Long      freight;

    public String getFreightYuan() {
        return String.valueOf(this.freight);
    }
}
