package com.aliyun.gts.gmall.manager.front.customer.dto.output;

import com.aliyun.gts.gmall.platform.gim.api.dto.output.NoticeDTO;
import lombok.Data;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/8/9 10:41
 */
@Data
public class ImNoticeVo extends NoticeDTO {
    /**
     * 发送者名称
     */
    private String senderName;
}
