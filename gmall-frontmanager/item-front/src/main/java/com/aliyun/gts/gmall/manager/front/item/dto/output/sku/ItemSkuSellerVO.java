package com.aliyun.gts.gmall.manager.front.item.dto.output.sku;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import com.aliyun.gts.gmall.framework.i18n.MultiLangText;
import com.aliyun.gts.gmall.platform.item.api.dto.output.item.ItemSkuSellerDTO;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@ApiModel("SKU商家信息")
public class ItemSkuSellerVO extends AbstractOutputInfo {

    private Long itemId;

    private Long skuId;

    private Long sellerId;

    private List<Integer> loan;

    private List<Integer> installment;

    private List<Long> warehouseIdList;

}
