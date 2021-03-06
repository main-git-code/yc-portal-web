package com.ai.yc.protal.web.controller.order;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ai.opt.base.exception.BusinessException;
import com.ai.opt.base.vo.BaseResponse;
import com.ai.opt.base.vo.PageInfo;
import com.ai.opt.base.vo.ResponseHeader;
import com.ai.opt.sdk.components.ccs.CCSClientFactory;
import com.ai.opt.sdk.components.dss.DSSClientFactory;
import com.ai.opt.sdk.dubbo.util.DubboConsumerFactory;
import com.ai.opt.sdk.util.CollectionUtil;
import com.ai.opt.sdk.util.DateUtil;
import com.ai.opt.sdk.web.model.ResponseData;
import com.ai.paas.ipaas.dss.base.interfaces.IDSSClient;
import com.ai.paas.ipaas.i18n.ResWebBundle;
import com.ai.paas.ipaas.i18n.ZoneContextHolder;
import com.ai.slp.balance.api.deduct.interfaces.IDeductSV;
import com.ai.slp.balance.api.deduct.param.DeductParam;
import com.ai.slp.balance.api.deduct.param.DeductResponse;
import com.ai.yc.order.api.orderallocation.interfaces.IOrderAllocationSV;
import com.ai.yc.order.api.orderallocation.param.OrdAllocationPersonInfo;
import com.ai.yc.order.api.orderallocation.param.OrderAllocationBaseInfo;
import com.ai.yc.order.api.orderallocation.param.OrderAllocationReceiveFollowInfo;
import com.ai.yc.order.api.orderallocation.param.OrderAllocationRequest;
import com.ai.yc.order.api.orderclose.interfaces.IOrderCancelSV;
import com.ai.yc.order.api.orderclose.param.OrderCancelRequest;
import com.ai.yc.order.api.orderdeplay.interfaces.IOrderDeplaySV;
import com.ai.yc.order.api.orderdeplay.param.OrderDeplayRequest;
import com.ai.yc.order.api.orderdetails.interfaces.IQueryOrderDetailsSV;
import com.ai.yc.order.api.orderdetails.param.PersonInfoVo;
import com.ai.yc.order.api.orderdetails.param.QueryOrderDetailsRequest;
import com.ai.yc.order.api.orderdetails.param.QueryOrderDetailsResponse;
import com.ai.yc.order.api.orderevaluation.interfaces.IOrderEvaluationSV;
import com.ai.yc.order.api.orderevaluation.param.OrderEvaluationBaseInfo;
import com.ai.yc.order.api.orderevaluation.param.OrderEvaluationExtendInfo;
import com.ai.yc.order.api.orderevaluation.param.OrderEvaluationRequest;
import com.ai.yc.order.api.orderevaluation.param.QueryOrdEvaluteRequest;
import com.ai.yc.order.api.orderevaluation.param.QueryOrdEvaluteResponse;
import com.ai.yc.order.api.orderfee.interfaces.IOrderFeeQuerySV;
import com.ai.yc.order.api.orderfee.param.OrderFeeInfo;
import com.ai.yc.order.api.orderfee.param.OrderFeeProdInfo;
import com.ai.yc.order.api.orderfee.param.OrderFeeQueryRequest;
import com.ai.yc.order.api.orderfee.param.OrderFeeQueryResponse;
import com.ai.yc.order.api.orderquery.interfaces.IOrderQuerySV;
import com.ai.yc.order.api.orderquery.param.OrdOrderVo;
import com.ai.yc.order.api.orderquery.param.QueryOrdCountRequest;
import com.ai.yc.order.api.orderquery.param.QueryOrdCountResponse;
import com.ai.yc.order.api.orderquery.param.QueryOrderRequest;
import com.ai.yc.order.api.orderquery.param.QueryOrderRsponse;
import com.ai.yc.protal.web.constants.Constants;
import com.ai.yc.protal.web.constants.ErrorCode;
import com.ai.yc.protal.web.constants.OrderConstants;
import com.ai.yc.protal.web.constants.YEPayResultConstants;
import com.ai.yc.protal.web.model.PersonInfo;
import com.ai.yc.protal.web.model.PersonListInfo;
import com.ai.yc.protal.web.model.pay.AccountBalanceInfo;
import com.ai.yc.protal.web.model.pay.OrderPay;
import com.ai.yc.protal.web.model.pay.YEPayResult;
import com.ai.yc.protal.web.service.BalanceService;
import com.ai.yc.protal.web.service.OrderService;
import com.ai.yc.protal.web.utils.AmountUtil;
import com.ai.yc.protal.web.utils.ConfigUtil;
import com.ai.yc.protal.web.utils.DateUtils;
import com.ai.yc.protal.web.utils.PasswordMD5Util;
import com.ai.yc.protal.web.utils.PaymentUtil;
import com.ai.yc.protal.web.utils.UserUtil;
import com.ai.yc.protal.web.utils.VerifyUtil;
import com.ai.yc.user.api.usercompany.interfaces.IYCUserCompanySV;
import com.ai.yc.user.api.usercompany.param.UserCompanyInfoRequest;
import com.ai.yc.user.api.usercompany.param.UserCompanyInfoResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import net.sf.json.*;
import com.alibaba.fastjson.JSONObject;

