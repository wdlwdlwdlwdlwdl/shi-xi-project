package com.aliyun.gts.gmall.manager.front.item.dto.output;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @Title: SkuSearchPageThirdVO.java
 * @Description: TODO(用一句话描述该文件做什么)
 * @author zhao.qi
 * @date 2024年11月28日 17:42:20
 * @version V1.0
 */
@Getter
@Setter
public class SkuSearchPageThirdVO {
    private Long total;
    private List<SearchThirdItemVO> list;
    private List<KvModel<Long, String>> brands;
    private List<Long> categoryIds;
    private List<KvModel<Long, String>> sellers;

    private Long[] priceRange = new Long[2];
}
