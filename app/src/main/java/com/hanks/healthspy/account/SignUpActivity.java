package com.hanks.healthspy.account;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hanks.healthspy.R;
import com.hanks.healthspy.utils.Data;
import com.hanks.healthspy.utils.RequestHandler;

import org.json.JSONObject;

public class SignUpActivity extends AppCompatActivity {

    private String account = "", password = "", c_password = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
    }

    public void add_account(View view) {
        EditText accountET = (EditText) findViewById(R.id.edit_phone);
        EditText passwordET = (EditText) findViewById(R.id.edit_password);
        EditText c_passwordET = (EditText) findViewById(R.id.comfirm_password);
        try {
            account = accountET.getText().toString();
            password = passwordET.getText().toString();
            c_password = c_passwordET.getText().toString();
        } catch (NullPointerException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "注册失败",
                    Toast.LENGTH_SHORT).show();
        }

        if(!password.equals(c_password)) {
            Toast.makeText(getApplicationContext(), "密码不一致", Toast.LENGTH_LONG).show();
        } else {
            new Thread() {
                @Override
                public void run() {
                    String JSONString = "{\"account\":" + account
                            + ",\"password\":" + password
                            + "}";
                    String msg = RequestHandler.sendPostRequest(Data.url + "/regist", JSONString);
                    try {
                        JSONObject jO = new JSONObject(msg);
                        if (jO.getInt("status") != 200) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "注册失败",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "注册成功",
                                            Toast.LENGTH_SHORT).show();
                                    Intent it = new Intent(SignUpActivity.this, LoginActivity.class);
                                    startActivity(it);
                                    SignUpActivity.this.finish();
                                }
                            });

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }

    }
}
