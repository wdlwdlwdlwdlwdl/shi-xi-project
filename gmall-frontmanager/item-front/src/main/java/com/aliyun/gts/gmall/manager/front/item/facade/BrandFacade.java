package com.aliyun.gts.gmall.manager.front.item.facade;

import com.aliyun.gts.gmall.manager.front.item.dto.input.query.BrandIdsQuery;
import com.aliyun.gts.gmall.manager.front.item.dto.output.BrandVO;

import java.util.List;

public interface BrandFacade {

    List<BrandVO> queryAllByParam(BrandIdsQuery req);
}
