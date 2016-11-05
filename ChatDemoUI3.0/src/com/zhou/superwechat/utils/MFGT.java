package com.zhou.superwechat.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.zhou.superwechat.R;
import com.zhou.superwechat.ui.GuideActivity;
import com.zhou.superwechat.ui.LoginActivity;
import com.zhou.superwechat.ui.RegisterActivity;
import com.zhou.superwechat.ui.SettingsActivity;
import com.zhou.superwechat.ui.UserProfileActivity;

public class MFGT {
    public static void finish(Activity activity) {
        activity.finish();
        activity.overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    public static void startActivity(Activity context, Class<?> cls) {
        Intent intent = new Intent();
        intent.setClass(context, cls);
        startActivity(context, intent);
    }

    public static void startActivity(Context context, Intent intent) {
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    public static void startActivityForResult(Activity context, Intent intent, int requestCode) {
        context.startActivityForResult(intent, requestCode);
        context.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    public static void gotoGuide(Activity context) {
        startActivity(context, GuideActivity.class);
    }

    public static void gotoLogin(Activity context) {
        startActivity(context, LoginActivity.class);
    }

    public static void gotoRegister(Activity context) {
        startActivity(context, RegisterActivity.class);
    }

    public static void gotoSetting(Activity context) {
        startActivity(context, SettingsActivity.class);
    }

    public static void gotoUserProfile(Activity context) {
        startActivity(context, UserProfileActivity.class);
    }
}