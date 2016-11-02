package com.zhou.superwechat.ui;

import android.os.Bundle;
import android.view.View;

import com.zhou.superwechat.R;
import com.zhou.superwechat.utils.MFGT;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class GuideActivity extends BaseActivity {
    private GuideActivity mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        ButterKnife.inject(this);
    }

    @OnClick({R.id.btn_gotoLogin, R.id.btn_gotoRegister})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_gotoLogin:
                MFGT.gotoLogin(this);
                break;
            case R.id.btn_gotoRegister:
                MFGT.gotoRegister(this);
                break;
        }
    }
}
