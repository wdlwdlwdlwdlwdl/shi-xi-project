package com.aliyun.gts.gmall.manager.front.trade.dto.input.command;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import java.util.List;

import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestCommand;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.param.ItemSkuId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 购物车删除商品
 *
 * @author tiansong
 */
@ApiModel(description = "购物车删除商品")
@Data
public class DelCartRestCommand extends LoginRestCommand {

    @ApiModelProperty(value = "删除的商品列表，至少包含一个商品", required = true)
    private List<ItemSkuId> itemSkuIds;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.notEmpty(itemSkuIds, I18NMessageUtils.getMessage("select.delete.product"));  //# "请选择需要删除的商品"
        itemSkuIds.forEach(ItemSkuId::checkInput);
    }
}