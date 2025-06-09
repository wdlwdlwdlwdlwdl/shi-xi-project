package com.aliyun.gts.gmall.manager.front.b2bcomm.service;

import com.aliyun.gts.gmall.manager.front.b2bcomm.input.ByPidQueryRestReq;
import com.aliyun.gts.gmall.manager.front.b2bcomm.input.CategoryQueryRestReq;
import com.aliyun.gts.gmall.manager.front.b2bcomm.output.CategoryNodeVO;
import com.aliyun.gts.gmall.manager.front.b2bcomm.output.CategoryVO;

import java.util.List;

/**
 * @author gshine
 * @since 2/24/21 11:30 AM
 */
public interface CategoryService {
    List<CategoryVO> queryListByParentId(ByPidQueryRestReq req);

    List<CategoryVO>  queryCategoryPathById(Long categoryId);

    List<CategoryNodeVO> queryTreeByParams(CategoryQueryRestReq req);
}
