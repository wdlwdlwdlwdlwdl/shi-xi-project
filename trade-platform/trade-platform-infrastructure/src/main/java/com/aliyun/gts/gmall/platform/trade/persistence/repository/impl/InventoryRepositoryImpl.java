package com.aliyun.gts.gmall.platform.trade.persistence.repository.impl;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.rpc.dto.FailInfo;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.item.api.dto.input.inventory.InventoryReq;
import com.aliyun.gts.gmall.platform.item.api.dto.input.inventory.SkuInfo;
import com.aliyun.gts.gmall.platform.item.api.dto.output.inventory.InventoryOpResult;
import com.aliyun.gts.gmall.platform.item.api.facade.inventory.InventoryWriteFacade;
import com.aliyun.gts.gmall.platform.item.common.enums.ItemResponseCode;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.domain.entity.inventory.InventoryReduceParam;
import com.aliyun.gts.gmall.platform.trade.domain.entity.inventory.InventoryRollbackParam;
import com.aliyun.gts.gmall.platform.trade.domain.repository.InventoryRepository;
import com.aliyun.gts.gmall.platform.trade.persistence.rpc.util.RpcUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "trade", name = "inventoryRepository", havingValue = "default", matchIfMissing = true)
public class InventoryRepositoryImpl implements InventoryRepository {

    @Autowired
    private InventoryWriteFacade inventoryWriteFacade;

    @Override
    public boolean lockInventory(List<InventoryReduceParam> list) {
        InventoryReq req = new InventoryReq();
        req.setAllowPartSuccess(false);
        req.setSkuInfoList(convertSkuList(list));
        RpcResponse<List<InventoryOpResult>> resp = RpcUtils.invokeRpc(
                () -> inventoryWriteFacade.lock(req),
                "inventoryWriteFacade.lock", I18NMessageUtils.getMessage("reserved.stock"), req,  //# "预占库存"
                this::isInventoryBizFail);
        return resp.isSuccess() || isOperateSuccess(resp.getFail());
    }

    private boolean isInventoryBizFail(FailInfo fail) {
        if (ItemResponseCode.Fail_40030001.getCode().equals(fail.getCode())
                || ItemResponseCode.Fail_40010005.getCode().equals(fail.getCode())
                || ItemResponseCode.Fail_40010001.getCode().equals(fail.getCode())
                || ItemResponseCode.Fail_40020001.getCode().equals(fail.getCode())
                || ItemResponseCode.Fail_40020002.getCode().equals(fail.getCode())
                || ItemResponseCode.Fail_40010008.getCode().equals(fail.getCode())) {
            return true;
        }
        return isOperateSuccess(fail);
    }

    private boolean isOperateSuccess(FailInfo fail) {
        //# "当前状态[扣减]下不能变更到目标状态[锁定
        // 幂等调用 返回 true
        return ItemResponseCode.Fail_40030009.getCode().equals(fail.getCode())
                && (I18NMessageUtils.getMessage("current.status") + "[" + I18NMessageUtils.getMessage("deduct.stock") + "]" + I18NMessageUtils.getMessage("cannot.change.status") + "[" + I18NMessageUtils.getMessage("lock") + "]").equals(fail.getMessage());
    }

    @Override
    public void unlockInventory(List<InventoryReduceParam> list) {
        InventoryReq req = new InventoryReq();
        req.setAllowPartSuccess(true);
        req.setSkuInfoList(convertSkuList(list));
        RpcResponse<List<InventoryOpResult>> resp = RpcUtils.invokeRpc(
                () -> inventoryWriteFacade.unlock(req),
                "inventoryWriteFacade.unlock", I18NMessageUtils.getMessage("release.stock"), req);  //# "释放预占库存"
    }

    @Override
    public boolean reduceInventory(List<InventoryReduceParam> list) {
        InventoryReq req = new InventoryReq();
        req.setAllowPartSuccess(false);
        req.setSkuInfoList(convertSkuList(list));
        RpcResponse<List<InventoryOpResult>> resp = RpcUtils.invokeRpc(
                () -> inventoryWriteFacade.reduce(req),
                "inventoryWriteFacade.reduce", "减库存", req,
                this::isInventoryBizFail);
        return resp.isSuccess() || isOperateSuccess(resp.getFail());
    }

    @Override
    public void rollbackInventory(List<InventoryRollbackParam> list) {
        InventoryReq req = new InventoryReq();
        req.setAllowPartSuccess(false);
        req.setSkuInfoList(convertSkuListRb(list));
        RpcResponse<List<InventoryOpResult>> resp = RpcUtils.invokeRpc(
                () -> inventoryWriteFacade.compensate(req),
                "inventoryWriteFacade.compensate", I18NMessageUtils.getMessage("replenish.stock"), req);  //# "回补库存"
    }

    private List<SkuInfo> convertSkuList(List<InventoryReduceParam> list) {
        return list.stream().map(i -> {
            SkuInfo sku = new SkuInfo();
            sku.setOrderId(i.getOrderId());
            sku.setSkuId(i.getSkuId().getSkuId());
            sku.setQuantity(i.getSkuQty().longValue());
            sku.setReqId("ORD_" + i.getOrderId());
            return sku;
        }).collect(Collectors.toList());
    }

    private List<SkuInfo> convertSkuListRb(List<InventoryRollbackParam> list) {
        return list.stream().map(i -> {
            SkuInfo sku = new SkuInfo();
            sku.setOrderId(i.getOrderId());
            sku.setSkuId(i.getSkuId().getSkuId());
            sku.setQuantity(i.getSkuQty().longValue());
            if (i.getReversalId() != null) {
                sku.setReqId("REV_" + i.getReversalId());   // 售后回补库存
            } else {
                sku.setReqId("CLOSE_" + i.getOrderId());    // 未支付回补库存
            }
            return sku;
        }).collect(Collectors.toList());
    }
}
