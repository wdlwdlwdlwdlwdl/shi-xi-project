package com.aliyun.gts.gmall.manager.front.item.convertor;


import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.manager.front.item.dto.input.query.SellerDetailQuery;
import com.aliyun.gts.gmall.manager.front.item.dto.input.query.SellerWarehouseQuery;
import com.aliyun.gts.gmall.manager.front.item.dto.output.SellerDetailInfoVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.SellerWarehouseVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.WarehouseBusinessHoursVO;
import com.aliyun.gts.gmall.platform.item.api.dto.input.warehouse.WarehouseQueryDetailReq;
import com.aliyun.gts.gmall.platform.item.api.dto.output.warehouse.WarehouseBusinessHoursDTO;
import com.aliyun.gts.gmall.platform.item.api.dto.output.warehouse.WarehouseDTO;
import com.aliyun.gts.gmall.platform.user.api.dto.input.SellerAvgScoreQuery;
import com.aliyun.gts.gmall.platform.user.api.dto.output.SellerAvgScoreDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.testng.collections.Lists;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 商家转换类
 */
public class SellerConverter {


    public static SellerAvgScoreQuery toSellerAvgScoreQuery(SellerDetailQuery restReq){
        if (restReq == null){
            return null;
        }
        SellerAvgScoreQuery rpcReq = new SellerAvgScoreQuery();
        rpcReq.setSellerIds(Arrays.asList(restReq.getSellerId()));
        return rpcReq;
    }

    public static SellerDetailInfoVO toSellerDetailInfoVO(RpcResponse<List<SellerAvgScoreDTO>> rpcResponse){
        SellerAvgScoreDTO dto = Optional.ofNullable(rpcResponse)
                .filter(v -> v.isSuccess())
                .filter(v -> !CollectionUtils.isEmpty(v.getData()))
                .map(v -> v.getData().get(0))
                .orElse(null);
        SellerDetailInfoVO vo = null;
        if (Objects.nonNull(dto)){
            vo = new SellerDetailInfoVO();
            BeanUtils.copyProperties(dto, vo);
        }
        return vo;
    }

    public static WarehouseQueryDetailReq toWarehouseQueryDetailReq(SellerWarehouseQuery restReq){
        if (Objects.isNull(restReq)){
            return null;
        }
        WarehouseQueryDetailReq rpcReq = new WarehouseQueryDetailReq();
        BeanUtils.copyProperties(restReq, rpcReq);
        return rpcReq;
    }

    public static List<SellerWarehouseVO> toSellerWarehouseVO(RpcResponse<List<WarehouseDTO>> rpcResponse){
        List<SellerWarehouseVO> voList = Optional.ofNullable(rpcResponse)
                .filter(v -> v.isSuccess())
                .filter(v -> !CollectionUtils.isEmpty(v.getData()))
                .map(v -> v.getData().stream()
                        .map(dto -> toSellerWarehouseVOSingle(dto))
                        .collect(Collectors.toList()))
                .orElse(Lists.newArrayList());
        return voList;
    }

    private static SellerWarehouseVO toSellerWarehouseVOSingle(WarehouseDTO dto){
        if (Objects.isNull(dto)){
            return null;
        }
        SellerWarehouseVO vo = new SellerWarehouseVO();
        BeanUtils.copyProperties(dto, vo);
        vo.setWarehouseBusinessHoursList(toWarehouseBusinessHoursVO(dto.getWarehouseBusinessHoursList()));
        return vo;
    }

    private static List<WarehouseBusinessHoursVO> toWarehouseBusinessHoursVO(List<WarehouseBusinessHoursDTO> warehouseBusinessHoursList){
        if (CollectionUtils.isEmpty(warehouseBusinessHoursList)){
            return Lists.newArrayList();
        }
        List<WarehouseBusinessHoursVO> voList = Lists.newArrayList();
        for (WarehouseBusinessHoursDTO dto : warehouseBusinessHoursList) {
            WarehouseBusinessHoursVO vo = new WarehouseBusinessHoursVO();
            BeanUtils.copyProperties(dto, vo);
            voList.add(vo);
        }
        return voList;
    }

}
