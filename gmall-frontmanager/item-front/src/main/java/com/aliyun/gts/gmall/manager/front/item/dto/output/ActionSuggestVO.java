package com.aliyun.gts.gmall.manager.front.item.dto.output;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @Title: ActionSuggestVO.java
 * @Description: TODO(用一句话描述该文件做什么)
 * @author zhao.qi
 * @date 2025年3月20日 16:58:47
 * @version V1.0
 */
@Getter
@Setter
public class ActionSuggestVO {
    private List<List<String>> keywords;
}
