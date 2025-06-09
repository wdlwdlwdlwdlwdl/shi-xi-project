package com.aliyun.gts.gmall.platform.trade.core.util;

import com.aliyun.gts.gmall.platform.item.api.dto.output.item.SkuQuoteWarehourseStockDTO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SplitOrderItemInfo;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 拆单算法计算工具类
 *
 * 参数按照商品分组 每个商品都有自己的仓库
 * 以仓库维度分组 每个仓库存在自己的商品
 * 按照贪心算法  计算取最大权益
 */
public class SplitOrderUtils {

    /**
     * 拆单计算
     * @param categoryList 要拆单的订单集合
     * @return List<List<SplitOrderItemInfo>>
     * 2024-12-17 17:31:43
     */
    public static List<List<SplitOrderItemInfo>> splitOrder(List<SplitOrderItemInfo> categoryList) {
        List<List<SplitOrderItemInfo>> splitList = new ArrayList<>();
//        // 优先匹配同城市的
//        List<SplitOrderItemInfo> sameCityList = categoryList.stream().filter(splitOrderItemInfo -> splitOrderItemInfo.getCityCode().equals(splitOrderItemInfo.getCityCode())).collect(Collectors.toList());
//        // 同城存在 优先同城
//        if (CollectionUtils.isNotEmpty(splitList)) {
//            splitList.add(sameCityList);
//        }
        // 剩余的使用算法计算
//        List<SplitOrderItemInfo> otherCityList = categoryList.stream().filter(splitOrderItemInfo -> !splitOrderItemInfo.getCityCode().equals(splitOrderItemInfo.getCityCode())).collect(Collectors.toList());
//         按照发货点反过来分组
        Multimap<Long, SplitOrderItemInfo> warehourseStockMap = LinkedHashMultimap.create();
        //每个仓库对应的stockId和仓库对应关系
        Map<Long, SkuQuoteWarehourseStockDTO> warehourseStockIdMap = new HashMap<>();
        for (SplitOrderItemInfo splitOrderItemInfo : categoryList) {
            for (SkuQuoteWarehourseStockDTO skuQuoteWarehourseStockDTO : splitOrderItemInfo.getWarehourseStockList()) {
               warehourseStockMap.put(skuQuoteWarehourseStockDTO.getStockId(), splitOrderItemInfo);
               warehourseStockIdMap.put(skuQuoteWarehourseStockDTO.getStockId(),skuQuoteWarehourseStockDTO);
            }
        }

        //订单数量
        int categoryListSize = categoryList.size();
        //包含所有订单的仓库
        Set<SkuQuoteWarehourseStockDTO> warehourseStockIncludeAllList = new HashSet<>();
        for (Long key : warehourseStockMap.keys()) {
            //每个仓库的订单数量
          int stockItemSize = warehourseStockMap.get(key).size();
          if(categoryListSize == stockItemSize){
              //筛选所有的包含
              warehourseStockIncludeAllList.add(warehourseStockIdMap.get(key));
          }
        }


        /**
         *  贪心算法计算
         *  获取可以使用最多的的仓库点 找到每个仓库能发的商品 生成一单
         *  理论上出现
         */
        Map<Long, Set<SplitOrderItemInfo>> skuQuoteWarehourseStock = greedyCover(warehourseStockMap);
        int length = skuQuoteWarehourseStock.size();
        // 遍历每个仓库点，一个仓库点,给每个商品设置发货地址。
        for (Map.Entry<Long, Set<SplitOrderItemInfo>> entry : skuQuoteWarehourseStock.entrySet()) {
            List<SplitOrderItemInfo> warehourseList = new ArrayList<>(entry.getValue());
            for (SplitOrderItemInfo splitOrderItemInfo : warehourseList) {
                if (length == 1 && CollectionUtils.isNotEmpty(warehourseStockIncludeAllList) &&warehourseStockIncludeAllList.size()> 1 ) {
                    splitOrderItemInfo.setWarehourseStockList(new ArrayList<>(warehourseStockIncludeAllList));
                }
                else {
                    splitOrderItemInfo.setWarehourseStockList(List.of(warehourseStockIdMap.get(entry.getKey())));
                }
            }
            splitList.add(warehourseList);
        }
        return splitList;
    }

