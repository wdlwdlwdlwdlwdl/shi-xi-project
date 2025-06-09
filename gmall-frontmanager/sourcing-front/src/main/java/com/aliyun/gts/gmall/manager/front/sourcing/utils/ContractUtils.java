package com.aliyun.gts.gmall.manager.front.sourcing.utils;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gcai.platform.contract.common.type.ContractStatus;
import com.aliyun.gts.gcai.platform.contract.common.type.SignStatus;
import com.aliyun.gts.gcai.platform.sourcing.api.dto.model.PurchaseContractDTO;
import com.aliyun.gts.gcai.platform.sourcing.api.dto.model.QuoteDTO;
import com.aliyun.gts.gcai.platform.sourcing.api.dto.model.QuoteDetailDTO;
import com.aliyun.gts.gcai.platform.sourcing.api.dto.model.SourcingDTO;
import com.aliyun.gts.gcai.platform.sourcing.common.model.PayByDateDO;
import com.aliyun.gts.gcai.platform.sourcing.common.model.PayByOther;
import com.aliyun.gts.gcai.platform.sourcing.common.model.PayByPeriod;
import com.aliyun.gts.gcai.platform.sourcing.common.model.PayTimeLimitDO;
import com.aliyun.gts.gcai.platform.sourcing.common.model.inner.QuoteDetailFeature;
import com.aliyun.gts.gcai.platform.sourcing.common.type.FulfillConstants;
import com.aliyun.gts.gmall.manager.front.b2bcomm.constants.ButtonNames;
import com.aliyun.gts.gmall.manager.front.b2bcomm.utils.PriceUtils;
import github.clyoudu.consoletable.ConsoleTable;
import github.clyoudu.consoletable.enums.Align;
import github.clyoudu.consoletable.table.Cell;
import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContractUtils {

    static DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static Map<String ,String> buildAvariables(PurchaseContractDTO purchaseContractDTO ,
        QuoteDTO quoteDTO , SourcingDTO sourcingDTO){
        Map<String , String> avariables = new HashMap<>();
        avariables.put(I18NMessageUtils.getMessage("supplier.company.name") , purchaseContractDTO.getSupplierCompanyName());  //# "供应商企业名称"
        avariables.put(I18NMessageUtils.getMessage("purchaser.company.name") , purchaseContractDTO.getPurchaserCompanyName());  //# "采购方企业名称"
        if(purchaseContractDTO.getQuoteDetails() != null) {
            avariables.put(I18NMessageUtils.getMessage("total.contract.amount"), purchaseContractDTO.getQuoteDetails().stream().  //# "合同总金额"
                mapToLong(p -> p.getPrice() != null ? p.getPrice() : 0).summaryStatistics().getSum()
                + purchaseContractDTO.getQuoteDetails().stream().
                mapToLong(p -> p.getFreightCost() != null ? p.getFreightCost() : 0).summaryStatistics().getSum() + "");

            avariables.put(I18NMessageUtils.getMessage("quote.material.list") , buildTable2(purchaseContractDTO.getQuoteDetails()));  //# "报价物料列表"
        }


        if(quoteDTO != null){
            if(quoteDTO.getPriceStartTime() != null && quoteDTO.getPriceEndTime() != null) {
                avariables.put(I18NMessageUtils.getMessage("price.validity.period"), format.format(quoteDTO.getPriceStartTime()) +  //# "价格有效期"
                    " - " + format.format(quoteDTO.getPriceEndTime()));
            }
            if(quoteDTO.getFulfillPromise() != null && StringUtils.isNotBlank(quoteDTO.getFulfillPromise().getDiliverData())) {
                avariables.put(I18NMessageUtils.getMessage("expected.delivery.date"), "自下单后" + quoteDTO.getFulfillPromise().getDiliverData() + "天");  //# "期望交期"
            }
        }

        avariables.put(I18NMessageUtils.getMessage("payment.deadline") , getPayLimist(purchaseContractDTO.getFulfillRequirement().getPayTimeLimit()));  //# "支付期限"
        avariables.put(I18NMessageUtils.getMessage("payment.channel") , getPayChannel(purchaseContractDTO.getFulfillRequirement().getPayChannels()));  //# "支付渠道"
        avariables.put(I18NMessageUtils.getMessage("invoice.requirement") , getInvoice(purchaseContractDTO.getFulfillRequirement().getInvoiceRequire()));  //# "发票要求"

        if(sourcingDTO != null){
            avariables.put(I18NMessageUtils.getMessage("delivery.address") , sourcingDTO.getContact().getAddress());  //# "收货地址"
            avariables.put(I18NMessageUtils.getMessage("specified.tax.rate") , getTaxRate(sourcingDTO.getPurchaseRequire().getTaxRates()));  //# "指定税率"
        }



        return avariables;
    }

    public static String buildTable2(List<QuoteDetailDTO> quoteDetails) {
        StringBuffer sb = new StringBuffer();
        for (QuoteDetailDTO quoteDetailDTO : quoteDetails) {
            if(quoteDetailDTO.getAwardNum() == null || quoteDetailDTO.getAwardNum() <= 0){
                continue;
            }
            sb.append(I18NMessageUtils.getMessage("material.name")+":").append(quoteDetailDTO.getQuoteFeature().getSourcingMaterialName()).append("\n");  //# "物料名称
            sb.append(I18NMessageUtils.getMessage("material.code")+":").append(quoteDetailDTO.getQuoteFeature().getSourcingMaterialCode()).append("\n");  //# "物料编码
            sb.append(I18NMessageUtils.getMessage("brand")+":").append(quoteDetailDTO.getBrandName()).append("\n");  //# "品牌
            sb.append(I18NMessageUtils.getMessage("model")+":").append(quoteDetailDTO.getModel()).append("\n");  //# "型号
            sb.append(I18NMessageUtils.getMessage("purchase.qty")+"/"+I18NMessageUtils.getMessage("unit")+":").append(quoteDetailDTO.getAwardNum() + "/" +  //# "采购量/单位
                quoteDetailDTO.getQuoteFeature().getSourcingMaterialUnit()).append("\n");
            sb.append(I18NMessageUtils.getMessage("tax.incl.unit.price")+":").append(PriceUtils.fenToYuan(quoteDetailDTO.getPrice()) + "+"+I18NMessageUtils.getMessage("yuan")).append("\n");  //# "含税单价:").append(PriceUtils.fenToYuan(quoteDetailDTO.getPrice()) + "元"
            sb.append(I18NMessageUtils.getMessage("tax.rate")+":").append(quoteDetailDTO.getTaxRate() + "").append("\n");  //# "税率
            sb.append(I18NMessageUtils.getMessage("freight")+":").append(PriceUtils.fenToYuan(quoteDetailDTO.getFreightCost()) + "+"+I18NMessageUtils.getMessage("yuan")).append("\n");  //# "运费:").append(PriceUtils.fenToYuan(quoteDetailDTO.getFreightCost()) + "元"
            sb.append("\n");
        }
        return sb.toString();
    }

    public static String buildTable(List<QuoteDetailDTO> quoteDetails) {
        if(quoteDetails == null || quoteDetails.size() == 0){
            return "";
        }
        List<Cell> header = new ArrayList<Cell>() {{
            add(new Cell(Align.CENTER,I18NMessageUtils.getMessage("material.name")));  //# "物料名称"
            add(new Cell(Align.CENTER,I18NMessageUtils.getMessage("material.code")));  //# "物料编码"
            add(new Cell(Align.CENTER,I18NMessageUtils.getMessage("brand")));  //# "品牌"
            add(new Cell(Align.CENTER,I18NMessageUtils.getMessage("model")));  //# "型号"
            add(new Cell(Align.CENTER,I18NMessageUtils.getMessage("purchase.qty")+"/"+I18NMessageUtils.getMessage("unit")));  //# "采购量/单位"
            add(new Cell(Align.CENTER,I18NMessageUtils.getMessage("tax.incl.unit.price")));  //# "含税单价"
            add(new Cell(Align.CENTER,I18NMessageUtils.getMessage("tax.rate")));  //# "税率"
            add(new Cell(Align.CENTER,I18NMessageUtils.getMessage("freight")));  //# "运费"
        }};

        List<List<Cell>> body = new ArrayList<List<Cell>>();

        for (QuoteDetailDTO quoteDetailDTO : quoteDetails) {
            List<Cell> row = new ArrayList<Cell>(){
            {
                add(new Cell(Align.CENTER, quoteDetailDTO.getQuoteFeature().getSourcingMaterialName()));
                add(new Cell(Align.CENTER, quoteDetailDTO.getQuoteFeature().getSourcingMaterialCode()));
                add(new Cell(Align.CENTER, quoteDetailDTO.getBrandName()));
                add(new Cell(Align.CENTER, quoteDetailDTO.getModel()));
                add(new Cell(Align.CENTER, quoteDetailDTO.getNum() + "/" +
                        quoteDetailDTO.getQuoteFeature().getSourcingMaterialUnit()));
                add(new Cell(Align.CENTER, quoteDetailDTO.getPrice() + ""));
                add(new Cell(Align.CENTER, quoteDetailDTO.getTaxRate() + ""));
                add(new Cell(Align.CENTER, quoteDetailDTO.getFreightCost() + ""));
            }};
            body.add(row);
        }
        String table = new ConsoleTable.ConsoleTableBuilder().addHeaders(header).addRows(body).build().toString();
        return table;
    }

    /**
     * 根据合同状态和签署状态控制前端显示按钮
     * @param contractDTO
     * @return
     */
    public static Map<String , Boolean> buttons(PurchaseContractDTO contractDTO){
        Map<String , Boolean> buttons = new HashMap<>();
        Integer status = contractDTO.getStatus();
        Long signStatus = contractDTO.getSignStatus();

        if(status.equals(ContractStatus.Draft.getValue()) || status.equals(ContractStatus.Rejected.getValue())){
            buttons.put(ButtonNames.contractDelete , true);
        }

        if(ContractStatus.Approving.getValue().equals(status)){
            buttons.put(ButtonNames.contractCancel , true);
        }
        if(ContractStatus.Draft.getValue().equals(status) || ContractStatus.Rejected.getValue().equals(status) ){
            buttons.put(ButtonNames.contractEdit , true);
        }
        //线下签署会显示的按钮包括  签署  收到    寄出
        if(contractDTO.getFeature().getSignType() == 2){
            //采购商先签
            if(contractDTO.getFeature().getSignOrder() == 1){
                //显示合同已签署按钮
                if(signStatus == null
                    && ContractStatus.Signing.getValue().equals(status)){
                    buttons.put(ButtonNames.contractSigned , true);
                }
                //采购商已签署  且合同还在签署中  且 采购商未寄出  显示合同已寄出按钮
                if(signStatus != null
                    && (SignStatus.PurchaseSigned.getValue() & signStatus) == SignStatus.PurchaseSigned.getValue()
                    && ContractStatus.Signing.getValue().equals(status)
                    && (SignStatus.PurchaseSended.getValue() & signStatus) != SignStatus.PurchaseSended.getValue()){
                    buttons.put(ButtonNames.contractSend , true);
                }
                //供应商已寄出 且合同还在签署中 且采购商未收到 显示合同已收到按钮
                if(signStatus != null
                    && (SignStatus.SupplySended.getValue() & signStatus) == SignStatus.SupplySended.getValue()
                    && ContractStatus.Signing.getValue().equals(status)
                    && (SignStatus.PurchaseReceived.getValue() & signStatus) != SignStatus.PurchaseReceived.getValue()){
                    buttons.put(ButtonNames.contractReceived , true);
                }
            }
            //供应商先签
            if(contractDTO.getFeature().getSignOrder() == 2){
                //供应商已寄出 且合同还在签署中 且采购商未收到  显示收到按钮
                if(signStatus != null
                    && (SignStatus.SupplySended.getValue() & signStatus) == SignStatus.SupplySended.getValue()
                    && ContractStatus.Signing.getValue().equals(status)
                    && (SignStatus.PurchaseReceived.getValue() & signStatus) != SignStatus.PurchaseReceived.getValue()){
                    buttons.put(ButtonNames.contractReceived , true);
                }
                //采购商已收到 且合同还在签署中 且未签署  显示签署按钮
                if(signStatus != null
                    && (SignStatus.PurchaseReceived.getValue() & signStatus) == SignStatus.PurchaseReceived.getValue()
                    && ContractStatus.Signing.getValue().equals(status)
                    && (SignStatus.PurchaseSigned.getValue() & signStatus) != SignStatus.PurchaseSigned.getValue()){
                    buttons.put(ButtonNames.contractSigned , true);
                }
                //采购商已收到 且合同还在签署中 且未签署  显示寄出按钮
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
                if( signStatus != null &&
                    (SignStatus.PurchaseSigned.getValue() & signStatus) == SignStatus.PurchaseSigned.getValue()){
                    buttons.put(ButtonNames.contractEsignView, true);
                }else{
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
        if((SignStatus.PurchaseSigned.getValue() & signStatus) == SignStatus.PurchaseSigned.getValue()){
            sb.append(SignStatus.PurchaseSigned.getCode()).append("/");
        }
        if((SignStatus.PurchaseSended.getValue() & signStatus) == SignStatus.PurchaseSended.getValue()){
            sb.append(SignStatus.PurchaseSended.getCode()).append("/");
        }
        if((SignStatus.PurchaseReceived.getValue() & signStatus) == SignStatus.PurchaseReceived.getValue()){
            sb.append(SignStatus.PurchaseReceived.getCode()).append("/");
        }
        if((SignStatus.SupplySigned.getValue() & signStatus) == SignStatus.SupplySigned.getValue()){
            sb.append(SignStatus.SupplySigned.getCode()).append("/");
        }
        if((SignStatus.SupplyReceived.getValue() & signStatus) == SignStatus.SupplyReceived.getValue()){
            sb.append(SignStatus.SupplyReceived.getCode()).append("/");
        }
        if((SignStatus.SupplySended.getValue() & signStatus) == SignStatus.SupplySended.getValue()){
            sb.append(SignStatus.SupplySended.getCode()).append("/");
        }

        return sb.toString();
    }

    private static String getPayLimist(PayTimeLimitDO payTimeLimitDO){
        if(payTimeLimitDO.getType().equals("on_time")){
            return I18NMessageUtils.getMessage("instant.payment");  //# "即时到账"
        }
        if(payTimeLimitDO.getType().equals("by_time")){
            PayByDateDO payByDateDO = (PayByDateDO)payTimeLimitDO;
            if(payByDateDO.getFixedType().equals(1)){
                return "每月" + payByDateDO.getFixedTime() + "号结算";
            }else{
                return "确认收货" + payByDateDO.getAfterTime() + "天后结算";
            }
        }
        if(payTimeLimitDO.getType().equals("by_period")){
            PayByPeriod payByPeriod = (PayByPeriod)payTimeLimitDO;
            return payByPeriod.getPeriod() + I18NMessageUtils.getMessage("stage.payment");  //# "分阶段支付"
        }
        if(payTimeLimitDO.getType().equals("other")){
            PayByOther payByOther = (PayByOther)payTimeLimitDO;
            return payByOther.getWay();
        }
        return "";
    }

    private static String getInvoice(Integer invoice){
        if(invoice.equals(FulfillConstants.INV_REQUIREMENT_1)){
            return I18NMessageUtils.getMessage("vat.invoice")+"("+I18NMessageUtils.getMessage("general.taxpayer")+")";  //# "增值税专票(一般纳税人
        }
        if(invoice.equals(FulfillConstants.INV_REQUIREMENT_2)){
            return I18NMessageUtils.getMessage("vat.invoice")+"("+I18NMessageUtils.getMessage("no.limited.issuer")+")";  //# "增值税专票(不限开具方
        }
        if(invoice.equals(FulfillConstants.INV_REQUIREMENT_3)){
            return I18NMessageUtils.getMessage("normal.invoice");  //# "增值税普通发票"
        }
        if(invoice.equals(FulfillConstants.INV_REQUIREMENT_4)){
            return I18NMessageUtils.getMessage("no.invoice");  //# "不用发票"
        }

        return "";
    }

    private static String getPayChannel(List<String> payChannels){
        if(payChannels == null || payChannels.size() ==0){
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for(String channel : payChannels){
            if(channel.equals(FulfillConstants.CHANNEL_company_alipay)){
                sb.append(I18NMessageUtils.getMessage("enterprise.alipay")+"；");  //# "企业支付宝
            }
            if(channel.equals(FulfillConstants.CHANNEL_bank_transfer)){
                sb.append(I18NMessageUtils.getMessage("bank.transfer")+"；");  //# "银行转账
            }
            if(channel.equals(FulfillConstants.CHANNEL_private_alipay)){
                sb.append(I18NMessageUtils.getMessage("personal.alipay")+"；");  //# "个人支付宝
            }
            if(channel.equals(FulfillConstants.CHANNEL_4)){
                sb.append(I18NMessageUtils.getMessage("bank.electronic.bill")+"；");  //# "银行电子承兑汇票
            }
        }
        return sb.toString();
    }

    private static String getTaxRate(List<Integer> taxRates){
        if(taxRates == null || taxRates.size() ==0){
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for(Integer taxRate : taxRates){
            sb.append(taxRate + "%；");
        }
        return sb.toString();
    }

}
