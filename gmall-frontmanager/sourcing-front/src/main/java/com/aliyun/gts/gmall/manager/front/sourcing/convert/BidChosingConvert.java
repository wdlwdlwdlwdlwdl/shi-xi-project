package com.aliyun.gts.gmall.manager.front.sourcing.convert;

import com.aliyun.gts.gcai.platform.sourcing.api.dto.model.BidChosingConfigDTO;
import com.aliyun.gts.gcai.platform.sourcing.api.dto.model.BidChosingDetailDTO;
import com.aliyun.gts.gcai.platform.sourcing.api.dto.model.BidExpertOpinionDTO;
import com.aliyun.gts.gcai.platform.sourcing.api.dto.model.QuoteDTO;
import com.aliyun.gts.gcai.platform.sourcing.common.model.inner.ScoringConfig;
import com.aliyun.gts.gcai.platform.sourcing.common.model.inner.ScoringCriterion;
import com.aliyun.gts.gcai.platform.sourcing.common.model.inner.ScoringDomain;
import com.aliyun.gts.gcai.platform.sourcing.common.model.inner.ScoringGroup;
import com.aliyun.gts.gcai.platform.sourcing.common.type.QuoteStatusType;
import com.aliyun.gts.gcai.platform.sourcing.common.type.UserGroupType;
import com.aliyun.gts.gmall.manager.front.b2bcomm.model.KVVO;
import com.aliyun.gts.gmall.manager.front.b2bcomm.model.OperatorDO;
import com.aliyun.gts.gmall.manager.front.sourcing.utils.ChosingConfigUtils;
import com.aliyun.gts.gmall.manager.front.sourcing.vo.bc.*;
import com.google.common.collect.Lists;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 */
@Mapper(componentModel = "spring" , imports = {Lists.class, ChosingConfigUtils.class})
public abstract class BidChosingConvert {

//    @Autowired
//    CompanyReadFacade companyReadFacade;

    @Mappings({
        @Mapping(source ="start",target = "startTime"),
        @Mapping(source = "end" , target = "endTime"),
        @Mapping(target = "chosingType",constant = "1"),
        @Mapping(target = "scoringType",constant = "1"),
        @Mapping(source = "userGroup",target = "chosingGroup"),
        @Mapping(source = "randomUser",target = "feature.randomUser"),
        @Mapping(source = "sourcingName" , target = "feature.sourcingName"),
        @Mapping(target = "scoringConfig" , expression = "java(convert2ConfigList(bidChosingConfigVO.getSections()))")
    })
    public abstract BidChosingConfigDTO convert(BidChosingConfigVO bidChosingConfigVO);

    @Mappings({
        @Mapping(target ="start",source = "startTime"),
        @Mapping(target = "end" , source = "endTime"),
        @Mapping(target = "userGroup",source = "chosingGroup"),
        @Mapping(target = "randomUser",source = "feature.randomUser"),
        @Mapping(target = "sourcingName" , source = "feature.sourcingName"),
        @Mapping(target = "sections" , expression = "java(convert2VO(bidChosingConfigDTO.getScoringConfig()))")
    })
    public abstract BidChosingConfigVO convert(BidChosingConfigDTO bidChosingConfigDTO);

    @Mappings({
        @Mapping(target ="chosingTime",expression = "java(Lists.newArrayList(bidChosingConfigDTO.getStartTime(),"
            + "bidChosingConfigDTO.getEndTime()))"),
        @Mapping(target = "sourcingName" , source = "bidChosingConfigDTO.feature.sourcingName"),
        @Mapping(target = "buttons" , expression = "java(ChosingConfigUtils.buttons(bidChosingConfigDTO,operatorDO))"),
    })
    public abstract BidChosingConfigRowVO convertRow(BidChosingConfigDTO bidChosingConfigDTO, OperatorDO operatorDO);

