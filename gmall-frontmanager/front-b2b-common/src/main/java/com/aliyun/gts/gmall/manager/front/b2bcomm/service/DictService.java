package com.aliyun.gts.gmall.manager.front.b2bcomm.service;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.manager.front.b2bcomm.input.ByIdCommandRestReq;
import com.aliyun.gts.gmall.manager.front.b2bcomm.input.DictAddRestReq;
import com.aliyun.gts.gmall.manager.front.b2bcomm.input.DictPageQueryRestReq;
import com.aliyun.gts.gmall.manager.front.b2bcomm.input.DictUpdateRestReq;
import com.aliyun.gts.gmall.manager.front.b2bcomm.output.DictVO;

import java.util.List;

/**
 * @author gshine
 * @since 3/8/21 11:03 AM
 */
public interface DictService {

    RestResponse<DictVO> queryByKey(String dictKey);

    RestResponse<DictVO> addDict(DictAddRestReq req);

    RestResponse<DictVO> updateDict(DictUpdateRestReq req);

    RestResponse<Boolean> deleteDict(ByIdCommandRestReq req);

    RestResponse<PageInfo<DictVO>> pageQuery(DictPageQueryRestReq req);

    List<DictVO> queryByKey(List<String> dictKeys);
}
