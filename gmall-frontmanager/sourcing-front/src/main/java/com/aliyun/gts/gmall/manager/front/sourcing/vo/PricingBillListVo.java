package com.aliyun.gts.gmall.manager.front.sourcing.vo;

import com.aliyun.gts.gcai.platform.sourcing.api.dto.model.PricingBillDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/8/6 16:05
 */
@Data
public class PricingBillListVo extends PricingBillDTO {
    private String auditUrl;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date gmtCreate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date gmtModified;
}
