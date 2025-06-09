//package com.aliyun.gts.gmall.test.platform.trade.integrate.mock;
//
//import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
//import com.aliyun.gts.gmall.platform.item.api.dto.input.item.ItemBatchQueryReq;
//import com.aliyun.gts.gmall.platform.item.api.dto.input.item.ItemFreightQueryReq;
//import com.aliyun.gts.gmall.platform.item.api.dto.input.item.ItemQueryReq;
//import com.aliyun.gts.gmall.platform.item.api.dto.input.item.ItemScanQueryReq;
//import com.aliyun.gts.gmall.platform.item.api.dto.input.sku.BatchSkuQueryReq;
//import com.aliyun.gts.gmall.platform.item.api.dto.input.sku.SkuQueryReq;
//import com.aliyun.gts.gmall.platform.item.api.dto.output.item.ItemDTO;
//import com.aliyun.gts.gmall.platform.item.api.dto.output.item.SkuDTO;
//import com.aliyun.gts.gmall.platform.item.api.dto.output.item.SkuPropDTO;
//import com.aliyun.gts.gmall.platform.item.api.facade.item.ItemReadFacade;
//import com.aliyun.gts.gmall.platform.item.common.enums.ItemStatus;
//import com.aliyun.gts.gmall.test.platform.trade.integrate.cases.base.TestConstants;
//import org.springframework.stereotype.Component;
//import org.testng.collections.Lists;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//@Component
//public class MockItemReadFacade implements ItemReadFacade {
//    @Override
//    public RpcResponse<ItemDTO> queryItem(ItemQueryReq itemQueryReq) {
//        ItemDTO item = new ItemDTO();
//        item.setId(itemQueryReq.getItemId());
//        item.setTitle("xxx");
//        item.setStatus(ItemStatus.ENABLE.getStatus());
//        item.setSellerId(TestConstants.SELLER_ID);
//        if (itemQueryReq.getItemId() == TestConstants.ITEM_ID) {
//            List<SkuDTO> list = new ArrayList<>();
//            item.setSkuList(list);
//            for (Long skuId : Arrays.asList(
//                    TestConstants.SKU_ID_1,
//                    TestConstants.SKU_ID_2,
//                    TestConstants.SKU_ID_3,
//                    TestConstants.SKU_ID_4)) {
//                list.add(mockSku(TestConstants.ITEM_ID, skuId));
//            }
//        }
//        return RpcResponse.ok(item);
//    }
//
//    @Override
//    public RpcResponse<List<SkuDTO>> querySku(SkuQueryReq skuQueryReq) {
//        List<SkuDTO> list = new ArrayList<>();
//        if (skuQueryReq.getSkuId() != null) {
//            list.add(mockSku(TestConstants.ITEM_ID, skuQueryReq.getSkuId()));
//        } else if (skuQueryReq.getItemId() != null) {
//            for (Long skuId : Arrays.asList(
//                    TestConstants.SKU_ID_1,
//                    TestConstants.SKU_ID_2,
//                    TestConstants.SKU_ID_3,
//                    TestConstants.SKU_ID_4)) {
//                list.add(mockSku(TestConstants.ITEM_ID, skuId));
//            }
//        }
//        return RpcResponse.ok(list);
//    }
//
//    private SkuDTO mockSku(Long itemId, Long skuId) {
//        SkuDTO sku = new SkuDTO();
//        sku.setItemId(TestConstants.ITEM_ID);
//        sku.setId(skuId);
//        sku.setQuantity(100L);
//        sku.setHoldQuantity(0L);
//        sku.setPrice(100L);
//        SkuPropDTO prop = new SkuPropDTO();
////        prop.setName("XX");
//        prop.setPicUrl("XXX");
//        sku.setSkuPropList(Lists.newArrayList(prop));
//        sku.setStatus(ItemStatus.ENABLE.getStatus());
//        sku.setWeight(100L);
//        return sku;
//    }
//
//    @Override
//    public RpcResponse<Long> queryFreight(ItemFreightQueryReq itemFreightQueryReq) {
//        if (itemFreightQueryReq.getCustomerAddressReq().getProviceId() > 99999990L) {
//            return RpcResponse.ok(null);    // 不支持配送地址
//        }
//        return RpcResponse.ok(1L);
//    }
//
//    @Override
//    public RpcResponse<ItemDTO> queryCacheItem(ItemQueryReq itemQueryReq) {
//        return queryItem(itemQueryReq);
//    }
//
//    @Override
//    public RpcResponse<List<ItemDTO>> queryCacheItems(ItemBatchQueryReq itemBatchQueryReq) {
//        List<ItemDTO> list = new ArrayList<>();
//        for (ItemQueryReq req : itemBatchQueryReq.getItemQueryReqList()) {
//            RpcResponse<ItemDTO> item = queryItem(req);
//            if (item.isSuccess()) {
//                list.add(item.getData());
//            }
//        }
//        return RpcResponse.ok(list);
//    }
//
//    @Override
//    public RpcResponse<List<SkuDTO>> queryCacheSku(SkuQueryReq skuQueryReq) {
//        return null;
//    }
//
//    @Override
//    public RpcResponse<List<SkuDTO>> queryCacheSkus(BatchSkuQueryReq batchSkuQueryReq) {
//        return null;
//    }
//
//    @Override
//    public RpcResponse<List<Long>> scanItems(ItemScanQueryReq itemScanQueryReq) {
//        return null;
//    }
//}
