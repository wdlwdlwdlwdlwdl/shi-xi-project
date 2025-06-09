package com.aliyun.gts.gmall.manager.front.trade.dto.output.order.extend;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.center.item.api.dto.output.EvoucherPeriodDTO;
import com.aliyun.gts.gmall.framework.server.util.DateTimeUtils;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel("电子凭证确认信息")
public class EvoucherTimeVO extends OrderExtendVO{

    String evoucherDisplay;

    public EvoucherTimeVO(EvoucherPeriodDTO evoucherPeriodDTO){
        StringBuffer sb = new StringBuffer();
        if(evoucherPeriodDTO.getType().equals(EvoucherPeriodDTO.TYPE_1)){
            sb.append("购买之日起").append(evoucherPeriodDTO.getDay()).append("天有效");
        }
        if(evoucherPeriodDTO.getType().equals(EvoucherPeriodDTO.TYPE_2)){
            sb.append(I18NMessageUtils.getMessage("valid")).append(DateTimeUtils.nyrFormat(evoucherPeriodDTO.getEnd())).append("有效");  //# "购买之日起到"
        }
        if(evoucherPeriodDTO.getType().equals(EvoucherPeriodDTO.TYPE_3)){
            sb.append(DateTimeUtils.nyrFormat(evoucherPeriodDTO.getStart()))
                .append(" - ")
                .append(DateTimeUtils.nyrFormat(evoucherPeriodDTO.getEnd()));
        }
        if(evoucherPeriodDTO.getType().equals(EvoucherPeriodDTO.TYPE_4)){
            sb.append("长期有效");
        }
        this.evoucherDisplay = sb.toString();
    }

    public EvoucherTimeVO(){

    }

}
