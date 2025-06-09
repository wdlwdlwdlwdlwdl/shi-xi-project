package com.aliyun.gts.gmall.platform.trade.core.domainservice;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CustDeliveryFeeQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CustDeliveryFeeRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.CustDeliveryFeeDTO;

import java.util.List;

public interface CustDeliveryFeeService {

    PageInfo<CustDeliveryFeeDTO> queryCustDeliveryFee(CustDeliveryFeeQueryRpcReq req);

    List<CustDeliveryFeeDTO> queryCustDeliveryFeeList(CustDeliveryFeeQueryRpcReq req);

    CustDeliveryFeeDTO saveCustDeliveryFee(CustDeliveryFeeRpcReq req);

    CustDeliveryFeeDTO updateCustDeliveryFee(CustDeliveryFeeRpcReq req);

    CustDeliveryFeeDTO custDeliveryFeeDetail(CustDeliveryFeeRpcReq req);

    boolean exist(CustDeliveryFeeRpcReq req);
}
