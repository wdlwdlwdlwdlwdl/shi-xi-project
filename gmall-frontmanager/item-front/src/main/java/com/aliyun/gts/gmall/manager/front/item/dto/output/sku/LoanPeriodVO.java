package com.aliyun.gts.gmall.manager.front.item.dto.output.sku;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @Title: LoanPeriodVO.java
 * @Description: TODO(用一句话描述该文件做什么)
 * @author zhao.qi
 * @date 2025年3月7日 15:31:39
 * @version V1.0
 */
@Getter
@Setter
public class LoanPeriodVO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer type;
    private Long value;
    private Long valuePromotion;
}
