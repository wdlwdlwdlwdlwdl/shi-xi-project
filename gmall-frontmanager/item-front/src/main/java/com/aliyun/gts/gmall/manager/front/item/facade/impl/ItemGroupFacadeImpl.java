package com.aliyun.gts.gmall.manager.front.item.facade.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.aliyun.gts.gmall.center.item.common.consts.ItemExtendConstant;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.manager.biz.output.CustDTO;
import com.aliyun.gts.gmall.manager.front.common.util.UserHolder;
import com.aliyun.gts.gmall.manager.front.item.dto.input.query.ItemDetailRestQuery;
import com.aliyun.gts.gmall.manager.front.item.dto.output.ItemDetailVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.sku.ItemSkuVO;
import com.aliyun.gts.gmall.manager.front.item.facade.ItemDetailFacade;
import com.aliyun.gts.gmall.manager.front.item.facade.ItemGroupFacade;
import com.aliyun.gts.gmall.platform.item.api.dto.input.item.ItemQueryReq;
import com.aliyun.gts.gmall.platform.item.api.dto.output.item.ItemDTO;
import com.aliyun.gts.gmall.platform.item.api.facade.item.ItemReadFacade;
import com.aliyun.gts.gmall.platform.item.common.enums.ItemStatus;
import com.aliyun.gts.gmall.platform.promotion.api.dto.model.GrGroupRelationDTO;
import com.aliyun.gts.gmall.platform.promotion.api.facade.admin.GrBizGroupReadFacade;
import com.aliyun.gts.gmall.platform.promotion.common.query.GrGroupQuery;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * description: 商品分组查询
 *
 * @author hu.zhiyong
 * @date 2022/09/26 17:10
 **/
@Service
@Slf4j
public class ItemGroupFacadeImpl implements ItemGroupFacade {

    @Autowired
    private GrBizGroupReadFacade grBizGroupReadFacade;

    @Autowired
    private ItemReadFacade itemReadFacade;

    @Autowired
    private ItemDetailFacade itemDetailFacade;

    @Override
    public List<ItemDetailVO> queryRelation(GrGroupQuery grGroupQuery) {
        if (null == grGroupQuery.getGroupId() && CollectionUtil.isEmpty(grGroupQuery.getGroupIds())) {
            return Lists.newArrayList();
        }
        RpcResponse<PageInfo<GrGroupRelationDTO>> response = grBizGroupReadFacade.queryRelation(grGroupQuery);
        return fillItemInfo(response.getData(), grGroupQuery);
    }

