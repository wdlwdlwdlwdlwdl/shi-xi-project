package com.aliyun.gts.gmall.manager.front.sourcing.utils;

import com.aliyun.gts.gcai.platform.sourcing.api.dto.model.PurchaseRequestDTO;
import com.aliyun.gts.gcai.platform.sourcing.common.type.PurchaseRequestStatus;
import com.aliyun.gts.gmall.manager.front.b2bcomm.constants.ButtonNames;

import java.util.HashMap;
import java.util.Map;

public class PurchaseRequestUtils {

    public static Map<String , Boolean> buttons(PurchaseRequestDTO dto){
        Map<String , Boolean> buttons = new HashMap<>();
        Integer status = dto.getStatus();
        if(status.equals(PurchaseRequestStatus.Approved.getCode())){
            buttons.put(ButtonNames.prRelate , true);
        }
        if(status.equals(PurchaseRequestStatus.Draft.getCode())){
            buttons.put(ButtonNames.prEdit , true);
            buttons.put(ButtonNames.prDelete , true);
        }
        if(status.equals(PurchaseRequestStatus.Approving.getCode())){
            buttons.put(ButtonNames.prCancel , true);
        }
        if(status.equals(PurchaseRequestStatus.Rejected.getCode())){
            buttons.put(ButtonNames.prEdit , true);
            buttons.put(ButtonNames.prDelete , true);
        }
        if(status.equals(PurchaseRequestStatus.Processed.getCode())){
            buttons.put(ButtonNames.prEdit , true);
            buttons.put(ButtonNames.prDelete , true);
            buttons.put(ButtonNames.prRelate , true);
        }
        return buttons;
    }

}
