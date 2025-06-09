package com.aliyun.gts.gmall.manager.front.b2bcomm.controller;

import com.aliyun.gts.gmall.center.misc.api.dto.input.region.RegionQueryByParentIdRpcReq;
import com.aliyun.gts.gmall.center.misc.api.dto.output.region.RegionInfo;
import com.aliyun.gts.gmall.center.misc.api.facade.region.RegionReadFacade;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.manager.front.b2bcomm.converter.RegionConverter;
import com.aliyun.gts.gmall.manager.front.b2bcomm.input.ByPidQueryRestReq;
import com.aliyun.gts.gmall.manager.front.b2bcomm.output.RegionVO;
import com.aliyun.gts.gmall.manager.front.b2bcomm.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author gshine
 * @since 2/24/21 10:40 AM
 */
@RestController(value = "b2bRegionRestController")
@RequestMapping("/api/region")
public class RegionRestController {

    @Autowired
    private RegionReadFacade regionReadFacade;
    @Resource
    private RegionConverter regionConverter;

    @RequestMapping("/getRegionsByParentId")
    public RestResponse<List<RegionVO>> getRegionsByParentId(@RequestBody ByPidQueryRestReq req) {
        return queryRegionsByParentId(req.getParentId());
    }
    public RestResponse<List<RegionVO>> queryRegionsByParentId(Long id) {
        RegionQueryByParentIdRpcReq req = new RegionQueryByParentIdRpcReq();
        req.setParentId(id);
        RpcResponse<List<RegionInfo>> queryResp = regionReadFacade.queryByParentId(req);
        return ResponseUtils.convertVOResponse(queryResp, regionConverter::toRegionVOList, false);
    }
}
