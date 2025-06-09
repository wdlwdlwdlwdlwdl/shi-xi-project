package com.aliyun.gts.gmall.manager.front.b2bcomm.service.impl;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/2/3 20:40
 */
//@Service("purchaseSessionService")
//public class SessionServiceImpl { //implements SessionService {
//
//    public static long oneHour = 36000000;
//    @Autowired
//    @Qualifier("loginCacheManager")
//    private CacheManager cacheManager;
//
//    @Override
//    public OperatorDO getUserByToken(String token) {
//        if(token == null){
//            return null;
//        }
//        OperatorDO operatorDO = cacheManager.get(token);
//        Date now = new Date();
//        if(operatorDO == null){
//            return null;
//        }
//        //已经失效
//        long diff = now.getTime() - operatorDO.getLoginTime().getTime();
//        if(diff > oneHour){
//            return null;
//        }
//        return operatorDO;
//    }
//
//    @Override
//    public boolean addToRedis(String token, OperatorDO operatorDO) {
//        cacheManager.set(token, operatorDO,3000, TimeUnit.MINUTES);
//        return true;
//    }
//
//    @Override
//    public String generateToken(OperatorDO operatorDO) {
//        StringBuilder key = new StringBuilder();
//        key.append(operatorDO.getOperatorId());
//        key.append(CommonConstants.APP);
//        key.append(operatorDO.getLoginTime().getSeconds());
//        String md5 = Md5Utils.md5(key.toString());
//        //把小时放在最后
//        return md5;
//    }
//
//    @Override
//    public void expireToken(String token) {
//         cacheManager.delete(token);
//    }
//
//    @Override
//    public String getToken(HttpServletRequest request) {
//        String token = request.getHeader("X-access-token");
//        if(token != null){
//            return token;
//        }
//        //为了兼容
//        String token2 = request.getHeader("token");
//        if(token2 != null){
//            return token2;
//        }
//        return request.getParameter("token");
//    }
//}
