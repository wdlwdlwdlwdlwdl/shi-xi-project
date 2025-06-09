package com.aliyun.gts.gmall.platform.trade.common.domain.step;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Data
public class StepTemplate implements Serializable {

    private String templateName;
    private List<StepMeta> stepMetas;

    public StepMeta getStepMeta(int stepNo) {
        return stepMetas == null ? null :
                stepMetas.stream()
                        .filter(step -> Objects.equals(step.getStepNo(), stepNo))
                        .findFirst().orElse(null);
    }
}
