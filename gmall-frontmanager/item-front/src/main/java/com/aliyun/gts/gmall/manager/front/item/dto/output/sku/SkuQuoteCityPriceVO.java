package com.aliyun.gts.gmall.manager.front.item.dto.output.sku;

import com.aliyun.gts.gmall.platform.item.api.dto.output.item.LoanPeriodDTO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SkuQuoteCityPriceVO implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 主键 */
    private Long id;

    /** 城市code */
    private String cityCode;

    /** 价格 */
    private List<LoanPeriodDTO> priceList;

    /** 是否销售 */
    private Integer onSale;
}
