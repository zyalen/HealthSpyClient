package com.hanks.healthspy.report;

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
import android.widget.BaseAdapter;
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
 * Created by hanks on 16年4月13日.
 */
public class ReportFragment extends Fragment {

    private int id;
    private int w;
    private View view;
    private ListView listview;
    private JSONArray listdata = new JSONArray();
    private SharedPreferences sharedPref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.text, container, false);

        sharedPref = getActivity().getSharedPreferences("user_id", Context.MODE_PRIVATE);
        id = sharedPref.getInt("user_id", -1);

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        w = dm.widthPixels / 2;

        listview = (ListView) view.findViewById(R.id.text_list);
        TextView t12 = (TextView) view.findViewById(R.id.item2bzrbjzb);
        TextView t13 = (TextView) view.findViewById(R.id.item3bzrbjzb);
        t12.setWidth(w);
        t13.setWidth(w);

        getReport();

        return view;
    }

    private void getReport() {

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg){
                super.handleMessage(msg);
                if(msg.what == 1){
                    DataAdapter1 mAdapter1 = new DataAdapter1();
                    listview.setAdapter(mAdapter1);
                }
            }
        };

        final Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                Looper.prepare();
                get_report();
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }

        });
        thread.start();

    }

    private void get_report() {
        String jsonString = "{\"user_id\":"+ Integer.toString(id) +"}";
        String msg = RequestHandler.sendPostRequest(Data.url + "/get_health_list", jsonString);
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
                if(!jO.isNull("health_list")) {
                    if(jO.getJSONArray("health_list").length() != 0) {
                        flag = 1;
                        listdata = jO.getJSONArray("health_list");
                    }
                }
                if (flag == 0) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity().getApplicationContext(), "未找到用户体检报告",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class ViewHolderbjzb {
        TextView item2bjzb;
        TextView item3bjzb;
    }

    private class DataAdapter1 extends BaseAdapter {

        @Override
        public int getCount() {
            return listdata.length();
        }

        @Override
        public JSONObject getItem(int position) {
            // TODO Auto-generated method stub
            try {
                return listdata.getJSONObject(position);
            } catch (Exception e){
                JSONObject jO = new JSONObject();
                return jO;
            }
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolderbjzb holder = new ViewHolderbjzb();
            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(
                        R.layout.activity_report, null);

                holder = new ViewHolderbjzb();
                holder.item2bjzb = ((TextView) convertView
                        .findViewById(R.id.item2bzrbjzb));
                holder.item3bjzb = ((TextView) convertView
                        .findViewById(R.id.item3bzrbjzb));
                convertView.setTag(holder);

                holder.item2bjzb.setWidth(w);
                holder.item3bjzb.setWidth(w);

                holder.item2bjzb.setText("");
                holder.item3bjzb.setText("");
            } else {
                holder = (ViewHolderbjzb) convertView.getTag();
            }

            for (int i = 0; i < listdata.length(); i++) {

                if (position == i) {
                    try {
                        holder.item2bjzb.setText(listdata.getJSONObject(position).getString("time"));
                        holder.item3bjzb.setText(listdata.getJSONObject(position).getString("heart_rate"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return convertView;
        }
    }
}
