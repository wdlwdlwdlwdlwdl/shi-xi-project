package com.aliyun.gts.gmall.manager.front.promotion.common.converter;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.gts.gmall.center.promotion.api.dto.model.ShareFissionDTO;
import com.aliyun.gts.gmall.center.promotion.api.dto.model.ShareVoteDTO;
import com.aliyun.gts.gmall.manager.front.promotion.dto.output.ShareFissionVO;
import com.aliyun.gts.gmall.manager.front.promotion.dto.output.ShareVoteVO;
import org.apache.commons.collections4.CollectionUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ShareFissionConverter {
    @Mappings({
            @Mapping(target = "shareCnt",source = "totalCnt" ),
            @Mapping(target = "nickname",expression = "java(getNickName(dto))" ),
    })
    ShareFissionVO dto2VO(ShareFissionDTO dto);

    default String getNickName(ShareFissionDTO dto){
        if(dto.getFeature() != null){
            return dto.getFeature().getString("nickname");
        }
        return null;
    }

    default List<ShareVoteVO> dto2Vo(List<ShareVoteDTO> list) {
        List<ShareVoteVO> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(list)) {
            return result;
        }
        for (ShareVoteDTO dto : list) {
            ShareVoteVO vo = new ShareVoteVO();
            JSONObject feature = dto.getFeature();
            if (feature != null) {
                vo.setHeadUrl(feature.getString("headUrl"));
                vo.setNickname(feature.getString("nickname"));
            }
            result.add(vo);
        }
        return result;
    }
}
