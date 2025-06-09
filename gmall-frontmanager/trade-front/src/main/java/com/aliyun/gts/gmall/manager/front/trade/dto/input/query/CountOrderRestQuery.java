package com.aliyun.gts.gmall.manager.front.trade.dto.input.query;

import java.util.List;

import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestQuery;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * 获取订单数量的请求
 *
 * @author GTS
 * @date 2021/03/09
 */
@ToString
@Data
public class CountOrderRestQuery extends LoginRestQuery {

    @ApiModelProperty(value = "订单状态列表")
    private List<Integer> status;
}