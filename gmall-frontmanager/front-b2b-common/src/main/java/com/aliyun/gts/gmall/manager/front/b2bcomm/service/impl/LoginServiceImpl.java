package com.aliyun.gts.gmall.manager.front.b2bcomm.service.impl;

/**
 * @author 俊贤
 * @date 2021/02/18
 */
//@Service("purchaseLoginService")
//public class LoginServiceImpl { //implements LoginService {
//    @Autowired
//    private LoginConverter loginConverter;
//    @Resource
//    private SessionService sessionService;
//
//    @Resource
//    private AccountReadFacade accountReadFacade;
//
//    @Override
//    public OperatorDO loginByPwd(String username, String pwd) {
//        CheckAccountPwdRequest check = new CheckAccountPwdRequest();
//        check.setPwd(pwd);
//        check.setUserName(username);
//        check.setType(AccountTypeEnum.PURCHASER_ACCOUNT.getCode());
//        RpcResponse<AccountDTO> response = accountReadFacade.checkPwd(check);
//        if(response.getData() == null){
//            return null;
//        }
//        AccountDTO data = response.getData();
//        //判断accountType是否是采购账号、专家或者监理,不是则登录失败
//        if(!AccountTypeEnum.canLoginInPurchaseAdmin(data.getType())) {
//            return null;
//        }
//        //把token加入到缓存
//        OperatorDO operatorDO = convertOperate(data);
//        operatorDO.setLoginTime(new Date());
//        String token = sessionService.generateToken(operatorDO);
//        operatorDO.setToken(token);
//        sessionService.addToRedis(token, operatorDO);
//        return operatorDO;
//    }
//
//    /**
//     * 供应商信息
//     * @param accountDTO
//     * @return
//     */
//    public OperatorDO convertOperate(AccountDTO accountDTO){
//        OperatorDO operatorDO = loginConverter.account2operator(accountDTO);
//        operatorDO.setOperatorId(accountDTO.getId());
//        operatorDO.setMain(Objects.equals(AccountConstants.MAIN_ACCOUNT_FLAG, accountDTO.getMainFlag()));
//        operatorDO.setMainAccountId(accountDTO.getMainAccountId());
//        if(accountDTO.getPurchaserInfo()!=null) {
//            operatorDO.setPurchaserId(accountDTO.getPurchaserInfo().getId());
//            PurchaserDO purchaserDO = loginConverter.purchaseInfo2Do(accountDTO.getPurchaserInfo());
//            operatorDO.setPurchaserDO(purchaserDO);
//        }
//        return operatorDO;
//    }
//}