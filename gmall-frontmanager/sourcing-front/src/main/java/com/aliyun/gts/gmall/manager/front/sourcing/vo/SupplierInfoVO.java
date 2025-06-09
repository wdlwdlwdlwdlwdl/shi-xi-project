package com.aliyun.gts.gmall.manager.front.sourcing.vo;

import com.aliyun.gts.gcai.platform.sourcing.common.model.BaseDTO;
import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SupplierInfoVO extends BaseVO {

    private String name;
    private Integer status;
    private Integer deleted;
    private String linkManName;
    private String linkManIdCardFront;
    private String linkManIdCardBack;
    private String telephoneNum;
    private String phoneNum;
    private String mailUrl;
    private Integer auditStatus;
    private Long inviteId;
    private String supplierGrade;
    private SupplierCompanyVO companyInfo;
    /**
     * 报价次数
     */
    private Long quoteCount;
    /**
     * 中标次数
     */
    private Long quoteAwardCount;
    /**
     * 合同签署量
     */
    private Long contractCount;
    private List<CertificateVO> certificates;
}
