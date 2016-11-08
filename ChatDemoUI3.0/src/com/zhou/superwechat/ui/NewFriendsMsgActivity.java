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

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.zhou.superwechat.R;
import com.zhou.superwechat.adapter.NewFriendsMsgAdapter;
import com.zhou.superwechat.db.InviteMessgeDao;
import com.zhou.superwechat.domain.InviteMessage;
import com.zhou.superwechat.utils.MFGT;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Application and notification
 *
 */
public class NewFriendsMsgActivity extends BaseActivity {

    @InjectView(R.id.iv_back)
    ImageView imBack;
    @InjectView(R.id.txt_littleTitle)
    TextView txtLittleTitle;
    @InjectView(R.id.list)
    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.em_activity_new_friends_msg);
        ButterKnife.inject(this);

        imBack.setVisibility(View.VISIBLE);
        txtLittleTitle.setVisibility(View.VISIBLE);
        txtLittleTitle.setText(getString(R.string.recommended_friends));
        ListView list = (ListView) findViewById(R.id.list);
        InviteMessgeDao dao = new InviteMessgeDao(this);
        List<InviteMessage> msgs = dao.getMessagesList();

        NewFriendsMsgAdapter adapter = new NewFriendsMsgAdapter(this, 1, msgs);
        list.setAdapter(adapter);
        dao.saveUnreadMessageCount(0);

    }

    @OnClick(R.id.iv_back)
    public void onClick() {
        MFGT.finish(this);
    }
}
