package com.aliyun.gts.gmall.manager.front.trade.dto.input.command;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestCommand;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 商品批量添加购物车
 *
 * @author tiansong
 */
@ApiModel(description = "商品批量添加购物车")
@Data
public class BatchAddCartRestCommand extends LoginRestCommand {

    @ApiModelProperty(value = "添加列表", required = true)
    private List<AddCartRestCommand> addList;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.notEmpty(addList, I18NMessageUtils.getMessage("add.list.required"));  //# "添加列表不能为空"
        for (AddCartRestCommand cmd : addList) {
            cmd.setCustId(getCustId());
            cmd.setChannel(getChannel());
            cmd.checkInput();
        }
    }
}