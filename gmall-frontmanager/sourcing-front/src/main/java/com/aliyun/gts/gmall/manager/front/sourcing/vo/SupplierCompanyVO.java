package com.aliyun.gts.gmall.manager.front.sourcing.vo;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
public class SupplierCompanyVO extends AbstractOutputInfo {

    /**
     * 公司名称
     */
    @ApiModelProperty("公司名称")
    private String name;
    @ApiModelProperty("统一社会信用代码")
    private String bizLicenseNo;

    /**
     *营业期限起始
     */
    private Date bizValidityStart;

    /**
     *营业期限截止
     */
    private Date bizValidityEnd;

    /**
     *法人姓名
     */
    private String legalName;

    /**
     *公司员工数
     */
    private Integer staffCnt;

    /**
     *注册资本，单位元
     */
    private String regCaptial;


    /**
     *1-表示营业中
     */
    private Integer status;

    /**
     *关联采购方id
     */
    private Long relatedId;

    /**
     *0-采购方公司，1-供应方公司
     */
    private Integer type;

    /**
     *0-未删除，1-已删除
     */
    private Integer deleted;

    private Integer auditStatus;

    /**
     * 公司地址
     */
    private String address;

    private Map<String, String> features;
}
