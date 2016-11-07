package com.zhou.superwechat.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hyphenate.easeui.domain.User;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.zhou.superwechat.I;
import com.zhou.superwechat.R;
import com.zhou.superwechat.utils.MFGT;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class FriendProfileActivity extends BaseActivity {

    @InjectView(R.id.iv_back)
    ImageView imBack;
    @InjectView(R.id.txt_littleTitle)
    TextView txtLittleTitle;
    @InjectView(R.id.im_add_friend_avatar)
    ImageView imAddFriendAvatar;
    @InjectView(R.id.tv_add_friend_name)
    TextView tvAddFriendName;
    @InjectView(R.id.tv_add_friend_remarks)
    TextView tvAddFriendRemarks;
    User user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);
        ButterKnife.inject(this);
         user = (User) getIntent().getSerializableExtra(I.User.USER_NAME);
        if (user == null) {
            MFGT.finish(this);
        }
        initView();
    }

    private void initView() {
        imBack.setVisibility(View.VISIBLE);
        txtLittleTitle.setVisibility(View.VISIBLE);
        txtLittleTitle.setText(getString(R.string.userinfo_txt_profile));
        setUserInfo();
    }

    private void setUserInfo() {
        EaseUserUtils.setAppUserAvatar(this, user.getMUserName(),imAddFriendAvatar);
        EaseUserUtils.setAppUserNick(user.getMUserName(),tvAddFriendRemarks);
        EaseUserUtils.setAppUserNameWithNo(user.getMUserName(),tvAddFriendName);
    }


    @OnClick(R.id.iv_back)
    public void onClick() {
    }
}
