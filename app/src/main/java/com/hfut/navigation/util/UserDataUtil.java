package com.hfut.navigation.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class UserDataUtil {
    private static final String dataFileName = "userData";
    private static final String searchHistory = "searchHistory";
    private static SharedPreferences data;

    /**
     * 读取本地用户信息
     *
     * @param context 所需上下文
     * @param input   所需要的内容
     * @return 所读取的内容
     */
    public static String loadStringData(Context context, String input) {
        data = context.getSharedPreferences(dataFileName, Context.MODE_PRIVATE);
        return data.getString(input, "");
    }

    public static Boolean loadBoolData(Context context, String input) {
        data = context.getSharedPreferences(dataFileName, Context.MODE_PRIVATE);
        return data.getBoolean(input, false);
    }

    //默认True的Boolean
    public static Boolean loadTrueBoolData(Context context, String input) {
        data = context.getSharedPreferences(dataFileName, Context.MODE_PRIVATE);
        return data.getBoolean(input, true);
    }

    public static int loadIntData(Context context, String input) {
        data = context.getSharedPreferences(dataFileName, Context.MODE_PRIVATE);
        return data.getInt(input, 0);
    }

    /**
     * 读取搜索历史信息
     *
     * @param context 所需上下文
     * @return 所读取的内容
     */
    public static String loadHistoryData(Context context) {
        data = context.getSharedPreferences(searchHistory, Context.MODE_PRIVATE);
        return data.getString(ConstantUtil.sp_SearchHistory, "");
    }

    /**
     * 更改本地用户信息
     *
     * @param context 所需上下文
     * @param key     想要更改的项
     * @param content 想要更改的内容
     */
    public static void updateLocalData(Context context, String key,
                                       String content) {

        data = context.getSharedPreferences(dataFileName, Context.MODE_PRIVATE);
        Editor editor = data.edit();
        editor.putString(key, content);
        editor.apply();
    }

    public static void updateLocalData(Context context, String key,
                                       Boolean flag) {

        data = context.getSharedPreferences(dataFileName, Context.MODE_PRIVATE);
        Editor editor = data.edit();
        editor.putBoolean(key, flag);
        editor.apply();

    }

    public static void updateLocalData(Context context, String key,
                                       int m) {

        data = context.getSharedPreferences(dataFileName, Context.MODE_PRIVATE);
        Editor editor = data.edit();
        editor.putInt(key, m);
        editor.apply();

    }

    /**
     * 更改搜索历史信息
     *
     * @param context 所需上下文
     * @param key     想要更改的项
     * @param content 想要更改的内容
     */
    public static void updateHistoryData(Context context, String key,
                                         String content) {

        data = context.getSharedPreferences(searchHistory, Context.MODE_PRIVATE);
        Editor editor = data.edit();
        editor.putString(key, content);
        editor.apply();
    }
}
