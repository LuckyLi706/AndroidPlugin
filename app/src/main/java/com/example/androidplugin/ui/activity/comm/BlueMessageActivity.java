package com.example.androidplugin.ui.activity.comm;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androidplugin.Constants;
import com.example.androidplugin.R;
import com.lucky.commplugin.CommConfig;
import com.lucky.commplugin.bluetooth.client.BleClient;
import com.lucky.commplugin.bluetooth.client.ClassicClient;
import com.lucky.commplugin.bluetooth.server.BleServer;
import com.lucky.commplugin.bluetooth.server.ClassicServer;
import com.lucky.commplugin.listener.ClientConnectListener;
import com.lucky.commplugin.listener.ReadListener;
import com.lucky.commplugin.listener.ServerAcceptListener;
import com.lucky.commplugin.usb.UsbManagerClient;

public class BlueMessageActivity extends AppCompatActivity implements ReadListener {
    private BluetoothDevice bluetoothDevice;
    private String name;
    private TextView tvMsg;
    private EditText etMsg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blue_message);
        initView();

        switch (name) {
            case Constants.CLASSIC_BLUE_CLIENT:
                setTitle("经典蓝牙客户端");
                break;
            case Constants.CLASSIC_BLUE_SERVER:
                setTitle("经典蓝牙服务端");
                break;
            case Constants.BLE_BLUE_CLIENT:
                setTitle("低功耗蓝牙客户端");
            default:
                setTitle("低功耗蓝牙服务端");
                break;
        }
    }

    private void initView() {

        name = getIntent().getStringExtra("name");
        bluetoothDevice = getIntent().getParcelableExtra("blue");

        tvMsg = findViewById(R.id.tv_mag);
        etMsg = findViewById(R.id.et_msg);

        findViewById(R.id.btn_start_connect).setOnClickListener(v -> {
            switch (name) {
                case Constants.CLASSIC_BLUE_CLIENT:
                    ClassicClient.getInstance().connect(bluetoothDevice, new ClientConnectListener() {
                        @Override
                        public void connectSuccess() {
                            ClassicClient.getInstance().read(BlueMessageActivity.this);
                        }

                        @Override
                        public void connectFail(Exception e) {
                            Toast.makeText(BlueMessageActivity.this, "连接发生异常：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                case Constants.CLASSIC_BLUE_SERVER:
                    ClassicServer.getInstance().accept(new ServerAcceptListener() {
                        @Override
                        public void connectSuccess(Object object) {
                            ClassicServer.getInstance().read(BlueMessageActivity.this);
                        }

                        @Override
                        public void connectFail(Exception e) {
                            Toast.makeText(BlueMessageActivity.this, "接收连接发生异常：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                case Constants.BLE_BLUE_CLIENT:
                    BleClient.getInstance().connect(bluetoothDevice, new ClientConnectListener() {
                        @Override
                        public void connectSuccess() {
                            ClassicClient.getInstance().read(BlueMessageActivity.this);
                        }

                        @Override
                        public void connectFail(Exception e) {
                            Toast.makeText(BlueMessageActivity.this, "连接发生异常：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                default:
                    BleServer.getInstance().accept(new ServerAcceptListener() {
                        @Override
                        public void connectSuccess(Object object) {
                            ClassicServer.getInstance().read(BlueMessageActivity.this);
                        }

                        @Override
                        public void connectFail(Exception e) {
                            Toast.makeText(BlueMessageActivity.this, "接收连接发生异常：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
            }

            findViewById(R.id.btn_send_msg).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String message = etMsg.getText().toString();
                    if (message.equals("")) {
                        Toast.makeText(BlueMessageActivity.this, "消息不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    switch (name) {
                        case Constants.CLASSIC_BLUE_CLIENT:
                            try {
                                ClassicClient.getInstance().write(message.getBytes());
                            } catch (Exception e) {
                                Toast.makeText(BlueMessageActivity.this, "写消息发生异常：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case Constants.CLASSIC_BLUE_SERVER:
                            try {
                                ClassicServer.getInstance().write(message.getBytes());
                            } catch (Exception e) {
                                Toast.makeText(BlueMessageActivity.this, "写消息发生异常：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case Constants.BLE_BLUE_CLIENT:
                            try {
                                BleClient.getInstance().write(message.getBytes());
                            } catch (Exception e) {
                                Toast.makeText(BlueMessageActivity.this, "写消息发生异常：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            break;
                        default:
                            try {
                                BleServer.getInstance().write(message.getBytes());
                            } catch (Exception e) {
                                Toast.makeText(BlueMessageActivity.this, "写消息发生异常：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            break;
                    }
                }
            });
        });

        findViewById(R.id.btn_close_connect).setOnClickListener(v -> {
            ClassicClient.getInstance().close();
        });
    }

    @Override
    public void readData(byte[] b) {
        runOnUiThread(() -> {
            tvMsg.append("收到消息：" + new String(b));
        });
    }

    @Override
    public void readError(Exception e) {
        Toast.makeText(BlueMessageActivity.this, "读数据发生异常：" + e.getMessage(), Toast.LENGTH_SHORT).show();
    }
}
