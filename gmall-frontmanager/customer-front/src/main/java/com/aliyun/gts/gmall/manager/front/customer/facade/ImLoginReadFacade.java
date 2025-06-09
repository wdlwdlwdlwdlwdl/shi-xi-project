package com.aliyun.gts.gmall.manager.front.customer.facade;

import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.gim.common.query.MessageQuery;

import java.util.List;
import java.util.Map;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/8/11 10:46
 */
public interface ImLoginReadFacade {

    /**
     * 判断是否在线
     * @param ids
     * @return
     */
    public Map<Long,String> isOnline(List<Long> ids);

    /**
     * 返回客服的聊天ID
     * @param sellerId
     * @return
     */
    public Long queryCCOImUid(Long sellerId);

    public Long getCustImUid(Long custId);

    /**
     *
     * @param receiveId
     * @return
     */
    public RestResponse<String> queryImChatUrl(Long custId,Long receiveId, Integer type);

    Long countUnRead(MessageQuery query);
}
