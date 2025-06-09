package com.aliyun.gts.gmall.manager.front.sourcing.vo;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class StatusVO {

    @NotNull
    Long id;

    @Max(1)
    @Min(-1)
    @NotNull
    Integer status;
}
