package com.aliyun.gts.gmall.manager.front.media.web.output;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.gts.gmall.center.media.api.dto.input.ItemInfo;
import com.aliyun.gts.gmall.center.media.api.dto.output.ShortVideoInfoDTO;
import com.aliyun.gts.gmall.platform.item.api.dto.output.item.ItemDTO;
import com.aliyun.gts.gmall.platform.user.api.dto.output.SellerDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ShortVideoVO extends ShortVideoInfoDTO {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("分类名称")
    private String categoryName;
    @ApiModelProperty("店铺名称")
    private String storeName;
    @ApiModelProperty("商品名称")
    private String productName;
    @ApiModelProperty("视频状态：10已发布 20被禁播 30已下架")
    private String videoStatusName;
    @ApiModelProperty("评论状态：10可评论 20禁止评论")
    private String commentStatusName;
    @ApiModelProperty(value = "产品信息")
    private ItemInfo productInfo;
    @ApiModelProperty(value = "店铺信息")
    private SellerDTO sellerDTO;
    @ApiModelProperty(value = "店铺logoUrl")
    private String  logoUrl;


    /**
     * 商品详情数据信息
     */
    @ApiModelProperty(value = "商品详情数据信息")
    private JSONObject itemDetail;
}
