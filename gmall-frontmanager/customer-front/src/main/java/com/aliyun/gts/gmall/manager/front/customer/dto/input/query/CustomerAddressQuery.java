package com.aliyun.gts.gmall.manager.front.customer.dto.input.query;

import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestCommand;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 会员收货地址查询
 *
 * @author tiansong
 */

@Data
@ApiModel(description = "会员收货地址查询")
public class CustomerAddressQuery extends LoginRestCommand {

    @ApiModelProperty("是否查询默认地址")
    private Boolean defaultYn;
    @Override
    public void checkInput() {
        super.checkInput();
    }
}
