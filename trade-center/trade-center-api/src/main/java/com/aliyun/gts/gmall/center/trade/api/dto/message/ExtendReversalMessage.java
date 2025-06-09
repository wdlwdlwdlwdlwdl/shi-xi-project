package com.aliyun.gts.gmall.center.trade.api.dto.message;

import com.aliyun.gts.gmall.platform.trade.api.dto.message.ReversalMessageDTO;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: yuli
 * @data: 2022/9/23 3:00 PM
 */
@Data
public class ExtendReversalMessage extends ReversalMessageDTO {
    private String sellerId;
    private String sellerName;
    private Long custId;
    private String primaryOrderId;
    private String outReversalId;
    private List<MaterialInfo> materialInfos;
    private Integer reversalType;
    private Map<String, String> extras = new HashMap<>();

    @Data
    public static class MaterialInfo {
        private String itemCode;
        private String barcode;
        private Integer count;
    }

    public static final String EXTRAS_KEY_LOGISTICS = "logistics";
}
