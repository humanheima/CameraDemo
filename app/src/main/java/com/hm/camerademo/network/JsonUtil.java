package com.hm.camerademo.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

public class JsonUtil {

    private static Gson gson = new Gson();

    private static GsonBuilder gb =new GsonBuilder();

    public static String toString(Object object) {
        gb.disableHtmlEscaping();
        return gb.create().toJson(object);
    }

    public static <T> T toObject(String json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }

    public static <T> T toObjectByType(String json, Type typeOfT) {
        return gson.fromJson(json, typeOfT);
    }

    public static Map<String, String> toMap(String json) {
        return gson.fromJson(json, new TypeToken<Map<String, String>>() {
        }.getType());
    }
}