package com.aliyun.gts.gmall.manager.front.sourcing.convert;

import com.aliyun.gts.gcai.platform.sourcing.api.dto.model.PricingBillDTO;
import com.aliyun.gts.gmall.manager.front.sourcing.input.PricingBillCreateReq;
import com.aliyun.gts.gmall.manager.front.sourcing.vo.PricingBillListVo;
import org.mapstruct.Mapper;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/5/21 15:17
 */
@Mapper(componentModel = "spring")
public interface PricingBillConvert {
    PricingBillDTO re2Dto(PricingBillCreateReq req);

    PricingBillListVo dto2Vo(PricingBillDTO vo);
}
