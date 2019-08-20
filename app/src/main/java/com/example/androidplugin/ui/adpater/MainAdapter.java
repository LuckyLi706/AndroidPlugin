package com.example.androidplugin.ui.adpater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.androidplugin.R;

/**
 * 作者：jacky on 2019/8/17 12:13
 * 邮箱：jackyli706@gmail.com
 */
public class MainAdapter extends BaseAdapter {

    private String[] mainValue;
    private Context context;

    public MainAdapter(Context context, String[] mainValue) {
        //list = new ArrayList<>();
        this.mainValue = mainValue;
        this.context = context;
    }

    @Override
    public int getCount() {
        return mainValue.length;
    }

    @Override
    public Object getItem(int i) {
        return mainValue[i];
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.adapter, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.button = view.findViewById(R.id.tv);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.button.setText(mainValue[i]);
        return view;
    }

    private class ViewHolder {
        TextView button;
    }

}
