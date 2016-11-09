package com.zhou.superwechat.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.easeui.domain.User;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.zhou.superwechat.I;
import com.zhou.superwechat.R;
import com.zhou.superwechat.SuperWeChatHelper;
import com.zhou.superwechat.utils.MFGT;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.zhou.superwechat.R.id.username;

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
    @InjectView(R.id.btn_add_contact)
    Button btnAddContact;
    @InjectView(R.id.btn_add_send_msg)
    Button btnAddSendMsg;
    @InjectView(R.id.btn_add_send_video)
    Button btnAddSendVideo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);
        ButterKnife.inject(this);
        user = (User) getIntent().getSerializableExtra(I.User.USER_NAME);
        if (user == null) {
            MFGT.finish(this);
            return;
        }
        initView();
        isFriend();
    }

    private void isFriend() {
        if (SuperWeChatHelper.getInstance().getAppContactList().containsKey(user.getMUserName())) {
            btnAddSendMsg.setVisibility(View.VISIBLE);
            btnAddSendVideo.setVisibility(View.VISIBLE);
        } else {
            btnAddContact.setVisibility(View.VISIBLE);
        }
    }

    private void initView() {
        imBack.setVisibility(View.VISIBLE);
        txtLittleTitle.setVisibility(View.VISIBLE);
        txtLittleTitle.setText(getString(R.string.userinfo_txt_profile));
        setUserInfo();
    }

    private void setUserInfo() {
        EaseUserUtils.setAppUserAvatar(this, user.getMUserName(), imAddFriendAvatar);
        EaseUserUtils.setAppUserNick(user.getMUserNick(), tvAddFriendRemarks);
        EaseUserUtils.setAppUserNameWithNo(user.getMUserName(), tvAddFriendName);
    }

    @OnClick({R.id.iv_back, R.id.btn_add_contact, R.id.btn_add_send_msg, R.id.btn_add_send_video})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                MFGT.finish(this);
                break;
            case R.id.btn_add_contact:
                MFGT.gotoAddFriend(this, user.getMUserName());
                break;
            case R.id.btn_add_send_msg:
                MFGT.gotoChat(this, user.getMUserName());
                break;
            case R.id.btn_add_send_video:
                break;
        }
    }
}
