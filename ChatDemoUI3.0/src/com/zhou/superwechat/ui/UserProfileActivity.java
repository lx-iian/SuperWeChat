package com.zhou.superwechat.ui;

import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.domain.User;
import com.hyphenate.easeui.utils.EaseImageUtils;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.zhou.superwechat.I;
import com.zhou.superwechat.R;
import com.zhou.superwechat.SuperWeChatHelper;
import com.zhou.superwechat.bean.Result;
import com.zhou.superwechat.data.NetDao;
import com.zhou.superwechat.data.OkHttpUtils;
import com.zhou.superwechat.utils.CommonUtils;
import com.zhou.superwechat.utils.L;
import com.zhou.superwechat.utils.MFGT;
import com.zhou.superwechat.utils.ResultUtils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class UserProfileActivity extends BaseActivity implements OnClickListener {

    private static final int REQUESTCODE_PICK = 1;
    private static final int REQUESTCODE_CUTTING = 2;
    private static final String TAG = UserProfileActivity.class.getSimpleName();
    @InjectView(R.id.iv_back)
    ImageView imBack;
    @InjectView(R.id.txt_littleTitle)
    TextView txtLittleTitle;
    @InjectView(R.id.im_userInfo_head_avatar)
    ImageView imUserInfoHeadAvatar;
    @InjectView(R.id.txt_userInfo_nick)
    TextView txtUserInfoNick;
    @InjectView(R.id.layout_userInfo_user_nick)
    LinearLayout layoutUserInfoUserNick;
    @InjectView(R.id.txt_userInfo_name_withNo)
    TextView txtUserInfoName;
    @InjectView(R.id.layout_userInfo_name_withNo)
    LinearLayout layoutUserInfoNameWithNo;
    @InjectView(R.id.layout_userInfo_head_avatar)
    LinearLayout layoutUserInfoHeadAvatar;

    private ProgressDialog dialog;
    private RelativeLayout rlNickName;
    User user = null;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.em_activity_user_profile);
        ButterKnife.inject(this);
        initView();
        initListener();
        user = EaseUserUtils.getCurrentAppUserInfo();
    }

    private void initView() {
        imBack.setVisibility(View.VISIBLE);
        txtLittleTitle.setVisibility(View.VISIBLE);
        txtLittleTitle.setText(getString(R.string.title_user_profile));
    }

    private void initListener() {
        EaseUserUtils.setCurrentAppUserAvatar(this, imUserInfoHeadAvatar);
        EaseUserUtils.setCurrentAppUserNick(txtUserInfoNick);
        EaseUserUtils.setCurrentAppUserNameWithNo(txtUserInfoName);
    }

    public void asyncFetchUserInfo(String username) {
        SuperWeChatHelper.getInstance().getUserProfileManager().asyncGetUserInfo(username, new EMValueCallBack<EaseUser>() {

            @Override
            public void onSuccess(EaseUser user) {
                if (user != null) {
                    SuperWeChatHelper.getInstance().saveContact(user);
                    if (isFinishing()) {
                        return;
                    }
                    txtUserInfoNick.setText(user.getNick());
                    if (!TextUtils.isEmpty(user.getAvatar())) {
                        Glide.with(UserProfileActivity.this).load(user.getAvatar()).placeholder(R.drawable.em_default_avatar).into(imUserInfoHeadAvatar);
                    } else {
                        Glide.with(UserProfileActivity.this).load(R.drawable.em_default_avatar).into(imUserInfoHeadAvatar);
                    }
                }
            }

            @Override
            public void onError(int error, String errorMsg) {
            }
        });
    }


    private void uploadHeadPhoto() {
        Builder builder = new Builder(this);
        builder.setTitle(R.string.dl_title_upload_photo);
        builder.setItems(new String[]{getString(R.string.dl_msg_take_photo), getString(R.string.dl_msg_local_upload)},
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        switch (which) {
                            case 0:
                                Toast.makeText(UserProfileActivity.this, getString(R.string.toast_no_support),
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case 1:
                                Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
                                pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                                startActivityForResult(pickIntent, REQUESTCODE_PICK);
                                break;
                            default:
                                break;
                        }
                    }
                });
        builder.create().show();
    }


    private void updateRemoteNick(final String nickName) {
        dialog = ProgressDialog.show(this, getString(R.string.dl_update_nick), getString(R.string.dl_waiting));
        new Thread(new Runnable() {

            @Override
            public void run() {
                boolean updatenick = SuperWeChatHelper.getInstance().getUserProfileManager().updateCurrentUserNickName(nickName);
                if (UserProfileActivity.this.isFinishing()) {
                    return;
                }
                if (!updatenick) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(UserProfileActivity.this, getString(R.string.toast_updatenick_fail), Toast.LENGTH_SHORT)
                                    .show();
                            dialog.dismiss();
                        }
                    });
                } else {
                    updateAppNick(nickName);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            Toast.makeText(UserProfileActivity.this, getString(R.string.toast_updatenick_success), Toast.LENGTH_SHORT)
                                    .show();
                            txtUserInfoNick.setText(nickName);
                        }
                    });
                }
            }
        }).start();
    }

    private void updateAppNick(String nickName) {
        NetDao.updateNick(this, user.getMUserName(), nickName, new OkHttpUtils.OnCompleteListener<String>() {
            @Override
            public void onSuccess(String s) {
                if (s != null) {
                    Result result = ResultUtils.getResultFromJson(s, User.class);
                    L.e(TAG, "result=" + result);
                    if (result != null && result.isRetMsg()) {
                        User u = (User) result.getRetData();
                        undateLocatUser(user);
                    } else {
                        CommonUtils.showLongToast(getString(R.string.toast_updatenick_fail));
                        dialog.dismiss();
                    }
                }
            }

            @Override
            public void onError(String error) {
                L.e(TAG, "error = " + error);
                CommonUtils.showLongToast(getString(R.string.toast_updatenick_fail));
                dialog.dismiss();
            }
        });
    }

    private void undateLocatUser(User u) {
        user = u;
        SuperWeChatHelper.getInstance().saveAppContact(u);
        EaseUserUtils.setCurrentAppUserNick(txtUserInfoNick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUESTCODE_PICK:
                if (data == null || data.getData() == null) {
                    return;
                }
                startPhotoZoom(data.getData());
                break;
            case REQUESTCODE_CUTTING:
                if (data != null) {
                    updateAppUserAvatar(data);
                    //setPicToView(data);
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateAppUserAvatar(final Intent picData) {
        dialog = ProgressDialog.show(this, getString(R.string.dl_update_photo), getString(R.string.dl_waiting));
        dialog.show();
        File file = saveBitmapFile(picData);
        NetDao.updateAvatar(this, user.getMUserName(), file, new OkHttpUtils.OnCompleteListener<String>() {
            @Override
            public void onSuccess(String s) {
                if (s != null) {
                    Result result = ResultUtils.getResultFromJson(s, User.class);
                    L.e(TAG, "result=" + result);
                    if (result != null && result.isRetMsg()) {
                        User u = (User) result.getRetData();
                        SuperWeChatHelper.getInstance().saveAppContact(u);
                        setPicToView(picData);
                    } else {
                        dialog.dismiss();
                        CommonUtils.showMsgShortToast(result != null ? result.getRetCode() : -1);
//                        CommonUtils.showLongToast(result!=null?getString(R.string.toast_updatephoto_fail));
                    }
                } else {
                    dialog.show();
                    CommonUtils.showLongToast(getString(R.string.toast_updatephoto_fail));
                }
            }

            @Override
            public void onError(String error) {
                L.e(TAG, "error = " + error);
                dialog.show();
                CommonUtils.showLongToast(getString(R.string.toast_updatephoto_fail));
            }
        });

    }

    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", true);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, REQUESTCODE_CUTTING);
    }

    /**
     * save the picture data
     *
     * @param picdata
     */
    private void setPicToView(Intent picdata) {
        Bundle extras = picdata.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            Drawable drawable = new BitmapDrawable(getResources(), photo);
            imUserInfoHeadAvatar.setImageDrawable(drawable);
            CommonUtils.showShortToast(R.string.toast_updatephoto_success);
            // uploadUserAvatar(Bitmap2Bytes(photo));
        }

    }

    private void uploadUserAvatar(final byte[] data) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                final String avatarUrl = SuperWeChatHelper.getInstance().getUserProfileManager().uploadUserAvatar(data);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        if (avatarUrl != null) {
                            Toast.makeText(UserProfileActivity.this, getString(R.string.toast_updatephoto_success),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(UserProfileActivity.this, getString(R.string.toast_updatephoto_fail),
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }
        }).start();

    }


    public byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    @OnClick({R.id.iv_back, R.id.layout_userInfo_user_nick, R.id.layout_userInfo_name_withNo, R.id.layout_userInfo_head_avatar})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                MFGT.finish(this);
                break;
            case R.id.layout_userInfo_user_nick:
                final EditText editText = new EditText(this);
                editText.setText(user.getMUserNick());
                new Builder(this).setTitle(R.string.setting_nickname).setIcon(android.R.drawable.ic_dialog_info).setView(editText)
                        .setPositiveButton(R.string.dl_ok, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String nickString = editText.getText().toString().trim();
                                if (TextUtils.isEmpty(nickString)) {
                                    CommonUtils.showShortToast(getString(R.string.toast_nick_not_isnull));
                                    return;
                                }
                                if (nickString.equals(user.getAvatar())) {
                                    CommonUtils.showShortToast(getString(R.string.msg_108));
                                    return;
                                }
                                updateRemoteNick(nickString);
                            }
                        }).setNegativeButton(R.string.dl_cancel, null).show();
                break;
            case R.id.layout_userInfo_name_withNo:
                CommonUtils.showLongToast(R.string.User_name_only_settle_once);
                break;
            case R.id.layout_userInfo_head_avatar:
                uploadHeadPhoto();
                break;
        }
    }

    public File saveBitmapFile(Intent picdata) {
        Bundle extras = picdata.getExtras();
        if (extras != null) {
            Bitmap bitmap = extras.getParcelable("data");
            String imagePath = EaseImageUtils.getImagePath(user.getMUserName() + I.AVATAR_SUFFIX_JPG);
            // 降压片把保存图片的路径
            File file = new File(imagePath);
            L.e("file path=" + file.getAbsolutePath());
            try {
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                bos.flush();
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return file;
        }
        return null;
    }
}
