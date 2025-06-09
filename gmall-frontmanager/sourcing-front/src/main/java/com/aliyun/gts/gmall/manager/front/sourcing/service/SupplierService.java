package com.aliyun.gts.gmall.manager.front.sourcing.service;

import com.aliyun.gts.gmall.manager.front.sourcing.vo.SupplierVo;

import java.util.List;
import java.util.Map;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/5/30 16:24
 */
public interface SupplierService {
    /**
     * 查询供应商信息
     * @param ids
     * @return
     */
    public Map<Long,SupplierVo> query(List<Long> ids);

    /**
     * 查询供应商信息
     * @return
     */
    public SupplierVo queryById(Long id);

    /**
     *
     * @param ids
     * @return
     */
    public Map<Long,SupplierVo> queryWithDetail(List<Long> ids);
}
