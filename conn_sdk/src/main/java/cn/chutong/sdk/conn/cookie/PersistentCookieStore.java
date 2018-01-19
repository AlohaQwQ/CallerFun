package cn.chutong.sdk.conn.cookie;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 持久化CookieStore
 *
 * 主要功能：
 *  1.持久化缓存Cookie数据至本地
 *
 * @author shiliang.zou
 * @version 0.0.1
 */
public class PersistentCookieStore implements CookieStore {

    private static final String LOG_TAG = "PersistentCookieStore";
    private static final String COOKIE_PREFS = "Cookie_Prefs";
    private static final String COOKIE_NAME_PREFIX = "cookie_";

    private final HashMap<String, ConcurrentHashMap<String, HttpCookie>> cookies;
    private final SharedPreferences cookiePrefs;

    public PersistentCookieStore(Context context) {
        cookiePrefs = context.getSharedPreferences(COOKIE_PREFS, Context.MODE_PRIVATE);
        cookies = new HashMap<>();

        //将持久化的cookies缓存到内存中 即map cookies
        Map<String, ?> prefsMap = cookiePrefs.getAll();
        for (Map.Entry<String, ?> prefsEntry : prefsMap.entrySet()) {
            if (prefsEntry.getValue() instanceof String) {
                String key = prefsEntry.getKey();
                String value = (String) prefsEntry.getValue();

                if (null != value && value.startsWith(COOKIE_NAME_PREFIX)) {
                    String[] cookieNames = TextUtils.split(value, ",");
                    for (String name : cookieNames) {
                        String encodeCookie = cookiePrefs.getString(COOKIE_NAME_PREFIX + name, null);
                        if (null != encodeCookie) {
                            HttpCookie decodeCookie = decodeCookie(encodeCookie);
                            if (null != decodeCookie) {
                                if (!cookies.containsKey(key)) {
                                    cookies.put(key, new ConcurrentHashMap<String, HttpCookie>());
                                }
                                cookies.get(key).put(name, decodeCookie);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void add(URI uri, HttpCookie cookie) {
        String name = getCookieToken(cookie);

        //将cookies缓存到内存中 如果缓存过期 就重置此cookie
        if (!cookie.hasExpired()) {
            if (!cookies.containsKey(uri.getHost())) {
                cookies.put(uri.getHost(), new ConcurrentHashMap<String, HttpCookie>());
            }
            cookies.get(uri.getHost()).put(name, cookie);
        } else {
            if (cookies.containsKey(uri.getHost())) {
                cookies.get(uri.getHost()).remove(name);
            }
        }

        //将cookies持久化到本地
        SharedPreferences.Editor prefsWriter = cookiePrefs.edit();
        prefsWriter.putString(uri.getHost(), TextUtils.join(",", cookies.get(uri.getHost()).keySet()));
        prefsWriter.putString(COOKIE_NAME_PREFIX + name, encodeCookie(new SerializableOkHttpCookie(cookie)));
        prefsWriter.apply();
    }

    protected String getCookieToken(HttpCookie cookie) {
        return cookie.getName() + cookie.getDomain();
    }

    @Override
    public List<HttpCookie> get(URI uri) {
        ArrayList<HttpCookie> ret = new ArrayList<>();

        if (cookies.containsKey(uri.getHost())) {
            ret.addAll(cookies.get(uri.getHost()).values());
        }
        return ret;
    }

    @Override
    public List<HttpCookie> getCookies() {
        ArrayList<HttpCookie> ret = new ArrayList<>();

        for (String key : cookies.keySet()) {
            ret.addAll(cookies.get(key).values());
        }
        return ret;
    }

    @Override
    public List<URI> getURIs() {
        ArrayList<URI> ret = new ArrayList<>();

        for (String key : cookies.keySet()) {
            try {
                ret.add(new URI(key));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    @Override
    public boolean remove(URI uri, HttpCookie cookie) {
        String name = getCookieToken(cookie);

        if (cookies.containsKey(uri.getHost()) && cookies.get(uri.getHost()).containsKey(name)) {
            cookies.get(uri.getHost()).remove(name);

            SharedPreferences.Editor prefsWriter = cookiePrefs.edit();
            if (cookiePrefs.contains(COOKIE_NAME_PREFIX + name)) {
                prefsWriter.remove(COOKIE_NAME_PREFIX + name);
            }
            prefsWriter.putString(uri.getHost(), TextUtils.join(",", cookies.get(uri.getHost()).keySet()));
            prefsWriter.apply();

            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean removeAll() {
        SharedPreferences.Editor prefsWriter = cookiePrefs.edit();
        prefsWriter.clear();
        prefsWriter.apply();
        cookies.clear();
        return true;
    }

    /**
     * cookie 序列化成 string
     *
     * @param cookie 要序列化的cookie
     * @return 序列化之后的string
     */
    protected String encodeCookie(SerializableOkHttpCookie cookie) {
        if (null == cookie)
            return null;

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(cookie);
        } catch (IOException e) {
            Log.d(LOG_TAG, "IOException in encodeCookie", e);
            return null;
        }

        return byteArrayToHexString(byteArrayOutputStream.toByteArray());
    }

    /**
     * 将字符串反序列化成cookies
     *
     * @param cookieString 待序列化字符串
     * @return 序列化cookie
     */
    protected HttpCookie decodeCookie(String cookieString) {
        HttpCookie cookie = null;
        byte[] bytes = hexStringToByteArray(cookieString);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            cookie = ((SerializableOkHttpCookie) objectInputStream.readObject()).getCookie();
        } catch (IOException e) {
            Log.d(LOG_TAG, "IOException in decodeCookie", e);
        } catch (ClassNotFoundException e) {
            Log.d(LOG_TAG, "ClassNotFoundException in decodeCookie", e);
        }

        return cookie;
    }

    /**
     * 二进制数组转十六进制字符串
     *
     * @param bytes 待转换二进制数组
     * @return 十六进制字符串
     */
    protected String byteArrayToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte element : bytes) {
            int v = element & 0xff;
            if (v < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString().toUpperCase(Locale.US);
    }

    /**
     * 十六进制字符串转二进制数组
     *
     * @param hexString 待转换十六进制字符串
     * @return 二进制数组
     */
    protected byte[] hexStringToByteArray(String hexString) {
        int len = hexString.length();
        byte[] data = new byte[len / 2];

        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4) + Character.digit(hexString.charAt(i + 1), 16));
        }
        return data;
    }

}
