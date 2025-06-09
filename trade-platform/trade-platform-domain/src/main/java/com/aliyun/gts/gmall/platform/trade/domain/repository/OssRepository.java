package com.aliyun.gts.gmall.platform.trade.domain.repository;

import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;

public interface OssRepository {

    /**
     * 保存订单快照，返回OSS路径
     */
    String saveOrderSnapshot(CreatingOrder order);

}
