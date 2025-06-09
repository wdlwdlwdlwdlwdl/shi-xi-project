package com.aliyun.gts.gmall.manager.front.item.dto.output;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "商品详情返回对象")
public class ItemDeadlineVO extends AbstractOutputInfo {


    @ApiModelProperty(value = "分期")
    private String deadline;

    @ApiModelProperty(value = "是否展示点赞")
    private Boolean showPraise;
    @ApiModelProperty(value = "分期标题")
    private String title;

    @ApiModelProperty(value = "明细")
    private List<ItemDeadlineDetailVO> list;
}