    public List<BidChosingDetailVO> newBCDetailVOS(BidChosingConfigDTO configDTO ,
                                                   List<QuoteDTO> quoteDTOS){
        //一期没有标段概念  返回的list其实只有一个元素
        BidChosingDetailVO bidChosingDetailVO = new BidChosingDetailVO();
        List<SectionDetailVO> sectionDetailVOS = new ArrayList<>();
        bidChosingDetailVO.setDetailList(sectionDetailVOS);
        for(QuoteDTO quoteDTO : quoteDTOS){
            SectionDetailVO sectionDetailVO = new SectionDetailVO();
            sectionDetailVO.setQuoteId(quoteDTO.getId());
            sectionDetailVO.setSourcingId(quoteDTO.getSourcingId());
            sectionDetailVO.setConfigId(configDTO.getId());
            setCompany(quoteDTO.getSupplierId(),sectionDetailVO);
            SectionConfigVO  sectionConfigVO = convert2VO(configDTO.getScoringConfig()).get(0);
            List<ScoringDetailVO> scoringDetailList = sectionConfigVO.getDomains().
                getCriterionList().stream().map(c->convert(c)).collect(Collectors.toList());
            sectionDetailVO.setScoringDetailList(scoringDetailList);
            sectionDetailVOS.add(sectionDetailVO);
        }
        return Lists.newArrayList(bidChosingDetailVO);
    }

    public List<BidChosingDetailVO> convertBCDetailVOS(List<BidChosingDetailDTO> list ,
        List<BidExpertOpinionDTO> opinions){
        List<BidChosingDetailVO> result = new ArrayList<>();
        for(BidChosingDetailDTO detailDTO : list){
            BidChosingDetailVO chosingDetailVO = result.stream().filter(v->equal(v.getSectionId() ,
                detailDTO.getSectionId())).findFirst().orElse(new BidChosingDetailVO());
            List<SectionDetailVO> sectionDetailVOS = chosingDetailVO.getDetailList();
            if(sectionDetailVOS == null){
                sectionDetailVOS = new ArrayList<>();
                chosingDetailVO.setDetailList(sectionDetailVOS);
                chosingDetailVO.setSectionId(detailDTO.getSectionId());
                result.add(chosingDetailVO);
            }
            SectionDetailVO sectionDetailVO = sectionDetailVOS.stream().filter(s->(equal(s.getQuoteId(),
                detailDTO.getQuoteId()))).findFirst().orElse(new SectionDetailVO());
            if(sectionDetailVO.getQuoteId() == null){
                sectionDetailVO = convert(detailDTO);
                sectionDetailVOS.add(sectionDetailVO);
            }
            List<ScoringDetailVO> scoringDetailVOS = sectionDetailVO.getScoringDetailList();
            if(scoringDetailVOS == null){
                scoringDetailVOS = new ArrayList<>();
                sectionDetailVO.setScoringDetailList(scoringDetailVOS);
            }
            ScoringDetailVO scoringDetailVO = convert2ScoringVO(detailDTO);
            scoringDetailVOS.add(scoringDetailVO);
        }

        for(BidChosingDetailVO bidChosingDetailVO : result){
            Long sectionId = bidChosingDetailVO.getSectionId();
            BidExpertOpinionDTO opDTO = opinions.stream().filter(o->equal(sectionId ,
                o.getSectionId())).findFirst().orElse(new BidExpertOpinionDTO());
            bidChosingDetailVO.setDescription(opDTO.getOpinion());
            bidChosingDetailVO.setOpinionId(opDTO.getId());
        }
        return result;
    }

