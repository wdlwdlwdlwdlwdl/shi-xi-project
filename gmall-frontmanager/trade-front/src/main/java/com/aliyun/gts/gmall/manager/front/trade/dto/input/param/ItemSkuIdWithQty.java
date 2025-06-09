package com.aliyun.gts.gmall.manager.front.trade.dto.input.param;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品ID和SKU ID的对应关系
 *
 * @author tiansong
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@ApiModel("商品ID和SKU ID的对应关系")
public class ItemSkuIdWithQty extends ItemSkuId {
    @ApiModelProperty("数量")
    private Integer cartQty;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.nonNull(cartQty, I18NMessageUtils.getMessage("qty.required"));  //# "数量不能为空"
    }
}
