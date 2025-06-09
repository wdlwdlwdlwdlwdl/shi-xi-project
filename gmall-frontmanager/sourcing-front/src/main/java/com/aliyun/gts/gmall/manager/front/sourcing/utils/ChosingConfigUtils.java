package com.aliyun.gts.gmall.manager.front.sourcing.utils;

import com.aliyun.gts.gcai.platform.sourcing.api.dto.model.BidChosingConfigDTO;
import com.aliyun.gts.gcai.platform.sourcing.common.model.UserDisplayDO;
import com.aliyun.gts.gcai.platform.sourcing.common.model.inner.ScoringGroup;
import com.aliyun.gts.gcai.platform.sourcing.common.type.ChosingConfigStatus;
import com.aliyun.gts.gcai.platform.sourcing.common.type.UserGroupType;
import com.aliyun.gts.gmall.manager.front.b2bcomm.constants.ButtonNames;
import com.aliyun.gts.gmall.manager.front.b2bcomm.model.OperatorDO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChosingConfigUtils {

    public static Map<String , Boolean> buttons(BidChosingConfigDTO configDTO , OperatorDO operatorDO){
        Map<String,Boolean> map = new HashMap<>();
        List<ScoringGroup> groups = configDTO.getChosingGroup();
        for(ScoringGroup group : groups){
            Integer type = group.getType();
            List<UserDisplayDO> userList = group.getPeopleList();
            if(userList.stream().anyMatch(u->u.getUserId().equals(operatorDO.getOperatorId()))){
                if(type.equals(UserGroupType.EXPERT.getCode())){
                    if(configDTO.getStatus().equals(ChosingConfigStatus.chosing.getValue())){
                        map.put(ButtonNames.tenderingChosing,true);
                    }else{
                        map.put(ButtonNames.chosingDetail,true);
                    }
                }
                if(type.equals(UserGroupType.HOST.getCode())){

                    if(configDTO.getStatus().equals(ChosingConfigStatus.chosing.getValue())){
                        map.put(ButtonNames.tenderingSummary,true);
                    }else{
                        map.put(ButtonNames.summaryDetail,true);
                    }
                }
                if(type.equals(UserGroupType.GUEST.getCode())){
                    map.put(ButtonNames.summaryDetail,true);
                }
                if(configDTO.getStatus() > ChosingConfigStatus.not_begin.getValue()){
                    map.put(ButtonNames.tenderingDetail,true);
                }
            }
        }
        return map;
    }

}
