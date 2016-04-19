package com.hanks.healthspy.user;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hanks.healthspy.R;
import com.hanks.healthspy.account.LoginActivity;
import com.hanks.healthspy.utils.Data;
import com.hanks.healthspy.utils.RequestHandler;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by hanks on 16年4月11日.
 */
public class UserFragment extends Fragment {

    private int id;
    private TextView user_nicknameTView, nicknameTView, genderTView, ageTView, phoneTView;
    private String nickname, gender, age, phone;
    private Button userChangeBtn, signOutBtn;
    private View view;
    private SharedPreferences sharedPref;


    public UserFragment(int id) {
        this.id = id;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_user,container,false);
        sharedPref = getActivity().getSharedPreferences("user_id", Context.MODE_PRIVATE);

        bindViews();

        return view;
    }

    private void bindViews() {
        user_nicknameTView = (TextView) view.findViewById(R.id.user_nickname);
        nicknameTView = (TextView) view.findViewById(R.id.nickname);
        genderTView = (TextView) view.findViewById(R.id.gender);
        ageTView = (TextView) view.findViewById(R.id.age);
        phoneTView = (TextView) view.findViewById(R.id.phone);

        getUserInfo();

        userChangeBtn = (Button) view.findViewById(R.id.user_change_btn);
        signOutBtn = (Button) view.findViewById(R.id.sign_out_btn);

        userChangeBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent it = new Intent(getActivity(), UserEditActivity.class);
                it.putExtra("nickname", nickname);
                it.putExtra("age", age);
                it.putExtra("gender", gender);
                it.putExtra("phone", phone);
                startActivity(it);
                getActivity().finish();
            }
        });

        signOutBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.clear();
                editor.commit();

                Intent it = new Intent(getActivity(), LoginActivity.class);
                startActivity(it);
                getActivity().finish();
            }
        });

    }

    private void getUserInfo() {

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg){
                super.handleMessage(msg);
                if(msg.what == 1){
                    user_nicknameTView.setText(nickname);
                    nicknameTView.setText(nickname);
                    genderTView.setText(gender);
                    ageTView.setText(age);
                    phoneTView.setText(phone);
                }
            }
        };

        final Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                get_user_info();
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }

        });
        thread.start();

    }

    private void get_user_info() {
        String jsonString = "{\"id\":"+ Integer.toString(id) +"}";
        String msg = RequestHandler.sendPostRequest(Data.url+"/get_user_info", jsonString);
        if (msg == "false") {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity().getApplicationContext(), "连接失败，请检查网络是否连接并重试",
                            Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            try {
                JSONObject jO = new JSONObject(msg);
                if (jO.getInt("status") != 200) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity().getApplicationContext(), "未找到用户信息",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    nickname = "";
                    age = "";
                    phone = "";
                    gender = "";
                    if (!jO.isNull("nickname")) {
                        this.nickname = jO.getString("nickname");
                    }
                    if (!jO.isNull("age")) {
                        this.age = Integer.toString(jO.getInt("age"));
                    }
                    if (!jO.isNull("phone")) {
                        this.phone = jO.getString("phone");
                    }
                    if (!jO.isNull("gender")) {
                        if (jO.getInt("gender") == 0) {
                            this.gender = "男";
                        } else {
                            this.gender = "女";
                        }
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
