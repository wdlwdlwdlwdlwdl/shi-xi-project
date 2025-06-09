package com.aliyun.gts.gmall.manager.front.sourcing.vo;

import com.aliyun.gts.gcai.platform.sourcing.common.model.BaseDTO;
import lombok.Data;

/**
 * @author haibin.xhb
 * @description: TODO 供应商信息
 * @date 2021/5/20 14:25
 */
@Data
public class SupplierVo extends BaseDTO {
    private Long id;
    /**
     * 供应商名称/就是供应商公司信息
     */
    private String supplierName;
    /**
     * 邮箱地址
     */
    private String email;
    /**
     * 手机号
     */
    private String cellPhone;
    /**
     * 手机号
     */
    private String telephoneNum;
    /**
     * 公司信息
     */
    private SupplierCompanyVO company;
}
