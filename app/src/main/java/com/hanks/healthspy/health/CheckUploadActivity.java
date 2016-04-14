package com.hanks.healthspy.health;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hanks.healthspy.MainActivity;
import com.hanks.healthspy.R;
import com.hanks.healthspy.utils.Data;
import com.hanks.healthspy.utils.RequestHandler;

import org.json.JSONObject;

public class CheckUploadActivity extends AppCompatActivity {

    private SharedPreferences sharedPref;
    private String heart_rate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_upload);

        Intent it = getIntent();
        heart_rate = it.getStringExtra(CheckActivity.EXTRA_MESSAGE);
        TextView textView = (TextView) findViewById(R.id.heart_rate);
        textView.setText("您的心率为："+heart_rate);
    }

    public void cancle_upload(View view) {
        Intent it = new Intent(this, MainActivity.class);
        startActivity(it);
        this.finish();
    }

    public void heart_rate_upload(View view) {
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
                            + ",\"heart_rate\":" + heart_rate
                            + "}";
                    String msg = RequestHandler.sendPostRequest(Data.url+"/add_health", JSONString);
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
