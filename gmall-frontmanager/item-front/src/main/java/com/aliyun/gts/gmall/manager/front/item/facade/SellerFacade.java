package com.aliyun.gts.gmall.manager.front.item.facade;


import com.aliyun.gts.gmall.manager.front.item.dto.input.query.SellerDetailQuery;
import com.aliyun.gts.gmall.manager.front.item.dto.input.query.SellerWarehouseQuery;
import com.aliyun.gts.gmall.manager.front.item.dto.output.SellerDetailInfoVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.SellerWarehouseVO;

import java.util.List;

/**
 * 商家服务
 */
public interface SellerFacade {

    /**
     * 查询商家详情
     */
    SellerDetailInfoVO querySellerDetail(SellerDetailQuery query);

    /**
     * 查询仓库信息
     * @param query
     * @return
     */
    List<SellerWarehouseVO> querySellerWarehouse(SellerWarehouseQuery query);
}
