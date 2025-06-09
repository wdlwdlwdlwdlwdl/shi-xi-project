package com.aliyun.gts.gmall.manager.front.b2bcomm.input;

import com.aliyun.gts.gmall.framework.api.dto.PageParam;
import lombok.Data;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/2/4 17:52
 */
@Data
public class CommonPageReq extends CommonReq {
    private PageParam page = new PageParam();
}