    /**
     * 贪心算法 计算
     * @param warehouseStock 以仓库为单位的计算
     * @return List<SkuQuoteWarehourseStockDTO>
     * 2024-12-17 18:00:51
     */
    public static Map<Long, Set<SplitOrderItemInfo>> greedyCover(Multimap<Long, SplitOrderItemInfo> warehouseStock) {
        // 创建一个 HashSet 存放需要覆盖但还未覆盖的商品
        Set<SplitOrderItemInfo> notCover = new HashSet<>();//可去重
        // 填充需要覆盖的商品
        for (SplitOrderItemInfo splitOrderItemInfo : warehouseStock.values()) {
            notCover.add(splitOrderItemInfo);
        }
        // 用于存放最终选择的仓库及其包含的商品列表（结果）
        Map<Long, Set<SplitOrderItemInfo>> warehourseStockDTOS = new LinkedHashMap<>();
        while (!notCover.isEmpty()) {
            Long skuQuoteWarehourseStockDTO = null;
            Set<SplitOrderItemInfo> bestCover = new HashSet<>();
            int maxNum = 0;
            // 最大的贡献值，比如北京有3个商品（最多）
            // 选择覆盖了最多未覆盖商品的城市
            Set<Long> wareHourseSet = warehouseStock.keySet();
            List<Long> wareHourseList = new ArrayList<>(wareHourseSet);
            Collections.sort(wareHourseList, Collections.reverseOrder()); // 大到小排序
            for (Long skuQuoteWarehourseStock : wareHourseList) {
                //收货地点含有的商品
                Collection<SplitOrderItemInfo> splitOrderItemInfos =  warehouseStock.get(skuQuoteWarehourseStock);
                // 计算城市覆盖的商品和未覆盖商品的交集
                //将notCover复制到intersection中，intersection相当于notCover副本
                Set<SplitOrderItemInfo> intersection = new HashSet<>(notCover);
                //retainAll 用于保留集合中的交集部分，即 intersection 和 products 中共有的部分。
                intersection.retainAll(splitOrderItemInfos);
                if (intersection.size() > maxNum) {
                    skuQuoteWarehourseStockDTO = skuQuoteWarehourseStock;
                    maxNum = intersection.size();
                    bestCover = intersection;
                }
            }
            // 更新：选择当前最优仓库，并更新未覆盖的商品集合
            if (skuQuoteWarehourseStockDTO != null) {
                // 保存选中的仓库及其覆盖的商品
                warehourseStockDTOS.put(skuQuoteWarehourseStockDTO, bestCover);
                // 标记这些商品为已覆盖
                notCover.removeAll(bestCover);
            } else {
                // 如果没有满足的仓库（某些商品无法覆盖），退出
                break;
            }
        }
        // 如果还有未覆盖的商品，说明无解
        if (!notCover.isEmpty()) {
            throw new IllegalArgumentException("无法覆盖所有目标商品，某些商品缺失！");
        }
        return warehourseStockDTOS;
    }

    /**
     * 取相同城市的发货仓库
     * @param stockList
     * @param cityCode
     * @return SkuQuoteWarehourseStockDTO
     * 2024-12-17 17:08:18
     */
    public static SkuQuoteWarehourseStockDTO getSameCitySkuWarehourseStock(List<SkuQuoteWarehourseStockDTO> stockList, String cityCode) {
        SkuQuoteWarehourseStockDTO skuQuoteWarehourseStock = stockList
            .stream()
            .findFirst()
            .filter(skuQuoteWarehourseStockDTO -> cityCode.equals(skuQuoteWarehourseStockDTO.getStockCityCode()))
            .orElse(null);
        return skuQuoteWarehourseStock;
    }

    /**
     * 不拆单的情况下 单独一个商品一单， 优先取相同城市的 取不到 随便起一个城市的即可
     * @param stockList
     * @param cityCode
     * @return SkuQuoteWarehourseStockDTO
     * 2024-12-17 17:08:18
     */
    public static SkuQuoteWarehourseStockDTO getSkuWarehourseStock(List<SkuQuoteWarehourseStockDTO> stockList, String cityCode) {
        SkuQuoteWarehourseStockDTO skuQuoteWarehourseStock = stockList
            .stream()
            .findFirst()
            .filter(skuQuoteWarehourseStockDTO -> cityCode.equals(skuQuoteWarehourseStockDTO.getStockCityCode()))
            .orElse(null);
        return skuQuoteWarehourseStock != null ? skuQuoteWarehourseStock : stockList.get(0);
    }
}
