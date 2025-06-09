package com.aliyun.gts.gmall.platform.trade.domain.entity.user;

import com.aliyun.gts.gmall.platform.trade.domain.entity.AbstractBusinessEntity;
import lombok.Data;

@Data
public class Shop extends AbstractBusinessEntity {

    private static final long serialVersionUID = 1L;
    private Long sellerId;
    private Long id;
    private String name;
    private String desc;
    private String logoUrl;
    private String contactInformation;
    private Integer preciousMetalStone;
    private String domainName;
    private String icpNo;
    private boolean selfRun;
    private String address;
    private String contactPhone;
    private Boolean shopCVipEnabled;
    private Boolean shopBVipEnabled;

}
