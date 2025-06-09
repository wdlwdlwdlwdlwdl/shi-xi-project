package com.aliyun.gts.gmall.manager.front.sourcing.convert;

import com.aliyun.gts.gcai.platform.sourcing.api.dto.model.PurchaseRequestDTO;
import com.aliyun.gts.gcai.platform.sourcing.common.input.query.PurchaseRequestQuery;
import com.aliyun.gts.gmall.manager.front.b2bcomm.utils.PriceUtils;
import com.aliyun.gts.gmall.manager.front.sourcing.input.pr.PurchaseRequestReq;
import com.aliyun.gts.gmall.manager.front.sourcing.utils.PurchaseRequestUtils;
import com.aliyun.gts.gmall.manager.front.sourcing.vo.pr.PurchaseRequestBaseVO;
import com.aliyun.gts.gmall.manager.front.sourcing.vo.pr.PurchaseRequestDetailVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.Date;
import java.util.List;

/**
 *
 */
@Mapper(componentModel = "spring" , imports = {PurchaseRequestUtils.class, PriceUtils.class})
public abstract class PurchaseRequestConvert {

    @Mappings( {
        @Mapping(target="id", source = "prId"),
        @Mapping(target="name", source = "prName"),
        @Mapping(target="applicant", source = "prUser"),
        @Mapping(target="applicationDep", source = "prDep"),
        @Mapping(expression = "java(detailVO.getPrTime() != null? detailVO.getPrTime()[0]:null)", target = "requireStart"),
        @Mapping(expression = "java(detailVO.getPrTime() != null? detailVO.getPrTime()[1]:null)", target = "requireEnd"),
        @Mapping(target="contact.telephone", source = "telephone"),
        @Mapping(target="contact.cellphone", source = "cellphone"),
        @Mapping(target="contact.email", source = "email"),
        @Mapping(target="budget", expression = "java(PriceUtils.yuanToFen(detailVO.getBudget()))")
    })
    public abstract PurchaseRequestDTO convert(PurchaseRequestDetailVO detailVO);


    @Mappings( {
        @Mapping(expression = "java(hasTimeCondition(req.getPrTime() , 0) ? req.getPrTime()[0]:null)", target = "prStart"),
        @Mapping(expression = "java(hasTimeCondition(req.getPrTime() , 1) ? req.getPrTime()[1]:null)", target = "prEnd"),
    })
    public abstract PurchaseRequestQuery convert(PurchaseRequestReq req);


    public abstract List<PurchaseRequestBaseVO> convert2BaseList(List<PurchaseRequestDTO> purchaseRequestDTOList);


    @Mappings( {
        @Mapping(source="id", target = "prId"),
        @Mapping(source="name", target = "prName"),
        @Mapping(source="applicant", target = "prUser"),
        @Mapping(source="applicationDep", target = "prDep"),
        @Mapping(expression = "java(new java.util.Date[]{purchaseRequestDTO.getRequireStart() , "
            + "purchaseRequestDTO.getRequireEnd()})" , target = "prTime"),
        @Mapping(source="contact.telephone", target = "telephone"),
        @Mapping(source="contact.cellphone", target = "cellphone"),
        @Mapping(source="contact.email", target = "email"),
        @Mapping(expression="java(PriceUtils.fenToYuan(purchaseRequestDTO.getBudget()))", target = "budget"),
        @Mapping(expression="java(PurchaseRequestUtils.buttons(purchaseRequestDTO))", target = "buttons")
    })
    public abstract PurchaseRequestDetailVO convert2Detail(PurchaseRequestDTO purchaseRequestDTO);

    //@Mappings( {
    //    @Mapping(source="id", target = "prId"),
    //    @Mapping(source="name", target = "prName"),
    //    @Mapping(source="applicant", target = "prUser"),
    //    @Mapping(source="applicationDep", target = "prDep"),
    //    @Mapping(source="approveStatus", target = "approveStatus"),
    //    @Mapping(expression = "java(new java.util.Date[]{purchaseRequestDTO.getRequireStart() , "
    //        + "purchaseRequestDTO.getRequireEnd()})" , target = "prTime")
    //})
    //protected abstract PurchaseRequestBaseVO convert2Base(PurchaseRequestDTO purchaseRequestDTO);

    protected boolean hasTimeCondition(Date[] prTime , int length){
        if(prTime != null && prTime.length > length){
            return true;
        }
        return false;
    }



}
