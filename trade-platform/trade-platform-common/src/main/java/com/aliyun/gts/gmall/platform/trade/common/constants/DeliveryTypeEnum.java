package com.aliyun.gts.gmall.platform.trade.common.constants;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 配送方式
 */
@Getter
public enum DeliveryTypeEnum {
    //# \"上门自提\"
    PVZ(4, "pvz"),//PVZ
    POSTAMAT(5, "postamat"),//Postamat
    WAREHOURSE_PICK_UP(8, "warehoursePickup"),//courier to door
    HM_SERVICE(6, "hmService"),
    SELF_SERVICE(7, "selfService"), //point to door或者DC
    SELLER_KA(9, "sellerKa");
    private final Integer code;

    private final String script;

    DeliveryTypeEnum(Integer code, String script) {
        this.code = code;
        this.script = script;
    }

    public static Integer getCodeByName(final String script) {
        for(DeliveryTypeEnum deliveryTypeEnum :DeliveryTypeEnum.values()){
            if(deliveryTypeEnum.getScript().equals(script)){
                return deliveryTypeEnum.getCode();
            }
        }
        return null;
    }

    /**
     * HM物流
     * @param deliveryType
     * @return
     */
    public static Boolean isHM(Integer deliveryType) {
        return DeliveryTypeEnum.PVZ.getCode().equals(deliveryType) ||
            DeliveryTypeEnum.POSTAMAT.getCode().equals(deliveryType) ||
            DeliveryTypeEnum.HM_SERVICE.getCode().equals(deliveryType);
    }


    /**
     * 自有物流
     * @param deliveryType
     * @return
     */
    public static Boolean isSelf(Integer deliveryType) {
        return SELF_SERVICE.getCode().equals(deliveryType);
    }

    /**
     * 自提单
     * @param deliveryType
     * @return
     */
    public static Boolean isPick(Integer deliveryType) {
        return WAREHOURSE_PICK_UP.getCode().equals(deliveryType);
    }

    /**
     * 初始化数组
     * @return
     */
    public static List<String> initDeliverList() {
        List<String> supportDeliveryList = new ArrayList<>();
        supportDeliveryList.add(DeliveryTypeEnum.PVZ.getScript());
        supportDeliveryList.add(DeliveryTypeEnum.POSTAMAT.getScript());
        supportDeliveryList.add(DeliveryTypeEnum.HM_SERVICE.getScript());
        supportDeliveryList.add(DeliveryTypeEnum.SELF_SERVICE.getScript());
        supportDeliveryList.add(DeliveryTypeEnum.WAREHOURSE_PICK_UP.getScript());
        supportDeliveryList.add(DeliveryTypeEnum.SELLER_KA.getScript());
        return supportDeliveryList;
    }

}