    public List<BidChosingSummaryVO> convert2SummaryList(List<BidChosingDetailDTO> list, List<QuoteDTO> quoteDTOList){
        //被选为候选的投标
        Set<Long> selectQuote = quoteDTOList.stream().
            map(q->{
                if(q.getStatus().equals(QuoteStatusType.wait_award.getValue())){
                    return q.getId();
                }
                return null;
            }).collect(Collectors.toSet());
        List<BidChosingSummaryVO> result = new ArrayList<>();
        for(BidChosingDetailDTO detailDTO : list){
            //标段维度
            BidChosingSummaryVO summaryVO = result.stream().filter(b->equal(b.getSectionId() ,
                detailDTO.getSectionId())).findFirst().orElse(new BidChosingSummaryVO());
            List<BidChosingCompanySummaryVO> companySummaryVOS = summaryVO.getSummaryList();
            if(companySummaryVOS == null){
                companySummaryVOS = new ArrayList<>();
                summaryVO = new BidChosingSummaryVO();
                summaryVO.setSectionId(detailDTO.getSectionId());
                summaryVO.setSummaryList(companySummaryVOS);
                result.add(summaryVO);
            }
            //投标公司维度
            BidChosingCompanySummaryVO bidChosingCompanySummaryVO = companySummaryVOS.stream().filter(c->equal(c.getCompanyId(),
                detailDTO.getCompanyId())).findFirst().orElse(new BidChosingCompanySummaryVO());
            List<BidChosingDetailSummaryVO> detailSummaryVOS = bidChosingCompanySummaryVO.getScoringSummaryList();
            if(detailSummaryVOS == null){
                detailSummaryVOS = new ArrayList<>();
                bidChosingCompanySummaryVO.setScoringSummaryList(detailSummaryVOS);
                summaryVO.getSummaryList().add(bidChosingCompanySummaryVO);
            }
            bidChosingCompanySummaryVO.setCompanyId(detailDTO.getCompanyId());
            bidChosingCompanySummaryVO.setCompanyName(detailDTO.getCompanyName());
            bidChosingCompanySummaryVO.setQuoteId(detailDTO.getQuoteId());
            bidChosingCompanySummaryVO.setSelected(selectQuote.contains(detailDTO.getQuoteId()));
            bidChosingCompanySummaryVO.setSupplierId(quoteDTOList.stream().filter(q->q.getId().equals(detailDTO.
                getQuoteId())).findFirst().get().getSupplierId());

            //投标公司单个指标维度
            BidChosingDetailSummaryVO detailSummaryVO = detailSummaryVOS.stream().filter(d->equal(d.getDomainName()+ "-" +
                d.getCriterionName(),detailDTO.getDomain() + "-" + detailDTO.getCriterion())).
                findFirst().orElse(new BidChosingDetailSummaryVO());
            detailSummaryVOS.add(detailSummaryVO);
            detailSummaryVO.setDomainName(detailDTO.getDomain());
            detailSummaryVO.setCriterionName(detailDTO.getCriterion());
            detailSummaryVO.setMax(detailDTO.getFeature().getMax());



            List<ExpertScoringDisplayVO> scoringDisplayVOS = detailSummaryVO.getExpertScoring();
            if(scoringDisplayVOS == null){
                scoringDisplayVOS = new ArrayList<>();
                detailSummaryVO.setExpertScoring(scoringDisplayVOS);
            }
            ExpertScoringDisplayVO displayVO = new ExpertScoringDisplayVO();
            displayVO.setName(detailDTO.getExpertName());
            displayVO.setUserId(detailDTO.getExpertId());
            displayVO.setScore(detailDTO.getScore());
            scoringDisplayVOS.add(displayVO);
            detailSummaryVO.calcAverage();
            bidChosingCompanySummaryVO.calcTotal();

        }
        return result;
    }

