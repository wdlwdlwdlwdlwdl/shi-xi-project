package com.aliyun.gts.gmall.manager.front.media.dto;

import com.aliyun.gts.gmall.framework.api.consts.CommonResponseCode;
import com.aliyun.gts.gmall.framework.api.dto.PageParam;
import com.aliyun.gts.gmall.framework.api.rest.dto.AbstractCommandRestRequest;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.manager.biz.output.CustDTO;
import com.aliyun.gts.gmall.manager.front.common.util.UserHolder;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class ShortVideoInfoQuery extends AbstractCommandRestRequest {
    @ApiModelProperty("主键ID")
    private Long id;
    @ApiModelProperty("名称")
    private String name;
    @ApiModelProperty("视频分类主键ID")
    private Long categoryId;
    @ApiModelProperty("视频状态列表：10已发布 20被禁播 30已下架")
    private List<String> videoStatusList;
    @ApiModelProperty("视频状态列表：10已发布 20被禁播 30已下架")
    private String videoStatus;
    @ApiModelProperty("店铺ID")
    private Long storeId;
    @ApiModelProperty("店铺名称")
    private String storeName;
    @ApiModelProperty("分页信息")
    private PageParam page;
    @ApiModelProperty("创建人")
    private String createId;
    @ApiModelProperty(value = "视频滑动方向：10下滑（默认） 20上滑")
    private String slideDirection;

    @ApiModelProperty("登录用户ID")
    public Long getCustId() {
        CustDTO user = UserHolder.getUser();
        return user == null ? null : user.getCustId();
    }

    public void setCustId(Long v) { }

    @Override
    public void checkInput() {
        super.checkInput();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public List<String> getVideoStatusList() {
        return videoStatusList;
    }

    public void setVideoStatusList(List<String> videoStatusList) {
        this.videoStatusList = videoStatusList;
    }

    public String getVideoStatus() {
        return videoStatus;
    }

    public void setVideoStatus(String videoStatus) {
        this.videoStatus = videoStatus;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public PageParam getPage() {
        return page;
    }

    public void setPage(PageParam page) {
        this.page = page;
    }

    public String getCreateId() {
        return createId;
    }

    public void setCreateId(String createId) {
        this.createId = createId;
    }

    public String getSlideDirection() {
        return slideDirection;
    }

    public void setSlideDirection(String slideDirection) {
        this.slideDirection = slideDirection;
    }
}
