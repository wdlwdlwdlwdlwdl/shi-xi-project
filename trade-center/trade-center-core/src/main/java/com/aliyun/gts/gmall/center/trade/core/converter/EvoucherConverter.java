package com.aliyun.gts.gmall.center.trade.core.converter;

import com.aliyun.gts.gmall.center.item.api.dto.output.EvoucherPeriodDTO;
import com.aliyun.gts.gmall.center.trade.api.dto.output.EvoucherDTO;
import com.aliyun.gts.gmall.center.trade.domain.dataobject.evoucher.EvoucherFeatureDO;
import com.aliyun.gts.gmall.center.trade.domain.dataobject.evoucher.TcEvoucherDO;
import com.aliyun.gts.gmall.center.trade.domain.entity.evoucher.EvoucherInstance;
import com.aliyun.gts.gmall.center.trade.domain.entity.evoucher.EvoucherTemplate;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EvoucherConverter {

    EvoucherTemplate toEvTemplate(EvoucherPeriodDTO dto);

    List<EvoucherDTO> toDtoList(List<EvoucherInstance> list);

    default EvoucherDTO toDto(EvoucherInstance inst) {
        if (inst == null) {
            return null;
        }
        EvoucherDTO dto = new EvoucherDTO();
        toDTO(dto, inst);
        toDTO(dto, inst.getFeaturesDO());
        return dto;
    }

    void toDTO(@MappingTarget EvoucherDTO dto, EvoucherInstance inst);

    void toDTO(@MappingTarget EvoucherDTO dto, EvoucherFeatureDO features);

    List<EvoucherInstance> toEntityList(List<TcEvoucherDO> list);
}
