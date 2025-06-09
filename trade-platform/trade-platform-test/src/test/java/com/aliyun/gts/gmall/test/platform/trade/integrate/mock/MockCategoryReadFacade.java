package com.aliyun.gts.gmall.test.platform.trade.integrate.mock;

import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.item.api.dto.input.category.CategoryFeatureQueryReq;
import com.aliyun.gts.gmall.platform.item.api.dto.input.category.CategoryQueryAllReq;
import com.aliyun.gts.gmall.platform.item.api.dto.input.category.CategoryQueryByIdReq;
import com.aliyun.gts.gmall.platform.item.api.dto.input.category.CategoryQueryByIdsReq;
import com.aliyun.gts.gmall.platform.item.api.dto.input.category.CategoryQueryByParamReq;
import com.aliyun.gts.gmall.platform.item.api.dto.input.category.CategoryQueryByPidReq;
import com.aliyun.gts.gmall.platform.item.api.dto.input.category.CategoryQueryReq;
import com.aliyun.gts.gmall.platform.item.api.dto.output.category.CategoryDTO;
import com.aliyun.gts.gmall.platform.item.api.dto.output.category.CategoryNodeDTO;
import com.aliyun.gts.gmall.platform.item.api.dto.output.category.CategoryTreeDTO;
import com.aliyun.gts.gmall.platform.item.api.facade.category.CategoryReadFacade;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class MockCategoryReadFacade implements CategoryReadFacade {
    @Override
    public RpcResponse<CategoryDTO> queryById(CategoryQueryByIdReq categoryQueryByIdReq) {
        return null;
    }

    @Override
    public RpcResponse<List<CategoryDTO>> queryByIds(CategoryQueryByIdsReq req) {
        return null;
    }

    @Override
    public RpcResponse<List<CategoryNodeDTO>> queryTreeByParam(CategoryQueryReq categoryQueryReq) {
        return null;
    }

    @Override
    public RpcResponse<List<CategoryDTO>> queryListByParam(CategoryQueryReq categoryQueryReq) {
        return null;
    }

    @Override
    public RpcResponse<List<CategoryDTO>> queryByParentId(CategoryQueryByPidReq categoryQueryByPidReq) {
        return null;
    }

    @Override
    public RpcResponse<List<CategoryDTO>> queryCategoryPathById(CategoryQueryByIdReq categoryQueryByIdReq) {
        return null;
    }

    @Override
    public RpcResponse<List<CategoryDTO>> queryCacheCategoryPathById(CategoryQueryByIdReq categoryQueryByIdReq) {
        return null;
    }

    @Override
    public RpcResponse<Map<String, String>> getFeatures(CategoryFeatureQueryReq categoryFeatureQueryReq) {
        return null;
    }

    @Override
    public RpcResponse<List<CategoryTreeDTO>> queryAll(CategoryQueryAllReq req) {
        return null;
    }

    @Override
    public RpcResponse<List<CategoryDTO>> queryParentByParam(CategoryQueryByParamReq req) {
        return null;
    }
}
