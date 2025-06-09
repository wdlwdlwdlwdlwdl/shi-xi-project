package com.aliyun.gts.gmall.platform.trade.core.domainservice;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CityQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CityRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.CityDTO;

import java.util.List;

public interface CityService {

    PageInfo<CityDTO> queryCity(CityQueryRpcReq req);

    CityDTO saveCity(CityRpcReq req);

    CityDTO updateCity(CityRpcReq req);

    CityDTO cityDetail(CityRpcReq req);

    boolean exist(CityRpcReq req);

    List<CityDTO> queryCityList(CityRpcReq req);

    boolean isAccess(CityRpcReq req);
}
