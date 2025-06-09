package com.aliyun.gts.gmall.manager.front.item.dto.output;

import com.aliyun.gts.gmall.framework.api.dto.Response;
import com.aliyun.gts.gmall.platform.item.api.dto.output.item.ItemImgInfoDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "商品图片返回对象")
public class ItemImageVO implements Response {
    @ApiModelProperty(value = "选择的图片")
    private String img;
    @ApiModelProperty(value = "图片信息")
    private ItemImgInfoVO info;
}
