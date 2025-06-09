package com.aliyun.gts.gmall.manager.front.b2bcomm.converter;

import com.aliyun.gts.gmall.center.misc.api.dto.output.region.RegionInfo;
import com.aliyun.gts.gmall.manager.front.b2bcomm.output.RegionVO;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @author gshine
 * @since 2/24/21 11:14 AM
 */
@Mapper(componentModel = "spring", implementationName = "b2bRegionConverter")
public interface RegionConverter {

    RegionVO toRegionVO(RegionInfo regionInfo);

    List<RegionVO> toRegionVOList(List<RegionInfo> infoList);

}
