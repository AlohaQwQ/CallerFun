/**
 * Copyright (c) 2015 Chutong Technologies All rights reserved.
 * <p/>
 * Version Control
 * <p/>
 * | version | date        | author         | description
 * 0.0.1     2015.11.30    shiliang.zou     整理代码
 * 0.0.2     2016.1.6      tianhong.cai     添加getId方法
 * <p/>
 * Version Control
 * <p/>
 * | version | date        | author         | description
 * 0.0.1     2015.11.30    shiliang.zou     整理代码
 * 0.0.2     2016.1.6      tianhong.cai     添加getId方法
 * <p/>
 * Version Control
 * <p/>
 * | version | date        | author         | description
 * 0.0.1     2015.11.30    shiliang.zou     整理代码
 * 0.0.2     2016.1.6      tianhong.cai     添加getId方法
 * <p/>
 * Version Control
 * <p/>
 * | version | date        | author         | description
 * 0.0.1     2015.11.30    shiliang.zou     整理代码
 * 0.0.2     2016.1.6      tianhong.cai     添加getId方法
 */

/**
 * Version Control
 *
 * | version | date        | author         | description
 *   0.0.1     2015.11.30    shiliang.zou     整理代码
 *   0.0.2     2016.1.6      tianhong.cai     添加getId方法
 */

package cn.chutong.sdk.common.util;

import android.text.TextUtils;

import java.util.List;
import java.util.Map;

/**
 * 类型转换工具类
 *
 * @author shiliang.zou
 * @version 0.0.2
 */
public class TypeUtil {
    private static String tempDate;

    public static Map<String, Object> convertStringToMap(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        } else {
            return new CommonJSONParser().parseMap(str);
        }
    }

    public static List<Map<String, Object>> convertStringToList(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        } else {
            return new CommonJSONParser().parseList(str);
        }
    }

    public static String getString(final Object object)
    {
        return getString(object, null);
    }

    public static String getString(final Object object, final String defaultValue) {
        String str = defaultValue;
        if (null != object && object instanceof String)
        {
            str = (String) object;
        } else if (null != object && object instanceof Number)
        {
            str = object + "";
        }
        return str;
    }

    public static boolean getBoolean(final Object object)
    {
        return getBoolean(object, false);
    }

    public static boolean getBoolean(final Object object, final boolean defaultValue)
    {
        boolean b = defaultValue;
        if (null != object && object instanceof Boolean)
        {
            b = (Boolean) object;
        }
        return b;
    }

    public static Integer getInteger(final Object object)
    {
        return getInteger(object, 0);
    }

    public static Integer getInteger(final Object object, final Integer defaultValue)
    {
        Integer i = defaultValue;
        if (null != object && object instanceof Integer)
        {
            i = (Integer) object;
        }
        return i;
    }

    public static Float getFloat(final Object object)
    {
        return getFloat(object, 0.0f);
    }

    public static Float getFloat(final Object object, final float defaultValue)
    {
        float f = defaultValue;
        if (object instanceof Number)
        {
            return ((Number) object).floatValue();
        }
        return f;
    }

    public static Double getDouble(final Object object)
    {
        return getDouble(object, 0);
    }

    public static Double getDouble(final Object object, final double defaultValue)
    {
        double f = defaultValue;
        if (object instanceof Number)
        {
            return ((Double) object).doubleValue();
        }
        return f;
    }

    public static Long getLong(final Object object)
    {
        return getLong(object, null);
    }

    public static Long getLong(final Object object, final Long defaultValue)
    {
        Long l = defaultValue;
        if (null != object)
        {

            if (object instanceof Long)
            {
                l = (Long) object;
            } else if (object instanceof Integer)
            {
                l = Long.parseLong(((Integer) object).toString());
            }
        }
        return l;
    }

    /**
     * Created by Aloha <br>
     * -explain 对应 json obj
     * @Date 2016/11/29 15:47
     */
    public static Map<String, Object> getMap(final Object object) {
        return getMap(object, null);
    }

    public static Map<String, Object> getMap(final Object object, final Map<String, Object> defaultValue) {
        Map<String, Object> map = defaultValue;
        if (null != object && object instanceof Map<?, ?>) {
            map = (Map<String, Object>) object;
        }
        return map;
    }

    /**
     * Created by Aloha <br>
     * -explain 对应 json array
     * @Date 2016/11/29 15:47
     */
    public static List<Map<String, Object>> getList(final Object object) {
        return getList(object, null);
    }

    public static List<Map<String, Object>> getList(final Object object, final List<Map<String, Object>> defaultValue) {
        List<Map<String, Object>> list = defaultValue;
        if (null != object && object instanceof List<?>) {
            list = (List<Map<String, Object>>) object;
        }
        return list;
    }

    public static String getId(Object object)
    {
        return object != null ? object.toString() : "";
    }}
