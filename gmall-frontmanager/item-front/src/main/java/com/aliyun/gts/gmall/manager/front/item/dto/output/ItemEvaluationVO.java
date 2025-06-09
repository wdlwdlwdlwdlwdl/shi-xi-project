package com.aliyun.gts.gmall.manager.front.item.dto.output;

import java.util.Date;
import java.util.List;

import com.aliyun.gts.gmall.framework.server.util.DateTimeUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * 商品评价信息
 *
 * @author tiansong
 */
@Data
@ApiModel("商品评价信息")
public class ItemEvaluationVO {
    @ApiModelProperty("评论id")
    private String                 id;
    @ApiModelProperty("商品id")
    private String                 itemId;
    @ApiModelProperty("买家id")
    private String                 custId;
    @ApiModelProperty("买家昵称")
    private String                 nickname;
    @ApiModelProperty("买家头像")
    private String                 headUrl;
    @ApiModelProperty("评分")
    private String                 rateScore;
    @ApiModelProperty("评价视频")
    private String                 rateVideo;
    @ApiModelProperty("评价视频列表")
    private List<String>           rateVideoList;
    @ApiModelProperty("扩展信息")
    private String                 extend;
    @ApiModelProperty("评价描述")
    private String                 rateDesc;
    @ApiModelProperty("主评价ID")
    private String                 replyId;
    @ApiModelProperty("评价图片")
    private String                 ratePic;
    @ApiModelProperty("评价图片列表")
    private List<String>           ratePicList;
    @ApiModelProperty("评价的回复")
    public  List<ItemEvaluationVO> replyList;
    @ApiModelProperty("修改时间")
    private String                 gmtModified;
    @ApiModelProperty("创建时间")
    private String                 gmtCreate;
    @ApiModelProperty("主订单ID")
    private Long                   primaryOrderId;
    @ApiModelProperty("子订单ID, 店铺评价记录同主订单ID")
    private Long                   orderId;

    public String getPublishTime() {
        return DateTimeUtils.format(new Date(NumberUtils.toLong(this.gmtCreate)));
    }
}
