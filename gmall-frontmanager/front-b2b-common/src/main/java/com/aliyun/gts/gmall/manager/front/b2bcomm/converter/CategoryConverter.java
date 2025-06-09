package com.aliyun.gts.gmall.manager.front.b2bcomm.converter;

import com.aliyun.gts.gmall.framework.config.I18NConfig;
import com.aliyun.gts.gmall.framework.i18n.MultiLangText;
import com.aliyun.gts.gmall.manager.front.b2bcomm.output.CategoryNodeVO;
import com.aliyun.gts.gmall.manager.front.b2bcomm.output.CategoryVO;
import com.aliyun.gts.gmall.platform.item.api.dto.output.category.CategoryDTO;
import com.aliyun.gts.gmall.platform.item.api.dto.output.category.CategoryNodeDTO;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.context.i18n.LocaleContextHolder;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author gshine
 * @since 2/24/21 7:51 PM
 */
@Mapper(componentModel = "spring", imports = {DateFormatUtils.class})
public abstract class CategoryConverter implements BaseDateFormatterConverter {

    @Resource
    protected I18NConfig i18NConfig;

    public abstract List<CategoryVO> toVOList(List<CategoryDTO> list);

    public abstract CategoryVO toVO(CategoryDTO dto);

    public abstract List<CategoryNodeVO> toNodeList(List<CategoryNodeDTO> list);

    public abstract CategoryNodeVO toNode(CategoryNodeDTO dto);

    //String <---> MultiLangText
    protected String common_map_to_str(MultiLangText mText) {
        if (mText == null) {
            return null;
        }
        String fallback = i18NConfig.getDefaultLang();
        return mText.getValueByLang(LocaleContextHolder.getLocale().getLanguage(), fallback);
    }
}
