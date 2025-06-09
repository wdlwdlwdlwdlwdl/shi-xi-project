package com.aliyun.gts.gmall.center.trade.normal.ext;

import com.aliyun.gts.gmall.center.trade.core.extension.common.CommonReversalProcessExt;
import com.aliyun.gts.gmall.platform.trade.core.extension.reversal.ReversalProcessExt;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.Extension;

@Slf4j
@Extension(points = {ReversalProcessExt.class})
public class NormalReversalProcessExt extends CommonReversalProcessExt {

}

