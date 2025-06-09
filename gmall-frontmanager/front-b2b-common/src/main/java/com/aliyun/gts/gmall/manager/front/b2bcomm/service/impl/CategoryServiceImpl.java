package com.aliyun.gts.gmall.manager.front.b2bcomm.service.impl;

import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.i18n.LangText;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.manager.front.b2bcomm.converter.CategoryConverter;
import com.aliyun.gts.gmall.manager.front.b2bcomm.input.ByPidQueryRestReq;
import com.aliyun.gts.gmall.manager.front.b2bcomm.input.CategoryQueryRestReq;
import com.aliyun.gts.gmall.manager.front.b2bcomm.output.CategoryNodeVO;
import com.aliyun.gts.gmall.manager.front.b2bcomm.output.CategoryVO;
import com.aliyun.gts.gmall.manager.front.b2bcomm.service.CategoryService;
import com.aliyun.gts.gmall.manager.front.b2bcomm.utils.ErrorCodeUtils;
import com.aliyun.gts.gmall.manager.utils.CacheUtils;
import com.aliyun.gts.gmall.platform.item.api.dto.input.category.CategoryQueryByIdReq;
import com.aliyun.gts.gmall.platform.item.api.dto.input.category.CategoryQueryByPidReq;
import com.aliyun.gts.gmall.platform.item.api.dto.input.category.CategoryQueryReq;
import com.aliyun.gts.gmall.platform.item.api.dto.output.category.CategoryDTO;
import com.aliyun.gts.gmall.platform.item.api.dto.output.category.CategoryNodeDTO;
import com.aliyun.gts.gmall.platform.item.api.facade.category.CategoryReadFacade;
import com.google.common.cache.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author gshine
 * @since 2/24/21 11:30 AM
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryReadFacade categoryReadFacade;
    @Autowired
    private CategoryConverter categoryConverter;

    private Cache<Long, ?> pathCache = CacheUtils.defaultLocalCache(3600);

    @Override
    public List<CategoryVO> queryListByParentId(ByPidQueryRestReq req) {
        CategoryQueryByPidReq q = new CategoryQueryByPidReq();
        q.setParentId(req.getParentId());
        RpcResponse<List<CategoryDTO>> resp = categoryReadFacade.queryByParentId(q);
        if (!resp.isSuccess()) {
            throw new GmallException(ErrorCodeUtils.mapCode(resp.getFail()));
        }
        return categoryConverter.toVOList(resp.getData());
    }

    @Override
    public List<CategoryVO> queryCategoryPathById(Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        return CacheUtils.getNullableQuietly(pathCache, categoryId, () -> {
            CategoryQueryByIdReq q = new CategoryQueryByIdReq();
            q.setId(categoryId);
            RpcResponse<List<CategoryDTO>> resp = categoryReadFacade.queryCacheCategoryPathById(q);
            if (!resp.isSuccess()) {
                throw new GmallException(ErrorCodeUtils.mapCode(resp.getFail()));
            }
            return categoryConverter.toVOList(resp.getData());
        });
    }

    @Override
    public List<CategoryNodeVO> queryTreeByParams(CategoryQueryRestReq req) {
        CategoryQueryReq q = new CategoryQueryReq();
        q.setName(new LangText(req.getLang(), req.getName()));
        RpcResponse<List<CategoryNodeDTO>> resp = categoryReadFacade.queryTreeByParam(q);
        if (!resp.isSuccess()) {
            throw new GmallException(ErrorCodeUtils.mapCode(resp.getFail()));
        }
        return categoryConverter.toNodeList(resp.getData());
    }
}
