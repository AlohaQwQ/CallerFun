package com.xixi.finance.callerfun.constant;

/**
 * Created by leon on 16/4/4.
 */
public class ServiceAPIConstant {

    //正式环境
   /*  public static String API_BASE_URL = "http://192.168.1.5:8000/crm/app/interface.php?";

    public static String API_BASE_PAGE_URL = "http://192.168.1.5:8000/crm/crm/";*/

    //测试环境
    public static String API_BASE_URL = "http://heehee.com.cn:8000/crm_test/app/interface.php?";

    public static String API_BASE_PAGE_URL = "http://heehee.com.cn:8000/crm_test/crm_test/";

    /* 用户 */
    public static final String REQUEST_API_USER_LOGIN = "action=login";
    public static final String REQUEST_API_USER_LOGOUT = "action=logout";
    public static final String REQUEST_API_CUSTOMER_PAGE = "action=getdetail";

    /* 录音 */
    public static final String REQUEST_API_RECORD_UPLOAD = "action=uploadrecord";
    public static final String REQUEST_API_RECORD_FETCH_RECORDS = "action=getrecordlist";
    public static final String REQUEST_API_RECORD_FETCH_RECORD_DETAIL_MONTH = "action=getrecordanalysis";

    /* 升级 */
    public static final String REQUEST_API_CHECK = "action=checkVersion";

    /* Web */
    public static final String REQUEST_API_CALL_IN_PAGE = "action=callin";
    public static final String REQUEST_API_FETCH_CALL_DETAIL = "action=callindata";

}
