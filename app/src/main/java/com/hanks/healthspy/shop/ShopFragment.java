package com.hanks.healthspy.shop;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hanks.healthspy.R;
import com.hanks.healthspy.utils.Data;
import com.hanks.healthspy.utils.RequestHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by hanks on 16年4月14日.
 */
public class ShopFragment extends Fragment{

    private int id;
    private View view;
    private ListViewAdapter listViewAdapter;
    private ListView listView, alistView;
    private JSONArray listdata = new JSONArray();
    private SharedPreferences sharedPref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_items, container, false);

        sharedPref = getActivity().getSharedPreferences("user_id", Context.MODE_PRIVATE);
        id = sharedPref.getInt("user_id", -1);

        listView = (ListView) view.findViewById(R.id.list_goods);
        alistView = (ListView) view.findViewById(R.id.list_good_goods);

        getItems();

        return view;
    }

    private void getItems() {

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg){
                super.handleMessage(msg);
                if(msg.what == 1) {
                    listViewAdapter = new ListViewAdapter(ShopFragment.this.getActivity(), listdata); //创建适配器
                    listView.setAdapter(listViewAdapter);
                } else if (msg.what == 2) {
                    listViewAdapter = new ListViewAdapter(ShopFragment.this.getActivity(), listdata); //创建适配器
                    alistView.setAdapter(listViewAdapter);
                }
            }
        };

        final Thread a_thread = new Thread(new Runnable() {

            @Override
            public void run() {
                Looper.prepare();
                get_recommand();
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }

        });
        a_thread.start();


        final Thread b_thread = new Thread(new Runnable() {

            @Override
            public void run() {
                Looper.prepare();
                get_items();
                Message message = new Message();
                message.what = 2;
                handler.sendMessage(message);
            }

        });
        b_thread.start();
    }

    private void get_recommand() {
        String jsonString = "{\"user_id\":"+ Integer.toString(id) +"}";
        String msg = RequestHandler.sendPostRequest(Data.url + "/get_item_list", jsonString);
        if (msg == "false") {
            Toast.makeText(getActivity().getApplicationContext(), "连接失败，请检查网络是否连接并重试",
                    Toast.LENGTH_SHORT).show();
        } else {
            try {
                int flag = 0;
                JSONObject jO = new JSONObject(msg);
                if(!jO.isNull("item_list")) {
                    if(jO.getJSONArray("item_list").length() != 0) {
                        flag = 1;
                        listdata = jO.getJSONArray("item_list");
                    }
                }
                if (flag == 0) {
                    Toast.makeText(getActivity().getApplicationContext(), "未找到商品",
                            Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void get_items() {
        String jsonString = "{\"user_id\":"+ Integer.toString(id) +"}";
        String msg = RequestHandler.sendPostRequest(Data.url + "/get_recommand", jsonString);
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
                int flag = 0;
                JSONObject jO = new JSONObject(msg);
                if(!jO.isNull("item_list")) {
                    if(jO.getJSONArray("item_list").length() != 0) {
                        flag = 1;
                        listdata = jO.getJSONArray("item_list");
                    }
                }
                if (flag == 0) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity().getApplicationContext(), "未找到商品",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
