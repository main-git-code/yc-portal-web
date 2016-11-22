package com.ai.yc.protal.web.constants;

/**
 * Created by liutong on 16/11/11.
 */
public final class OrderConstants {
    private OrderConstants(){

    }
    //个人类型订单
    public static final String ORDER_TYPE_PERSON = "1";
    //企业类型订单
    public static final String ORDER_TYPE_ENTERPRISE = "2";

    //用户类型 个人
    public static final String USER_TYPE_PERSON = "10";
    //用户类型 企业
    public static final String USER_TYPE_ENTERPRISE = "11";
    //用户类型 代理人
    public static final String USER_TYPE_AGENT = "12";

    public static class ErrorCode{
        private ErrorCode(){}

        /**
         * 订单领取到达上限
         */
        public static final String NUM_MAX_LIMIT = "100001";
        /**
         * 订单已被领取
         */
        public static final String ALREADY_CLAIM = "100002";
    }
    /**
     * 客户端显示状态
     */
    private static class DisplayState{
        private DisplayState(){}
    }

    private static class State{
        private State(){}
    }
}
