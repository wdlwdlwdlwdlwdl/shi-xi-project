package com.aliyun.gts.gmall.manager.front.trade.dto.input.command;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestCommand;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 购物车修改
 *
 * @author tiansong
 */
@ApiModel(description = "购物车修改")
@Data
public class ModifyCartRestCommand extends LoginRestCommand {

    @ApiModelProperty(value = "商品ID", required = true)
    private Long itemId;

    @ApiModelProperty(value = "商品SKU-ID", required = true)
    private Long skuId;

    @ApiModelProperty("修改后的总数")
    private Integer newItemQty;

    @ApiModelProperty("修改后的skuId")
    private Long newSkuId;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.nonNull(this.itemId, I18NMessageUtils.getMessage("product")+"ID"+I18NMessageUtils.getMessage("cannot.be.empty"));  //# "商品ID不能为空"
        ParamUtil.nonNull(this.skuId, I18NMessageUtils.getMessage("product")+"SKU-ID"+I18NMessageUtils.getMessage("cannot.be.empty"));  //# "商品SKU-ID不能为空"
        ParamUtil.expectFalse(newItemQty == null && newSkuId == null, I18NMessageUtils.getMessage("select.modify.content"));  //# "请选择修改内容"
        // 更换SKU的情况下，同时修改的数量失效；实现：同SKU数量合并
        // 注意：修改sku同时修改数量，无法进行sku数量合并
        if(this.newSkuId != null) {
            this.newItemQty = null;
        }
    }

    public Long getAfterSkuId() {
        return newSkuId != null ? newSkuId : skuId;
    }
}
