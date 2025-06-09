package com.aliyun.gts.gmall.manager.front.sourcing.service;

import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.manager.front.sourcing.vo.contract.ContractSignStatusVO;

public interface ContractService {

    /**
     * 线下签署方式 手动修改签署状态
     * @param signStatusVO
     * @return
     */
    RestResponse signStatus(ContractSignStatusVO signStatusVO);

    /**
     * 生成电子签署页面
     * @param contractId
     * @return
     */
    RestResponse<String> signPage(Long contractId);

    /**
     * 电子签章平台回调
     * @param contractId
     * @param sign
     * @return
     */
    boolean esignCallback(Long contractId , String sign);

}
