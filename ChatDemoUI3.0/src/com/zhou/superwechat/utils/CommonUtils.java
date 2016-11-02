package com.zhou.superwechat.utils;

import android.widget.Toast;

import com.zhou.superwechat.I;
import com.zhou.superwechat.R;
import com.zhou.superwechat.SuperWeChatApplication;


/**
 * Created by clawpo on 16/9/20.
 */
public class CommonUtils {
    public static void showLongToast(String msg) {
        Toast.makeText(SuperWeChatApplication.applicationContext, msg, Toast.LENGTH_LONG).show();
    }

    public static void showShortToast(String msg) {
        Toast.makeText(SuperWeChatApplication.applicationContext, msg, Toast.LENGTH_SHORT).show();
    }

    public static void showLongToast(int rId) {
        showLongToast(SuperWeChatApplication.applicationContext.getString(rId));
    }

    public static void showShortToast(int rId) {
        showShortToast(SuperWeChatApplication.applicationContext.getString(rId));
    }

    public static void showMsgShortToast(int msgId) {
        if (msgId > 0) {
            showShortToast(SuperWeChatApplication.getInstance().getResources()
                    .getIdentifier(I.MSG_PREFIX_MSG + msgId, "string",
                            SuperWeChatApplication.getInstance().getPackageCodePath()));
        } else {
            showShortToast(R.string.msg_1);
        }
    }

  /*    public static void showLongResultMsg(int msg) {
        showLongToast(getMsgString(msg));
    }

    public static void showShortResultMsg(int msg) {
        showShortToast(getMsgString(msg));
    }

  private static int getMsgString(int msg) {
        int resId = R.string.msg_1;
        if(msg>0){
            resId = SuperWeChatApplication.getInstance().getResources()
                    .getIdentifier(I.MSG_PREFIX_MSG+msg,"string",
                            SuperWeChatApplication.getInstance().getPackageName());
        }
        return resId;
    }

    public static String getWeChatNoString(){
        return SuperWeChatApplication.getInstance().getString(R.string.userinfo_txt_wechat_no);
    }

    public static String getAddContactPrefixString(){
        return SuperWeChatApplication.getInstance().getString(R.string.addcontact_send_msg_prefix);
    }*/
}