package com.aliyun.gts.gmall.manager.front.item.facade.impl;

import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.manager.front.item.convertor.SellerConverter;
import com.aliyun.gts.gmall.manager.front.item.dto.input.query.SellerDetailQuery;
import com.aliyun.gts.gmall.manager.front.item.dto.input.query.SellerWarehouseQuery;
import com.aliyun.gts.gmall.manager.front.item.dto.output.SellerDetailInfoVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.SellerWarehouseVO;
import com.aliyun.gts.gmall.manager.front.item.facade.SellerFacade;
import com.aliyun.gts.gmall.platform.item.api.dto.input.warehouse.WarehouseQueryDetailReq;
import com.aliyun.gts.gmall.platform.item.api.dto.output.warehouse.WarehouseDTO;
import com.aliyun.gts.gmall.platform.item.api.facade.warehouse.WarehouseReadFacade;
import com.aliyun.gts.gmall.platform.user.api.dto.input.SellerAvgScoreQuery;
import com.aliyun.gts.gmall.platform.user.api.dto.output.SellerAvgScoreDTO;
import com.aliyun.gts.gmall.platform.user.api.facade.SellerReadFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 商家服务
 */
@Slf4j
@Service
public class SellerFacadeImpl implements SellerFacade {

    @Autowired
    private WarehouseReadFacade warehouseReadFacade;

    @Autowired
    private SellerReadFacade sellerReadFacade;


    @Override
    public SellerDetailInfoVO querySellerDetail(SellerDetailQuery query) {
        SellerAvgScoreQuery rpcReq = SellerConverter.toSellerAvgScoreQuery(query);
        RpcResponse<List<SellerAvgScoreDTO>> rpcResponse = sellerReadFacade.querySellerAvgScore(rpcReq);
        return SellerConverter.toSellerDetailInfoVO(rpcResponse);
    }

    @Override
    public List<SellerWarehouseVO> querySellerWarehouse(SellerWarehouseQuery query) {
        WarehouseQueryDetailReq rpcReq = SellerConverter.toWarehouseQueryDetailReq(query);
        RpcResponse<List<WarehouseDTO>> rpcResponse = warehouseReadFacade.queryWarehouseDetail(rpcReq);
        return SellerConverter.toSellerWarehouseVO(rpcResponse);
    }
}
