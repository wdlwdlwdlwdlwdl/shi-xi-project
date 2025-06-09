package com.aliyun.gts.gmall.manager.front.item.facade;

import com.aliyun.gts.gmall.center.item.api.dto.input.PointItemQueryReq;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.manager.front.item.dto.output.ItemDetailVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.PointItemQueryVO;

public interface PointItemFacade {

    /**
     *功能描述 分页查询积分商品信息
     * @author yilj
     * @date 2022/12/21
     * @param itemQueryReq
     * @return
     */
    RestResponse<PageInfo<PointItemQueryVO>> queryPointItemPage(PointItemQueryReq itemQueryReq);

    /**
     *功能描述 通过积分商品ID获取积分商品详细信息
     * @author yilj
     * @date 2022/12/21
     * @param itemQueryReq
     * @return
     */
    RestResponse<ItemDetailVO> queryPointItemDetail(PointItemQueryReq itemQueryReq);

    /**
     *功能描述 分页查询积分优惠券信息
     * @author yilj
     * @date 2022/12/21
     * @param itemQueryReq
     * @return
     */
    RestResponse<PageInfo<PointItemQueryVO>> queryPointCouponPage(PointItemQueryReq itemQueryReq);

    /**
     *功能描述 通过积分商品ID获取优惠券详细信息
     * @author yilj
     * @date 2022/12/21
     * @param itemQueryReq
     * @return
     */
    RestResponse<PointItemQueryVO> queryPointCouponDetail(PointItemQueryReq itemQueryReq);
}
