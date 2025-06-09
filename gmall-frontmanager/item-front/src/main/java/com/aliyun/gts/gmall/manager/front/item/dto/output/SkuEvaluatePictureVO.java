package com.aliyun.gts.gmall.manager.front.item.dto.output;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "评价图片")
public class SkuEvaluatePictureVO extends AbstractOutputInfo {

    /**
     * 评论ID
     */
    private Long id;

    /**
     * 评价图片地址
     */
    private String pictureUrl;

}
