package com.aliyun.gts.gmall.manager.front.item.convertor;

import com.aliyun.gts.gmall.manager.front.item.dto.output.CouponInstanceVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.PromotionDetailVO;
import com.aliyun.gts.gmall.platform.promotion.api.dto.output.CouponInstanceDTO;
import com.aliyun.gts.gmall.platform.promotion.api.dto.output.PromotionDetailDTO;
import org.mapstruct.Mapper;

/**
 * @author GTS
 * @date 2021/03/12
 */
@Mapper(componentModel = "spring")
public interface PromotionCouponConverter {


    /**
     * @param couponInstanceDTO
     * @return
     */
    CouponInstanceVO convertCouponInstance(CouponInstanceDTO couponInstanceDTO);

    PromotionDetailVO convertPromotionDetailVO(PromotionDetailDTO promotionDetailDTO);


}