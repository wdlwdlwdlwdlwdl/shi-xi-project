package com.aliyun.gts.gmall.manager.front.item.dto.output;

import java.util.List;
import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import lombok.Getter;
import lombok.Setter;


/**
 * 
 * @Title: CategoryTreeVO.java
 * @Description: TODO(用一句话描述该文件做什么)
 * @author zhao.qi
 * @date 2025年2月24日 14:25:32
 * @version V1.0
 */
@Getter
@Setter
public class CategoryTreeVO extends AbstractOutputInfo {
    private static final long serialVersionUID = 1L;
    /** 分类id */
    private Long id;
    
    /** 计数 */
    private Integer count;
    
    /** 编码 */
    private String code;
    
    /** 父分类id，第一层为0 */
    private String parentId;
    
    /** 是否叶子类目 */
    private Boolean leafYn;
    
    /** 类目名称 */
    private String name;
    
    /** 图标地址 */
    private String iconUrl;
    
    /** 图片地址 */
    private String imageUrl;
    
    /** 源地址 */
    private String originalUrl;
    
    /** 子类目 */
    private List<CategoryTreeVO> subCategories;

    /** 类目城市名称 */
    private String title;
}
