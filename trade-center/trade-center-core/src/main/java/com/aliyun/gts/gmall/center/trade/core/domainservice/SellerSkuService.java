package com.aliyun.gts.gmall.center.trade.core.domainservice;


import java.util.Date;

public interface SellerSkuService {


    Boolean syncUpdateSkuSales(Long primaryOrderId);


    Boolean autoSync(Date begin, Date end,Long sellerId);


}
