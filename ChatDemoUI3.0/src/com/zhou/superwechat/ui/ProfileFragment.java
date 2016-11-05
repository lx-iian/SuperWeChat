package com.zhou.superwechat.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.easemob.redpacketui.utils.RedPacketUtil;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.zhou.superwechat.Constant;
import com.zhou.superwechat.R;
import com.zhou.superwechat.utils.MFGT;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    @InjectView(R.id.im_profile_avatar)
    ImageView imProfileAvatar;
    @InjectView(R.id.tv_profile_nick)
    TextView tvProfileNick;
    @InjectView(R.id.tv_profile_nusername)
    TextView tvProfileNusername;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false))
            return;
        setUserInfo();
    }

    private void setUserInfo() {
        EaseUserUtils.setCurrentAppUserAvatar(getActivity(), imProfileAvatar);
        EaseUserUtils.setCurrentAppUserNick(tvProfileNick);
        EaseUserUtils.setCurrentAppUserNameWithNo(tvProfileNusername);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @OnClick({R.id.layout_profile_view, R.id.tv_profile_money, R.id.tv_profile_settings})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_profile_view:
                MFGT.gotoUserProfile(getActivity());
                break;
            //red packet code : 进入零钱页面
            case R.id.tv_profile_money:
                RedPacketUtil.startChangeActivity(getActivity());
                break;
            //end of red packet code
            case R.id.tv_profile_settings:
                MFGT.gotoSetting(getActivity());
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (((MainActivity) getActivity()).isConflict) {
            outState.putBoolean("isConflict", true);
        } else if (((MainActivity) getActivity()).getCurrentAccountRemoved()) {
            outState.putBoolean(Constant.ACCOUNT_REMOVED, true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setUserInfo();
    }
}