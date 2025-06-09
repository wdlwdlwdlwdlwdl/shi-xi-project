package com.aliyun.gts.gmall.manager.front.item.facade;

import java.util.List;

import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.manager.front.item.dto.output.RegionVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.SimpleRegionVO;

/**
 * @author gshine
 * @since 2/24/21 11:03 AM
 */
public interface CommonRegionFacade {

    RestResponse<List<RegionVO>> queryRegionsByParentId(Long id);

}


