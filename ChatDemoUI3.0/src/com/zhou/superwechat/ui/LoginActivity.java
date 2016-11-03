/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zhou.superwechat.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.domain.User;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.zhou.superwechat.R;
import com.zhou.superwechat.SuperWeChatApplication;
import com.zhou.superwechat.SuperWeChatHelper;
import com.zhou.superwechat.bean.Result;
import com.zhou.superwechat.data.NetDao;
import com.zhou.superwechat.data.OkHttpUtils;
import com.zhou.superwechat.db.SuperWeChatDBManager;
import com.zhou.superwechat.db.UserDao;
import com.zhou.superwechat.utils.CommonUtils;
import com.zhou.superwechat.utils.L;
import com.zhou.superwechat.utils.MFGT;
import com.zhou.superwechat.utils.ResultUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Login screen
 */
public class LoginActivity extends BaseActivity {
    private static final String TAG = "LoginActivity";
    public static final int REQUEST_CODE_SETNICK = 1;
    @InjectView(R.id.txt_littleTitle)
    TextView littleTxtLeft;
    @InjectView(R.id.iv_back)
    ImageView imBack;
    @InjectView(R.id.username)
    EditText etUsername;
    @InjectView(R.id.password)
    EditText etPassword;

    private boolean progressShow;
    private boolean autoLogin = false;
    private String currentUsername;
    private String currentPassword;
    private ProgressDialog pd;
    private LoginActivity mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // enter the main activity if already logged in
        if (SuperWeChatHelper.getInstance().isLoggedIn()) {
            autoLogin = true;
            startActivity(new Intent(LoginActivity.this, MainActivity.class));

            return;
        }
        setContentView(R.layout.em_activity_login);
        ButterKnife.inject(this);
        mContext = this;
        setListener();
        initView();
    }

    private void initView() {
        if (SuperWeChatHelper.getInstance().getCurrentUsernName() != null) {
            etUsername.setText(SuperWeChatHelper.getInstance().getCurrentUsernName());
        }
        imBack.setVisibility(View.VISIBLE);
        littleTxtLeft.setVisibility(View.VISIBLE);
        littleTxtLeft.setText(R.string.login);
    }

    private void setListener() {
        // if user changed, clear the password
        etUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                etPassword.setText(null);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * login
     */
    public void login() {
        if (!EaseCommonUtils.isNetWorkConnected(this)) {
            Toast.makeText(this, R.string.network_isnot_available, Toast.LENGTH_SHORT).show();
            return;
        }
        currentUsername = etUsername.getText().toString().trim();
        currentPassword = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(currentUsername)) {
            CommonUtils.showLongToast(R.string.User_name_cannot_be_empty);
            return;
        }
        if (TextUtils.isEmpty(currentPassword)) {
            CommonUtils.showLongToast(R.string.Password_cannot_be_empty);
            return;
        }

        progressShow = true;
        pd = new ProgressDialog(mContext);
        pd.setCanceledOnTouchOutside(false);
        pd.setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                Log.d(TAG, "EMClient.getInstance().onCancel");
                progressShow = false;
            }
        });
        pd.setMessage(getString(R.string.Is_landing));
        pd.show();

        loginEMServer();
    }

    private void loginEMServer() {
        // After logout，the DemoDB may still be accessed due to async callback, so the DemoDB will be re-opened again.
        // close it before login to make sure DemoDB not overlap
        SuperWeChatDBManager.getInstance().closeDB();

        // reset current user name before login
        SuperWeChatHelper.getInstance().setCurrentUserName(currentUsername);

        final long start = System.currentTimeMillis();
        // call login method
        Log.d(TAG, "EMClient.getInstance().login");
        EMClient.getInstance().login(currentUsername, currentPassword, new EMCallBack() {

            @Override
            public void onSuccess() {
                Log.d(TAG, "login: onSuccess");
                loginAppServer();
                loginSuccess();
            }

            @Override
            public void onProgress(int progress, String status) {
                Log.d(TAG, "login: onProgress");
            }

            @Override
            public void onError(final int code, final String message) {
                Log.d(TAG, "login: onError: " + code);
                if (!progressShow) {
                    return;
                }
                runOnUiThread(new Runnable() {
                    public void run() {
                        pd.dismiss();
                        Toast.makeText(getApplicationContext(), getString(R.string.Login_failed) + message,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void loginAppServer() {
        NetDao.login(mContext, currentUsername, currentPassword, new OkHttpUtils.OnCompleteListener<String>() {
            @Override
            public void onSuccess(String s) {
                L.e(TAG, "s= " + s);
                if (s != null && s != "") {
                    Result result = ResultUtils.getResultFromJson(s, User.class);
                    if (result != null && result.isRetMsg()) {
                        L.e(TAG, "result =" + result);
                        User user = (User) result.getRetData();
                        if (user != null) {
                            L.e(TAG, "user =" + user);
                            UserDao dao = new UserDao(mContext);
                            dao.saveUser(user);
                            SuperWeChatHelper.getInstance().setCurrentUser(user);
                            loginSuccess();
                        }
                    } else {
                        pd.dismiss();
                        L.e(TAG, "login fail = " + result);
                    }
                } else {
                    pd.dismiss();
                }
            }

            @Override
            public void onError(String error) {
                pd.dismiss();
                L.e(TAG, "onError = " + error);
            }
        });
    }

    private void loginSuccess() {
        // ** manually load all local groups and conversation
        EMClient.getInstance().groupManager().loadAllGroups();
        EMClient.getInstance().chatManager().loadAllConversations();

        // update current user's display name for APNs
        boolean updatenick = EMClient.getInstance().updateCurrentUserNick(
                SuperWeChatApplication.currentUserNick.trim());
        if (!updatenick) {
            Log.e("LoginActivity", "update current user nick fail");
        }

        if (!LoginActivity.this.isFinishing() && pd.isShowing()) {
            pd.dismiss();
        }
        // get user's info (this should be get from App's server or 3rd party service)
        SuperWeChatHelper.getInstance().getUserProfileManager().asyncGetCurrentUserInfo();

        Intent intent = new Intent(LoginActivity.this,
                MainActivity.class);
        startActivity(intent);

        MFGT.finish(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (autoLogin) {
            return;
        }
        if (SuperWeChatHelper.getInstance().getCurrentUsernName() != null) {
            etUsername.setText(SuperWeChatHelper.getInstance().getCurrentUsernName());
        }
    }

    @OnClick({R.id.iv_back, R.id.btnLogin})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                MFGT.finish(this);
                break;
            case R.id.btnLogin:
                login();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pd != null) {
            pd.dismiss();
        }
    }
}