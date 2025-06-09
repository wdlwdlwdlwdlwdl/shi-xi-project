package com.aliyun.gts.gmall.platform.trade.persistence.util;

import com.alibaba.druid.util.StringUtils;
import com.aliyun.gts.gmall.platform.item.api.dto.output.commercial.DeliveryTypeFullInfoDTO;
import com.aliyun.gts.gmall.platform.item.api.dto.output.item.SkuQuoteCityPriceDTO;
import com.aliyun.gts.gmall.platform.trade.common.constants.CityPriceEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.DeliveryTypeEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 购物车业务相关计算共计方法
 */
public class TradeUtils {

    /**
     * 获取城市价格
     * @param skuQuoteCityPriceDTOs
     * @return
     */
    public static SkuQuoteCityPriceDTO converItemCityPrice(List<SkuQuoteCityPriceDTO> skuQuoteCityPriceDTOs, String cityCode) {
        SkuQuoteCityPriceDTO skuQuoteCityPrice = skuQuoteCityPriceDTOs.stream()
            .filter(p -> StringUtils.equals(p.getCityCode(), cityCode))
            .filter(p -> CityPriceEnum.ON_SALES.getCode().equals(p.getOnSale()))
            .findAny().orElse(null);
        if (Objects.isNull(skuQuoteCityPrice)) {
            // 不存在 取默认价格
            skuQuoteCityPrice = skuQuoteCityPriceDTOs.stream()
                .filter(p -> StringUtils.equals(p.getCityCode(), "all"))
                .filter(p -> CityPriceEnum.ON_SALES.getCode().equals(p.getOnSale()))
                .findAny().orElse(null);
        }
        return skuQuoteCityPrice;
    }


    /**
     * 获取物流枚举
     * @param deliveryTypeFullInfo
     * @return
     */
    public static  List<DeliveryTypeEnum> converDeliveryType(DeliveryTypeFullInfoDTO deliveryTypeFullInfo) {
        List<DeliveryTypeEnum> deliveryTypeEnums = new ArrayList<>();

        if (Boolean.TRUE.equals(deliveryTypeFullInfo.isSellerKa()) &&  deliveryTypeFullInfo.getKaDeliveryDto().isProvideHomeServices()) {
            deliveryTypeEnums.add(DeliveryTypeEnum.SELLER_KA);
        }
        else if (deliveryTypeFullInfo.getHmDeliveryDto().isProvideHomeServices()) {
            deliveryTypeEnums.add(DeliveryTypeEnum.HM_SERVICE);
        }
        else if (deliveryTypeFullInfo.getSellerDeliveryDto().isProvideHomeServices()) {
            deliveryTypeEnums.add(DeliveryTypeEnum.SELF_SERVICE);
        }
        
        if (deliveryTypeFullInfo.getHmDeliveryDto().isPvz()) {
            deliveryTypeEnums.add(DeliveryTypeEnum.PVZ);
        }
        if (deliveryTypeFullInfo.getSellerDeliveryDto().isWarehoursePickup()) {
            deliveryTypeEnums.add(DeliveryTypeEnum.WAREHOURSE_PICK_UP);
        }
        if (deliveryTypeFullInfo.getHmDeliveryDto().isPostamat()) {
            deliveryTypeEnums.add(DeliveryTypeEnum.POSTAMAT);
        }
        return deliveryTypeEnums;
    }

}
