package com.aliyun.gts.gmall.manager.front.customer.dto.output;

import com.aliyun.gts.gmall.center.misc.api.dto.output.city.CityDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class SortCityVO {

    private String sortFirstChart;

    private List<CityDTO> cityList;

}
