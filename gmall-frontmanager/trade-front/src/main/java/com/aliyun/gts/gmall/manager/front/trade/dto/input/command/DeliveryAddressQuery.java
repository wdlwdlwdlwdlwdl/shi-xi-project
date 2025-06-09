package com.aliyun.gts.gmall.manager.front.trade.dto.input.command;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: yangl
 * @Date: 20224/8/9 10:37
 * @Desc: 物流地址Request
 */
@Data
public class DeliveryAddressQuery implements Serializable {

    /**
     * 城市code
     */
    private String cityCode;

    /**
     * lockerSize
     */
    private String lockerSize;

    /**
     * type 1 PVZ 2 Postamat
     */
    private String type;
    private double latitude;
    private double longitude;
}
