package com.aliyun.gts.gmall.manager.front.sourcing.utils;

import com.aliyun.gts.gcai.platform.sourcing.api.dto.model.SourcingApplyDTO;
import com.aliyun.gts.gmall.manager.front.b2bcomm.utils.PriceUtils;
import com.aliyun.gts.gmall.manager.front.sourcing.vo.BidingPriceVo;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/6/9 17:50
 */
public class BidingPriceBuildUtils {

    public static List<BidingPriceVo> computeRange(List<BidingPriceVo> current){
        for(BidingPriceVo priceVo : current){
            Long div = priceVo.getFirstPriceFen() - priceVo.getTotalPriceFen();
            priceVo.setPriceRange(PriceUtils.fenToYuan(div));
        }
        return current;
    }
    public static List<BidingPriceVo> rank(List<BidingPriceVo> list) {
        if (CollectionUtils.isEmpty(list)) {
            return list;
        }
        //排序
        Collections.sort(list, new Comparator<BidingPriceVo>() {
            public int compare(BidingPriceVo arg0, BidingPriceVo arg1) {
                return arg0.getTotalPriceFen().compareTo(arg1.getTotalPriceFen());
            }
        });
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setRank(i + 1);
        }
        return list;
    }
    public static List<BidingPriceVo> merge(List<BidingPriceVo> list, List<SourcingApplyDTO> applyDTOS) {
        Map<Long, BidingPriceVo> priceMap = new HashMap<>();
        List<BidingPriceVo> result = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            priceMap = list.stream().collect(Collectors.toMap(en -> en.getSupplierId(), en -> en));
        } else {
            list = new ArrayList<>();
        }
        result.addAll(list);
        //报名为空就返回
        if (CollectionUtils.isEmpty(applyDTOS)) {
            return result;
        }
        for (SourcingApplyDTO applyDTO : applyDTOS) {
            BidingPriceVo find = priceMap.get(applyDTO.getSupplierId());
            if (find != null) {
                continue;
            }
            BidingPriceVo vo = new BidingPriceVo();
            vo.setSupplierName(applyDTO.getSupplierName());
            result.add(vo);
        }
        return result;
    }
}
