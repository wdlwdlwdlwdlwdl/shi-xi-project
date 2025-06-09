package com.aliyun.gts.gmall.manager.front.item.facade;

import java.util.List;
import com.aliyun.gts.gmall.manager.biz.output.CustDTO;
import com.aliyun.gts.gmall.manager.front.item.dto.ItemPageQuery;
import com.aliyun.gts.gmall.manager.front.item.dto.output.ItemDetailV2VO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.ItemPageVO;
import com.aliyun.gts.gmall.manager.front.item.dto.temp.ItemSaleSellerQuery;

public interface ItemFacade {
    /**
     * 商品分页主接口
     *
     * @param itemDetailRestQuery
     * @return
     */
    List<ItemPageVO> queryPage(ItemPageQuery itemDetailRestQuery);

    /**
     * 
     * @param query
     * @param custDto
     * @return
     */
    ItemDetailV2VO query(ItemSaleSellerQuery query, CustDTO custDto);
}
