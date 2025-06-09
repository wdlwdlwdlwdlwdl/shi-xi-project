package com.aliyun.gts.gmall.center.trade.persistence.rpc.converter;

import com.aliyun.gts.gmall.center.trade.domain.dataobject.point.AcBookOrderRecordDO;
import com.aliyun.gts.gmall.center.trade.domain.dataobject.point.AcBookRecordDO;
import com.aliyun.gts.gmall.center.trade.domain.dataobject.point.AcBookRecordParam;
import com.aliyun.gts.gmall.platform.item.common.utils.FeaturesUtil;
import com.aliyun.gts.gmall.platform.promotion.api.dto.account.AcBookOrderRecordDTO;
import com.aliyun.gts.gmall.platform.promotion.api.dto.account.AcBookRecordDTO;
import com.aliyun.gts.gmall.platform.promotion.common.query.account.AcBookOrderRecordQuery;
import com.aliyun.gts.gmall.platform.promotion.common.query.account.AcBookRecordQuery;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * @author gshine
 * @since 3/26/21 5:19 PM
 */
@Mapper(componentModel = "spring", imports = {FeaturesUtil.class})
public interface PointGrantRpcConverter {

    AcBookRecordQuery toAcBookRecorQuery(AcBookRecordParam acBookRecordParam);

    AcBookOrderRecordQuery toAcBookOrderRecorQuery(AcBookRecordParam acBookRecordParam);
    
    AcBookRecordDO toAcBookRecordDO(AcBookRecordDTO acBookRecordDTO);

    AcBookOrderRecordDO toAcBookOrderRecordDO(AcBookOrderRecordDTO acBookOrderRecordDTO);
}
