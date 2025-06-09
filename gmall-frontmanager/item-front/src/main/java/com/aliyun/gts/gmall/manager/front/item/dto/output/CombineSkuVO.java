package com.aliyun.gts.gmall.manager.front.item.dto.output;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import java.util.List;
import java.util.stream.Collectors;

import com.aliyun.gts.gmall.manager.front.item.dto.output.sku.SkuPropVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

@Data
@ApiModel(value = "组合商品的SKU返回对象")
public class CombineSkuVO {

    @ApiModelProperty(value = "商品标题")
    private String itemTitle;

    @ApiModelProperty(value = "商品状态")
    private Integer itemStatus;

    @ApiModelProperty(value = "被组合的份数")
    private Integer perNum;

    @ApiModelProperty(value = "skuId")
    private Long id;

    @ApiModelProperty(value = "商品id")
    private Long itemId;

    @ApiModelProperty(value = "库存")
    private Long quantity;

    @ApiModelProperty(value = "价格,元")
    private String price;

    @ApiModelProperty(value = "重量,g")
    private Long weight;

    @ApiModelProperty(value = "1:启用,0:禁用")
    private Integer status;

    @ApiModelProperty(value = "sku属性信息")
    private List<SkuPropVO> skuPropList;

    @ApiModelProperty(value = "是否可以修改价格")
    private boolean canEditPrice = true;

    @ApiModelProperty(value = "库存单位")
    private String unit = I18NMessageUtils.getMessage("pieces");  //# "件"

    @ApiModelProperty("商品图片，优先使用SKU图片")
    private String picUrl;

    @ApiOperation(value = "获得sku名称")
    public String getName() {
        if (skuPropList == null || skuPropList.isEmpty()) {
            return "";
        }
        return skuPropList.stream().map(x -> x.getName() + ":" + x.getValue()).collect(Collectors.joining("/"));
    }

}
