package com.aliyun.gts.gmall.manager.front.sourcing.utils;

import com.aliyun.gts.gcai.platform.sourcing.api.dto.model.AwardQuoteDetailDTO;
import com.aliyun.gts.gcai.platform.sourcing.api.dto.model.PricingBillDTO;
import com.aliyun.gts.gmall.manager.front.b2bcomm.utils.PriceUtils;
import com.aliyun.gts.gmall.manager.front.sourcing.vo.PricingBillVo;
import com.aliyun.gts.gmall.manager.front.sourcing.vo.QuoteDetailVo;
import com.aliyun.gts.gmall.manager.front.sourcing.vo.QuoteVo;
import com.aliyun.gts.gmall.manager.front.sourcing.vo.SourcingMaterialVo;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/5/21 13:34
 */
public class PricingBillBuildUtils {

    public static List<Long> combine(List<Long> a,List<Long> b) {
        if (CollectionUtils.isEmpty(a)) {
            return b;
        }
        List<Long> result = new ArrayList<>();
        Set<Long> sets = new HashSet<>();
        sets.addAll(a);
        for(Long id : b){
            if(!sets.contains(id)){
                a.add(id);
            }
        }
        return a;
    }
    public static List<Long> unique(Collection<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return new ArrayList<>();
        }
        List<Long> result = new ArrayList<>();
        Set<Long> set = new HashSet<>();
        for (Long id : ids) {
            if (!set.contains(id)) {
                result.add(id);
            }
            set.add(id);
        }
        return result;
    }

    /**
     * 构建命中对象
     *
     * @param dto
     * @param billVo
     */
    public static void buildSelectQuote(PricingBillDTO dto, PricingBillVo billVo) {
        Map<Long, AwardQuoteDetailDTO> maps = colletMap(dto);
        if (CollectionUtils.isEmpty(billVo.getMaterialQuotes())) {
            return;
        }
        for (PricingBillVo.MaterialQuoteVo materialQuoteVo : billVo.getMaterialQuotes()) {
            if (CollectionUtils.isEmpty(materialQuoteVo.getQuoteDetails())) {
                continue;
            }
            //直接命中
            for (QuoteDetailVo detailVo : materialQuoteVo.getQuoteDetails()) {
                AwardQuoteDetailDTO selectQuoteDetailDTO = maps.get(detailVo.getId());
                if (selectQuoteDetailDTO != null) {
                    detailVo.setSelected(true);
                    detailVo.setAwardNum(selectQuoteDetailDTO.getAwardNum());
                }
            }
        }
    }

    public static Map<Long, AwardQuoteDetailDTO> colletMap(PricingBillDTO dto) {
        Map<Long, AwardQuoteDetailDTO> map = new HashMap<>();
        if (CollectionUtils.isEmpty(dto.getAwardQuote())) {
            return map;
        }
        for (AwardQuoteDetailDTO select : dto.getAwardQuote()) {
            map.put(select.getQuoteDetailId(), select);
        }
        return map;
    }

    /*
     * @param scMaterialId
     * @param quoteVos
     * @return
     */
    public static List<QuoteDetailVo> filterQuote(SourcingMaterialVo vo, List<QuoteVo> quoteVos,
                                                  boolean fillDefaultAwardNum) {
        Long scMaterialId = vo.getId();
        List<QuoteDetailVo> result = new ArrayList<QuoteDetailVo>();
        if (CollectionUtils.isEmpty(quoteVos)) {
            return result;
        }
        for (QuoteVo dto : quoteVos) {
            if (CollectionUtils.isEmpty(dto.getDetails())) {
                continue;
            }
            //详情
            for (QuoteDetailVo detailVO : dto.getDetails()) {
                //找到对应的报价信息
                if (detailVO.getScMaterialId().equals(scMaterialId)) {
                    if (detailVO.getNum() == null) {
                        detailVO.setNum(vo.getNum());
                    }
                    //授标数量
                    if (detailVO.getAwardNum() == null) {
                        if (fillDefaultAwardNum) {
                            detailVO.setAwardNum(vo.getNum());  // 默认填报价数量
                        } else {
                            detailVO.setAwardNum(0);
                        }
                    }
                    result.add(detailVO);
                }
            }
        }
        return result;
    }

    public static void rank(List<QuoteVo> quoteDetailVos) {
        if (CollectionUtils.isEmpty(quoteDetailVos)) {
            return;
        }
        //排序
        Collections.sort(quoteDetailVos, new Comparator<QuoteVo>() {
            public int compare(QuoteVo arg0, QuoteVo arg1) {
                if (arg0.getTotalPriceFen() != null && arg1.getTotalPriceFen() != null) {
                    return arg1.getTotalPriceFen().compareTo(arg0.getTotalPriceFen());
                }
                return 0;
            }
        });
        for(int i = 0 ;i< quoteDetailVos.size();i++){
            QuoteVo quoteDetailVo  = quoteDetailVos.get(i);
            quoteDetailVo.setRank(i+1);
            quoteDetailVo.setTotalPrice(PriceUtils.fenToYuan(quoteDetailVo.getTotalPriceFen()));
        }
    }
}
