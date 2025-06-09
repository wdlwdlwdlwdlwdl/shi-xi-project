package com.aliyun.gts.gmall.manager.front.item.convertor;

import com.aliyun.gts.gmall.center.item.api.dto.output.PointItemQueryDTO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.ItemDetailVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.PointItemDetailVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.PointItemQueryVO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PointItemConvertor {

    PointItemQueryVO convertVO(PointItemQueryDTO pointItemQueryDTO);

    PointItemDetailVO convertPointItemDetailVO(ItemDetailVO itemDetailVO);


}