    public List<ItemDetailVO> fillItemInfo(PageInfo<GrGroupRelationDTO> pageInfo, GrGroupQuery grGroupQuery) {
        List<ItemDetailVO> list = new ArrayList<>();
        if (pageInfo == null || pageInfo.getList() == null) {
            return list;
        }
        //注释查询分组信息接口，返回分组名称，前端不能展示
//        RpcResponse<PageInfo<GrBizGroupDTO>> pageInfoRpcResponse = grBizGroupReadFacade.queryGroup(grGroupQuery);
        List<Long> itemIds = this.parseItemIds(pageInfo.getList());
        // 获取商品信息，含sku和类目
        for (Long itemId : itemIds) {

            ItemQueryReq itemQueryReq = ItemQueryReq.create(itemId);
            //获取商品状态不需要查询sku 扩展信息 类目信息
            itemQueryReq.setWithSku(Boolean.FALSE);
            itemQueryReq.setWithExtend(Boolean.FALSE);
            itemQueryReq.setWithCatProp(Boolean.FALSE);
            RpcResponse<ItemDTO> rpcResponse = itemReadFacade.queryCacheItem(itemQueryReq);
            if (!rpcResponse.isSuccess()) {
                continue;
            }
            ItemDTO itemDTO = rpcResponse.getData();
            // 商品状态不是上架，则该商品不存在,则跳过
            if (itemDTO == null || !ItemStatus.ENABLE.getStatus().equals(itemDTO.getStatus())) {
                continue;
            }
//            // 如果商品是外采就过跳过
//            if(Objects.nonNull(itemDTO.getFeatureMap()) &&
//                    itemDTO.getFeatureMap().containsKey(ItemExtendConstant.WAICAI) &&
//                    "1".equals(itemDTO.getFeatureMap().get(ItemExtendConstant.WAICAI))) {
//                continue;
//            }
            CustDTO user = UserHolder.getUser();
            ItemDetailRestQuery query = new ItemDetailRestQuery();
            query.setItemId(itemId);
            //query.setCustId(Objects.nonNull(user) ? user.getCustId() : null);
            ItemDetailVO itemDetailVO = itemDetailFacade.queryItemBase(query);
            if (Objects.nonNull(itemDetailVO)) {
                //移除没有库存的sku信息 返回给前端用  前端需要展示无库存的商品不能勾选
                this.filterSku(itemDetailVO);
                //填充商品分组名称
                //注释查询分组信息接口，返回分组名称，前端不能展示
//                this.fillGroupName(itemId, pageInfoRpcResponse.getData(), itemDetailVO, pageInfo.getList());
                //sku price 排序 asc
                this.priceSortAsc(itemDetailVO);
                list.add(itemDetailVO);
            }
        }
        return list;
    }

//    private void fillGroupName(Long itemId, PageInfo<GrBizGroupDTO> groupDTOPageInfo, ItemDetailVO itemDetailVO,
//                               List<GrGroupRelationDTO> grGroupRelationDTOList) {
//        if (groupDTOPageInfo == null || CollectionUtils.isEmpty(groupDTOPageInfo.getList())) {
//            return;
//        }
//        Optional<GrGroupRelationDTO> grGroupRelationDTOOptional = grGroupRelationDTOList.stream()
//                .filter(grGroupRelationDTO -> itemId.equals(grGroupRelationDTO.getDomainId()))
//                .findFirst();
//        if (!grGroupRelationDTOOptional.isPresent()) {
//            return;
//        }
//        GrGroupRelationDTO grGroupRelationDTO = grGroupRelationDTOOptional.get();
//        List<GrBizGroupDTO> list = groupDTOPageInfo.getList();
//        Optional<GrBizGroupDTO> grBizGroupDTOOptional = list.stream()
//                .filter(grBizGroupDTO -> grGroupRelationDTO.getGroupId().equals(grBizGroupDTO.getId()))
//                .findFirst();
//        if (!grBizGroupDTOOptional.isPresent()) {
//            return;
//        }
//        GrBizGroupDTO grBizGroupDTO = grBizGroupDTOOptional.get();
//        itemDetailVO.setGroupName(grBizGroupDTO.getName());
//    }

    private void filterSku(ItemDetailVO itemDetailVO) {
        List<ItemSkuVO> itemSkuVOList = itemDetailVO.getItemSkuVOList();
        if (CollectionUtils.isEmpty(itemSkuVOList)) {
            return;
        }
        List<ItemSkuVO> collect = itemSkuVOList.stream().filter(itemSkuVO -> itemSkuVO.getQuantity() > 0)
                .collect(Collectors.toList());
        itemDetailVO.setItemSkuVOList(collect);
    }

    private void priceSortAsc(ItemDetailVO itemDetailVO) {
        List<ItemSkuVO> itemSkuVOList = itemDetailVO.getItemSkuVOList();
        if (CollectionUtils.isEmpty(itemSkuVOList)) {
            return;
        }
        List<ItemSkuVO> collect = itemSkuVOList.stream()
                .sorted(Comparator.comparing(ItemSkuVO::getPrice))
                .collect(Collectors.toList());
        itemDetailVO.setItemSkuVOList(collect);
    }

    private List<Long> parseItemIds(List<GrGroupRelationDTO> voList) {
        List<Long> list = Lists.newArrayList();
        if (null == voList) {
            return list;
        }
        for (GrGroupRelationDTO detailVo : voList) {
            list.add(detailVo.getDomainId());
        }
        return list;
    }

}
