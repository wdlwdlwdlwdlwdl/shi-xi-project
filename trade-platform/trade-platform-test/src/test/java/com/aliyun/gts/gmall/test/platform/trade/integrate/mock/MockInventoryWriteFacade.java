package com.aliyun.gts.gmall.test.platform.trade.integrate.mock;

import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.item.api.dto.input.inventory.InventoryReq;
import com.aliyun.gts.gmall.platform.item.api.dto.output.inventory.InventoryOpResult;
import com.aliyun.gts.gmall.platform.item.api.facade.inventory.InventoryWriteFacade;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MockInventoryWriteFacade implements InventoryWriteFacade {
    @Override
    public RpcResponse<List<InventoryOpResult>> lock(InventoryReq inventoryReq) {
        return RpcResponse.ok(new ArrayList<>());
    }

    @Override
    public RpcResponse<List<InventoryOpResult>> unlock(InventoryReq inventoryReq) {
        return RpcResponse.ok(new ArrayList<>());
    }

    @Override
    public RpcResponse<List<InventoryOpResult>> reduce(InventoryReq inventoryReq) {
        return RpcResponse.ok(new ArrayList<>());
    }

    @Override
    public RpcResponse<List<InventoryOpResult>> compensate(InventoryReq inventoryReq) {
        return RpcResponse.ok(new ArrayList<>());
    }
}
