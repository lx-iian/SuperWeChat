package com.zhou.superwechat.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;

import com.hyphenate.easeui.domain.User;
import com.zhou.superwechat.I;
import com.zhou.superwechat.R;
import com.zhou.superwechat.ui.AddContactActivity;
import com.zhou.superwechat.ui.AddFriendActivity;
import com.zhou.superwechat.ui.ChatActivity;
import com.zhou.superwechat.ui.FriendProfileActivity;
import com.zhou.superwechat.ui.GroupsActivity;
import com.zhou.superwechat.ui.GuideActivity;
import com.zhou.superwechat.ui.LoginActivity;
import com.zhou.superwechat.ui.NewFriendsMsgActivity;
import com.zhou.superwechat.ui.NewGroupActivity;
import com.zhou.superwechat.ui.PublicGroupsActivity;
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

    public static void gotoAddFriend(Activity context) {
        startActivity(context, AddContactActivity.class);
    }

    public static void gotoFriendProfile(Activity context, User user) {
        Intent intent = new Intent();
        intent.setClass(context, FriendProfileActivity.class);
        intent.putExtra(I.User.USER_NAME, user);
        startActivity(context, intent);
    }

    public static void gotoAddFriend(Activity context, String username) {
        Intent intent = new Intent();
        intent.setClass(context, FriendProfileActivity.class);
        intent.putExtra(I.User.USER_NAME, AddFriendActivity.class);
        startActivity(context, intent);
    }

    public static void gotoNewFriendsMsg(Activity context) {
        startActivity(context, NewFriendsMsgActivity.class);
    }

    public static void gotoChat(Activity context,String username) {
        Intent intent = new Intent();
        intent.setClass(context, ChatActivity.class);
        intent.putExtra("userId", username);
        startActivity(context, intent);
    }


    public static void gotoGroup(Activity context) {
        startActivity(context, GroupsActivity.class);
    }

    public static void gotoCreateNewGroup(Activity context) {
        startActivity(context, NewGroupActivity.class);
    }

    public static void gotoPublicGroup(Activity context) {
        startActivity(context, PublicGroupsActivity.class);
    }
}