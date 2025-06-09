package com.aliyun.gts.gmall.manager.front.b2bcomm.converter;

import com.aliyun.gts.gmall.center.misc.api.dto.input.dict.DictAddRpcReq;
import com.aliyun.gts.gmall.center.misc.api.dto.input.dict.DictPageQueryRpcReq;
import com.aliyun.gts.gmall.center.misc.api.dto.input.dict.DictUpdateRpcReq;
import com.aliyun.gts.gmall.center.misc.api.dto.output.dict.DictDTO;
import com.aliyun.gts.gmall.manager.front.b2bcomm.input.DictAddRestReq;
import com.aliyun.gts.gmall.manager.front.b2bcomm.input.DictPageQueryRestReq;
import com.aliyun.gts.gmall.manager.front.b2bcomm.input.DictUpdateRestReq;
import com.aliyun.gts.gmall.manager.front.b2bcomm.output.DictVO;
import org.mapstruct.Mapper;

/**
 * @author gshine
 * @since 3/8/21 11:07 AM
 */
@Mapper(componentModel = "spring")
public interface DictConverter {

    DictVO toDictVO(DictDTO dictDTO);

    DictAddRpcReq toDictAddRpcReq(DictAddRestReq req);

    DictUpdateRpcReq toDictUpdateRpcReq(DictUpdateRestReq req);

    DictPageQueryRpcReq toDictPageQueryRpcReq(DictPageQueryRestReq req);
}
