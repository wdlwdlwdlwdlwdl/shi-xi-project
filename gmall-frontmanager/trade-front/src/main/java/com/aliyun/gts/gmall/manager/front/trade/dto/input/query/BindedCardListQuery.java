package com.aliyun.gts.gmall.manager.front.trade.dto.input.query;

import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestQuery;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * 已绑卡列表查询
 * @author liang.ww(305643)
 * @date 2024/11/6 16:47
 */
@ApiModel("已绑卡列表查询")
@Data
public class BindedCardListQuery extends LoginRestQuery {

    private String accountId;
    @Override
    public void checkInput() {
        super.checkInput();
    }
}