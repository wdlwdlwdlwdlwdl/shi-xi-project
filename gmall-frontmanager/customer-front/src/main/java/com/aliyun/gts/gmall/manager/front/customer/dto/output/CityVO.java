package com.aliyun.gts.gmall.manager.front.customer.dto.output;

import com.aliyun.gts.gmall.center.misc.api.dto.output.city.CityDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class CityVO {

    // 热门城市列表
    private List<CityDTO> hotCityList;

    // 城市列表
    private List<CityDTO> cityList;

    // 字母排序城市列表
    private Map<String, List<CityDTO>> sortCity;

}
