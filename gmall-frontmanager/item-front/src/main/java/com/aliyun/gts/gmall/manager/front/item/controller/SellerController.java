package com.aliyun.gts.gmall.manager.front.item.controller;


import com.alibaba.fastjson.JSON;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.manager.front.item.dto.input.query.SellerDetailQuery;
import com.aliyun.gts.gmall.manager.front.item.dto.input.query.SellerWarehouseQuery;
import com.aliyun.gts.gmall.manager.front.item.dto.output.SellerDetailInfoVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.SellerWarehouseVO;
import com.aliyun.gts.gmall.manager.front.item.facade.SellerFacade;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 商家服务
 */
@Api(value = "商家服务", tags = {"item"})
@Slf4j
@RestController
@RequestMapping("/api/seller")
public class SellerController {

    @Autowired
    private SellerFacade sellerFacade;


    /**
     * 查询商家详情
     * @return
     */
    @PostMapping("/querySellerDetail")
    public RestResponse<SellerDetailInfoVO> querySellerDetail(@RequestBody SellerDetailQuery query){
        log.info("SellerController_querySellerDetail:query:{}", JSON.toJSONString(query));
        SellerDetailInfoVO infoVo = sellerFacade.querySellerDetail(query);
        log.info("SellerController_querySellerDetail:infoVo:{}", JSON.toJSONString(infoVo));
        return RestResponse.okWithoutMsg(infoVo);
    }

    /**
     * 查询商家仓库
     * @return
     */
    @PostMapping("/querySellerWarehouse")
    public RestResponse<List<SellerWarehouseVO>> querySellerWarehouse(@RequestBody SellerWarehouseQuery query){
        log.info("SellerController_querySellerWarehouse:query:{}", JSON.toJSONString(query));
        List<SellerWarehouseVO> warehouseVOList = sellerFacade.querySellerWarehouse(query);
        log.info("SellerController_querySellerWarehouse:warehouseVOList:{}", JSON.toJSONString(warehouseVOList));
        return RestResponse.okWithoutMsg(warehouseVOList);
    }
}
