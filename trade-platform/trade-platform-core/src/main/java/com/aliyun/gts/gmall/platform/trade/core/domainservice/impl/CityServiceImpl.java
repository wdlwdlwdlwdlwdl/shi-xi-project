package com.aliyun.gts.gmall.platform.trade.core.domainservice.impl;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CityQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CityRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.CityDTO;
import com.aliyun.gts.gmall.platform.trade.common.util.PolygonUtils;
import com.aliyun.gts.gmall.platform.trade.core.convertor.CityConverter;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.CityService;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcCityDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.City;
import com.aliyun.gts.gmall.platform.trade.domain.repository.CityRepository;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CityServiceImpl implements CityService {

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    CityConverter cityConverter;

    @Override
    public PageInfo<CityDTO> queryCity(CityQueryRpcReq req) {
        PageInfo<TcCityDO> list = cityRepository.queryCityList(cityConverter.toTcCity(req));
        return  cityConverter.toCityDTOPage(list);
    }

    @Override
    public CityDTO saveCity(CityRpcReq req) {
        TcCityDO cityDO = cityConverter.toTcCityDO(req);
        return cityConverter.toCityDTO(cityRepository.saveCity(cityDO));
    }

    @Override
    public CityDTO updateCity(CityRpcReq req) {
        TcCityDO cityDO = cityConverter.toTcCityDO(req);
        return cityConverter.toCityDTO(cityRepository.updateCity(cityDO));
    }

    @Override
    public CityDTO cityDetail(CityRpcReq req) {
        TcCityDO cityDO = cityRepository.queryTcCity(req.getId());
        return cityConverter.toCityDTO(cityDO);
    }

    @Override
    public boolean exist(CityRpcReq req) {
        return cityRepository.exist(req.getCityCode());
    }

    @Override
    public List<CityDTO> queryCityList(CityRpcReq req) {
        City city = new City();
        city.setCodes(req.getCodes());
        return cityConverter.toCityListDTO(cityRepository.queryList(city));
    }

    @Override
    public boolean isAccess(CityRpcReq req) {
        TcCityDO cityDO = cityRepository.detail(req.getCityCode());
        return PolygonUtils.isPointInPolygon(Double.parseDouble(req.getLongitude()),Double.parseDouble(req.getLatitude())
               ,cityDO.getPolygon());
    }


    public static void main(String[] args) {
        // 定义多边形的经纬度点
        List<double[]> polygon = List.of(
                new double[] {68.1432496, 43.3189363},
                new double[] {68.1700287, 43.3519165},
                new double[] {68.12883, 43.2694326},
                new double[] {68.1418763, 43.3204358}
        );
        // 当前点的经纬度
        double currentLongitude = 68.1432496;  // 经度
        double currentLatitude = 43.3189363;   // 纬度
        Gson gson = new Gson();
        String aa = "    [\n" +
                "      [\n" +
                "        68.1432496,\n" +
                "        43.3189363\n" +
                "      ],\n" +
                "      [\n" +
                "        68.1700287,\n" +
                "        43.3519165\n" +
                "      ],\n" +
                "      [\n" +
                "        68.270279,\n" +
                "        43.3534151\n" +
                "      ],\n" +
                "      [\n" +
                "        68.3155976,\n" +
                "        43.3728945\n" +
                "      ],\n" +
                "      [\n" +
                "        68.4412537,\n" +
                "        43.2789365\n" +
                "      ],\n" +
                "      [\n" +
                "        68.4192811,\n" +
                "        43.2449159\n" +
                "      ],\n" +
                "      [\n" +
                "        68.2901917,\n" +
                "        43.2058688\n" +
                "      ],\n" +
                "      [\n" +
                "        68.1810151,\n" +
                "        43.2188873\n" +
                "      ],\n" +
                "      [\n" +
                "        68.12883,\n" +
                "        43.2694326\n" +
                "      ],\n" +
                "      [\n" +
                "        68.1418763,\n" +
                "        43.3204358\n" +
                "      ]\n" +
                "    ]";
        // 判断当前点是否在多边形内
        System.out.println("Current point is inside polygon: " + gson.toJson(polygon));
        PolygonUtils.isPointInPolygon(currentLongitude,currentLatitude,aa);
    }
}
