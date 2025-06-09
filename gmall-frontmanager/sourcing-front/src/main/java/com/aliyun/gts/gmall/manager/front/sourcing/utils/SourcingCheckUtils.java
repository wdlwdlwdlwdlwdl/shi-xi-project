package com.aliyun.gts.gmall.manager.front.sourcing.utils;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gcai.platform.sourcing.common.model.inner.PurchaseRequirement;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.manager.front.sourcing.vo.SourcingMaterialVo;
import com.aliyun.gts.gmall.manager.front.sourcing.vo.SourcingVo;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/6/8 17:15
 */
public class SourcingCheckUtils {

    public static void baseCheck(SourcingVo sourcingVo){
        ParamUtil.expectTrue(!CollectionUtils.isEmpty(sourcingVo.getMaterials()),I18NMessageUtils.getMessage("material.required"));  //# "物料不能为空"
        ParamUtil.notBlank(sourcingVo.getTitle(),I18NMessageUtils.getMessage("title.required"));  //# "标题不能为空"
        ParamUtil.nonNull(sourcingVo.getApplyType(),I18NMessageUtils.getMessage("invitation.type.required"));  //# "邀约类型不能为空"
        ParamUtil.nonNull(sourcingVo.getContact(),I18NMessageUtils.getMessage("contact.address.required"));  //# "联系地址不能为空"
        ParamUtil.nonNull(sourcingVo.getContact().getAddress(),I18NMessageUtils.getMessage("contact.address.required"));  //# "联系地址不能为空"
        ParamUtil.nonNull(sourcingVo.getSourcingType(),I18NMessageUtils.getMessage("purchase.type.required"));  //# "采购类型不能为空"
        ParamUtil.nonNull(sourcingVo.getPurchaseRequire(),I18NMessageUtils.getMessage("purchase.requirement.required"));  //# "采购要求不能为空"
        ParamUtil.nonNull(sourcingVo.getStartTime(),I18NMessageUtils.getMessage("quote.time.required"));  //# "报价时间不能为空"
        ParamUtil.nonNull(sourcingVo.getEndTime(),I18NMessageUtils.getMessage("quote.time.required"));  //# "报价时间不能为空"
        //物料校验
        for(SourcingMaterialVo materialVo : sourcingVo.getMaterials()){
            ParamUtil.notBlank(materialVo.getUnit(),I18NMessageUtils.getMessage("material.unit.required"));  //# "物料单位不能为空"
            ParamUtil.nonNull(materialVo.getNum(),I18NMessageUtils.getMessage("material.purchase.qty.required"));  //# "物料采购数量不能为空"
        }
        PurchaseRequirement purchaseRequire  = sourcingVo.getPurchaseRequire();
        ParamUtil.expectTrue(!CollectionUtils.isEmpty(purchaseRequire.getTaxRates()),I18NMessageUtils.getMessage("tax.rate.required"));  //# "税率不能为空"
    }

    public static boolean contain(List<String> list , String key){
        if(CollectionUtils.isEmpty(list)){
            return false;
        }
        for(String str : list){
            if(str.equals(key)){
                return true;
            }
        }
        return false;
    }
}
