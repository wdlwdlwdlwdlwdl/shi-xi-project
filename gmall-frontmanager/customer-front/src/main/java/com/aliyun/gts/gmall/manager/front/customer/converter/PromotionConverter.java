package com.aliyun.gts.gmall.manager.front.customer.converter;

import com.aliyun.gts.gmall.manager.front.customer.dto.input.command.ApplyCouponRestCommand;
import com.aliyun.gts.gmall.manager.front.customer.dto.output.*;
import com.aliyun.gts.gmall.manager.utils.PriceUtils;
import com.aliyun.gts.gmall.platform.promotion.api.dto.account.AcBookRecordDTO;
import com.aliyun.gts.gmall.platform.promotion.api.dto.input.CouponApplyReq;
import com.aliyun.gts.gmall.platform.promotion.api.dto.model.DiscountRuleDTO;
import com.aliyun.gts.gmall.platform.promotion.api.dto.model.PromCampaignDTO;
import com.aliyun.gts.gmall.platform.promotion.api.dto.model.PromDetailDTO;
import com.aliyun.gts.gmall.platform.promotion.api.dto.output.CouponInstanceDTO;
import com.aliyun.gts.gmall.platform.promotion.common.constant.ConditionRuleKey;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.List;

/**
 * @author GTS
 * @date 2021/03/12
 */
@Mapper(componentModel = "spring")
public interface PromotionConverter {

    //普通模式
    public static final int campaignTimeType_normal = 1;
    //按照优惠券的模式;领券后多久可用
    public static final int campaignTimeType_assets = 2;

    /**
     * @param command
     * @return
     */
    @Mapping( target = "applyUniqueId",source = "bizId")
    CouponApplyReq convertApplyCouponRequest(ApplyCouponRestCommand command);

    /**
     * @param couponInstanceDTO
     * @return
     */
    CouponInstanceVO convertCouponInstance(CouponInstanceDTO couponInstanceDTO);

    /**
     * @param couponInstanceDTO
     * @return
     */
    CouponInstanceDetailVO convertCouponInstanceDetail(CouponInstanceDTO couponInstanceDTO);

    /**
     * @param dto
     * @return
     */
    AcBookRecordVO convertAcBookRecord(AcBookRecordDTO dto);

    @Mapping(target = "discountRule", ignore = true)
    @Mapping(target = "details", ignore = true)
    @Mapping(target = "couponCode", source = "campaignCode")
    public PromCampaignVO toPromCampaignVO(PromCampaignDTO dto);

    @Mapping(target = "discountRule", ignore = true)
    public PromDetailVO toPromDetailVO(PromDetailDTO dto);

    default PromDetailVO defaultToVo(PromDetailDTO dto) {
        PromDetailVO detailVo = toPromDetailVO(dto);
        if (dto.getDiscountRule() != null) {
            detailVo.setDiscountRule(defaultToVo(dto.getDiscountRule()));
        }
        return detailVo;
    }

    DiscountRuleVO toDiscountRuleVO(DiscountRuleDTO ruleDTO);

    default DiscountRuleVO defaultToVo(DiscountRuleDTO ruleDTO) {
        DiscountRuleVO vo = toDiscountRuleVO(ruleDTO);
        if (ruleDTO.getMan() != null) {
            vo.setManYuan(Double.valueOf(ruleDTO.getMan()));
        }
        if (ruleDTO.getJian() != null) {
            vo.setJianYuan(Double.valueOf(ruleDTO.getJian()));
        }
        if (ruleDTO.getPrice() != null) {
            vo.setPriceYuan(Double.valueOf(ruleDTO.getPrice()));
        }
        return vo;
    }


    default PromCampaignVO toDefaultVO(PromCampaignDTO dto) {
        PromCampaignVO vo = toPromCampaignVO(dto);
        if (dto.getDiscountRule() != null) {
            vo.setDiscountRule(defaultToVo(dto.getDiscountRule()));
        }
        if(dto.getConditionRule() != null){
            //优惠时间
            Integer assetsDays = ConditionRuleKey.build(dto.getConditionRule()).assetsDays();
            if(assetsDays != null && assetsDays > 0){
                vo.setCampaignTimeType(campaignTimeType_assets);
            }
        }
        if (dto.getDetails() != null) {
            List<PromDetailVO> detailVoList = new ArrayList<>();
            for (PromDetailDTO detailDTO : dto.getDetails()) {
                PromDetailVO detailVo = defaultToVo(detailDTO);
                detailVoList.add(detailVo);
            }
            vo.setDetails(detailVoList);
        }
        if(vo.getTotalCnt() == null || vo.getTotalCnt() == 0){
            vo.setTotalCnt(null);
        }
        return vo;
    }
}