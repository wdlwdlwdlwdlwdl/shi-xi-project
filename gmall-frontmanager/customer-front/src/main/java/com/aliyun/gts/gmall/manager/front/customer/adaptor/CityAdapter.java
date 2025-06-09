package com.aliyun.gts.gmall.manager.front.customer.adaptor;

import com.aliyun.gts.gmall.center.misc.api.dto.input.city.CityQueryRpcReq;
import com.aliyun.gts.gmall.center.misc.api.dto.output.city.CityDTO;
import com.aliyun.gts.gmall.center.misc.api.facade.city.CityReadFacade;
import com.aliyun.gts.gmall.center.misc.common.enums.HotCityEnum;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.manager.framework.common.DubboBuilder;
import com.aliyun.gts.gmall.manager.front.common.config.DatasourceConfig;
import com.aliyun.gts.gmall.manager.front.common.consts.DsIdConst;
import com.aliyun.gts.gmall.manager.front.common.dto.CityRequestQuery;
import com.aliyun.gts.gmall.manager.front.customer.dto.output.CityVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.aliyun.gts.gmall.manager.front.common.exception.FrontCommonResponseCode.CUSTOMER_CENTER_ERROR;
import static com.aliyun.gts.gmall.manager.front.customer.dto.utils.CustomerFrontResponseCode.CUSTOMER_UPDATE_FAIL;


/**
 * 城市查询
 */
@Slf4j
@Service
public class CityAdapter {

    @Autowired
    private CityReadFacade cityReadFacade;

    @Autowired
    private DatasourceConfig datasourceConfig;

    private final DubboBuilder builder = DubboBuilder.builder().logger(log).sysCode(CUSTOMER_CENTER_ERROR).build();

    public CityVO queryAll(CityRequestQuery cityRequestQuery) {
        return builder
            .create(datasourceConfig)
            .id(DsIdConst.city_all)
            .queryFunc(
                (Function<CityQueryRpcReq, RpcResponse<CityVO>>) request -> {
                    CityVO cityVO = new CityVO();
                    CityQueryRpcReq cityQueryRpcReq = new CityQueryRpcReq();
                    RpcResponse<List<CityDTO>> rpcResponse = cityReadFacade.queryCityAll(cityQueryRpcReq);
                    if (Objects.nonNull(rpcResponse) &&
                        Boolean.TRUE.equals(rpcResponse.isSuccess()) &&
                        Objects.nonNull(rpcResponse.getData())) {
                        List<CityDTO> cityDTOList = rpcResponse.getData();
                        if (!CollectionUtils.isEmpty(cityDTOList)) {
                            cityVO.setHotCityList(cityDTOList.stream()
                                .filter(cityDTO -> HotCityEnum.YES.getCode().equals(cityDTO.getIsHot()))
                                .collect(Collectors.toList())
                            );
                            if (StringUtils.isNotEmpty(cityRequestQuery.getKeyWords())) {
                                cityDTOList = cityDTOList.stream()
                                    .filter(cityDTO -> cityDTO.getCityName().contains(cityRequestQuery.getKeyWords()))
                                    .collect(Collectors.toList());
                            }
                            cityVO.setCityList(cityDTOList);
                            cityVO.setSortCity(categorizeStrings(cityDTOList));
                        }
                        return RpcResponse.ok(cityVO);
                    }
                    return RpcResponse.fail(rpcResponse.getFail());
                })
            .bizCode(CUSTOMER_UPDATE_FAIL).query(new CityQueryRpcReq());
    }

    /**
     * 首字母排序
     * @param cityList
     * @return
     */
    private static Map<String, List<CityDTO>> categorizeStrings(List<CityDTO> cityList) {
        Map<String, List<CityDTO>> resultMap = new HashMap<>();
        for (CityDTO cityDTO : cityList) {
            if (cityDTO != null && StringUtils.isNotEmpty(cityDTO.getCityName())) {
                char firstChar = cityDTO.getCityName().toLowerCase().charAt(0);
                resultMap.computeIfAbsent(String.valueOf(firstChar), k -> new ArrayList<>()).add(cityDTO);
            }
        }
        return resultMap;
    }
}
