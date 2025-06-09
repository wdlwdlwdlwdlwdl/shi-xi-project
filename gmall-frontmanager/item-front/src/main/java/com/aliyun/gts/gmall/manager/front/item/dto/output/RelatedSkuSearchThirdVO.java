package com.aliyun.gts.gmall.manager.front.item.dto.output;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @Title: RelatedSkuSearchThirdVO.java
 * @Description: TODO(用一句话描述该文件做什么)
 * @author zhao.qi
 * @date 2024年11月28日 17:42:20
 * @version V1.0
 */
@Getter
@Setter
public class RelatedSkuSearchThirdVO {
    private Long total;
    private List<SearchThirdItemVO> list;
}
