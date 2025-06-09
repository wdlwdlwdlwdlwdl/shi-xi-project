package com.aliyun.gts.gmall.manager.front.customer.dto.output;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

@Data
public class PromDetailVO extends PromBaseVO{

    private Long itemId;


    /**
     * 商品商品信息
     */
    private JSONObject itemInfo;
}
