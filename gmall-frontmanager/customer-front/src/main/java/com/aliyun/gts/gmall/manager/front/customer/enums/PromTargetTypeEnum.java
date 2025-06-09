package com.aliyun.gts.gmall.manager.front.customer.enums;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.dto.PageParam;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.query.ByIdCouponQuery;
import com.aliyun.gts.gmall.manager.front.customer.dto.output.PromCampaignVO;
import com.aliyun.gts.gmall.manager.front.customer.dto.output.PromDetailVO;
import com.aliyun.gts.gmall.platform.promotion.api.dto.model.GrGroupRelationDTO;
import com.aliyun.gts.gmall.platform.promotion.api.facade.admin.GrBizGroupReadFacade;
import com.aliyun.gts.gmall.platform.promotion.common.query.GrGroupQuery;
import com.aliyun.gts.gmall.searcher.api.dto.input.ItemQueryRequest;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

@Slf4j
public enum PromTargetTypeEnum {

    ITEM_TOTAL(1) {
        @Override
        public ItemQueryRequest buildSearchReq(PromCampaignVO promCampaignVO, ByIdCouponQuery query, GrBizGroupReadFacade grBizGroupReadFacade) {
            ItemQueryRequest itemQueryRequest = new ItemQueryRequest();
            //sellerId！=0说明是店铺券
            if(promCampaignVO.getSellerId()>0) {
                itemQueryRequest.setSellerIds(Collections.singletonList(String.valueOf(promCampaignVO.getSellerId())));
            }
            itemQueryRequest.setPage(query.getPage());
            return itemQueryRequest;
        }
    },

    ITEM_PREDEFIND(2) {
        @Override
        public ItemQueryRequest buildSearchReq(PromCampaignVO promCampaignVO, ByIdCouponQuery query, GrBizGroupReadFacade grBizGroupReadFacade) {
            ItemQueryRequest itemQueryRequest = new ItemQueryRequest();
            List<String> itemIds = getItemIds(promCampaignVO.getDetails());
            if (CollectionUtils.isEmpty(itemIds)) {
                return null;
            }
            itemQueryRequest.setItemIds(itemIds);
            itemQueryRequest.setPage(query.getPage());
            return itemQueryRequest;
        }
    },

    ITEM_GROUP(4) {
        @Override
        public ItemQueryRequest buildSearchReq(PromCampaignVO promCampaignVO, ByIdCouponQuery query, GrBizGroupReadFacade grBizGroupReadFacade) {
            try {
                JSONObject conditionRule = promCampaignVO.getConditionRule();
                JSONArray includeItemGroup = conditionRule.getJSONArray("includeItemGroup");
                if (includeItemGroup == null || includeItemGroup.isEmpty()) {
                    return null;
                }
                List<Long> groupIds = Lists.newArrayList();
                for (Object o : includeItemGroup) {
                    groupIds.add((Long) o);
                }
                GrGroupQuery grGroupQuery = new GrGroupQuery();
                grGroupQuery.setPage(new PageParam());
                setGroupIds(grGroupQuery, groupIds);
                RpcResponse<PageInfo<GrGroupRelationDTO>> response = grBizGroupReadFacade.queryRelation(grGroupQuery);
                if (response == null || !response.isSuccess()) {
                    log.error("grBizGroupReadFacade.queryRelation not success response=" + JSONObject.toJSONString(response));
                    return null;
                }
                ItemQueryRequest itemQueryRequest = new ItemQueryRequest();
                itemQueryRequest.setItemIds(parseItemIds(response.getData().getList()));
                itemQueryRequest.setPage(query.getPage());
                return itemQueryRequest;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return null;
            }
        }
    };

    private static List<String> parseItemIds(List<GrGroupRelationDTO> voList) {
        List<String> list = Lists.newArrayList();
        if (null == voList) {
            return list;
        }
        for (GrGroupRelationDTO detailVo : voList) {
            list.add(detailVo.getDomainId().toString());
        }
        return list;
    }

    /**
     * 考虑分组太多影响查询性能、前台限制最多只展示5个分组
     *
     * @param grGroupQuery
     * @param groupIds
     */
    private static void setGroupIds(GrGroupQuery grGroupQuery, List<Long> groupIds) {
        if (groupIds.size() <= 1) {
            grGroupQuery.setGroupId(groupIds.get(0));
            return;
        }
        List<Long> newGroupIds = Lists.newArrayList();
        if (groupIds.size() > MAX_GROUPS_ID_NUM) {
            newGroupIds.addAll(groupIds.subList(0, MAX_GROUPS_ID_NUM));
        } else {
            newGroupIds.addAll(groupIds);
        }
        grGroupQuery.setGroupIds(newGroupIds);
    }

    /**
     * 查找类型
     *
     * @param type
     * @return
     */
    public static PromTargetTypeEnum find(Integer type) {
        for (PromTargetTypeEnum promTargetTypeEnum : values()) {
            if (promTargetTypeEnum.type.intValue() == type.intValue()) {
                return promTargetTypeEnum;
            }
        }
        return null;
    }

    private Integer type;

    private static final int MAX_GROUPS_ID_NUM = 5;

    PromTargetTypeEnum(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return this.type;
    }

    private static List<String> getItemIds(List<PromDetailVO> details) {
        List<String> itemIds = Lists.newArrayList();
        for (PromDetailVO promDetailVO : details) {
            if (promDetailVO.getItemId() == null) {
                continue;
            }
            itemIds.add(promDetailVO.getItemId().toString());
        }
        return itemIds;
    }

    public abstract ItemQueryRequest buildSearchReq(PromCampaignVO promCampaignVO, ByIdCouponQuery query, GrBizGroupReadFacade grBizGroupReadFacade);

}
