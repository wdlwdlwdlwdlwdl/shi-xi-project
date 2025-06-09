package com.aliyun.gts.gmall.manager.front.item.facade.impl;

import java.util.List;

import com.aliyun.gts.gmall.center.misc.api.dto.input.region.RegionQueryByParentIdRpcReq;
import com.aliyun.gts.gmall.center.misc.api.dto.output.region.RegionInfo;
import com.aliyun.gts.gmall.center.misc.api.facade.region.RegionReadFacade;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.manager.front.item.convertor.RegionConverter;
import com.aliyun.gts.gmall.manager.front.item.dto.output.RegionVO;
import com.aliyun.gts.gmall.manager.front.item.facade.CommonRegionFacade;
import com.aliyun.gts.gmall.manager.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author gshine
 * @since 2/24/21 11:04 AM
 */
@Service
public class CommonRegionFacadeImpl implements CommonRegionFacade {
    @Autowired
    private RegionReadFacade regionReadFacade;
    @Autowired
    private RegionConverter  regionConverter;

    @Override
    public RestResponse<List<RegionVO>> queryRegionsByParentId(Long id) {
        RegionQueryByParentIdRpcReq req = new RegionQueryByParentIdRpcReq();
        req.setParentId(id);
        RpcResponse<List<RegionInfo>> queryResp = regionReadFacade.queryByParentId(req);
        return ResponseUtils.convertVOResponse(queryResp, regionConverter::toRegionVOList, false);
    }
}
