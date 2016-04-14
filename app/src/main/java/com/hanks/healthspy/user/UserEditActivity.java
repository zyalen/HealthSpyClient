package com.hanks.healthspy.user;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.hanks.healthspy.MainActivity;
import com.hanks.healthspy.R;
import com.hanks.healthspy.health.CheckActivity;
import com.hanks.healthspy.utils.Data;
import com.hanks.healthspy.utils.RequestHandler;

import org.json.JSONObject;

public class UserEditActivity extends AppCompatActivity {

    private int id;
    private SharedPreferences sharedPref;
    private String nickname, gender, age, phone;
    private EditText nicknameET, ageET, phoneET;
    private Switch genderSW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit);

        bindview();
    }

    private void bindview() {
        Intent it = getIntent();
        nickname = it.getStringExtra("nickname");
        gender = it.getStringExtra("gender");
        age = it.getStringExtra("age");
        phone = it.getStringExtra("phone");

        nicknameET = (EditText) findViewById(R.id.nickname_edit);
        genderSW = (Switch) findViewById(R.id.gender_edit);
        ageET = (EditText) findViewById(R.id.age_edit);
        phoneET = (EditText) findViewById(R.id.phone_edit);

        nicknameET.setText(nickname);
        ageET.setText(age);
        phoneET.setText(phone);
        if(gender == "1") {
            genderSW.setEnabled(false);
        }
    }

    public void cancel(View view) {
        Intent it = new Intent(this, MainActivity.class);
        startActivity(it);
        this.finish();
    }

    public void user_upload(View view) {
        nickname = nicknameET.getText().toString();
        age = ageET.getText().toString();
        phone = phoneET.getText().toString();
        gender = "0";
        if(genderSW.isChecked()) {
            gender = "1";
        }
        upload();
        Intent it = new Intent(this, MainActivity.class);
        startActivity(it);
        this.finish();
    }

    private void upload() {
        sharedPref = this.getSharedPreferences("user_id", Context.MODE_PRIVATE);
        final int id = sharedPref.getInt("user_id", -1);
        new Thread(){
            public void run() {
                try {
                    String JSONString = "{\"id\":" + Integer.toString(id)
                            + ",\"nickname\":" + "\"" + nickname + "\""
                            + ",\"age\":" + age
                            + ",\"gender\":" + gender
                            + ",\"phone\":" + phone
                            + "}";
                    String msg = RequestHandler.sendPostRequest(Data.url + "/modify_user_info", JSONString);
                    JSONObject jO = new JSONObject(msg);
                    if (jO.getInt("status") != 200) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "上传失败",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "上传成功",
                                        Toast.LENGTH_SHORT).show();
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
