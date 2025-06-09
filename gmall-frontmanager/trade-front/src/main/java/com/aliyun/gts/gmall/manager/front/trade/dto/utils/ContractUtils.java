package com.aliyun.gts.gmall.manager.front.trade.dto.utils;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import java.util.HashMap;
import java.util.Map;


import com.aliyun.gts.gmall.manager.front.trade.constants.ButtonNames;

public class ContractUtils {
/*    *//**
     * 根据合同状态和签署状态控制前端显示按钮
     * @param contractDTO
     * @return
     *//*
    public static Map<String , Boolean> buttons(B2BContractDTO contractDTO){
        Map<String , Boolean> buttons = new HashMap<>();
        Integer status = contractDTO.getStatus();
        Long signStatus = contractDTO.getSignStatus();
        if(ContractStatus.Approving.getValue().equals(status)){
            buttons.put(ButtonNames.contractCancel , true);
        }
        if(ContractStatus.Draft.getValue().equals(status) || ContractStatus.Rejected.getValue().equals(status) ){
            buttons.put(ButtonNames.contractEdit , true);
        }
        //线下签署会显示的按钮包括  签署  收到    寄出
        if(contractDTO.getFeature().getSignType() == 2){
            //卖方先签
            if(contractDTO.getFeature().getSignOrder() == 1){
                //买方已收到 且合同还在签署中 且 买方未签署
                if(signStatus != null
                    && (SignStatus.PurchaseReceived.getValue() & signStatus) == SignStatus.PurchaseReceived.getValue()
                    && ContractStatus.Signing.getValue().equals(status)
                    && (SignStatus.PurchaseSigned.getValue() & signStatus) != SignStatus.PurchaseSigned.getValue()){
                    buttons.put(ButtonNames.contractSigned , true);
                }
                //买方已签署  且合同还在签署中  且 买方未寄出  显示合同已寄出按钮
                if(signStatus != null
                    && (SignStatus.PurchaseSigned.getValue() & signStatus) == SignStatus.PurchaseSigned.getValue()
                    && ContractStatus.Signing.getValue().equals(status)
                    && (SignStatus.PurchaseSended.getValue() & signStatus) != SignStatus.PurchaseSended.getValue()){
                    buttons.put(ButtonNames.contractSend , true);
                }
                //卖方已寄出 且合同还在签署中 且买方未收到 显示合同已收到按钮
                if(signStatus != null
                    && (SignStatus.SupplySended.getValue() & signStatus) == SignStatus.SupplySended.getValue()
                    && ContractStatus.Signing.getValue().equals(status)
                    && (SignStatus.PurchaseReceived.getValue() & signStatus) != SignStatus.PurchaseReceived.getValue()){
                    buttons.put(ButtonNames.contractReceived , true);
                }
            }
            //买方先签
            if(contractDTO.getFeature().getSignOrder() == 2){
                //卖方已寄出 且合同还在签署中 且买方未收到  显示收到按钮
                if(signStatus != null
                    && (SignStatus.SupplySended.getValue() & signStatus) == SignStatus.SupplySended.getValue()
                    && ContractStatus.Signing.getValue().equals(status)
                    && (SignStatus.PurchaseReceived.getValue() & signStatus) != SignStatus.PurchaseReceived.getValue()){
                    buttons.put(ButtonNames.contractReceived , true);
                }
                //签署状态为空 且状态在签署中
                if(signStatus == null
                    && ContractStatus.Signing.getValue().equals(status)){
                    buttons.put(ButtonNames.contractSigned , true);
                }
                //买方已收到 且合同还在签署中 且未签署  显示寄出按钮
                if(signStatus != null
                    && (SignStatus.PurchaseSigned.getValue() & signStatus) == SignStatus.PurchaseSigned.getValue()
                    && ContractStatus.Signing.getValue().equals(status)
                    && (SignStatus.PurchaseSended.getValue() & signStatus) != SignStatus.PurchaseSended.getValue()){
                    buttons.put(ButtonNames.contractSend , true);
                }
            }
        }else{
            //显示电子签章签署页面
            if(ContractStatus.Signing.getValue().equals(status)) {
                //买方先签  但是买方还没签
                if(contractDTO.getFeature().getSignOrder() == 2 &&
                    signStatus == null) {
                    buttons.put(ButtonNames.contractSignPage, true);
                }
                //卖方先签  卖方已经签署  买方还没签署
                if(contractDTO.getFeature().getSignOrder() == 1 &&
                    signStatus != null &&
                    (SignStatus.PurchaseSigned.getValue() & signStatus) != SignStatus.PurchaseSigned.getValue()
                && (SignStatus.SupplySigned.getValue() & signStatus) == SignStatus.SupplySigned.getValue()) {
                    buttons.put(ButtonNames.contractSignPage, true);
                }
            }
        }


        return buttons;
    }

    public static String getSignDisplay(Long signStatus){
        StringBuffer sb = new StringBuffer();
        sb.append("/");
        if(signStatus == null){
            return sb.toString();
        }
        if((SignStatus.SupplySigned.getValue() & signStatus) == SignStatus.SupplySigned.getValue()){
            sb.append(I18NMessageUtils.getMessage("seller.signed")).append("/");  //# "卖方已签署"
        }
        if((SignStatus.SupplyReceived.getValue() & signStatus) == SignStatus.SupplyReceived.getValue()){
            sb.append(I18NMessageUtils.getMessage("seller.received")).append("/");  //# "卖方已收到"
        }
        if((SignStatus.SupplySended.getValue() & signStatus) == SignStatus.SupplySended.getValue()){
            sb.append(I18NMessageUtils.getMessage("seller.sent")).append("/");  //# "卖方已寄出"
        }
        if((SignStatus.PurchaseSigned.getValue() & signStatus) == SignStatus.PurchaseSigned.getValue()){
            sb.append(I18NMessageUtils.getMessage("buyer.signed")).append("/");  //# "买方已签署"
        }
        if((SignStatus.PurchaseSended.getValue() & signStatus) == SignStatus.PurchaseSended.getValue()){
            sb.append(I18NMessageUtils.getMessage("buyer.sent")).append("/");  //# "买方已寄出"
        }
        if((SignStatus.PurchaseReceived.getValue() & signStatus) == SignStatus.PurchaseReceived.getValue()){
            sb.append(I18NMessageUtils.getMessage("buyer.received")).append("/");  //# "买方已收到"
        }

        return sb.toString();
    }*/
}
