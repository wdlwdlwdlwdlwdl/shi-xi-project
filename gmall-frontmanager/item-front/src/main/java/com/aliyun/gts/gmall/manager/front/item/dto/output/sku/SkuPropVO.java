package com.aliyun.gts.gmall.manager.front.item.dto.output.sku;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import com.aliyun.gts.gmall.framework.i18n.MultiLangText;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 
* @Title: SkuPropVO.java 
* @Description: TODO(用一句话描述该文件做什么) 
* @author zhao.qi
* @date 2025年3月9日 13:09:37 
* @version V1.0
 */
@Getter
@Setter
public class SkuPropVO extends AbstractOutputInfo {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty("SKU的属性项ID")
    private Long pid;
    @ApiModelProperty("SKU的属性值ID")
    private String vid;
    @ApiModelProperty("属性名称")
    private MultiLangText name;
    @ApiModelProperty("属性值名称")
    private MultiLangText value;
    @ApiModelProperty("属性值图片")
    private String picUrl;
    @ApiModelProperty("属性值颜色")
    private String color;
}
