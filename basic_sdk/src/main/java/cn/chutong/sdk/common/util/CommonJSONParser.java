package cn.chutong.sdk.common.util;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CommonJSONParser {

    public Map<String, Object> parseMap(String jsonStr) {

        Map<String, Object> result = null;

        if (null != jsonStr) {
            try {

                JSONObject jsonObject = new JSONObject(jsonStr);
                result = parseJSONObject(jsonObject);

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } // if (null != jsonStr)

        return result;
    }

    public List<Map<String, Object>> parseList(String str) {
        List<Map<String, Object>> result = null;
        if (!TextUtils.isEmpty(str)) {
            try {
                JSONArray jsonArray = new JSONArray(str);
                if (null != jsonArray) {
                    result = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Map<String, Object> data = new HashMap<>();
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                        Iterator it = jsonObject.keys();
                        while (it.hasNext()) {
                            String key = String.valueOf(it.next());
                            String value = (String) jsonObject.get(key);
                            data.put(key, value);
                        }
                        result.add(data);
                    }
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } // if (null != jsonStr)
        return result;
    }

    public Map<String, Object> parse(String jsonStr) {
        Map<String, Object> result = null;
        if (null != jsonStr) {
            try {
                JSONObject jsonObject = new JSONObject(jsonStr);
                result = parseJSONObject(jsonObject);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } // if (null != jsonStr)
        return result;
    }

    private Object parseValue(Object inputObject) throws JSONException {
        Object outputObject = null;
        if (null != inputObject) {
            if (inputObject instanceof JSONArray) {
                outputObject = parseJSONArray((JSONArray) inputObject);
            } else if (inputObject instanceof JSONObject) {
                outputObject = parseJSONObject((JSONObject) inputObject);
            } else if (inputObject instanceof String || inputObject instanceof Boolean || inputObject instanceof Long || inputObject instanceof Integer || inputObject instanceof Float || inputObject instanceof Double) {
                outputObject = inputObject;
            }
        }
        return outputObject;
    }

    private List<Object> parseJSONArray(JSONArray jsonArray) throws JSONException {
        List<Object> valueList = null;
        if (null != jsonArray) {
            valueList = new ArrayList<Object>();

            for (int i = 0; i < jsonArray.length(); i++) {
                Object itemObject = jsonArray.get(i);
                if (null != itemObject) {
                    valueList.add(parseValue(itemObject));
                }
            } // for (int i = 0; i < jsonArray.length(); i++)
        } // if (null != valueStr)
        return valueList;
    }

    private Map<String, Object> parseJSONObject(JSONObject jsonObject) throws JSONException {
        Map<String, Object> valueObject = null;
        if (null != jsonObject) {
            valueObject = new HashMap<String, Object>();
            Iterator<String> keyIter = jsonObject.keys();
            while (keyIter.hasNext()) {
                String keyStr = keyIter.next();
                Object itemObject = jsonObject.opt(keyStr);
                if (null != itemObject) {
                    valueObject.put(keyStr, parseValue(itemObject));
                } // if (null != itemValueStr)

            } // while (keyIter.hasNext())
        } // if (null != valueStr)
        return valueObject;
    }

    /**
     * Json to List
     * @param jsonStr
     * @return
     * @throws JSONException
     */
    public static List<String> parseJsonToList(String jsonStr){
        List<String> dataList = new ArrayList<>();
        if (!TextUtils.isEmpty(jsonStr)) {
            try {
                JSONArray jsonArray = new JSONArray(jsonStr);
                for (int i = 0; i < jsonArray.length(); i++) {
                    dataList.add((String) jsonArray.get(i));
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return dataList;
    }

    /**
     * Json to List
     * @param jsonStr
     * @return
     * @throws JSONException
     */
    public static List<Map<String, String>> parseJsonToListMap(String jsonStr){
        List<Map<String, String>> valueObject = new ArrayList<>();
        if (!TextUtils.isEmpty(jsonStr)) {
            try {
                JSONArray jsonArray = new JSONArray(jsonStr);
                for (int i = 0; i < jsonArray.length(); i++) {
                    Map<String, String> jsonMap = new HashMap<>();
                    JSONObject object = (JSONObject) jsonArray.get(i);
                    Iterator iterator = object.keys();
                    String key;
                    String value ;
                    while (iterator.hasNext()) {
                        key = (String) iterator.next();
                        value = object.getString(key);
                        jsonMap.put(key, value);
                    }
                    valueObject.add(jsonMap);
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return valueObject;
    }
}
