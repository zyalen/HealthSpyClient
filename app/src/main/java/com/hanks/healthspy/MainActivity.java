package com.hanks.healthspy;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.hanks.healthspy.R;
import com.hanks.healthspy.health.CheckUpFragment;
import com.hanks.healthspy.report.ReportFragment;
import com.hanks.healthspy.shop.ShopFragment;
import com.hanks.healthspy.user.MyFragment;
import com.hanks.healthspy.user.UserFragment;

public class MainActivity extends AppCompatActivity  implements View.OnClickListener{
    //UI Object
    private TextView txt_topbar;
    private TextView txt_checkup;
    private TextView txt_report;
    private TextView txt_shop;
    private TextView txt_me;
    private FrameLayout ly_content;

    private SharedPreferences sharedPref;
    private int id;

    //Fragment Object
    private UserFragment ufg;
    private ShopFragment sfg;
    private ReportFragment rfg;
    private CheckUpFragment cfg;
    private FragmentManager fManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPref = this.getSharedPreferences("user_id", Context.MODE_PRIVATE);
        id = sharedPref.getInt("user_id", -1);
        fManager = getFragmentManager();
        bindViews();
        txt_checkup.performClick();   //模拟一次点击，既进去后选择第一项
    }

    //UI组件初始化与事件绑定
    private void bindViews() {
        txt_topbar = (TextView) findViewById(R.id.txt_topbar);
        txt_checkup = (TextView) findViewById(R.id.txt_checkup);
        txt_report = (TextView) findViewById(R.id.txt_report);
        txt_shop = (TextView) findViewById(R.id.txt_shop);
        txt_me = (TextView) findViewById(R.id.txt_me);
        ly_content = (FrameLayout) findViewById(R.id.ly_content);

        txt_checkup.setOnClickListener(this);
        txt_report.setOnClickListener(this);
        txt_shop.setOnClickListener(this);
        txt_me.setOnClickListener(this);
    }

    //重置所有文本的选中状态
    private void setSelected(){
        txt_checkup.setSelected(false);
        txt_report.setSelected(false);
        txt_shop.setSelected(false);
        txt_me.setSelected(false);
    }

    //隐藏所有Fragment
    private void hideAllFragment(FragmentTransaction fragmentTransaction){
        if(ufg != null)fragmentTransaction.hide(ufg);
        if(rfg != null)fragmentTransaction.hide(rfg);
        if(cfg != null)fragmentTransaction.hide(cfg);
        if(sfg != null)fragmentTransaction.hide(sfg);
    }


    @Override
    public void onClick(View v) {
        FragmentTransaction fTransaction = fManager.beginTransaction();
        hideAllFragment(fTransaction);
        switch (v.getId()){
            case R.id.txt_checkup:
                setSelected();
                txt_checkup.setSelected(true);
                if(cfg == null){
                    cfg = new CheckUpFragment();
                    fTransaction.add(R.id.ly_content,cfg);
                }else{
                    fTransaction.show(cfg);
                }
                break;
            case R.id.txt_report:
                setSelected();
                txt_report.setSelected(true);
                if(rfg == null){
                    rfg = new ReportFragment();
                    fTransaction.add(R.id.ly_content,rfg);
                }else{
                    fTransaction.show(rfg);
                }
                break;
            case R.id.txt_shop:
                setSelected();
                txt_shop.setSelected(true);
                if(sfg == null){
                    sfg = new ShopFragment();
                    fTransaction.add(R.id.ly_content,sfg);
                }else{
                    fTransaction.show(sfg);
                }
                break;
            case R.id.txt_me:
                setSelected();
                txt_me.setSelected(true);
                if(ufg == null){
                    ufg = new UserFragment(id);
                    fTransaction.add(R.id.ly_content,ufg);
                }else{
                    fTransaction.show(ufg);
                }
                break;
        }
        fTransaction.commit();
    }
}
