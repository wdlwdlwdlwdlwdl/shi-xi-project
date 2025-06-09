package com.aliyun.gts.gmall.manager.front.sourcing.service.impl;

import com.alibaba.fastjson.JSON;
import com.aliyun.gts.gmall.center.user.api.dto.constants.SellerBusinessLicenseFeature;
import com.aliyun.gts.gmall.manager.front.customer.adaptor.SellerOperatorAdapter;
import com.aliyun.gts.gmall.manager.front.sourcing.service.SupplierService;
import com.aliyun.gts.gmall.manager.front.sourcing.vo.SupplierCompanyVO;
import com.aliyun.gts.gmall.manager.front.sourcing.vo.SupplierVo;
import com.aliyun.gts.gmall.platform.user.api.dto.output.SellerBusinessLicenseDTO;
import com.aliyun.gts.gmall.platform.user.api.dto.output.SellerDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/5/30 16:25
 */
@Service
public class SupplierServiceImpl implements SupplierService {

    @Autowired
    private SellerOperatorAdapter sellerOperatorAdapter;

    @Override
    public Map<Long, SupplierVo> query(List<Long> ids) {
        Map<Long, SupplierVo> map = new HashMap<>();
        if (CollectionUtils.isEmpty(ids)) {
            return map;
        }
        for (Long id : ids) {
            map.put(id, queryById(id));
        }
        return map;
    }

    @Override
    public SupplierVo queryById(Long id) {
        SellerDTO seller = sellerOperatorAdapter.querySellerById(id);
        return convert(seller);
    }

    @Override
    public Map<Long, SupplierVo> queryWithDetail(List<Long> ids) {
        return query(ids);
    }

    private SupplierVo convert(SellerDTO seller) {
        if (seller == null) {
            return null;
        }
        SupplierVo supplier = new SupplierVo();
        supplier.setCellPhone(seller.getPhone());
        supplier.setTelephoneNum(seller.getTel());
        supplier.setSupplierName(seller.getUsername());
        supplier.setEmail(seller.getEmail());
        supplier.setId(seller.getId());

        //公司信息
        SupplierCompanyVO company = new SupplierCompanyVO();
        company.setName(seller.getCompanyName());
        company.setAddress(seller.getCompanyAddress());
        String fundsYuan = Optional.ofNullable(seller.getSellerBusinessLicenseDTO())
                .map(SellerBusinessLicenseDTO::getFeatures)
                .filter(StringUtils::isNotBlank)
                .map(x -> JSON.parseObject(x, SellerBusinessLicenseFeature.class))
                .map(SellerBusinessLicenseFeature::getFundsYuan)
                .orElse(null);
        company.setRegCaptial(fundsYuan);
        supplier.setCompany(company);

        return supplier;

    }

}
