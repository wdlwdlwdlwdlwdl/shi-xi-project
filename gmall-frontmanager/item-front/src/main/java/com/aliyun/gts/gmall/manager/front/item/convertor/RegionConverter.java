package com.aliyun.gts.gmall.manager.front.item.convertor;

import java.util.List;

import com.aliyun.gts.gmall.center.misc.api.dto.output.region.RegionInfo;
import com.aliyun.gts.gmall.manager.front.item.dto.output.RegionVO;
import org.mapstruct.Mapper;

/**
 * @author gshine
 * @since 2/24/21 11:14 AM
 */
@Mapper(componentModel = "spring")
public interface RegionConverter {

    RegionVO toRegionVO(RegionInfo regionInfo);

    List<RegionVO> toRegionVOList(List<RegionInfo> infoList);
}