    public List<BidChosingDetailDTO> convert2DetailDTOs(List<BidChosingDetailVO> voList, OperatorDO operatorDO){
        List<BidChosingDetailDTO> result = new ArrayList<>();
        for(BidChosingDetailVO detailVO : voList){
            for(SectionDetailVO sectionDetailVO : detailVO.getDetailList()){
                for(ScoringDetailVO scoringDetailVO : sectionDetailVO.getScoringDetailList()){
                    BidChosingDetailDTO bidChosingDetailDTO = convert2DetailDTO(scoringDetailVO);
                    bidChosingDetailDTO.setCompanyName(sectionDetailVO.getCompanyName());
                    bidChosingDetailDTO.setCompanyId(sectionDetailVO.getCompanyId());
                    bidChosingDetailDTO.setConfigId(sectionDetailVO.getConfigId());
                    bidChosingDetailDTO.setQuoteId(sectionDetailVO.getQuoteId());
                    bidChosingDetailDTO.setSourcingId(sectionDetailVO.getSourcingId());
                    bidChosingDetailDTO.setSectionId(detailVO.getSectionId());
                    bidChosingDetailDTO.setExpertId(operatorDO.getOperatorId());
                    bidChosingDetailDTO.setExpertName(operatorDO.getUsername());
                    bidChosingDetailDTO.setStatus(detailVO.getStatus());
                    result.add(bidChosingDetailDTO);
                }
            }
        }
        return result;
    }

    public List<BidExpertOpinionDTO> convert2Opinions(List<BidChosingDetailVO> voList, OperatorDO operatorDO){
        List<BidExpertOpinionDTO> result = new ArrayList<>();
        for(BidChosingDetailVO detailVO : voList){
            BidExpertOpinionDTO opinion = new BidExpertOpinionDTO();
            opinion.setOpinion(detailVO.getDescription());
            opinion.setSectionId(detailVO.getSectionId());
            opinion.setExpertId(operatorDO.getOperatorId());
            opinion.setExpertName(operatorDO.getUsername());
            opinion.setSourcingId(detailVO.getDetailList().get(0).getSourcingId());
            opinion.setStatus(detailVO.getStatus());
            result.add(opinion);
        }
        return result;
    }

    public List<BidExpertOpinionVO> convert(List<BidExpertOpinionDTO> opinionDTOS, BidChosingConfigDTO bidChosingConfigDTO){
        List<BidExpertOpinionVO> result = new ArrayList<>();
        ScoringGroup scoringGroup = bidChosingConfigDTO.getChosingGroup().stream().filter(g->equal(g.getType(),UserGroupType.EXPERT.getCode())).
            findFirst().orElse(null);
        int expertCount = 0;
        if(scoringGroup != null){
            expertCount = scoringGroup.getPeopleList().size();
        }
        for(BidExpertOpinionDTO bidExpertOpinionDTO : opinionDTOS){
            BidExpertOpinionVO bidExpertOpinionVO = result.stream().
                filter(r->equal(r.getSectionId(),bidExpertOpinionDTO.getSectionId())).findFirst().orElse(null);
            if(bidExpertOpinionVO == null){
                bidExpertOpinionVO = new BidExpertOpinionVO();
                result.add(bidExpertOpinionVO);
            }
            List<KVVO<String , String>> opinions = bidExpertOpinionVO.getOpinions();
            if(opinions == null){
                opinions = new ArrayList<>();
                bidExpertOpinionVO.setOpinions(opinions);
            }
            opinions.add(new KVVO<>(bidExpertOpinionDTO.getExpertName() , bidExpertOpinionDTO.getOpinion()));
            bidExpertOpinionVO.setExpertCount(expertCount);
            bidExpertOpinionVO.setOpinionsCount(opinions.size());
        }
        return result;
    }

