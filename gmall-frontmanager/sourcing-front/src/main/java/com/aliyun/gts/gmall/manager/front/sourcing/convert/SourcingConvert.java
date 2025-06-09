package com.aliyun.gts.gmall.manager.front.sourcing.convert;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.alibaba.fastjson.JSON;
import com.aliyun.gts.gcai.platform.sourcing.api.dto.model.SourcingDTO;
import com.aliyun.gts.gcai.platform.sourcing.api.dto.model.SourcingMaterialDTO;
import com.aliyun.gts.gcai.platform.sourcing.common.model.inner.SourcingFeature;
import com.aliyun.gts.gcai.platform.sourcing.common.type.SourcingType;
import com.aliyun.gts.gmall.manager.front.sourcing.constants.SourcingConstants;
import com.aliyun.gts.gmall.manager.front.sourcing.vo.SourcingHeadVO;
import com.aliyun.gts.gmall.manager.front.sourcing.vo.SourcingMaterialVo;
import com.aliyun.gts.gmall.manager.front.sourcing.vo.SourcingVo;
import org.mapstruct.Mapper;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/5/14 12:53
 */
@Mapper(componentModel = "spring")
public abstract class SourcingConvert {
    /**
     * @param dto
     * @return
     */
    public abstract SourcingVo dto2Vo(SourcingDTO dto);

    public abstract List<SourcingMaterialVo> convertMaterials(List<SourcingMaterialDTO> materials);

    /**
     *
     * @param dto
     * @return
     */
    public abstract SourcingDTO vo2DTO(SourcingVo dto);

    public abstract SourcingMaterialDTO sourcingMaterial2DTO(SourcingMaterialVo vo);

    public List<SourcingMaterialDTO> sourcingMaterial2DTOList(List<SourcingMaterialVo> vos) {
        if (vos == null) {
            return null;
        }
        List<SourcingMaterialDTO> list = new ArrayList<>();
        for (SourcingMaterialVo vo : vos) {
            SourcingMaterialDTO materialDTO = sourcingMaterial2DTO(vo);
            if (!CollectionUtils.isEmpty(vo.getCategoryList())) {
                List<Long> ids = vo.getCategoryList().stream().map(m -> m.getId()).collect(Collectors.toList());
                materialDTO.setCategoryIds(ids);
                materialDTO.setCategoryId(ids.get(ids.size() - 1));
                Map<String, String> extra = materialDTO.getExtra();
                if (extra == null) {
                    extra = new HashMap<>();
                    materialDTO.setExtra(extra);
                }
                extra.put(SourcingConstants.MATERIAL_EXT_KEY_CATEGORY, JSON.toJSONString(vo.getCategoryList()));
            }
            list.add(materialDTO);
        }
        return list;
    }

    protected abstract SourcingHeadVO baseConvert2Head(SourcingDTO sourcingDTO);

    public SourcingHeadVO convert2Head(SourcingDTO sourcingDTO) {
        SourcingHeadVO headVO = baseConvert2Head(sourcingDTO);
        if (sourcingDTO.getSourcingType().equals(SourcingType.Zhao.getValue())) {
            SourcingFeature feature = sourcingDTO.getFeature();
            headVO.addTimes(I18NMessageUtils.getMessage("bid.time"), sourcingDTO.getStartTime(), sourcingDTO.getEndTime());  //# "投标时间"
            headVO.addTimes(I18NMessageUtils.getMessage("bid.open.time"), feature.getOpenBidStart(), feature.getOpenBidEnd());  //# "开标时间"
            headVO.addTimes(I18NMessageUtils.getMessage("bid.evaluation.time"), feature.getChooseBidStart(), feature.getChooseBidEnd());  //# "评标时间"
        }
        return headVO;
    }

}
