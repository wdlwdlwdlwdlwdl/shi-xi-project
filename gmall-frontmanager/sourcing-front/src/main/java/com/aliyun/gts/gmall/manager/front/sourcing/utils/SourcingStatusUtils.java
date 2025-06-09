package com.aliyun.gts.gmall.manager.front.sourcing.utils;

import com.aliyun.gts.gcai.platform.sourcing.common.type.ApproveStatus;
import com.aliyun.gts.gcai.platform.sourcing.common.type.SourcingStatus;

public class SourcingStatusUtils {

    public static String getSourcingStatusName(Integer approveStatus , Integer status){
        if(approveStatus.equals(ApproveStatus.PASS_APPROVED)){
            return SourcingStatus.valueOf(status).getDesc();
        }else{
            return ApproveStatus.valueOf(approveStatus).getDesc();
        }
    }
}
