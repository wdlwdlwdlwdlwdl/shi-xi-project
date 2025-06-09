package com.aliyun.gts.gmall.manager.front.sourcing.vo.contract;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel("合同中的物料报价信息")
public class ContractMaterialsVO {

    Long quoteId;
    Long scMaterialId;
    Long supplierId;
    Long sourcingId;

    MaterialVO material;

    Long categoryId;

    String brandName;

    String model;

    /**
     * 授标数量
     */
    Long num;

    String unit;

    String price;

    Long taxRate;

    String freightCost;

    public static class MaterialVO{
        public String code;

        public String name;
    }


}
