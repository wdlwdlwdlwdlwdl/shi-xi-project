package com.aliyun.gts.gmall.manager.front.sourcing.controller;

import com.aliyun.gts.gcai.platform.sourcing.common.type.SourcingType;
import com.aliyun.gts.gmall.manager.front.sourcing.vo.SourcingVo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/5/13 16:12
 */
@RestController
@RequestMapping(value = "/inquiry")
public class InquiryController extends SourcingController {

    @Override
    public SourcingType getSourcingType() {
        return SourcingType.InquiryPrice;
    }

    @Override
    protected void initParam(SourcingVo sourcingVo){
        if(sourcingVo.getStartTime() == null){
            sourcingVo.setStartTime(new Date());
        }
    }
}
