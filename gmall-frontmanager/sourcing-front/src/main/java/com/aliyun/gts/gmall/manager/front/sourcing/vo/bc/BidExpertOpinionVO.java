package com.aliyun.gts.gmall.manager.front.sourcing.vo.bc;

import com.aliyun.gts.gmall.manager.front.b2bcomm.model.KVVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("一个标段的专家意见")
public class BidExpertOpinionVO {

    Long sectionId;
    String sectionName;
    @ApiModelProperty("意见数量")
    Integer opinionsCount;
    @ApiModelProperty("所有专家的数量")
    Integer expertCount;
    @ApiModelProperty("专家名称和意见")
    List<KVVO<String , String>> opinions;

}
