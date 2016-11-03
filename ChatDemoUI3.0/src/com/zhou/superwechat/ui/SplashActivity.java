package com.zhou.superwechat.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.domain.User;
import com.zhou.superwechat.SuperWeChatHelper;
import com.zhou.superwechat.R;
import com.zhou.superwechat.db.UserDao;
import com.zhou.superwechat.utils.L;
import com.zhou.superwechat.utils.MFGT;

/**
 * 开屏页
 */
public class SplashActivity extends BaseActivity {
    private static final String TAG = SplashActivity.class.getSimpleName();
    private boolean autoLogin;
    private static final int sleepTime = 2000;
    private SplashActivity mContext;

    @Override
    protected void onCreate(Bundle arg0) {
        setContentView(R.layout.em_activity_splash);
        super.onCreate(arg0);
        mContext = this;

     /*   RelativeLayout rootLayout = (RelativeLayout) findViewById(R.id.splash_root);
        TextView versionText = (TextView) findViewById(R.id.tv_version);

        versionText.setText(getVersion());
        AlphaAnimation animation = new AlphaAnimation(0.3f, 1.0f);
        animation.setDuration(1500);
        rootLayout.startAnimation(animation);*/
        L.e(TAG);
    }

    @Override
    protected void onStart() {
        super.onStart();

        new Thread(new Runnable() {
            public void run() {
                if (SuperWeChatHelper.getInstance().isLoggedIn()) {
                    // auto login mode, make sure all group and conversation is loaed before enter the main screen
                    long start = System.currentTimeMillis();
                    EMClient.getInstance().groupManager().loadAllGroups();
                    EMClient.getInstance().chatManager().loadAllConversations();
                    UserDao dao = new UserDao(mContext);
                    User user = dao.getUser(EMClient.getInstance().getCurrentUser());
                    L.e(TAG, "user = " + user);
                    SuperWeChatHelper.getInstance().setCurrentUser(user);
                    long costTime = System.currentTimeMillis() - start;
                    //wait
                    if (sleepTime - costTime > 0) {
                        try {
                            Thread.sleep(sleepTime - costTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (user == null) {
                        if (SuperWeChatHelper.getInstance().isLoggedIn()) {
                            autoLogin = false;
                            MFGT.gotoGuide(mContext);
                            finish();
                            return;
                        }
                    }
                    //enter main screen
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                } else {
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                    }
                    MFGT.gotoGuide(mContext);
                    finish();
                }
            }
        }).start();

    }

    /**
     * get sdk version
     */
    private String getVersion() {
        return EMClient.getInstance().getChatConfig().getVersion();
    }
}
