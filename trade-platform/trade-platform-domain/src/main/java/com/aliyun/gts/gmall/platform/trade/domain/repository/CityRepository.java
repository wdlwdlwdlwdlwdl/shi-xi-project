package com.aliyun.gts.gmall.platform.trade.domain.repository;


import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcCityDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.City;

import java.util.List;

public interface CityRepository {

    TcCityDO queryTcCity(long id);

    PageInfo<TcCityDO> queryCityList(City req);

    TcCityDO saveCity(TcCityDO tcCityDO);

    TcCityDO updateCity(TcCityDO tcCityDO);

    boolean exist(String cityCode);

    List<TcCityDO> queryList(City req);

    TcCityDO detail(String cityCode);
}
