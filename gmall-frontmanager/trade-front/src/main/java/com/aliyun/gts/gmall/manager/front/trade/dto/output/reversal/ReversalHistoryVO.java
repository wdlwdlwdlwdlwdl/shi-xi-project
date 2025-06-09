package com.aliyun.gts.gmall.manager.front.trade.dto.output.reversal;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * 协商历史
 *
 * @author tiansong
 */
@ApiModel("协商历史")
@Data
@Builder
public class ReversalHistoryVO {
    @ApiModelProperty("用户ID")
    private Long         custId;
    @ApiModelProperty("用户昵称")
    private String       custNick;
    @ApiModelProperty("用户Logo")
    private String       custLogo;
    @ApiModelProperty("事件发生时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date         eventTime;
    @ApiModelProperty("事件内容")
    private String       content;
    @ApiModelProperty("图片/视频URL列表")
    private List<String> urlList;
}