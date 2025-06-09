package com.aliyun.gts.gmall.manager.front.sourcing.controller;

import com.aliyun.gts.gcai.platform.sourcing.api.dto.model.QuoteDTO;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.manager.front.b2bcomm.controller.BaseRest;
import com.aliyun.gts.gmall.manager.front.b2bcomm.utils.ConvertUtils;
import com.aliyun.gts.gmall.manager.front.sourcing.convert.QuoteConvert;
import com.aliyun.gts.gmall.manager.front.sourcing.convert.SourcingMaterialConvert;
import com.aliyun.gts.gmall.manager.front.sourcing.facade.QuotePriceFacade;
import com.aliyun.gts.gmall.manager.front.sourcing.facade.SourcingFacade;
import com.aliyun.gts.gmall.manager.front.sourcing.input.QuoteQueryReq;
import com.aliyun.gts.gmall.manager.front.sourcing.service.SupplierService;
import com.aliyun.gts.gmall.manager.front.sourcing.service.impl.SourcingCheckOwnerService;
import com.aliyun.gts.gmall.manager.front.sourcing.vo.QuoteVo;
import com.aliyun.gts.gmall.manager.front.sourcing.vo.SupplierVo;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/5/18 14:06
 */
@RequestMapping(value = "/quote")
@RestController
public class QuoteController extends BaseRest {

    @Resource
    private QuotePriceFacade quotePriceFacade;
    @Resource
    private QuoteConvert convert;
    @Resource
    private SourcingMaterialConvert materialConvert;
    @Resource
    private SourcingFacade sourcingFacade;
    @Resource
    private SupplierService supplierService;
    @Resource
    private SourcingCheckOwnerService sourcingCheckOwnerService;

    @RequestMapping(value = "/page")
    public RestResponse<PageInfo<QuoteVo>> page(@RequestBody QuoteQueryReq query) {
        PageInfo<QuoteDTO> info = quotePriceFacade.page(query.build());
        PageInfo<QuoteVo> pageInfo = ConvertUtils.convertPage(info, convert::dto2Vo);
        return RestResponse.okWithoutMsg(pageInfo);
    }

    @RequestMapping(value = "/detail")
    public RestResponse<Map<String,Object>> detail(@RequestBody QuoteQueryReq query) {
        //询价活动信息
        QuoteVo quoteVo = quotePriceFacade.queryForDetail(query.getSourcingId(), query.getSupplierId());
        sourcingCheckOwnerService.checkQuoteOwner(quoteVo);

        SupplierVo supplierVo = supplierService.queryById(quoteVo.getSupplierId());
        Map<String,Object> result = new HashMap<>();
        result.put("supplierInfo",supplierVo);
        result.put("quoteInfo",quoteVo);
        result.put("sourcingInfo",quoteVo.getSourcingInfo());
        return RestResponse.okWithoutMsg(result);
    }
}
