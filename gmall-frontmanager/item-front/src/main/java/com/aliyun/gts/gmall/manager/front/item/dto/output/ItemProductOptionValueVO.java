package com.aliyun.gts.gmall.manager.front.item.dto.output;

import java.util.List;
import com.aliyun.gts.gmall.framework.api.dto.Response;
import com.aliyun.gts.gmall.framework.i18n.MultiLangText;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @Title: ItemProductOptionValueVO.java
 * @Description: TODO(用一句话描述该文件做什么)
 * @author zhao.qi
 * @date 2025年3月9日 13:06:26
 * @version V1.0
 */
@Getter
@Setter
public class ItemProductOptionValueVO implements Response {
    private static final long serialVersionUID = -1172006545089569847L;
    @ApiModelProperty(value = "属性值")
    private MultiLangText value;
    @ApiModelProperty(value = "属性主键")
    private String vid;
    @ApiModelProperty(value = "属性值")
    private MultiLangText name;
    @ApiModelProperty(value = "图片地址")
    private List<String> picUrl;
    @ApiModelProperty(value = "颜色")
    private String color;

}
