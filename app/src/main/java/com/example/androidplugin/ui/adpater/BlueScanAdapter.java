package com.example.androidplugin.ui.adpater;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidplugin.R;
import com.example.androidplugin.ui.activity.comm.BlueMessageActivity;

import java.util.List;

public class BlueScanAdapter extends RecyclerView.Adapter<BlueScanAdapter.MyViewHolder> {

    private Context context;
    private List<BluetoothDevice> mDatas;
    private String name;


    public BlueScanAdapter(Context context, List<BluetoothDevice> mDatas, String name) {
        this.mDatas = mDatas;
        this.context = context;
        this.name = name;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.activity_blue_scan_sub, parent, false));
    }


    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tv_name.setText(mDatas.get(position).getName() == null ? "NULL" : mDatas.get(position).getName());
        holder.tv_mac.setText(mDatas.get(position).getAddress());
        holder.btn_connect.setOnClickListener(v -> {
            Intent intent = new Intent(context, BlueMessageActivity.class);
            BluetoothDevice bluetoothDevice = mDatas.get(position);
            intent.putExtra("blue", bluetoothDevice);
            intent.putExtra("name", name);
            context.startActivity(new Intent(context, BlueMessageActivity.class));
        });
    }


    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_name;
        TextView tv_mac;
        Button btn_connect;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_name = itemView.findViewById(R.id.tv_name);
            tv_mac = itemView.findViewById(R.id.tv_mac);
            btn_connect = itemView.findViewById(R.id.btn_connect);
        }
    }
}
