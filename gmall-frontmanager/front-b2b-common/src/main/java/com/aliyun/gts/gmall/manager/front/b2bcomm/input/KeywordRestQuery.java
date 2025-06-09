package com.aliyun.gts.gmall.manager.front.b2bcomm.input;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author GTS
 * @date 2021/04/20
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KeywordRestQuery extends CommonReq {
    /**
     * 关键词
     */
    private String keyword;
}