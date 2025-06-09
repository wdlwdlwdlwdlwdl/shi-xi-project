package com.aliyun.gts.gmall.manager.front.b2bcomm.service.impl;

import com.aliyun.gts.gmall.center.misc.api.dto.input.common.ByIdDeleteRpcReq;
import com.aliyun.gts.gmall.center.misc.api.dto.input.dict.DictAddRpcReq;
import com.aliyun.gts.gmall.center.misc.api.dto.input.dict.DictPageQueryRpcReq;
import com.aliyun.gts.gmall.center.misc.api.dto.input.dict.DictQueryRpcReq;
import com.aliyun.gts.gmall.center.misc.api.dto.input.dict.DictUpdateRpcReq;
import com.aliyun.gts.gmall.center.misc.api.dto.output.dict.DictDTO;
import com.aliyun.gts.gmall.center.misc.api.facade.dict.DictReadFacade;
import com.aliyun.gts.gmall.center.misc.api.facade.dict.DictWriteFacade;
import com.aliyun.gts.gmall.center.misc.api.utils.ConvertUtils;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.manager.front.b2bcomm.converter.DictConverter;
import com.aliyun.gts.gmall.manager.front.b2bcomm.input.ByIdCommandRestReq;
import com.aliyun.gts.gmall.manager.front.b2bcomm.input.DictAddRestReq;
import com.aliyun.gts.gmall.manager.front.b2bcomm.input.DictPageQueryRestReq;
import com.aliyun.gts.gmall.manager.front.b2bcomm.input.DictUpdateRestReq;
import com.aliyun.gts.gmall.manager.front.b2bcomm.output.DictVO;
import com.aliyun.gts.gmall.manager.front.b2bcomm.service.DictService;
import com.aliyun.gts.gmall.manager.front.b2bcomm.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author gshine
 * @since 3/8/21 11:04 AM
 */
@Service
public class DictServiceImpl implements DictService {

    @Autowired
    private DictReadFacade dictReadFacade;
    @Autowired
    private DictWriteFacade dictWriteFacade;
    @Autowired
    private DictConverter dictConverter;

    @Override
    public RestResponse<DictVO> queryByKey(String dictKey) {
        DictQueryRpcReq query = new DictQueryRpcReq();
        query.setDictKey(dictKey);
        RpcResponse<DictDTO> queryResp = dictReadFacade.queryBy(query);
        return ResponseUtils.convertVOResponse(queryResp, dictConverter::toDictVO, false);
    }

    @Override
    public RestResponse<DictVO> addDict(DictAddRestReq req) {
        DictAddRpcReq rpcReq = dictConverter.toDictAddRpcReq(req);
        RpcResponse<DictDTO> addResp = dictWriteFacade.addDict(rpcReq);
        return ResponseUtils.convertVOResponse(addResp, dictConverter::toDictVO, true);
    }

    @Override
    public RestResponse<DictVO> updateDict(DictUpdateRestReq req) {
        DictUpdateRpcReq rpcReq = dictConverter.toDictUpdateRpcReq(req);
        RpcResponse<DictDTO> updateResp = dictWriteFacade.updateDict(rpcReq);
        return ResponseUtils.convertVOResponse(updateResp, dictConverter::toDictVO, true);
    }

    @Override
    public RestResponse<Boolean> deleteDict(ByIdCommandRestReq req) {
        ByIdDeleteRpcReq rpcReq = new ByIdDeleteRpcReq();
        rpcReq.setId(req.getId());
        rpcReq.setOperator(req.getOperator());
        RpcResponse<Integer> deleteResp = dictWriteFacade.deleteDict(rpcReq);
        return ResponseUtils.convertVOResponse(deleteResp, e -> (e != null && e > 0), true);
    }

    @Override
    public RestResponse<PageInfo<DictVO>> pageQuery(DictPageQueryRestReq req) {
        DictPageQueryRpcReq rpcReq = dictConverter.toDictPageQueryRpcReq(req);
        RpcResponse<PageInfo<DictDTO>> queryResult = dictReadFacade.pageQuery(rpcReq);
        return ResponseUtils.convertVOPageResponse(queryResult, dictConverter::toDictVO, false);
    }

    @Override
    public List<DictVO> queryByKey(List<String> dictKeys) {
        //去重
        Set<String> keys =  dictKeys.stream().collect(Collectors.toSet());
        dictKeys = keys.stream().collect(Collectors.toList());
        DictPageQueryRpcReq query = new DictPageQueryRpcReq();
        query.setDictKeys(dictKeys);
        RpcResponse<List<DictDTO>> queryResp = dictReadFacade.list(query);
        return ConvertUtils.converts(queryResp.getData(),dictConverter::toDictVO);
    }
}
