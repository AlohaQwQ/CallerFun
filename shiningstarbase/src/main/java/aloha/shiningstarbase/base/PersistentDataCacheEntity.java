package aloha.shiningstarbase.base;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

import aloha.shiningstarbase.constant.APIKey;
import cn.chutong.sdk.common.util.TypeUtil;
import cn.chutong.sdk.common.util.Validator;

/**
 * Created by Aloha <br>
 * -explain 用户信息存储
 * @Date 2017/2/7 10:32
 */
public class PersistentDataCacheEntity {

    private static PersistentDataCacheEntity instance;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Map<String, Object> loginUser;
    private String userId;
    private String userMobile;
    private boolean isHaveTransactPassword = false;
    private String value;
    private Context mContext;
    private String deviceId;

    /**
     * App版本号name
     */
    private String versionNameApp;

    /**
     * App版本号code
     */
    private int versionCode;

    /**
     * App渠道号
     */
    private String channelApp;

    /**
     * 埋点统计校验签名
     */
    private String statisticsSign;
    /**
     * 操作系统版本
     */
    private String osVersion;
    /**
     * 设备类型
     */
    private String mobileModel;

    /**
     * 埋点信息
     */
    private long id;
    private String username;
    private String token;
    private String name;
    private String idcard;
    private String sex;
    private int    isOcr;
    private String qq;
    private String email;
    private String alipay;
    private String idcardAddress;
    private String xuexinAccount;
    private String xuexinPassword;

    /**
     * 用户信息完整度
     */
    private int hasUser;//有用户信息
    private int hasUserPhoto; //有上传照片
    private int hasUserOrgInfo;//基本信息填写
    private int hasContactGet;//有联系人授权记录

    /**
     * 超级窗口跳转
     */
    private String productCode;//资方code
    private String productID;//资方id

