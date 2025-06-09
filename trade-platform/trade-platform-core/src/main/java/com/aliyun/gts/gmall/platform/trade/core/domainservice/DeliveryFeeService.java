package com.aliyun.gts.gmall.platform.trade.core.domainservice;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.DeliveryFeeQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.DeliveryFeeRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.DeliveryFeeDTO;

public interface DeliveryFeeService {

    /**
     * 查询卖家运费列表
     * @param req
     * @return
     */
    PageInfo<DeliveryFeeDTO> queryDeliveryFee(DeliveryFeeQueryRpcReq req);

    /**
     * 保存卖家运费
     * @param req
     * @return
     */
    DeliveryFeeDTO saveDeliveryFee(DeliveryFeeRpcReq req);

    /**
     * 保存卖家运费
     * @param req
     * @return
     */
    DeliveryFeeDTO updateDeliveryFee(DeliveryFeeRpcReq req);

    /**
     * 卖家运费详情
     * @param req
     * @return
     */
    DeliveryFeeDTO deliveryFeeDetail(DeliveryFeeRpcReq req);

    /**
     * 查询下单时候 收卖家的取费
     * @param req
     * @return DeliveryFeeDTO
     */
    Long deliveryMerchantFee(DeliveryFeeQueryRpcReq req);

}
