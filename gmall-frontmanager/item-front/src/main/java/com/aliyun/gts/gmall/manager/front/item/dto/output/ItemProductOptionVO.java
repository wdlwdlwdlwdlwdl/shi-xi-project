package com.aliyun.gts.gmall.manager.front.item.dto.output;

import java.util.List;
import com.aliyun.gts.gmall.framework.api.dto.Response;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 
* @Title: ItemProductOptionVO.java 
* @Description: TODO(用一句话描述该文件做什么) 
* @author zhao.qi
* @date 2025年3月9日 13:05:32 
* @version V1.0
 */
@Getter
@Setter
public class ItemProductOptionVO implements Response {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "名称")
    private String title;
    @ApiModelProperty(value = "类型")
    private String type;
    @ApiModelProperty(value = "属性值")
    private List<ItemProductOptionValueVO> list;
}