    private PersistentDataCacheEntity(Context context) {
        mContext = context;
        sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static PersistentDataCacheEntity getInstance() {
        if (null == instance) {
            instance = new PersistentDataCacheEntity(MyApplication.getInstance().getApplicationContext());
        }
        return instance;
    }

    /**
     * Created by Aloha <br>
     * -explain 校验签名sign
     * @Date 2017/2/7 13:52
     */
    public void setEncodeSign(String sign) {
        editor.putString(APIKey.COMMON_ENCODE_SIGN, sign);
        editor.commit();
    }

    public String getEncodeSign() {
        return sharedPreferences.getString(APIKey.COMMON_ENCODE_SIGN, "");
    }

    /**
     * 更新登录用户的某个参数信息
     *
     * @param keyInfo
     * @param valueInfo
     */
    public void updateLoginUserParam(String keyInfo, String valueInfo) {
        Map<String, Object> loginUser = getLoginUser();
        if (null != getLoginUser()) {
            if (!TextUtils.isEmpty(keyInfo)) {
                boolean hasCommitted = editor.putString(keyInfo, valueInfo).commit();
                if (hasCommitted) {
                    loginUser.put(keyInfo, valueInfo);
                }
            }
        }
    }

    public boolean isFirstUse() {
        return sharedPreferences.getBoolean("isFirst", true);
    }

    public void setFirstUse(boolean isFirstUse) {
        editor.putBoolean("isFirst", isFirstUse);
        editor.commit();
    }

    public boolean isLogin() {
        if (null == getUserId() || getUserId().equals("") || getToken() == null || getToken().equals("")) {
            return false;
        } else {
            return true;
        }
    }

    public Map<String, Object> getLoginUser() {
        if (loginUser == null) {
            String accountId = sharedPreferences.getString(APIKey.COMMON_ID, "");
            if (!Validator.isIdValid(accountId)) {
                loginUser = null;
            } else {
                loginUser = new HashMap<>();
                loginUser.put(APIKey.COMMON_ID, sharedPreferences.getString(APIKey.COMMON_ID, ""));
                loginUser.put(APIKey.USER_MOBILE, sharedPreferences.getString(APIKey.USER_MOBILE, ""));

                //                loginUser.put(APIKey.USER_USER_NAME, sharedPreferences.getString(APIKey.USER_USER_NAME, ""));
                //                loginUser.put(APIKey.USER_NAME, sharedPreferences.getString(APIKey.USER_NAME, ""));
                //                loginUser.put(APIKey.USER_IDCARD, sharedPreferences.getString(APIKey.USER_IDCARD, ""));
                //                loginUser.put(APIKey.USER_HOME_ADDRESS, sharedPreferences.getString(APIKey.USER_HOME_ADDRESS, ""));
                //                loginUser.put(APIKey.USER_DORM_ADDRESS, sharedPreferences.getString(APIKey.USER_DORM_ADDRESS, ""));
                //                loginUser.put(APIKey.USER_QQ, sharedPreferences.getString(APIKey.USER_QQ, ""));
                //                loginUser.put(APIKey.USER_EMAIL, sharedPreferences.getString(APIKey.USER_EMAIL, ""));
                //                loginUser.put(APIKey.COMMON_STATUS, sharedPreferences.getInt(APIKey.COMMON_STATUS, 0));
                //                loginUser.put(APIKey.USER_BORROW_STATUS, sharedPreferences.getInt(APIKey.USER_BORROW_STATUS, 0));
                //                loginUser.put(APIKey.USER_LAST_LOGIN_AT, sharedPreferences.getString(APIKey.USER_LAST_LOGIN_AT, ""));
                //                loginUser.put(APIKey.COMMON_CREATE_AT, sharedPreferences.getString(APIKey.COMMON_CREATE_AT, ""));

                loginUser.put(APIKey.USER_TOKEN, sharedPreferences.getString(APIKey.USER_TOKEN, ""));
                loginUser.put("isHaveTransactPassword", sharedPreferences.getBoolean("isHaveTransactPassword", false));
            }
        }
        return loginUser;
    }

    public void setLoginUser(Map<String, Object> loginUser) {
        this.loginUser = loginUser;
        if (loginUser != null) {
            Object userIdOj = loginUser.get(APIKey.COMMON_ID);
            String userId = null;

            if (userIdOj != null) {
                userId = userIdOj.toString();
            }

            if (Validator.isIdValid(userId)) {
                /**
                 * Created by Aloha <br>
                 * -explain 登录存储用户信息
                 * @Date 2016/11/1 14:08
                 */
                editor.putString(APIKey.COMMON_ID, userId);
                editor.putString(APIKey.USER_ID, TypeUtil.getString(loginUser.get(APIKey.USER_ID), ""));
                editor.putString(APIKey.USER_TOKEN, TypeUtil.getString(loginUser.get(APIKey.USER_TOKEN), ""));
                editor.putString(APIKey.USER_MOBILE, TypeUtil.getString(loginUser.get(APIKey.USER_MOBILE), ""));
                editor.putString(APIKey.USER_NAME, TypeUtil.getString(loginUser.get(APIKey.USER_NAME), ""));
                editor.putString(APIKey.USER_SEX, TypeUtil.getString(loginUser.get(APIKey.USER_SEX), ""));

                //                editor.putString(APIKey.USER_USER_NAME, TypeUtil.getString(loginUser.get(APIKey.USER_USER_NAME), ""));
                //                editor.putString(APIKey.USER_NAME, TypeUtil.getString(loginUser.get(APIKey.USER_NAME), ""));
                //                editor.putString(APIKey.USER_IDCARD, TypeUtil.getString(loginUser.get(APIKey.USER_IDCARD), ""));
                //                editor.putString(APIKey.USER_HOME_ADDRESS, TypeUtil.getString(loginUser.get(APIKey.USER_HOME_ADDRESS), ""));
                //                editor.putString(APIKey.USER_DORM_ADDRESS, TypeUtil.getString(loginUser.get(APIKey.USER_DORM_ADDRESS), ""));
                //                editor.putString(APIKey.USER_QQ, TypeUtil.getString(loginUser.get(APIKey.USER_QQ), ""));
                //                editor.putString(APIKey.USER_EMAIL, TypeUtil.getString(loginUser.get(APIKey.USER_EMAIL), ""));
                //                editor.putInt(APIKey.COMMON_STATUS, TypeUtil.getInteger(loginUser.get(APIKey.COMMON_STATUS), 0));
                //                editor.putInt(APIKey.USER_BORROW_STATUS, TypeUtil.getInteger(loginUser.get(APIKey.USER_BORROW_STATUS), 0));
                //                editor.putString(APIKey.USER_LAST_LOGIN_AT, TypeUtil.getString(loginUser.get(APIKey.USER_LAST_LOGIN_AT), ""));
                //                editor.putString(APIKey.COMMON_CREATE_AT, TypeUtil.getString(loginUser.get(APIKey.COMMON_CREATE_AT), ""));

                String transactPassword = TypeUtil.getString(loginUser.get(APIKey.USER_TRANSACT_PASSWORD));
                if (null == transactPassword || transactPassword.equals("")) {
                    editor.putBoolean("isHaveTransactPassword", false);
                } else {
                    editor.putBoolean("isHaveTransactPassword", true);
                }

                editor.commit();
            }
        } else {
            editor.putString(APIKey.COMMON_ID, null);
            editor.putString(APIKey.USER_MOBILE, null);

            //            editor.putString(APIKey.USER_USER_NAME, null);
            //            editor.putString(APIKey.USER_NAME, null);
            //            editor.putString(APIKey.USER_IDCARD, null);
            //            editor.putString(APIKey.USER_HOME_ADDRESS, null);
            //            editor.putString(APIKey.USER_DORM_ADDRESS, null);
            //            editor.putString(APIKey.USER_QQ, null);
            //            editor.putString(APIKey.USER_EMAIL, null);
            //            editor.putInt(APIKey.COMMON_STATUS, 0);
            //            editor.putInt(APIKey.USER_BORROW_STATUS, 0);
            //            editor.putString(APIKey.USER_LAST_LOGIN_AT, null);
            //            editor.putString(APIKey.COMMON_CREATE_AT, null);

            editor.putString(APIKey.USER_TOKEN, null);
            editor.putBoolean("isHaveTransactPassword", false);
            editor.commit();
        }
    }

    public String getUserId() {
        userId = sharedPreferences.getString(APIKey.COMMON_ID, "");
        if(TextUtils.isEmpty(userId)){
            //Toast.makeText(mContext,"用户令牌失效,为避免不必要的错误,请大人重新登录。",Toast.LENGTH_SHORT).show();
            return "";
        } else {
            return userId;
        }
    }

    public void setUserId(String userId) {
        this.userId = userId;
        editor.putString(APIKey.COMMON_ID, userId);
        editor.commit();
    }

    public String getUserMobile() {
        userMobile = sharedPreferences.getString(APIKey.USER_MOBILE, "");
        return userMobile;
    }

    public void setUserMobile(String userMobile) {
        this.userMobile = userMobile;
        editor.putString(APIKey.USER_MOBILE, userMobile);
        editor.commit();
    }

    public String getToken() {
        token = sharedPreferences.getString(APIKey.USER_TOKEN, "");
        if(TextUtils.isEmpty(token)){
            //Toast.makeText(mContext,"用户令牌失效,为避免不必要的错误,请大人重新登录。",Toast.LENGTH_SHORT).show();
            return "";
        } else {
            return token;
        }
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
        editor.putString(APIKey.CONFIG_OS_VERSION, osVersion);
        editor.commit();
    }

    public String getOsVersion() {
        osVersion = sharedPreferences.getString(APIKey.CONFIG_OS_VERSION, "");
        return osVersion;
    }

    public void setMobileModel(String mobileModel) {
        this.mobileModel = mobileModel;
        editor.putString(APIKey.CONFIG_MOBILE_MODEL, mobileModel);
        editor.commit();
    }

    public String getMobileModel() {
        mobileModel = sharedPreferences.getString(APIKey.CONFIG_MOBILE_MODEL, "");
        return mobileModel;
    }

    public void setToken(String token) {
        this.token = token;
        editor.putString(APIKey.USER_TOKEN, token);
        editor.commit();
    }

    public String getName() {
        name = sharedPreferences.getString(APIKey.USER_NAME, "");
        return name;
    }


    public boolean isHaveTransactPassword() {
        isHaveTransactPassword = sharedPreferences.getBoolean("isHaveTransactPassword", false);
        return isHaveTransactPassword;
    }

    public void setHaveTransactPassword(boolean isHaveTransactPassword) {
        this.isHaveTransactPassword = isHaveTransactPassword;
        editor.putBoolean("isHaveTransactPassword", isHaveTransactPassword);
        editor.commit();
    }

    public void setValue(String value) {
        this.value = value;
        editor.putString(APIKey.CONFIG_VALUE, value);
        editor.commit();
    }

    public String getValue() {
        value = sharedPreferences.getString(APIKey.CONFIG_VALUE, "");
        return value;
    }

    public String getVersionNameApp() {
        return versionNameApp;
    }

    public void setVersionNameApp(String versionNameApp) {
        this.versionNameApp = versionNameApp;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getChannelApp() {
        return channelApp;
    }

    public void setChannelApp(String channelApp) {
        this.channelApp = channelApp;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
        editor.putString(APIKey.CONFIG_DEVICE_ID, deviceId);
        editor.commit();
    }

    public String getDeviceId() {
        deviceId = sharedPreferences.getString(APIKey.CONFIG_DEVICE_ID, "");
        return deviceId;
    }

}
