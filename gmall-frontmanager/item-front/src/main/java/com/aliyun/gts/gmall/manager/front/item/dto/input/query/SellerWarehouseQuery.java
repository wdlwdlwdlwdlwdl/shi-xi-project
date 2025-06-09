package com.aliyun.gts.gmall.manager.front.item.dto.input.query;

import com.aliyun.gts.gmall.framework.api.rest.dto.AbstractQueryRestRequest;
import lombok.Data;

import java.io.Serializable;


/**
 * 商家详情查询入参模型
 */
@Data
public class SellerWarehouseQuery extends AbstractQueryRestRequest implements Serializable {

    /**
     * 商家id
     */
    private Long sellerId;

    /**
     * 城市编码
     */
    private String cityCode;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 自提状态
     */
    private Integer pickUp;
}
