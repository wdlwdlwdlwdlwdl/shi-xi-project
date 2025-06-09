package com.aliyun.gts.gmall.platform.trade.domain.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("tc_city")
public class TcCityDO implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String cityCode;
    @ApiModelProperty("城市名称")
    private String cityName;
    @ApiModelProperty("cityIndex")
    private String cityIndex;
    @ApiModelProperty("激活")
    private Integer active;
    @ApiModelProperty("polygon")
    private String polygon;
    @ApiModelProperty("经度")
    private String latitude;
    @ApiModelProperty("维度")
    private String longitude;
    @ApiModelProperty("zoom")
    private String zoom;
    @ApiModelProperty("color")
    private String color;

    @ApiModelProperty("删除标记")
    private Integer deleted;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建时间")
    private Date gmtCreate;

    @ApiModelProperty("create_id")
    private String createId;

    @ApiModelProperty("修改时间")
    private Date gmtModified;

    @ApiModelProperty("update_id")
    private String updateId;

    private String operator;
}
