package com.hanks.healthspy.account;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.hanks.healthspy.R;
import com.hanks.healthspy.MainActivity;
import com.hanks.healthspy.utils.Data;
import com.hanks.healthspy.utils.RequestHandler;
import com.hanks.healthspy.utils.SHA1;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private EditText Epassword;
    private EditText Eaccount;
    private String password;
    private String account;
    private String jsonStrng;
    private String message;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // 查询user_id信息，如已登陆，则user_id存在，跳过登陆直接进入主页
        sharedPref = this.getSharedPreferences("user_id", Context.MODE_PRIVATE);
        int id;
        id = sharedPref.getInt("user_id", -1);
        if (id != -1) {
            Intent it = new Intent(this, MainActivity.class);
            startActivity(it);
            LoginActivity.this.finish();
        }
    }

    public void sign_up(View view) {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    public void sign_in(View view) {
        Eaccount = (EditText)findViewById(R.id.account);
        Epassword = (EditText)findViewById(R.id.password);
        account = Eaccount.getText().toString();
        password = Epassword.getText().toString();
        // 在后台线程操作用户登录验证
        new Thread(runnable).start();
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Looper.prepare();
            login_help();
        }
    };

    // 用户验证过程
    private void login_help() {
        if ((!account.isEmpty()) &&(!password.isEmpty())) {
            jsonStrng = "{" +
                    "\"account\":\"" + account + "\"," +
                    "\"password\":\"\"" +  "}";
            message = RequestHandler.sendPostRequest(
                    Data.url+"/login", jsonStrng);
            if (message == "false") {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "连接失败，请检查网络是否连接并重试",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }
            String salt;
            try {
                JSONObject jO = new JSONObject(message);
                if (jO.getInt("status") == 500) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "用户未注册",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }
                salt = jO.getString("salt");
                String password2 = SHA1.SHA1_encode(password, salt);
                jsonStrng = "{" +
                        "\"account\":\"" + account + "\"," +
                        "\"password\":\"" + password2 + "\"," +
                        "\"salt\":\"" + salt + "\" " +  "}";
                message = RequestHandler.sendPostRequest(
                        Data.url+"/login", jsonStrng);
                if (message == "false") {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "连接失败，请检查网络是否连接并重试",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }
                jO = new JSONObject(message);
                if (jO.getInt("status") == 500) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "密码错误，登录失败",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else {
                    final int user_id = jO.getInt("id");
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putInt("user_id", user_id);
                    editor.putString("account", account);
                    editor.commit();

                    Intent it = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(it);
                    LoginActivity.this.finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "用户名或密码不能为空",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