/**
 * 客户订单
 * Created by liutong on 16/11/3.
 */
@Controller
@RequestMapping("/p/customer/order")
public class CustomerOrderController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerOrderController.class);
    @Autowired
    private BalanceService balanceService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ResWebBundle rb;

    /**
     * 我的订单,订单列表
     * @return
     */
    @RequestMapping("/list/view")
    public String orderListView(Model uiModel, String displayFlag){
        //查询 订单数量
        //待支付数量

        String userId = UserUtil.getUserId();
        IOrderQuerySV iOrderQuerySV = DubboConsumerFactory.getService(IOrderQuerySV.class);
        QueryOrdCountRequest ordCountReq = new QueryOrdCountRequest();
        ordCountReq.setUserId(userId);

        QueryOrdCountResponse ordCountRes = iOrderQuerySV.queryOrderCount(ordCountReq);

        LOGGER.info(JSONObject.toJSONString(ordCountRes));
        uiModel.addAttribute("CountMap", ordCountRes.getCountMap());

        Map<String,Integer> stateCount = ordCountRes.getCountMap();
        //待支付
        uiModel.addAttribute("UnPaidCount", stateCount.get(OrderConstants.DisplayState.UN_PAID));
        //翻译中
        uiModel.addAttribute("TranslateCount", stateCount.get(OrderConstants.DisplayState.TRANSLATING));
        //待确认
        uiModel.addAttribute("UnConfirmCount", stateCount.get(OrderConstants.DisplayState.UN_CONFIRM));
        //待评价
        uiModel.addAttribute("UnEvaluateCount", stateCount.get(OrderConstants.DisplayState.UN_EVALUATE));
        //用户ID
        uiModel.addAttribute("userId", UserUtil.getUserId());
        uiModel.addAttribute("displayFlag", displayFlag);

        //查询当前用户是否为企业管理员
        IYCUserCompanySV userCompanySV = DubboConsumerFactory.getService(IYCUserCompanySV.class);
        UserCompanyInfoRequest request = new UserCompanyInfoRequest();
        request.setUserId(userId);
        //只能查询到已认证企业
        UserCompanyInfoResponse response = userCompanySV.queryCompanyInfo(request);
        //是否为管理员
        if(response != null ){
            //是否为管理员
            boolean isManager = UserUtil.getUserId().equals(response.getAdminUserId());
            uiModel.addAttribute("isManager",isManager);
            uiModel.addAttribute("corporaId",isManager?response.getCompanyId():"");
        }
        return "customerOrder/orderList";
    }
    
    /**
     * 查询订单列表
     * @param request
     * @return
     * @author mimw
     */
    @RequestMapping("/orderList")
    @ResponseBody
    public ResponseData<PageInfo<OrdOrderVo> > orderList(HttpServletRequest request,
             @RequestParam(required = false) Boolean isManager,
             Boolean individualCheck, Boolean enterpriseCheck, QueryOrderRequest orderReq){
        ResponseData<PageInfo<OrdOrderVo>> resData = new ResponseData<>(ResponseData.AJAX_STATUS_SUCCESS,"OK");

        String orderTimeStart = request.getParameter("orderTimeStartStr");  //订单查询开始时间
        String orderTimeEnd = request.getParameter("orderTimeEndStr"); //订单查询结束时间
        String stateListStr = request.getParameter("stateListStr"); //后台、译员 订单状态
        String lspRole = request.getParameter("lspRole"); //译员角色
        try {
            //如果 译员id、用户id 都为null，返回空。一般不会出现
            if (StringUtils.isEmpty(orderReq.getUserId()) && StringUtils.isEmpty(orderReq.getInterperId())) {
                resData.setData(null);
                return resData;
            }
            //如果是LSP的管理员或项目经理
            if ("12".equals(lspRole) || "11".equals(lspRole)) {
                orderReq.setInterperId(null);
                orderReq.setUserId(null);
            }//如果不是LSP的管理员或项目经理,则清除LSP的标识
            else {
                orderReq.setLspId(null);
            }
            if(Boolean.TRUE.equals(isManager) && !Boolean.TRUE.equals(individualCheck)){
                orderReq.setUserId(null);
            }
            //不等于TRUE，则取消企业表示
            if(!Boolean.TRUE.equals(enterpriseCheck)){
                orderReq.setCorporaId(null);
            }

            //获取当前用户所处时区
            TimeZone timeZone = TimeZone.getTimeZone(ZoneContextHolder.getZone());
            
            if (StringUtils.isNotEmpty(orderTimeStart)) {
                String dateTmp = orderTimeStart+" 00:00:00";
                Timestamp date =DateUtil.getTimestamp(dateTmp,DateUtil.DATETIME_FORMAT,timeZone);
                orderReq.setOrderTimeStart(date);
            }
            if (StringUtils.isNotEmpty(orderTimeEnd)) {
                String dateTmp = orderTimeEnd+" 23:59:59";
                Timestamp date =DateUtil.getTimestamp(dateTmp,DateUtil.DATETIME_FORMAT,timeZone);
                orderReq.setOrderTimeEnd(date);
            }
            if (StringUtils.isNotEmpty(stateListStr)) {
                List<Object> states = JSONArray.parseArray(stateListStr);
                orderReq.setStateList(states);
            }
            LOGGER.info("订单列表查询数据：" +JSONObject.toJSONString(orderReq));
            /*if(StringUtils.isBlank(orderReq.getUserId()) && StringUtils.isBlank(orderReq.getCorporaId())){
                PageInfo<OrdOrderVo> pageInfo = new PageInfo<>();
                pageInfo.setResult(new ArrayList<OrdOrderVo>());
                pageInfo.setCount(0);
                pageInfo.setPageCount(0);
                pageInfo.setPageNo(1);
                pageInfo.setPageSize(10);
                //返回订单分页信息
                resData.setData(pageInfo);
            }else {*/
                IOrderQuerySV iOrderQuerySV = DubboConsumerFactory.getService(IOrderQuerySV.class);
                QueryOrderRsponse orderRes = iOrderQuerySV.queryOrder(orderReq);
                ResponseHeader resHeader = orderRes == null ? null : orderRes.getResponseHeader();
                LOGGER.info("订单列表查询 ：" + JSONObject.toJSONString(orderRes));
                //如果返回值为空,或返回信息中包含错误信息,返回失败
                if (orderRes == null || (resHeader != null && (!resHeader.isSuccess()))) {
                    resData = new ResponseData<>(ResponseData.AJAX_STATUS_FAILURE, rb.getMessage(""));
                } else {
                    PageInfo<OrdOrderVo> pageInfo = orderRes.getPageInfo();
                    //返回订单分页信息
                    resData.setData(pageInfo);
                }
            //}
        } catch (Exception e) {
            LOGGER.error("查询订单分页失败:",e);
            resData = new ResponseData<>(ResponseData.AJAX_STATUS_FAILURE, rb.getMessage(""));
        }
        return resData;
    }
    
    /**
     * 待报价页面
     * @return
     */
    @RequestMapping("/orderOffer")
    public String orderOffer(){
        return "order/orderOffer";
    }

    /**
     * 查询订单是否未支付，未支付返回成功
     * @param orderId
     * @return
     * @author mimw
     */
    @RequestMapping("/isPay")
    @ResponseBody
    public ResponseData<String> isPay(String orderId) {
        ResponseData<String> resData = new ResponseData<>(ResponseData.AJAX_STATUS_SUCCESS,"OK");
        //状态不是待支付
        if (!orderService.isUnPay(orderId)) {
            resData = new ResponseData<>(ResponseData.AJAX_STATUS_SUCCESS, "FAIL");
        }
        return resData;
    }

    /**
     * 取消订单，在未支付的情况下取消
     * @param orderId
     * @return
     * @author mimw
     */
    @RequestMapping("/cancelOrder")
    @ResponseBody
    public ResponseData<String> cancelOrder(String orderId) {
        ResponseData<String> resData = new ResponseData<>(ResponseData.AJAX_STATUS_SUCCESS,"OK");

        IOrderCancelSV iOrderCancelSV = DubboConsumerFactory.getService(IOrderCancelSV.class);
        OrderCancelRequest cancelReq = new OrderCancelRequest();
        cancelReq.setOrderId(Long.valueOf(orderId));
        cancelReq.setOperId(UserUtil.getUserId()); //操作员id 传入的是用户id
        cancelReq.setOperName(UserUtil.getUserName()); //操作员名，传入用户名称
        BaseResponse baseRes = iOrderCancelSV.handCancelNoPayOrder(cancelReq);
        //如果返回值为空,或返回信息中包含错误信息,返回失败
        if (!baseRes.getResponseHeader().isSuccess()){
            LOGGER.warn("取消订单返回 ："+JSONObject.toJSONString(baseRes.getResponseHeader()));
            resData = new ResponseData<>(ResponseData.AJAX_STATUS_FAILURE, rb.getMessage("order.info.cancel.fail"));
        }

        return resData;
    }

     /**
     * 支付页面
     * @return
     */
    @RequestMapping("/payOrder/{orderId}")
    public String createTextView(
            @PathVariable("orderId") Long orderId, @RequestParam(value = "unit",required = false) String unit,
            Model uiModel){
        String resultView = "order/payOrder";//订单支付页面
        IOrderFeeQuerySV iOrderFeeQuerySV = DubboConsumerFactory.getService(IOrderFeeQuerySV.class);
        OrderFeeQueryRequest feeQueryRequest = new OrderFeeQueryRequest();
        feeQueryRequest.setOrderId(orderId);
        OrderFeeQueryResponse feeQueryResponse = iOrderFeeQuerySV.orderFeeQuery(feeQueryRequest);
        OrderFeeInfo orderFeeInfo = feeQueryResponse.getOrderFeeInfo();
        OrderFeeProdInfo orderPordInfo = feeQueryResponse.getOrderFeeProdInfo();
        //检查订单权限，如果不允许查看，则不显示
        if(!checkOrder(feeQueryResponse.getUserInfo().getOperId())){
            return  "httpError/403";
        }
        //若订单金额等于0,则表示待报价
        if(new Long(0).equals(orderFeeInfo.getTotalFee())){
            resultView = "order/orderOffer";
        }
        //一阶段不支持余额支付，暂时隐藏
        //若是人民币,需要获取账户余额
        else if(Constants.CURRENCTY_UNIT_RMB.equals(orderFeeInfo.getCurrencyUnit())){
            //查询企业账户信息
            AccountBalanceInfo comBalance = balanceService.queryOfCompanyByUserId(UserUtil.getUserId());
            //企业账户余额信息
            uiModel.addAttribute("comBalanceInfo",comBalance);

            //查询个人账户信息
            AccountBalanceInfo balanceInfo = balanceService.queryOfUser(UserUtil.getUserId());
            //个人账户余额信息
            uiModel.addAttribute("balanceInfo",balanceInfo);
            //是否显示待充值信息
            uiModel.addAttribute("needPay",
                    (balanceInfo!=null&&balanceInfo.getBalance()<orderFeeInfo.getTotalFee())?true:false);
            //是否已设置支付密码
            uiModel.addAttribute("payPassExist",
                    (balanceInfo != null && StringUtils.isNotBlank(balanceInfo.getPayPassword()))?true:false);

            //默认设置成1为开启，0为关闭
            String accountEnable="1";
            try{
                accountEnable = CCSClientFactory.getDefaultConfigClient().get(Constants.Account.CCS_PATH_ACCOUNT_ENABLE);
                LOGGER.info("The account enable is {}",accountEnable);
            } catch(Exception e){
                //获取配置出错，直接忽略，视为开启
                LOGGER.error("获取配置信息{}错误，使用默认值{}",Constants.Account.CCS_PATH_ACCOUNT_ENABLE,accountEnable);
            }
            uiModel.addAttribute("accountEnable",accountEnable);
        }
        //订单编号
        uiModel.addAttribute("orderId",orderId);
        //订单信息
        uiModel.addAttribute("orderFee",feeQueryResponse.getOrderFeeInfo());
        //订单主题
        uiModel.addAttribute("translateName",orderPordInfo.getTranslateName());
        return resultView;
    }

    /**
     * 余额支付
     * @param discountSum 折扣
     * @param couponFee 优惠券优惠金额
     * @param orderType 订单类型
     * @param corporaId  企业id
     * @param couponId 优惠券id或优惠码
     * @param deductParam
     * @param totalPay 总金额
     * @return
     */
    @RequestMapping("/payOrder/balance")
    @ResponseBody
    public ResponseData<YEPayResult> balancePay(BigDecimal discountSum,Long couponFee,
            DeductParam deductParam,String orderType,String corporaId,String couponId,Long totalPay){
        YEPayResult yePayResult = new YEPayResult();
        yePayResult.setOrderId(deductParam.getExternalId());
        ResponseData<YEPayResult> responseData = new ResponseData<YEPayResult>(ResponseData.AJAX_STATUS_SUCCESS,"OK");
        //若订单不是未支付状态，则跳转到系统异常
        if(!orderService.isUnPay(deductParam.getExternalId())){
            yePayResult.setPayResultCode(YEPayResultConstants.ORDER_PAID);
            responseData.setData(yePayResult);
            return responseData;
        }
        String userId = UserUtil.getUserId();
        //进行余额扣款,页面
        deductParam.setTenantId(Constants.DEFAULT_TENANT_ID);
        //中心产品或垂直产品的唯一编码,传balance
        deductParam.setSystemId("balance");
        deductParam.setBusinessCode("001");//目前无用,使用固定内容
        deductParam.setChannel("中译语通科技有限公司");
        deductParam.setBusiDesc("订单支付,订单号:"+deductParam.getExternalId());
        deductParam.setPassword(PasswordMD5Util.Md5Utils.md5(deductParam.getPassword()));
        IDeductSV deductSV = DubboConsumerFactory.getService(IDeductSV.class);
        try {
            //使用优惠券
            balanceService.deductionCoupon(UserUtil.getUserId(),couponId,
                    Long.parseLong(deductParam.getExternalId()),deductParam.getTotalAmount(),
                    deductParam.getCurrencyUnit(),rb.getDefaultLocale(),orderType);
            DeductResponse deductResponse = deductSV.deductFund(deductParam);
            ResponseHeader responseHeader = deductResponse.getResponseHeader();
            //支付结果,默认为失败
            //扣款成功,同步订单状态
            if (responseHeader==null){
                throw new BusinessException("responseHeader is null");
            }//余额支付成功
            else if(responseHeader.isSuccess()
                    && YEPayResultConstants.RESULT_SUCCESS.equals(responseHeader.getResultCode())){
                long orderId = Long.parseLong(deductParam.getExternalId());
                orderService.orderPayProcessResult(userId,deductParam.getAccountId(),
                        orderId,orderType,totalPay,
                        (totalPay-deductParam.getTotalAmount()),deductParam.getTotalAmount(),
                        "YE",deductResponse.getSerialNo(),
                        DateUtil.getSysDate(),corporaId,discountSum,couponFee);
            }
            //返回指定的的状态码
            yePayResult.setPayResultCode(responseHeader.getResultCode());

        } catch (Exception e) {
            LOGGER.error("",e);
            yePayResult.setPayResultCode(ErrorCode.SYSTEM_ERROR);//系统异常
        }
        responseData.setData(yePayResult);
        //显示订单支付结果
        return responseData;
    }
     /**
     * 跳转支付页面,需要登录后才能进行支付
      * @param orderPay
     * @throws Exception
     */
    @RequestMapping(value = "/gotoPay")
    public String gotoPay(OrderPay orderPay, Model uiModel)
            throws Exception {
        String orderId = Long.toString(orderPay.getOrderId());
        //若订单不是未支付状态，则跳转到系统异常
        if(!orderService.isUnPay(Long.toString(orderPay.getOrderId()))){
            return "sysError";
        }
        //租户
        String tenantId= ConfigUtil.getProperty("TENANT_ID");
        //防止传递错误，将折扣乘以10000.
        BigDecimal discountInt = null;
        if(orderPay.getDiscount()!= null){
            discountInt = orderPay.getDiscount().multiply(new BigDecimal(10000));
        }
        //服务异步通知地址
        String notifyUrl= ConfigUtil.getProperty("NOTIFY_URL")+"/"+orderPay.getOrderType()+"/"+ UserUtil.getUserId()+
                "?totalPay="+orderPay.getTotalFee()+"&currencyUnit="+orderPay.getCurrencyUnit()+
                "&companyId="+(orderPay.getCorporaId()==null?"":orderPay.getCorporaId())+
                "&couponId="+(orderPay.getCouponId()==null?"":orderPay.getCouponId())+
                "&discountSum="+(discountInt==null?"":discountInt.intValue())+
                "&couponFee="+(orderPay.getCouponFee()==null?"":orderPay.getCouponFee());

        //异步通知地址,默认为用户
        //将订单金额直接转换为小数点后两位
        java.text.DecimalFormat df =new java.text.DecimalFormat("#0.00");
        String amount = String.valueOf(df.format(AmountUtil.changeLiToYuan(orderPay.getOrderAmount())));
        Map<String, String> map = new HashMap<>();
        map.put("tenantId", tenantId);//租户ID
        map.put("orderId", Long.toString(orderPay.getOrderId()));//请求单号
        map.put("returnUrl", ConfigUtil.getProperty("RETURN_URL"));//页面跳转地址
        map.put("notifyUrl", notifyUrl);//服务异步通知地址
        map.put("merchantUrl",orderPay.getMerchantUrl());//用户付款中途退出返回商户的地址
        map.put("requestSource", Constants.SELF_SOURCE);//终端来源
        map.put("currencyUnit",orderPay.getCurrencyUnit());//币种
        map.put("orderAmount", amount);//应付金额
        map.put("subject", orderPay.getTranslateName());//订单名称
        map.put("payOrgCode",orderPay.getPayOrgCode());
        // 加密
        String infoStr = orderId+ VerifyUtil.SEPARATOR
                + amount + VerifyUtil.SEPARATOR
                + notifyUrl + VerifyUtil.SEPARATOR
                + tenantId;
        String infoMd5 = VerifyUtil.encodeParam(infoStr, ConfigUtil.getProperty("REQUEST_KEY"));
        map.put("infoMd5", infoMd5);
        LOGGER.info("开始前台通知:" + map);
        String htmlStr = PaymentUtil.generateAutoSubmitForm(ConfigUtil.getProperty("ACTION_URL"), map);
        uiModel.addAttribute("paramsMap",map);
        uiModel.addAttribute("actionUrl",ConfigUtil.getProperty("ACTION_URL"));
        LOGGER.info("发起支付申请:" + htmlStr);
//        response.setStatus(HttpServletResponse.SC_OK);
//        response.getWriter().write(htmlStr);
//        response.getWriter().flush();
        return "gotoPay";
    }

    /**
     * 不使用任何支付方式，在应付金额为0，或选择翻译后付费时使用此方法
     * @return
     */
    @RequestMapping("/pay/noorg")
    public String payOrderNoOrg(OrderPay orderPay,BigDecimal discountSum,Model uiModel){
        try {
            orderPay.setDiscount(discountSum);
            //使用优惠券
            balanceService.deductionCoupon(UserUtil.getUserId(),orderPay.getCouponId(),
                    orderPay.getOrderId(),orderPay.getTotalFee(),orderPay.getCurrencyUnit(),rb.getDefaultLocale(),
                    orderPay.getOrderType());
            //调用订单支付
            orderService.orderPayProcessResult(UserUtil.getUserId(),null,orderPay.getOrderId(),
                    orderPay.getOrderType(),orderPay.getTotalFee(),(orderPay.getTotalFee()-orderPay.getOrderAmount()),
                    orderPay.getOrderAmount(),orderPay.getPayOrgCode(),null,
                    DateUtil.getSysDate(),orderPay.getCorporaId(),orderPay.getDiscount(),orderPay.getCouponFee());
            //订单号
            uiModel.addAttribute("orderId",orderPay.getOrderId());
            //支付结果
            uiModel.addAttribute("payResult",true);
        } catch (BusinessException e) {
            LOGGER.error("订单支付失败。",e);
            //支付结果
            uiModel.addAttribute("payResult",false);
        }
        return "order/orderPayResult";
    }

    /**
     * 显示订单详情
     * @param orderId
     * @param uiModel
     * @return
     */
    @RequestMapping("/{orderId}")
    public String orderInfoView(@PathVariable("orderId") String orderId, Model uiModel,HttpSession session){
        LOGGER.info("customer order session "+session.getAttribute("USER_TIME_ZONE"));
        String viewStr = "customerOrder/orderInfo";
        try {
            if (StringUtils.isEmpty(orderId)) {
                throw new BusinessException("","订单号为空："+orderId);
            }
            IQueryOrderDetailsSV iQueryOrderDetailsSV = DubboConsumerFactory.getService(IQueryOrderDetailsSV.class);

            QueryOrderDetailsRequest orderDetailsReq = new QueryOrderDetailsRequest();
            orderDetailsReq.setOrderId(Long.valueOf(orderId));
            orderDetailsReq.setChgStateFlag(OrderConstants.STATECHG_FLAG);

            QueryOrderDetailsResponse orderDetailsRes = iQueryOrderDetailsSV.queryOrderDetails4Portal(orderDetailsReq);
            ResponseHeader resHeader = orderDetailsRes==null?null:orderDetailsRes.getResponseHeader();
            LOGGER.info("订单详细信息 ：" + JSONObject.toJSONString(orderDetailsRes));
            //如果返回值为空,或返回信息中包含错误信息,返回失败
            if (orderDetailsRes==null|| (resHeader!=null && (!resHeader.isSuccess()))){
                throw new BusinessException("","查询失败："+orderDetailsRes);
            }
            //检查订单权限，如果不为本人下单，则不允许查看
            if(!checkOrder(orderDetailsRes.getUserId())){
                viewStr = "httpError/403";
            }else{
                //若是口译订单，且已分配
                if (OrderConstants.TranslateType.ORAL.equals(orderDetailsRes.getTranslateType())
                        && OrderConstants.State.ASSIGNED.equals(orderDetailsRes.getState())) {
                    List<PersonInfoVo> personList = CollectionUtil.isEmpty(orderDetailsRes.getFollowInfoes()) ?
                            orderDetailsRes.getFollowInfoes().get(0).getPersonInfos():Collections.<PersonInfoVo>emptyList();
                    uiModel.addAttribute("personList",personList);
                }
                uiModel.addAttribute("OrderDetails", orderDetailsRes);
            }
        } catch (Exception e) {
            LOGGER.error("查询订单详情失败:",e);
            viewStr = "customerOrder/orderError";
        }
        return viewStr;
    }
    
    /**
     * 文档订单详细页面 下载文件
     * @param fileId
     * @param request
     * @param response
     * @author mimw
     */
    @RequestMapping("/download")
    public void download(String fileId, String fileName, HttpServletRequest request,
            HttpServletResponse response) {
        IDSSClient client = DSSClientFactory.getDSSClient(Constants.IPAAS_ORDER_FILE_DSS);
        byte[] b = client.read(fileId);
    
        try {

            String agent = request.getHeader("User-Agent");
            //不是ie
            if (agent.indexOf("MSIE") == -1 && agent.indexOf("like Gecko")== -1) {
                //空格、（、）、；、@、#、&
                String newFileName = java.net.URLDecoder.decode(fileName,"utf-8")
                      ;
                fileName = new String(newFileName.getBytes("utf-8"), "ISO-8859-1");
            }

            OutputStream os = response.getOutputStream();
//            response.setCharacterEncoding("utf-8");
            response.setContentType("multipart/form-data");
            response.setHeader("Content-Disposition", "attachment;fileName=\"" + fileName +"\"");
            response.setHeader("Content-Length", b.length+"");
            os.write(b);
            os.close();
        } catch (IOException e) {
           LOGGER.info("下载文件异常：", e);
        }
    }

    /**
     * 订单评价view
     * @return
     */
    @RequestMapping("/evaluate/{orderId}")
    public String evaluateView(Model uiModel,@PathVariable("orderId") String orderId, HttpSession session){
        String viewStr = "customerOrder/orderEvaluate";
        //判断订单的状态和是否有权限
        try {
            if (StringUtils.isEmpty(orderId)) {
                throw new BusinessException("","订单号为空："+orderId);
            }
            IQueryOrderDetailsSV iQueryOrderDetailsSV = DubboConsumerFactory.getService(IQueryOrderDetailsSV.class);

            QueryOrderDetailsRequest orderDetailsReq = new QueryOrderDetailsRequest();
            orderDetailsReq.setOrderId(Long.valueOf(orderId));
            orderDetailsReq.setChgStateFlag(OrderConstants.STATECHG_FLAG);

            QueryOrderDetailsResponse orderDetailsRes = iQueryOrderDetailsSV.queryOrderDetails4Portal(orderDetailsReq);
            ResponseHeader resHeader = orderDetailsRes==null?null:orderDetailsRes.getResponseHeader();
            LOGGER.info("订单详细信息 ：" + JSONObject.toJSONString(orderDetailsRes));
            //如果返回值为空,或返回信息中包含错误信息,返回失败
            if (orderDetailsRes==null|| (resHeader!=null && (!resHeader.isSuccess()))){
                throw new BusinessException("","查询失败："+orderDetailsRes);
            }
            //检查订单权限，如果不为本人下单或不是待评价状态，则不允许查看
            if(!OrderConstants.DisplayState.UN_EVALUATE.equals(orderDetailsRes.getDisplayFlag())
                || !checkOrder(orderDetailsRes.getUserId())){
                viewStr = "httpError/403";
            }
        } catch (Exception e) {
            LOGGER.error("查询订单详情失败:",e);
            viewStr = "customerOrder/orderError";
        }

        uiModel.addAttribute("orderId", orderId);
        return viewStr;
    }

    /**
     * 查看评价订单view
     * @return
     */
    @RequestMapping("/seeEvaluate/{orderId}")
    public String seeEvaluateView(Model uiModel,@PathVariable("orderId") String orderId, HttpSession session){
        if (StringUtils.isEmpty(orderId)) {
            throw new BusinessException("","订单号为空："+orderId);
        }
        uiModel.addAttribute("orderId", orderId);
        IOrderEvaluationSV iOrderEvaluationSV = DubboConsumerFactory.getService(IOrderEvaluationSV.class);
        QueryOrdEvaluteRequest ordEvaluateReq = new QueryOrdEvaluteRequest();
        ordEvaluateReq.setOrderId(Long.valueOf(orderId));
        QueryOrdEvaluteResponse ordEvaluateRes = iOrderEvaluationSV.queryOrderEvalute(ordEvaluateReq);

        /*QueryOrdEvaluteResponse ordEvaluateRes= new QueryOrdEvaluteResponse();
        ordEvaluateRes.setOrderId(Long.valueOf(orderId));
        ordEvaluateRes.setServeQuality("32");
        ordEvaluateRes.setServeSpeed("24");
        ordEvaluateRes.setServeManner("30");
        ordEvaluateRes.setEvaluateContent("翻译的不错哦！");*/
        uiModel.addAttribute("orderEvaluateInfo", ordEvaluateRes);
        return "customerOrder/viewEvaluate";
    }


    @RequestMapping("/evaluateOrder")
    @ResponseBody
    public ResponseData<String> evaluateOrder(HttpServletRequest request){
        ResponseData<String> resData =  new ResponseData<>(ResponseData.AJAX_STATUS_SUCCESS,"OK");
        String orderId = request.getParameter("orderId");
        String orderEvaluateInfoStr = request.getParameter("orderEvaluateInfo");

        IOrderEvaluationSV iOrderEvaluationSV = DubboConsumerFactory.getService(IOrderEvaluationSV.class);
        OrderEvaluationRequest evaluationRequest = new OrderEvaluationRequest();
        //订单基本信息
        OrderEvaluationBaseInfo orderBase = new OrderEvaluationBaseInfo();
        orderBase.setOrderId(Long.valueOf(orderId));
        evaluationRequest.setBaseInfo(orderBase);

        //订单评价信息
        OrderEvaluationExtendInfo orderEvaluateInfo = JSONObject.parseObject(orderEvaluateInfoStr, OrderEvaluationExtendInfo.class);
        orderEvaluateInfo.setEvaluateTime(new Timestamp(System.currentTimeMillis()));
        evaluationRequest.setExtendInfo(orderEvaluateInfo);

        iOrderEvaluationSV.orderEvaluation(evaluationRequest);
        return resData;
    }

    /**
     * 延时确认订单
     * @param orderId
     * @return
     */
    @RequestMapping("/delayed")
    @ResponseBody
    public ResponseData<String> delayedConfirmOrder(Long orderId){
        ResponseData<String> response = new ResponseData<String>(ResponseData.AJAX_STATUS_SUCCESS,"OK");
        IOrderDeplaySV orderDeplaySV = DubboConsumerFactory.getService(IOrderDeplaySV.class);
        OrderDeplayRequest svRequest = new OrderDeplayRequest();
        svRequest.setOrderId(orderId);
        svRequest.setOperId(UserUtil.getUserId());
        svRequest.setOperName(UserUtil.getUserName());
        //延迟3天
        svRequest.setEndChgTime(DateUtils.afterNow(3*24*60*60));
        BaseResponse svResponse = orderDeplaySV.orderDeplay(svRequest);
        if (svResponse!=null && !svResponse.getResponseHeader().isSuccess()){
            LOGGER.warn("订单延时失败，订单号：{}，返回信息：{}",orderId, JSON.toJSONString(svResponse));
            response = new ResponseData<String>(ResponseData.AJAX_STATUS_FAILURE,
                    rb.getMessage("order.info.delayed.fail"));
        }
        return response;
    }
    /**
     * 判断用户是否查看订单的权限
     * 目前统一方法，便于之后扩展
     *
     * @return 若允许，返回true，否则为false
     */
    private boolean checkOrder(String orderUserId){
        return UserUtil.getUserId().equals(orderUserId);
    }
    /**
     * 口译订单分配
     * @param orderId
     * @return
     */
    @RequestMapping("/alloOrder")
    @ResponseBody
    public ResponseData<String> alloOrder(String orderId,String  personInfo){
        ResponseData<String> response = new ResponseData<String>(ResponseData.AJAX_STATUS_SUCCESS,"OK");
        IOrderAllocationSV orderAllocationSV = DubboConsumerFactory.getService(IOrderAllocationSV.class);
        List<OrdAllocationPersonInfo>  personList = new ArrayList<OrdAllocationPersonInfo>();
        PersonListInfo info =  JSON.parseObject(personInfo,PersonListInfo.class);
        List<PersonInfo> list = info.getPersonList();
        if(!CollectionUtil.isEmpty(list)){
        	for(PersonInfo person:list){
        		OrdAllocationPersonInfo ordPerson = new OrdAllocationPersonInfo();
        		ordPerson.setInterperName(person.getInterperName());
        		ordPerson.setTel(person.getTel());
        		personList.add(ordPerson);
        	}
        }
        OrderAllocationRequest alloRequest = new OrderAllocationRequest();
        OrderAllocationBaseInfo baseInfo = new OrderAllocationBaseInfo();
        List<OrderAllocationReceiveFollowInfo> orderAllocationList = new ArrayList<OrderAllocationReceiveFollowInfo>();
        OrderAllocationReceiveFollowInfo follow = new OrderAllocationReceiveFollowInfo();
        follow.setFinishState("2");
        follow.setStep("1");
        follow.setReceiveState("1");
        follow.setOrdAllocationPersonInfoList(personList);
        orderAllocationList.add(follow);
        baseInfo.setOrderId(Long.valueOf(orderId));
        baseInfo.setUserId(UserUtil.getUserId());
        baseInfo.setOperName(UserUtil.getUserName());
        alloRequest.setOrderAllocationBaseInfo(baseInfo);
        alloRequest.setOrderAllocationReceiveFollowList(orderAllocationList);
        BaseResponse svResponse = orderAllocationSV.orderAllocation(alloRequest);
        if (svResponse!=null && !svResponse.getResponseHeader().isSuccess()){
            LOGGER.warn("订单分配失败，订单号：{}，返回信息：{}",orderId, JSON.toJSONString(svResponse));
            response = new ResponseData<String>(ResponseData.AJAX_STATUS_FAILURE,
                    rb.getMessage("order.info.delayed.fail"));
        }
        return response;
    }
}
