package com.aliyun.gts.gmall.manager.front.sourcing.vo;

import com.aliyun.gts.gmall.framework.api.rest.dto.AbstractRestRequest;
import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestQuery;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Data
public class BaseVO extends LoginRestQuery {

    @ApiModelProperty("主健")
    private Long id;
    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date gmtCreate;
    @ApiModelProperty("更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date gmtModified;

    Map<String , Boolean> buttons = new HashMap<>();



    protected void addButton(String key){
        buttons.put(key , true);
    }

    @Override
    public boolean isWrite() {
        return true;
    }
}
