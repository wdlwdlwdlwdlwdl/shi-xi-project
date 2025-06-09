package com.aliyun.gts.gmall.platform.trade.api.dto.common;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

public class WarehouseHoursDTO extends AbstractOutputInfo {
    @ApiModelProperty("仓库主键")
    private Long warehouseId;
    @ApiModelProperty("开始时间")
    private String startTime;
    @ApiModelProperty("结束时间")
    private String endTime;
    @ApiModelProperty("星期几,1:Monday,2:Tuesday,3:Wednesday,4:Thursday,5:Friday,6:Saturday,7:Sunday")
    private Integer type;
    private Date gmtCreate;
    private Date gmtModified;
    private Long id;

    public WarehouseHoursDTO() {
    }

    public Long getWarehouseId() {
        return this.warehouseId;
    }

    public String getStartTime() {
        return this.startTime;
    }

    public String getEndTime() {
        return this.endTime;
    }

    public Integer getType() {
        return this.type;
    }

    public Date getGmtCreate() {
        return this.gmtCreate;
    }

    public Date getGmtModified() {
        return this.gmtModified;
    }

    public Long getId() {
        return this.id;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String toString() {
        Long var10000 = this.getWarehouseId();
        return "WarehouseBusinessHoursDTO(warehouseId=" + var10000 + ", startTime=" + this.getStartTime() + ", endTime=" + this.getEndTime() + ", type=" + this.getType() + ", gmtCreate=" + this.getGmtCreate() + ", gmtModified=" + this.getGmtModified() + ", id=" + this.getId() + ")";
    }
}