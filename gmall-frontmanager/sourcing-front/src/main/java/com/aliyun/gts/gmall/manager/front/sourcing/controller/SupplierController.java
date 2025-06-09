package com.aliyun.gts.gmall.manager.front.sourcing.controller;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.manager.front.b2bcomm.controller.BaseRest;
import com.aliyun.gts.gmall.manager.front.customer.adaptor.SellerOperatorAdapter;
import com.aliyun.gts.gmall.manager.front.sourcing.input.SupplierInfoQuery;
import com.aliyun.gts.gmall.manager.front.sourcing.vo.SupplierInfoVO;
import com.aliyun.gts.gmall.manager.utils.ResponseUtils;
import com.aliyun.gts.gmall.platform.user.api.dto.contants.SellerAuditStatusEnum;
import com.aliyun.gts.gmall.platform.user.api.dto.input.SellerQuery;
import com.aliyun.gts.gmall.platform.user.api.dto.output.SellerDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping(value = "/supplier/")
@Slf4j
public class SupplierController extends BaseRest {

    @Autowired
    private SellerOperatorAdapter sellerOperatorAdapter;

    @RequestMapping(value = "list", method = RequestMethod.POST)
    public RestResponse<PageInfo<SupplierInfoVO>> list(@RequestBody @Validated SupplierInfoQuery request) {
        SellerQuery q = new SellerQuery();
        q.setUsername(request.getName());
        q.setAuditStatus(SellerAuditStatusEnum.AUDIT_PASSED.getCode());
        q.setPage(request.getPage());
        PageInfo<SellerDTO> page = sellerOperatorAdapter.pageQuery(q);
        return RestResponse.okWithoutMsg(ResponseUtils.convertVOPage(page, this::convert));
    }

    private SupplierInfoVO convert(SellerDTO slr) {
        SupplierInfoVO vo = new SupplierInfoVO();
        vo.setId(slr.getId());
        vo.setName(slr.getUsername());
        vo.setMailUrl(slr.getEmail());
        vo.setPhoneNum(slr.getPhone());
        vo.setTelephoneNum(slr.getPhone());
        vo.setGmtCreate(slr.getGmtCreate());
        vo.setGmtModified(slr.getGmtModified());
        return vo;
    }
}
