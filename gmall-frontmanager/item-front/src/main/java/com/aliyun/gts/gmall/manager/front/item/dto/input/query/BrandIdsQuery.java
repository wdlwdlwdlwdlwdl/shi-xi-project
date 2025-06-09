package com.aliyun.gts.gmall.manager.front.item.dto.input.query;

import lombok.Data;

import java.util.List;

@Data
public class BrandIdsQuery {

    /**
     * 品牌ids
     */
    private List<Long> ids;
}
