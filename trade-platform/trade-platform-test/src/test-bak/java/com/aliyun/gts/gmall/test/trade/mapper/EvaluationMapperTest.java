package com.aliyun.gts.gmall.test.platform.mapper;

import java.util.HashMap;
import java.util.Map;

import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcEvaluationDO;
import com.aliyun.gts.gmall.platform.trade.persistence.mapper.TcEvaluationMapper;
import com.aliyun.gts.gmall.test.platform.base.BaseTest;
import com.google.common.collect.Lists;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class EvaluationMapperTest extends BaseTest {

    @Autowired
    TcEvaluationMapper tcEvaluationMapper;

    @Test
    @Ignore
    public void testInsert(){
        TcEvaluationDO tcEvaluationDO = new TcEvaluationDO();
        Map map = new HashMap<>();
        map.put("a","b");
        tcEvaluationDO.setExtend(map);
        tcEvaluationDO.setCustId(1L);
        tcEvaluationDO.setSellerId(2L);
        tcEvaluationDO.setPrimaryOrderId(111L);
        tcEvaluationDO.setOrderId(111L);
        tcEvaluationDO.setItemId(1L);
        tcEvaluationDO.setRateDesc("desc");
        tcEvaluationDO.setRatePic(Lists.newArrayList("xxx"));
        tcEvaluationDO.setRateVideo(Lists.newArrayList("xxx"));
        tcEvaluationDO.setRateScore(5);

        tcEvaluationMapper.insert(tcEvaluationDO);

        Long id  =tcEvaluationDO.getId();

        TcEvaluationDO evaluationDO = tcEvaluationMapper.selectById(id);

        System.out.println(evaluationDO.getExtend());

    }

}
