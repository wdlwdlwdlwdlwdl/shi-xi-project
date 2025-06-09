package com.aliyun.gts.gmall.manager.front.login.dto.input.query;

import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestQuery;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 查询绑定账号
 * @author alice
 * @version 1.0.0
 * @createTime 2022年09月26日 17:18:00
 */
@Data
public class BindCustomerQuery extends LoginRestQuery {

    @ApiModelProperty(value = "类型")
    private String type;

    @Override
    public void checkInput() {
        super.checkInput();
    }

}
