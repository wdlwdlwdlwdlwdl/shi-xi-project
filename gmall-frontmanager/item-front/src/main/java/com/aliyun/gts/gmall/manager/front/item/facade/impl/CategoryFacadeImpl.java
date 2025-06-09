package com.aliyun.gts.gmall.manager.front.item.facade.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSON;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.manager.front.common.util.GmallBeanUtils;
import com.aliyun.gts.gmall.manager.front.item.adaptor.CategoryAdaptor;
import com.aliyun.gts.gmall.manager.front.item.adaptor.CategoryPropGroupAdaptor;
import com.aliyun.gts.gmall.manager.front.item.convertor.CategoryConvertor;
import com.aliyun.gts.gmall.manager.front.item.convertor.CategoryPropGroupConvertor;
import com.aliyun.gts.gmall.manager.front.item.dto.input.query.CategoryAllByParamV2Query;
import com.aliyun.gts.gmall.manager.front.item.dto.input.query.CategoryRestQuery;
import com.aliyun.gts.gmall.manager.front.item.dto.output.CategoryAndPropVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.CategoryTreeVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.ItemCatPropValueVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.sku.PropValueVO;
import com.aliyun.gts.gmall.manager.front.item.facade.CategoryFacade;
import com.aliyun.gts.gmall.platform.item.api.dto.input.category.CategoryQueryByIdsReq;
import com.aliyun.gts.gmall.platform.item.api.dto.output.category.CategoryPropGroupDTO;
import com.aliyun.gts.gmall.platform.item.api.dto.output.category.CategoryTreeDTO;
import com.aliyun.gts.gmall.platform.item.api.dto.output.property.category.CatPropValueDTO;
import com.aliyun.gts.gmall.platform.item.api.dto.output.property.category.CategoryPropertyDTO;
import com.aliyun.gts.gmall.platform.item.api.facade.category.CategoryReadFacade;
import com.aliyun.gts.gmall.platform.item.common.enums.PropertyFillType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CategoryFacadeImpl implements CategoryFacade, GmallBeanUtils {
    @Resource
    private CategoryAdaptor categoryAdaptor;

    @Override
    public List<CategoryTreeVO> queryAllByParam(CategoryAllByParamV2Query req) {
        return categoryAdaptor.queryAllByParam(req);
    }

    @Autowired
    private CategoryReadFacade categoryReadFacade;

    @Autowired
    private CategoryPropGroupAdaptor categoryPropGroupAdaptor;

    @Autowired
    private CategoryPropGroupConvertor categoryPropGroupConvertor;

    @Autowired
    private CategoryConvertor categoryConvertor;

    /**
     * 获取类目树(倒查),属性数据(类目属性,属性值)
     * 
     * @param query
     * @return
     */
    @Override
    public CategoryAndPropVO queryCategoryList(CategoryRestQuery query) {
        CategoryAndPropVO categoryAndPropVo = new CategoryAndPropVO();
        List<Long> categoryIds = query.getCategoryIds();
        if (CollectionUtils.isEmpty(categoryIds)) {
            return categoryAndPropVo;
        }
        categoryAndPropVo.setCategoryTreeList(getCategoryTreeVoList(categoryIds));
        categoryAndPropVo.setCatPropValueList(getItemCatPropValueVoList(categoryIds));
        return categoryAndPropVo;
    }

    private List<CategoryTreeVO> getCategoryTreeVoList(List<Long> categoryIds) {
        CategoryQueryByIdsReq categoryReq = new CategoryQueryByIdsReq();
        categoryReq.setIds(categoryIds);
        RpcResponse<List<CategoryTreeDTO>> categoryTree = categoryReadFacade.queryCategoryParamIds(categoryReq);
        List<CategoryTreeVO> categoryTreeVos = categoryConvertor.toCategoryTreeVos(categoryTree.getData());
        return categoryTreeVos;
    }

    private List<ItemCatPropValueVO> getItemCatPropValueVoList(List<Long> categoryIds) {
        List<ItemCatPropValueVO> list = new ArrayList<>();

        for (Long categoryId : categoryIds) {
            // 获取分组
            List<CategoryPropGroupDTO> categoryPropGroupDtoList = categoryPropGroupAdaptor.queryCategoryByIds(categoryId);
            log.info("[查询类目信息]cateoryId={}, 分组={}", categoryId,
                    categoryPropGroupDtoList.stream().map(n -> n.getId().toString()).collect(Collectors.joining(",", "[", "]")));
            if (CollectionUtils.isEmpty(categoryPropGroupDtoList)) {
                continue;
            }

            // 查询单选和多选的属性
            List<CategoryPropertyDTO> categoryPropertyDtoList = categoryPropGroupDtoList.parallelStream()
                    .filter(dto -> CollectionUtils.isNotEmpty(dto.getCategoryPropList())).flatMap(dto -> dto.getCategoryPropList().parallelStream())
                    .filter(prop -> PropertyFillType.SINGLE_SELECT.getCode() == prop.getFillType()
                            || PropertyFillType.MULTI_SELECT.getCode() == prop.getFillType())
                    .collect(Collectors.toList());
            List<Long> prodIds = categoryPropertyDtoList.stream().map(CategoryPropertyDTO::getPropId).collect(Collectors.toList());
            log.info("[查询类目信息]单选和多选的属性有={}", JSON.toJSONString(prodIds));
            if (CollectionUtils.isEmpty(categoryPropertyDtoList)) {
                continue;
            }

            // 查询类目下所有属性的属性值
            List<CatPropValueDTO> catPropValueDtoList = categoryPropGroupAdaptor.queryPropValuesByCategoryId(categoryId, prodIds);
            log.info("[查询类目信息]查询类目属性值有={}", catPropValueDtoList.stream().map(n -> n.getId().toString()).collect(Collectors.joining(",", "[", "]")));
            if (CollectionUtils.isEmpty(catPropValueDtoList)) {
                continue;
            }

            Map<Long, List<CatPropValueDTO>> catPropValueDtoMap = catPropValueDtoList.stream().collect(Collectors.groupingBy(CatPropValueDTO::getPropId));
            categoryPropertyDtoList.forEach(it -> {
                ItemCatPropValueVO itemCatPropValueVo = categoryPropGroupConvertor.toItemCatPropValueVo(it);
                if (catPropValueDtoMap.containsKey(it.getPropId())) {
                    List<PropValueVO> propValueVoList = categoryPropGroupConvertor.toPropValueVos(catPropValueDtoMap.get(it.getPropId()));
                    itemCatPropValueVo.setPropValueList(propValueVoList);
                    list.add(itemCatPropValueVo);
                }
            });
        }
        return list;
    }
}
