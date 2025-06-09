package com.aliyun.gts.gmall.manager.biz.converter;


import com.aliyun.gts.gmall.center.misc.api.dto.output.dict.DictDTO;
import com.aliyun.gts.gmall.manager.biz.output.DictVO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MiscConverter {

    DictVO toDictVO(DictDTO source);
}
