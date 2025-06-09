package com.aliyun.gts.gmall.platform.trade.domain.entity.item;

import com.alibaba.fastjson.annotation.JSONField;
import com.aliyun.gts.gmall.platform.item.api.dto.output.item.LoanPeriodDTO;
import com.aliyun.gts.gmall.platform.item.api.dto.output.item.SkuQuoteWarehourseStockDTO;
import com.aliyun.gts.gmall.platform.trade.common.annotation.SearchMapping;
import com.aliyun.gts.gmall.platform.trade.common.constants.ItemStatusEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.SellerStatusEnum;
import com.aliyun.gts.gmall.platform.trade.domain.entity.AbstractBusinessEntity;
import com.aliyun.gts.gmall.platform.trade.domain.entity.price.ItemPrice;
import com.aliyun.gts.gmall.platform.trade.domain.entity.user.Seller;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ItemSku extends AbstractBusinessEntity {

    @ApiModelProperty("sku ID")
    @SearchMapping("sku_id")
    private Long skuId;

    @ApiModelProperty("sku名称")
    private String skuName;

    @ApiModelProperty("商品ID")
    @SearchMapping("item_id")
    private Long itemId;

    @ApiModelProperty("卖家信息")
    private Seller seller;

    @ApiModelProperty("借贷数目")
    private List<Integer> loan;

    @ApiModelProperty("借贷数目")
    private Long singleLoan;

    @ApiModelProperty("分期数")
    private Integer singleInstallment;

    @ApiModelProperty("分期数")
    private List<Integer> installment;

    @ApiModelProperty("sku库存")
    private Integer skuQty;

    @ApiModelProperty("sku描述, 例如 '颜色红色,尺码27' ")
    @SearchMapping("sku_desc")
    private String skuDesc;

    @ApiModelProperty("商品标题")
    @SearchMapping("item_title")
    private String itemTitle;

    @ApiModelProperty("商品主图")
    @SearchMapping("item_pic")
    private String itemPic;

    @ApiModelProperty("SKU图片, 如没有SKU图片则为空")
    private String skuPic;

    @ApiModelProperty("价格信息")
    @SearchMapping(mapChild = true)
    private ItemPrice itemPrice;

    @ApiModelProperty("商品状态, ItemStatusEnum")
    private Integer itemStatus;

    @ApiModelProperty("SKU状态, ItemStatusEnum")
    private Integer skuStatus;

    @ApiModelProperty("商品上架状态(含定时上架)")
    private Boolean online;

    @ApiModelProperty("单件SKU的重量, 单位g")
    private Long weight;

    @ApiModelProperty(value = "商品类型, IC字段")
    private Integer itemType;

    @ApiModelProperty(value = "商品features, IC字段")
    private Map<String, String> itemFeatureMap;

    @ApiModelProperty(value = "sku features, IC字段")
    private Map<String, String> skuFeatureMap;

    @ApiModelProperty(value = "商品扩展结构, IC字段")
    private Map<String, String> itemExtendMap;

    @ApiModelProperty(value = "可以存储到itemFeature的扩展")
    private Map<String, String> storedExt;

    @ApiModelProperty(value = "品牌ID")
    private Long brandId;

    @ApiModelProperty(value = "类目ID")
    private Long categoryId;

    @ApiModelProperty(value = "类目名称")
    private String categoryName;

    @ApiModelProperty(value = "是否支持退款")
    private Integer canRefunds;

    @ApiModelProperty(value = "类目信息")
    private ItemCategory itemCategory;

    @ApiModelProperty(value = "年龄限制分类")
    private Boolean ageCategory = Boolean.FALSE;

    @ApiModelProperty(value = "仓库ID列表")
    private List<Long> warehouseIdList;

    @ApiModelProperty(value = "仓库信息列表")
    private List<SkuQuoteWarehourseStockDTO> stockList;

    @ApiModelProperty(value = "支持的物流方式列表")
    private List<String> supportDeliveryList;

    @ApiModelProperty(value = "支持的物流方式列表")
    private List<ItemDelivery> itemDelivery;

    @ApiModelProperty(value = "SKU价格信息")
    private  List<LoanPeriodDTO> priceList;

    @ApiModelProperty("选中的物流方式, DeliveryTypeEnum")
    private Integer deliveryType;

    @ApiModelProperty("商家-商品关联ID")
    private Long skuQuoteId;

    @ApiModelProperty("物流到达预计时间,默认-1")
    private int selfTimeliness=-1;

    @ApiModelProperty(value = "类目详情")
    private String categoryDetails;

    @ApiModelProperty(value = "品牌名称")
    private String brandName;

    @ApiModelProperty(value = "佣金费率集合")
    private Map<String, Integer> categoryCommissionMap;

    @ApiModelProperty(value = "佣金费率")
    private Integer categoryCommissionRate;

    @JSONField(serialize = false)
    public ItemSkuId getItemSkuId() {
        return new ItemSkuId(itemId, skuId);
    }

    @ApiModelProperty("是否存在城市价格")
    private Boolean cityPriceStatus = Boolean.TRUE;

    @ApiModelProperty(value = "Merchant SKU Code商家sku码")
    private String merchantSkuCode;

    @JSONField(serialize = false)
    public ItemSkuId getSellerIdItemSkuId() {
        return new ItemSkuId(itemId, skuId, seller.getSellerId());
    }


    public void addStoreExt(Map<String, String> map){
        if(map == null){
            return;
        }
        if(storedExt == null){
            storedExt = new HashMap<>();
        }
        storedExt.putAll(map);
    }
    /**
     * 状态是否为有效, 商品有效且SKU有效
     * ；
     * 这边主要校验状态，商品有效且SKU有效，并且卖家状态正常
     */
    @JSONField(serialize = false)
    public boolean isEnabled() {
        if (ItemStatusEnum.codeOf(itemStatus) != ItemStatusEnum.ENABLE) {
            return false;
        }
        if (ItemStatusEnum.codeOf(skuStatus) != ItemStatusEnum.ENABLE) {
            return false;
        }
        if (seller != null && SellerStatusEnum.codeOf(seller.getSellerStatus()) != SellerStatusEnum.ENABLE) {
            return false;
        }
        return online == null || online.booleanValue();
    }
}
