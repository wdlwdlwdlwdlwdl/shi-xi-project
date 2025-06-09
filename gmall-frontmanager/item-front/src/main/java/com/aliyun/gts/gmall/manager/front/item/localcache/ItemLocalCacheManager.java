package com.aliyun.gts.gmall.manager.front.item.localcache;

import com.aliyun.gts.gmall.framework.server.util.PublicFileHttpUrl;
import com.aliyun.gts.gmall.manager.framework.localcache.BaseLocalCacheManager;
import com.aliyun.gts.gmall.manager.front.common.consts.BizConst;
import com.aliyun.gts.gmall.manager.front.common.util.ItemUtils;
import com.aliyun.gts.gmall.manager.front.item.adaptor.ItemAdaptor;
import com.aliyun.gts.gmall.manager.front.item.adaptor.SearchAdaptor;
import com.aliyun.gts.gmall.manager.front.item.dto.output.ItemDetailVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.ItemSearchVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.sku.ItemSkuPropVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.sku.ItemSkuVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.sku.ItemSkuValueVO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 商品缓存数据
 *
 * @author tiansong
 */
@Component
public class ItemLocalCacheManager extends BaseLocalCacheManager<Long, ItemDetailVO> {
    @Override
    protected long getMaxSize() {
        return 1000L;
    }

    @Override
    protected long getDurationSecond() {
        return 5L;
    }

    @Resource
    private ItemAdaptor    itemAdaptor;
    @Resource
    private SearchAdaptor  searchAdaptor;
    @Autowired
    private PublicFileHttpUrl publicFileHttpUrl;
    @Value("#{ '${minio.enabled}' == 'true' ? '${minio.bucket.item:itemBucket}' : '${oss.bucket.item}' }")
    private String itemBucket;

    @Override
    protected ItemDetailVO queryRpc(Long itemId) {
        // 1. 获取商品信息，含sku和类目
        ItemDetailVO itemDetailVO = itemAdaptor.getItemDetail(itemId);
        ItemSearchVO searchItem   = searchAdaptor.queryById(itemId);
        this.fillItemData(itemDetailVO, searchItem);
        // 过滤无效的sku属性值
        this.filterSkuPropValue(itemDetailVO);
        return itemDetailVO;
    }

    private void fillItemData(ItemDetailVO itemDetailVO, ItemSearchVO itemSearchVO) {
        // 汇总商品各SKU的库存
        itemDetailVO.setItemQuantity(itemDetailVO.getItemSkuVOList().stream().mapToLong(ItemSkuVO::getQuantity).sum());
        // 区间价格设置
        LongSummaryStatistics minAndMax = itemDetailVO.getItemSkuVOList().stream().mapToLong(
                ItemSkuVO::getPrice).summaryStatistics();
        itemDetailVO.setItemPrice(ItemUtils.fen2YuanRange(minAndMax.getMin(), minAndMax.getMax()));
        // 搜索相关数据

        if (itemSearchVO != null) {
            itemDetailVO.setSellCount(NumberUtils.toLong(itemSearchVO.getSaleCount30()));
            itemDetailVO.setEvaluationScore(formatScore(itemSearchVO.getEvaluationScore()));
        }
        // 补充Sku name
        this.fillSkuName(itemDetailVO);
        // 从OSS补充商品描述信息
        this.fillItemDescFromOss(itemDetailVO);
    }

    private static String formatScore(String score) {
        if (StringUtils.isBlank(score)) {
            return null;
        }
        BigDecimal d;
        try {
            d = new BigDecimal(score);
        } catch (NumberFormatException e) {
            return null;
        }
        if (d.compareTo(BigDecimal.ZERO) < 0) {
            return null;
        }
        return d.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
    }

    private void filterSkuPropValue(ItemDetailVO itemDetailVO) {
        if (CollectionUtils.isEmpty(itemDetailVO.getItemSkuVOList()) ||
                CollectionUtils.isEmpty(itemDetailVO.getItemSkuPropVOList())) {
            return;
        }
        // convert to set
        Set<String> valueSet = itemDetailVO.getItemSkuVOList().stream().flatMap(prop ->
                prop.getSkuPropList().stream()).map(value -> value.getVid()).collect(Collectors.toSet());
        // filter value
        itemDetailVO.getItemSkuPropVOList().forEach(itemSkuPropVO -> {
            List<ItemSkuValueVO> itemSkuValueVOList = itemSkuPropVO.getValueList().stream().filter(
                    itemSkuValueVO -> valueSet.contains(itemSkuValueVO.getVid())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(itemSkuValueVOList)) {
                itemSkuPropVO.setValueList(itemSkuValueVOList);
            }
        });
    }

    private void fillSkuName(ItemDetailVO itemDetailVO) {
        // 构建pid、vid为key，propName+valueName为value的双层map
        Map<Long/*pid*/, Map<String/*vid*/, String/*pName:vName*/>> skuPropValueMap = itemDetailVO
                .getItemSkuPropVOList().stream().collect(Collectors.toMap(ItemSkuPropVO::getPid,
                        itemSkuPropVO -> itemSkuPropVO.getValueList().stream().collect(Collectors.toMap(ItemSkuValueVO::getVid,
                                itemSkuValueVO -> itemSkuPropVO.getName() + BizConst.SKU_PV_SPLIT + itemSkuValueVO.getValue()))));

        itemDetailVO.getItemSkuVOList().forEach(itemSkuVO -> {
            // 组装SKU名称
            StringBuilder skuName = new StringBuilder();
            itemSkuVO.getSkuPropList().forEach(skuPropVO -> {
                String propValueName = skuPropValueMap.get(skuPropVO.getPid()).get(skuPropVO.getVid());
                if (propValueName == null) {
                    return;
                }
                skuName.append(BizConst.SKU_PROP_SPLIT + propValueName);
            });
            // 删除第一个分隔符
            itemSkuVO.setSkuName(skuName.length() > 0 ? skuName.deleteCharAt(0).toString() : null);
            // 设置选中sku名称
            if (itemDetailVO.getSelectedSkuId() != null && itemDetailVO.getSelectedSkuId().equals(itemSkuVO.getId())) {
                itemDetailVO.setSelectedSkuName(itemSkuVO.getSkuName());
            }
        });
    }

    private void fillItemDescFromOss(ItemDetailVO itemDetailVO) {
        String itemDescPath = itemDetailVO.getItemBaseVO().getItemDesc();
        if (StringUtils.isBlank(itemDescPath)) {
            return;
        }
        // 直接从oss读取，存在性能瓶颈，拼装前台访问地址，由客户端直接从oss读取即可
        String itemDesc = publicFileHttpUrl.getFileUrl(itemBucket, itemDescPath, Boolean.FALSE);
        itemDetailVO.getItemBaseVO().setItemDesc(itemDesc);
    }
}