    protected List<SectionConfigVO> convert2VO(List<ScoringConfig> configList){
        Map<Long , SectionConfigVO> map = new HashMap<>();

        for(ScoringConfig config : configList){
            SectionConfigVO sectionConfigVO = map.get(config.getSectionId());
            if(sectionConfigVO == null){
                sectionConfigVO = new SectionConfigVO();
                map.put(config.getSectionId() , sectionConfigVO);
            }
            DomainVO result = sectionConfigVO.getDomains();
            if(result == null){
                result = new DomainVO();
                sectionConfigVO.setDomains(result);
            }
            List<ScoringDomain> domainList = config.getDomainList();
            List<String> domainNames = domainList.stream().map(d->d.getName()).collect(Collectors.toList());
            List<CriterionVO> criterionVOS = new ArrayList<>();
            for(ScoringDomain domain : domainList){
                List<CriterionVO> eachCriterionList = domain.getCriterionList().stream().map(c->{
                    CriterionVO criterionVO = new CriterionVO();
                    criterionVO.setDescription(c.getDescription());
                    criterionVO.setMax(c.getMax());
                    criterionVO.setDomainName(domain.getName());
                    criterionVO.setCriterionName(c.getName());
                    return criterionVO;
                }).collect(Collectors.toList());
                criterionVOS.addAll(eachCriterionList);
            }
            result.setDomainNames(domainNames);
            result.setCriterionList(criterionVOS);
        }
        return Lists.newArrayList(map.values());
    }

    protected List<ScoringConfig> convert2ConfigList(List<SectionConfigVO> sectionConfigVOS){
        List<ScoringConfig> result = new ArrayList<>();
        for(SectionConfigVO sectionConfigVO : sectionConfigVOS) {
            DomainVO domainVO = sectionConfigVO.getDomains();
            ScoringConfig scoringConfig = new ScoringConfig();
            scoringConfig.setSectionId(sectionConfigVO.getSectionId());
            scoringConfig.setDomainList(new ArrayList<>());
            List<String> domainList = domainVO.getDomainNames();
            List<CriterionVO> criterionVOS = domainVO.getCriterionList();
            for (String domainName : domainList) {
                ScoringDomain scoringDomain = new ScoringDomain();
                scoringDomain.setName(domainName);
                scoringDomain.setCriterionList(new ArrayList<>());
                scoringConfig.getDomainList().add(scoringDomain);
                for (CriterionVO criterionVO : criterionVOS) {
                    if (domainName.equals(criterionVO.getDomainName())) {
                        ScoringCriterion criterion = new ScoringCriterion();
                        criterion.setName(criterionVO.getCriterionName());
                        criterion.setMax(criterionVO.getMax());
                        criterion.setDescription(criterionVO.getDescription());
                        scoringDomain.getCriterionList().add(criterion);
                    }
                }
            }
            result.add(scoringConfig);
        }
        return result;
    }

    private boolean equal(Object o1 , Object o2){
        if(o1 == null && o2 == null){
            return true;
        }
        if(o1 == null){
            return false;
        }
        return o1.equals(o2);
    }

    private void setCompany(Long  supplierId , SectionDetailVO detailVO){
        //detailVO.setCompanyId(supplierId);
        //detailVO.setCompanyName(supplierId + " 的公司");
//        CompanyRelatePageQueryRequest request = new CompanyRelatePageQueryRequest();
//        request.setRelateId(supplierId);
//        request.setRelateType(1);
//        request.setPage(new PageParam());
//        RpcResponse<List<CompanyDTO>> response = companyReadFacade.queryCompanyByRelateId(request);
//        CompanyDTO companyDTO = response.getData().get(0);
//        detailVO.setCompanyName(companyDTO.getName());
//        detailVO.setCompanyId(supplierId);
    }

    protected abstract SectionDetailVO convert(BidChosingDetailDTO detailDTO);

    protected abstract ScoringDetailVO convert(CriterionVO criterionVO);

    @Mappings({
        @Mapping(target ="domainName",source = "domain"),
        @Mapping(target = "criterionName" , source = "criterion"),
        @Mapping(target = "description",source = "feature.standard"),
        @Mapping(target = "max",source = "feature.max"),
    })
    protected abstract ScoringDetailVO convert2ScoringVO(BidChosingDetailDTO detailDTO);

    @Mappings({
        @Mapping(source ="domainName",target = "domain"),
        @Mapping(source = "criterionName" , target = "criterion")
    })
    protected abstract BidChosingDetailDTO convert2DetailDTO(ScoringDetailVO detailDTO);

}
