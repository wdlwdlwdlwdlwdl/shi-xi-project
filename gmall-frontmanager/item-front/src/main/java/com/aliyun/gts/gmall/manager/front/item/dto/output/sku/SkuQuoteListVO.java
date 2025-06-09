package com.aliyun.gts.gmall.manager.front.item.dto.output.sku;

import com.aliyun.gts.gmall.manager.front.item.dto.output.CityVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * 
 * @Title: ItemListVO.java
 * @Description: 商品sku引用列表数据
 * @author zhao.qi
 * @date 2024年9月25日 14:22:05
 * @version V1.0
 */
@Getter
@Setter
public class SkuQuoteListVO {
    /** 商品id */
    private Long id;
    
    private Long skuId;
    
    /** 业务来源 */
    private Integer sourceType;
    private String sourceTypeName;

    /** 商品类型 */
    private Integer type;
    private String typeName;
    
    /** 商品sku编码 */
    private String skuCode;

    /** 商家自定义sku编码 */
    private String sellerSkuCode;
    
    /** 商品标题 */
    private String title;
    
    /** 商品主图 */
    private String mainPicture;

    /** 商品类目*/
    private Long categoryId;
    private List<String> categoryNameList;
    
    /** 商品状态 */
    private Integer itemStatus;
    private String itemStatusName;
    
    /** 商品map状态 */
    private Integer mapStatus;
    private String mapStatusName;

    /** 商家id */
    private Long sellerId;
    
    /** 商家名称 */
    private String sellerName;
    
    /** 30天销量 */
    private Long saleCount30;

    /** 总销量 */
    private Long totalSaleCount;

    /** 创建时间 */
    private Date gmtCreate;

    /** 修改时间 */
    private Date gmtModified;

    /** 最后操作人 */
    private String operator;


    @ApiModelProperty(value = "支付方式")
    private Integer payMode;
    @ApiModelProperty(value = "分期")
    private String period;
    @ApiModelProperty(value = "是否官方分销商")
    private Boolean isOfficialDistributor;
    @ApiModelProperty(value = "是否展示点赞")
    private Boolean showPraise;
    @ApiModelProperty(value = "分期标题")
    private String periodTitle;
    @ApiModelProperty(value = "分期颜色")
    private String periodTitleColor;
    @ApiModelProperty(value = "评分")
    private Double star;
    @ApiModelProperty(value = "反馈")
    private Integer feedback;
    @ApiModelProperty(value = "时效")
    private String deliveryPeriod;
    @ApiModelProperty(value = "是否在引渡点")
    private Boolean isExtradite;
    @ApiModelProperty(value = "是否展示提货")
    private Boolean isBill;
    @ApiModelProperty(value = "是否展示储存柜")
    private Boolean isStockpile;
    @ApiModelProperty(value = "原价")
    private Long originPrice;
    @ApiModelProperty(value = "现价")
    private Long price;
    @ApiModelProperty(value = "分期价格")
    private Long amortizePrice;
    @ApiModelProperty(value = "商品主键")
    private Long itemId;
    @ApiModelProperty(value = "库存")
    private Boolean stock;
    @ApiModelProperty(value = "城市")
    private CityVO city;
    @ApiModelProperty("商家是否可靠的")
    private Boolean reliable;
    @ApiModelProperty("城市价")
    private List<SkuQuoteCityPriceVO> cityPriceInfo;
}
