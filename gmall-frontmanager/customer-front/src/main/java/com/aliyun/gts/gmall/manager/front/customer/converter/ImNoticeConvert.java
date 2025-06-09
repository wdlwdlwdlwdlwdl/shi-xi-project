package com.aliyun.gts.gmall.manager.front.customer.converter;

import com.aliyun.gts.gmall.manager.front.customer.dto.input.query.CusNoticeQuery;
import com.aliyun.gts.gmall.manager.front.customer.dto.output.ImNoticeVo;
import com.aliyun.gts.gmall.platform.gim.api.dto.output.NoticeDTO;
import com.aliyun.gts.gmall.platform.gim.common.query.NoticeQuery;
import org.mapstruct.Mapper;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/8/9 10:44
 */
@Mapper(componentModel = "spring")
public interface ImNoticeConvert {

    ImNoticeVo dto2vo(NoticeDTO dto);

    NoticeQuery convert(CusNoticeQuery cusNoticeQuery);
}
