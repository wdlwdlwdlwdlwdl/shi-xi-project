package com.aliyun.gts.gmall.manager.front.item.facade;

import com.aliyun.gts.gmall.manager.front.item.dto.output.ItemDetailVO;
import com.aliyun.gts.gmall.platform.promotion.common.query.GrGroupQuery;

import java.util.List;

/**
 * 商品分组
 *
 * @author hu.zhiyong
 */
public interface ItemGroupFacade {

    /**
     * description: 根据分组ID查询商品
     *
     * @param grGroupQuery grGroupQuery
     * @return
     */
    List<ItemDetailVO> queryRelation(GrGroupQuery grGroupQuery);

}
