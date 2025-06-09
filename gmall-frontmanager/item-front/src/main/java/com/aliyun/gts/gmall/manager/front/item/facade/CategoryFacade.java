package com.aliyun.gts.gmall.manager.front.item.facade;

import java.util.List;
import com.aliyun.gts.gmall.manager.front.item.dto.input.query.CategoryAllByParamV2Query;
import com.aliyun.gts.gmall.manager.front.item.dto.input.query.CategoryRestQuery;
import com.aliyun.gts.gmall.manager.front.item.dto.output.CategoryAndPropVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.CategoryTreeVO;

public interface CategoryFacade {
    /**
     *  查询类目
     * @param req
     * @return
     */

    List<CategoryTreeVO> queryAllByParam(CategoryAllByParamV2Query req);

    CategoryAndPropVO queryCategoryList(CategoryRestQuery query);
}
