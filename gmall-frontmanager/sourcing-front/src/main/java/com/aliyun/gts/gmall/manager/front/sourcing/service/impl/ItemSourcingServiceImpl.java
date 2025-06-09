package com.aliyun.gts.gmall.manager.front.sourcing.service.impl;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gcai.platform.sourcing.api.dto.model.SourcingApplyDTO;
import com.aliyun.gts.gcai.platform.sourcing.common.model.inner.Contact;
import com.aliyun.gts.gcai.platform.sourcing.common.type.ApplyType;
import com.aliyun.gts.gmall.manager.biz.output.CustDTO;
import com.aliyun.gts.gmall.manager.front.b2bcomm.output.CategoryVO;
import com.aliyun.gts.gmall.manager.front.common.util.UserHolder;
import com.aliyun.gts.gmall.manager.front.customer.adaptor.AddressAdapter;
import com.aliyun.gts.gmall.manager.front.customer.adaptor.SellerOperatorAdapter;
import com.aliyun.gts.gmall.manager.front.item.adaptor.ItemAdaptor;
import com.aliyun.gts.gmall.manager.front.item.dto.output.ItemCategoryVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.ItemDetailVO;
import com.aliyun.gts.gmall.manager.front.sourcing.service.ItemSourcingService;
import com.aliyun.gts.gmall.manager.front.sourcing.vo.SourcingMaterialVo;
import com.aliyun.gts.gmall.manager.front.sourcing.vo.SourcingVo;
import com.aliyun.gts.gmall.platform.user.api.dto.output.CustomerAddressDTO;
import com.aliyun.gts.gmall.platform.user.api.dto.output.SellerDTO;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemSourcingServiceImpl implements ItemSourcingService {

    @Autowired
    private ItemAdaptor itemAdaptor;
    @Autowired
    private AddressAdapter addressAdapter;
    @Autowired
    private SellerOperatorAdapter sellerOperatorAdapter;

    @Override
    public SourcingVo fromItem(Long itemId) {
        ItemDetailVO detail = itemAdaptor.getItemDetail(itemId, true);
        CustomerAddressDTO addr = queryDefaultAddr();

        SourcingVo vo = new SourcingVo();
        vo.setApplyType(ApplyType.BOOK.getValue());
        if (addr != null) {
            Contact c = new Contact();
            c.setContactName(addr.getName());
            c.setCellphone(addr.getPhone());
            c.setTelephone(addr.getTelPhone());
            c.setAddress(addr.getCompleteAddr());
            vo.setContact(c);
        }
        SourcingMaterialVo material = new SourcingMaterialVo();
        material.setCode("ITEM_" + itemId);
        material.setName(detail.getItemBaseVO().getTitle());
        if (CollectionUtils.isNotEmpty(detail.getItemCategoryVOList())) {
            List<CategoryVO> list = detail.getItemCategoryVOList().stream().map(this::convert).collect(Collectors.toList());
            material.setCategoryList(list);
        }
        material.setCategoryId(detail.getItemBaseVO().getCategoryId());
        material.setBrandName(detail.getItemBaseVO().getBrandName());
        material.setModel(getModel(detail));
        material.setNum(1);
        material.setUnit(I18NMessageUtils.getMessage("pieces"));  //# "件"
        material.setDescription(I18NMessageUtils.getMessage("product")+"-" + itemId);  //# "商品
        vo.setMaterials(Lists.newArrayList(material));
        vo.setTitle(I18NMessageUtils.getMessage("product.inquiry")+"-" + itemId);  //# "商品询价

        // 邀约卖家
        SellerDTO seller = sellerOperatorAdapter.querySellerById(detail.getSellerId());
        SourcingApplyDTO apply = convert(seller);
        if (apply != null) {
            vo.setApplySupplier(Lists.newArrayList(apply));
        }
        return vo;
    }

    private SourcingApplyDTO convert(SellerDTO seller) {
        if (seller == null) {
            return null;
        }
        SourcingApplyDTO apply = new SourcingApplyDTO();
        apply.setSupplierId(seller.getId());
        apply.setSupplierName(seller.getUsername());
        return apply;
    }

    // 型号
    private String getModel(ItemDetailVO detail) {
        if (CollectionUtils.isEmpty(detail.getCatPropInfo())) {
            return null;
        }
        String KW = I18NMessageUtils.getMessage("model");  //# "型号"
        return detail.getCatPropInfo().stream().filter(p -> KW.equals(p.getPropName()))
                .map(p -> StringUtils.join(p.getPropValue(), ','))
                .findFirst().orElse(null);
    }

    private CategoryVO convert(ItemCategoryVO s) {
        CategoryVO t = new CategoryVO();
        BeanUtils.copyProperties(s, t);
        return t;
    }

    private CustomerAddressDTO queryDefaultAddr() {
        CustDTO user = UserHolder.getUser();
        if (user == null) {
            return null;
        }
        List<CustomerAddressDTO> list = addressAdapter.queryList(user.getCustId());
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.stream().filter(a -> Boolean.TRUE.equals(a.getDefaultYn()))
                .findFirst().orElse(null);
    }
}
