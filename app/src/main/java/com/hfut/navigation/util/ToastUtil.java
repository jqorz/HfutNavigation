package com.hfut.navigation.util;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
    /**
     * Toast方法
     *
     * @param text 需要展示的文本
     * @param context  所需上下文
     */

    private static Toast mToast = null;

    public static void showToast(Context context, String text) {
        if (mToast == null) {
            mToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(text);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }


}
