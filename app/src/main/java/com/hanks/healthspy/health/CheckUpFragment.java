package com.hanks.healthspy.health;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.hanks.healthspy.R;

/**
 * Created by hanks on 16年4月12日.
 */
public class CheckUpFragment extends Fragment{

    public CheckUpFragment() {}

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_check,container,false);
        Button checkBtn = (Button) view.findViewById(R.id.heart_rate_btn);
        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getActivity(), CheckActivity.class);
                startActivity(it);
                getActivity().finish();
            }
        });
        return view;
    }
}
