package com.aliyun.gts.gmall.manager.front.sourcing.vo;

import com.aliyun.gts.gcai.platform.sourcing.common.model.BaseDTO;
import lombok.Data;

@Data
public class CertificateVO extends BaseDTO {
    private Long supplierId;
    private String name;
    private String code;
    private Integer type;
    private String picUrl;
    private Integer status;
    private Integer auditStatus;
    private Integer deleted;
}
