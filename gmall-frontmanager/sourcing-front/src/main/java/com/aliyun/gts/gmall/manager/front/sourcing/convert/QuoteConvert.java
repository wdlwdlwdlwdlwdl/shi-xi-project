package com.aliyun.gts.gmall.manager.front.sourcing.convert;

import com.aliyun.gts.gcai.platform.sourcing.api.dto.model.QuoteDTO;
import com.aliyun.gts.gcai.platform.sourcing.api.dto.model.QuoteDetailDTO;
import com.aliyun.gts.gmall.center.trade.api.dto.output.ConfirmOrderSplitDTO;
import com.aliyun.gts.gmall.manager.front.b2bcomm.utils.PriceUtils;
import com.aliyun.gts.gmall.manager.front.sourcing.vo.PricingToOrderVO;
import com.aliyun.gts.gmall.manager.front.sourcing.vo.QuoteDetailVo;
import com.aliyun.gts.gmall.manager.front.sourcing.vo.QuoteVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/5/14 12:53
 */
@Mapper(componentModel = "spring",imports = {PriceUtils.class})
public interface QuoteConvert {

    @Mappings(value = {
            @Mapping(target = "totalPrice", expression = "java(PriceUtils.fenToYuan(dto.getTotalPrice()))"),
            @Mapping(target = "freightCost", expression = "java(PriceUtils.fenToYuan(dto.getFreightCost()))"),
            @Mapping(target = "totalPriceFen", source = "totalPrice"),
    })
    QuoteVo dto2Vo(QuoteDTO dto);

    /**
     *  报价详情对象转换
     * @param vo
     * @return
     */
    @Mappings(value = {
            @Mapping(target = "price", expression = "java(PriceUtils.yuanToFen(vo.getPrice()))"),
            @Mapping(target = "freightCost", expression = "java(PriceUtils.yuanToFen(vo.getFreightCost()))"),
    })
    QuoteDetailDTO detailVo2Dto(QuoteDetailVo vo );


    @Mappings(value = {
            @Mapping(target = "totalPrice", expression = "java(PriceUtils.yuanToFen(vo.getTotalPrice()))"),
            @Mapping(target = "freightCost", expression = "java(PriceUtils.yuanToFen(vo.getFreightCost()))"),
    })
    QuoteDTO vo2DTO(QuoteVo vo);

    /**
     *  报价详情对象转换
     * @param dto
     * @return
     */
    @Mappings(value = {
            @Mapping(target = "price", expression = "java(PriceUtils.fenToYuan(dto.getPrice()))"),
            @Mapping(target = "freightCost", expression = "java(PriceUtils.fenToYuan(dto.getFreightCost()))"),
    })
    QuoteDetailVo detailDetail2Vo(QuoteDetailDTO dto );

    PricingToOrderVO convert(ConfirmOrderSplitDTO dto);


    /**
     * @param quoteDetailVo 物料信息
     * @param submit  用户报名信息
     */
    default void merge(QuoteDetailVo quoteDetailVo, QuoteDetailVo submit) {
        quoteDetailVo.setTaxRate(submit.getTaxRate());
        quoteDetailVo.setFreightCost(submit.getFreightCost());
        quoteDetailVo.setPrice(submit.getPrice());
        quoteDetailVo.setBrandName(submit.getBrandName());
        quoteDetailVo.setNum(submit.getNum());
        quoteDetailVo.setModel(submit.getModel());
    }
}
