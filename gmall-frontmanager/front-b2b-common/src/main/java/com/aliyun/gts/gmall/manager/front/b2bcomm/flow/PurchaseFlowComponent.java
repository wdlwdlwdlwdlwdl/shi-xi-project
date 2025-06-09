package com.aliyun.gts.gmall.manager.front.b2bcomm.flow;

import com.aliyun.gts.gmall.manager.front.b2bcomm.model.OperatorDO;

import java.util.List;
import java.util.Map;


public interface PurchaseFlowComponent {

    boolean startFlow(Long yourBizId , OperatorDO operatorDO , String url , String appCode);
//
//    boolean cancelFlow(Long yourBizId , OperatorDO operatorDO , String appCode);
//
//    /**
//     *
//     * @param appCode
//     * @param bizIds
//     * @return
//     */
//    Map<String,String> queryInstanceId(String appCode, List<Long> bizIds);
//
//    /**
//     * 获取审核地址
//     * @param instanceId
//     * @return
//     */
//    String getAuditFlowUrl(String token,String instanceId);
}
