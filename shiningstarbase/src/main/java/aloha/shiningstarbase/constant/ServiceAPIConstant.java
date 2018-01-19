package aloha.shiningstarbase.constant;

/**
 * Created by leon on 16/4/4.
 */
public class ServiceAPIConstant {

    //正式环境
    public static String API_BASE_URL = "https://app.lbdfun.com/lbdApp";

    //public static String API_BASE_URL = "http://192.168.1.207:8082/lbdApp";
    //测试环境
    public static String API_BASE_URL_PUBLIC_TEST = "http://112.74.16.61:8082/lbdApp";

    /* 用户 */
    public static final String REQUEST_API_NAME_USER_REGISTER = "/user/register.json";
    public static final String REQUEST_API_NAME_USER_REGISTER_NEW = "/userLogin/appUserRegister";
    public static final String REQUEST_API_NAME_USER_LOGON = "/user/logon.json";
    public static final String REQUEST_API_NAME_USER_LOGON_NEW = "/userLogin/appUserLogin";
    public static final String REQUEST_API_NAME_USER_UPDATE_PASSWORD = "/user/updatePassword.json";
    public static final String REQUEST_API_NAME_USER_SET_TRANSACT_PASSWORD = "/user/setTransactPassword.json";
    public static final String REQUEST_API_NAME_USER_UPDATE_TRANSACT_PASSWORD = "/user/updateTransactPassword.json";
    public static final String REQUEST_API_NAME_USER_UPDATE_M = "/user/updateM.json";

}
