package com.aliyun.gts.gmall.platform.trade.api.dto.input.order.operate;

import lombok.Data;

import java.io.Serializable;

/**
 * 说明： 商家仓库
 *
 * @author yangl
 * @version 1.0
 * @date 2024/10/10 14:56
 */
@Data
public class IcWarehouseRpcReq implements Serializable {

    private Long   id;
    private String name;
    private String address;
    private String city;
    private String cityCode;
    private String province;
    private String country;
    private String telephone;
    private String postalCode;
    private Long sellerId;
    private Double latitude;
    private Double longitude;
    private int seatNum;
    //是否商家运到DC 0是 1否
    private String isDc;
}
