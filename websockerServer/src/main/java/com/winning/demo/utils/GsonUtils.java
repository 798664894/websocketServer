package com.winning.demo.utils;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.winning.demo.common.ParameterizedTypeImpl;
import org.springframework.util.StringUtils;



import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GsonUtils {
    /**
     * json字符串转成实体列表
     * @param gsonStr JSON字符串
     * @param clazz 需要转换成的类
     * @param <T> 类泛型
     * @return 实体列表
     */
    public static <T> List<T> parseGsonToList(String gsonStr, Class<T> clazz){
        if (StringUtils.isEmpty(gsonStr)|| null == clazz) return null;
        Gson gson = new Gson();
        Type listType = new ParameterizedTypeImpl(List.class, new Class[]{clazz});
        return gson.fromJson(gsonStr, listType);
    }

    /**
     * bean转成json字符串
     * @param object 实体对象
     * @return JSON字符串
     */
    public static String createGsonString(Object object) {
        if (null == object) return null;
        Gson gson = new Gson();
        return gson.toJson(object);
    }

    /**
     * json转成bean
     * @param gsonStr JSON字符串
     * @param clazz 需要转换成的类
     * @param <T> 类泛型
     * @return 实体对象
     */
    public static <T> T changeGsonToBean(String gsonStr, Class<T> clazz) {
        try {
            if (StringUtils.isEmpty(gsonStr)|| null == clazz) return null;
            Gson gson = new Gson();
            return gson.fromJson(gsonStr, clazz);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 函数名称: parseData
     * 函数描述: 将json字符串转换为map
     * @param data
     * @return map对象
     */
    public static Map<String, String> parseGsonToMap(String data){
        GsonBuilder gb = new GsonBuilder();
        Gson g = gb.create();
        Map<String, String> map = g.fromJson(data, new TypeToken<Map<String, String>>() {}.getType());
        return map;
    }

    /**
     * 将JSON字符串转换成Map对象
     * @param jsonStr JSON字符串
     * @return Map对象
     */
    public static Map<String, Object> converJsonStrToMap(String jsonStr) {
        return new Gson().fromJson(jsonStr, new com.google.gson.reflect.TypeToken<HashMap<String,Object>>(){}.getType());
    }
}
