package com.hanks.healthspy.shop;

/**
 * Created by hanks on 16年4月14日.
 */
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hanks.healthspy.R;

import org.json.JSONArray;

public class ListViewAdapter extends BaseAdapter {
    private Context context;                        //运行上下文
    private JSONArray listItems;    //商品信息集合
    private LayoutInflater listContainer;           //视图容器

    public final class ListItemView {                //自定义控件集合
        public ImageView image;
        public TextView title;
        public TextView info;
        public TextView price;
        public TextView item_price;
    }


    public ListViewAdapter(Context context, JSONArray listItems) {
        this.context = context;
        listContainer = LayoutInflater.from(context);   //创建视图容器并设置上下文
        this.listItems = listItems;
    }

    public int getCount() {
        // TODO Auto-generated method stub
        return listItems.length();
    }

    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * ListView Item设置
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Log.e("method", "getView");
        final int selectID = position;
        //自定义视图
        ListItemView listItemView = null;
        if (convertView == null) {
            listItemView = new ListItemView();
            //获取list_item布局文件的视图
            convertView = listContainer.inflate(R.layout.list_item, null);
            //获取控件对象
            listItemView.image = (ImageView) convertView.findViewById(R.id.imageItem);
            listItemView.title = (TextView) convertView.findViewById(R.id.titleItem);
            listItemView.info = (TextView) convertView.findViewById(R.id.infoItem);
            listItemView.price= (TextView) convertView.findViewById(R.id.price);
            listItemView.item_price = (TextView) convertView.findViewById(R.id.priceItem);
            //设置控件集到convertView
            convertView.setTag(listItemView);
        } else {
            listItemView = (ListItemView) convertView.getTag();
        }
//      Log.e("image", (String) listItems.get(position).get("title"));  //测试
//      Log.e("image", (String) listItems.get(position).get("info"));

        //设置文字和图片
        try {
            listItemView.image.setBackgroundResource(R.drawable.item);
            listItemView.info.setText(listItems.getJSONObject(position).getString("item_name"));
            listItemView.item_price.setText(listItems.getJSONObject(position).getString("price"));
            return convertView;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }
}
