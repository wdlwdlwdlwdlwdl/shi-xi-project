package com.aliyun.gts.gmall.platform.trade.domain.entity.order;

import java.util.List;
import java.util.Map;

import com.aliyun.gts.gmall.platform.trade.domain.entity.AbstractBusinessEntity;
import com.aliyun.gts.gmall.searcher.common.domain.request.SimpleSearchRequest;
import lombok.Data;

@Data
public class SearchOrder extends AbstractBusinessEntity {

    /**
     * 搜索转换后的结果
     */
    ListOrder listOrder;

    /**
     * 搜索的原始结果
     */
    List<Map<String, Object>> itemList;

    /**
     * 搜索引擎条件
     */
    SimpleSearchRequest searchRequest;

}
