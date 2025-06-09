package com.aliyun.gts.gmall.manager.front.item.facade.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.apache.commons.lang3.tuple.Pair;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.client.naming.utils.CollectionUtils;
import com.aliyun.gts.gmall.framework.config.I18NConfig;
import com.aliyun.gts.gmall.manager.front.item.convertor.ItemEsConverter;
import com.aliyun.gts.gmall.manager.front.item.dto.output.sku.SkuQuoteListVO;
import com.aliyun.gts.gmall.manager.front.item.dto.temp.ItemEvaluationTempVO;
import com.aliyun.gts.gmall.manager.front.item.dto.temp.ItemSaleSellerQuery;
import com.aliyun.gts.gmall.manager.front.item.dto.temp.ItemSaleSellerTempVO;
import com.aliyun.gts.gmall.manager.front.item.dto.temp.PromotionInfoTempVO;
import com.aliyun.gts.gmall.manager.front.item.dto.temp.SellerDeliveryTempVO;
import com.aliyun.gts.gmall.manager.front.item.dto.temp.SellerScoreTempVO;
import com.aliyun.gts.gmall.manager.front.item.dto.temp.SellerTempVO;
import com.aliyun.gts.gmall.platform.item.api.dto.output.category.CategoryDTO;
import com.aliyun.gts.gmall.platform.item.api.dto.output.item.SkuQuoteEsDTO;

@Component
public class ItemComponent {
    @Resource
    private I18NConfig i18NConfig;

    @Autowired
    private ItemEsConverter itemEsConverter;

    /**
     * 获取语言
     * 
     * @return
     */
    public String getLang() {
        return "zh";
    }

    /**
     * 返回排序类型
     * 
     * @param value
     * @return
     */
    public static SortOrder getSortOrder(String value) {
        if ("0".equals(value)) {
            return SortOrder.ASC;
        } else {
            return SortOrder.DESC;
        }
    }

    /**
     * 模型转换
     *
     * @param target
     * @return
     */
    public SkuQuoteListVO convert2SkuQuoteVO(String target) {
        SkuQuoteEsDTO skuQuoteEsDTO = JSON.parseObject(target, SkuQuoteEsDTO.class);
        SkuQuoteListVO source = itemEsConverter.toVo(skuQuoteEsDTO, getLang());
        return source;
    }


    /**
     * 获取类目的名字(父亲+自己)
     * 
     * @param list
     * @return
     */
    public List<String> toCategoryName(List<CategoryDTO> list) {
        String fallback = i18NConfig.getDefaultLang();
        return list.stream().map(n -> n.getName().getValueByLang(getLang(), fallback)).collect(Collectors.toList());
    }

    /**
     * MultiLangText 类型获取真实值
     * 
     * @param obj
     * @param lang
     * @return
     */
    public static Pair<Long, String> toCategory(Object obj, String lang) {
        Map<String, Object> map = (Map<String, Object>) obj;
        Long id = Long.parseLong(map.get("id").toString());
        List<Map<String, String>> langSet = (List<Map<String, String>>) (map.get("langSet"));
        String value = langSet.stream().filter(n -> lang.equals(n.get("lang"))).findFirst().get().get("value");
        return Pair.of(id, value);
    }

    /**
     * ID + MultiLangValue 类型获取真实值
     * 
     * @param obj
     * @param lang
     * @return
     */
    public static String toValue(Object obj, String lang) {
        List<Map<String, String>> values = (List<Map<String, String>>) obj;
        String value = values.stream().filter(n -> lang.equals(n.get("lang"))).findFirst().get().get("value");
        return value;
    }

    // ----------------------------------------------商品详情的支持---------------------------------------------
    /**
     * 批量查询售卖商品的商家
     * 
     * @param query 查询条件
     * @return 售卖商家列表
     */
    public List<ItemSaleSellerTempVO> getItemSaleSellerTempList(ItemSaleSellerQuery query) {
        if (Objects.isNull(query)) {
            return Collections.emptyList();
        }

        try {

        } catch (Exception e) {

        }
        return new ArrayList<>();
    }

    /**
     * 批量查询商家基本信息
     * 
     * @param sellerIds 商家ids
     * @return <商家id, 商家信息>
     */
    public Map<Long, SellerTempVO> getSellerMap(List<Long> sellerIds) {
        if (CollectionUtils.isEmpty(sellerIds)) {
            return Collections.emptyMap();
        }

        try {

        } catch (Exception e) {

        }
        return new HashMap<>();
    }

    /**
     * 批量查询商品的评价信息
     * 
     * @param skuId skuId
     * @param pageIndex 分页页码
     * @param pageSize 分页数
     * @return 评价信息列表
     */
    public List<ItemEvaluationTempVO> getItemEvaluationList(Long skuId, int pageIndex, int pageSize) {
        if (Objects.isNull(skuId)) {
            return Collections.emptyList();
        }

        try {

        } catch (Exception e) {

        }
        return new ArrayList<>();
    }

    /**
     * 批量查询优惠信息
     * 
     * @param skuId skuId
     * @param sellerIds 商家ids
     * @return <商家id, 优惠信息>
     */
    public Map<Long, PromotionInfoTempVO> getPromotionInfoMap(Long skuId, List<Long> sellerIds) {
        if (Objects.isNull(skuId) || CollectionUtils.isEmpty(sellerIds)) {
            return Collections.emptyMap();
        }

        try {

        } catch (Exception e) {

        }

        return new HashMap<>();
    }

    /**
     * 查询商品的优惠信息
     * 
     * @param skuId skuId
     * @return 优惠信息列表
     */
    public List<PromotionInfoTempVO> getPromotionInfoList(Long skuId) {
        if (Objects.isNull(skuId)) {
            return Collections.emptyList();
        }

        try {

        } catch (Exception e) {

        }

        return new ArrayList<>();
    }

    /**
     * 批量查询商家评分
     * 
     * @param sellerIds 商家ids
     * @return <商家id, 商家评分>
     */
    public Map<Long, SellerScoreTempVO> getSellerScore(List<Long> sellerIds) {
        if (CollectionUtils.isEmpty(sellerIds)) {
            return Collections.emptyMap();
        }

        try {

        } catch (Exception e) {

        }

        return new HashMap<>();
    }

    /**
     * 批量查询商家物流信息
     * 
     * @param sellerIds 商家id
     * @return <商家Id, 物流信息>
     */
    public Map<Long, SellerDeliveryTempVO> getSellerDeliveryMap(List<Long> sellerIds) {
        if (CollectionUtils.isEmpty(sellerIds)) {
            return Collections.emptyMap();
        }

        try {

        } catch (Exception e) {

        }

        return new HashMap<>();
    }
}
