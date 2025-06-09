package com.aliyun.gts.gmall.manager.front.customer.dto.input.query;

import com.aliyun.gts.gmall.manager.front.common.dto.PageLoginRestQuery;
import lombok.Data;

/**
 * @Description
 * @Author FaberWong
 * @Date 2024/9/13 20:55
 */
@Data
public class CustomerFavouriteQuery extends PageLoginRestQuery {

    private boolean all;

    private String cityCode;
}
