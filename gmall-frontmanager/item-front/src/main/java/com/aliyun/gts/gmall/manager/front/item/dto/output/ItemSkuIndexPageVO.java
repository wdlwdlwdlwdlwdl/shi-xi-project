package com.aliyun.gts.gmall.manager.front.item.dto.output;

import java.util.List;
import java.util.Objects;
import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @Title: ItemSkuIndexPageVO.java
 * @Description: TODO(用一句话描述该文件做什么)
 * @author zhao.qi
 * @date 2024年11月12日 09:14:26
 * @version V1.0
 */
@Getter
@Setter
public class ItemSkuIndexPageVO extends AbstractOutputInfo {
    private static final long serialVersionUID = 1L;

    private Long itemId;
    private Long skuId;
    private String itemTitle;
    private Integer itemType;
    private String itemTypeName;
    private Long categoryId;
    private String categoryName;
    private Long basePrice;
    private List<String> pictureList;

    private Long itemTotalCount;

    private Long skuQuoteId;
    private Long sellerId;
    private String storeName;

    /**
     * 主图
     * 
     * @return
     */
    public String getMainPicture() {
        if (Objects.isNull(pictureList) || pictureList.isEmpty()) {
            return null;
        }
        return pictureList.get(0);
    }
}
