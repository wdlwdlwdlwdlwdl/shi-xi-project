package com.aliyun.gts.gmall.manager.front.sourcing.convert;

import com.aliyun.gts.gcai.platform.sourcing.api.dto.model.SourcingApplyDTO;
import com.aliyun.gts.gmall.manager.front.sourcing.vo.SourcingApplyVo;
import org.mapstruct.Mapper;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/5/21 19:21
 */
@Mapper(componentModel = "spring")
public interface SourcingApplyConvert {
    /**
     *
     * @param dto
     * @return
     */
    SourcingApplyVo dto2Vo(SourcingApplyDTO dto);

    /**
     *
     * @param
     * @return
     */
    SourcingApplyDTO vo2DTO(SourcingApplyVo vo);
}
