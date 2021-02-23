# 安卓库
## 危险信息检测库

引入方式
```
implementation 'cn.lucky.dangerinfoplugin:danagerinfoplugin:1.0.0'
```

调用方式
```
LogUtil.setDebug(false);  //关闭日志 
boolean emulator = DangerProtector.isEmulator(this);  //检测模拟器
boolean root = DangerProtector.isRoot(this);   //检测root
boolean debug = DangerProtector.isDebug(this); //检测debug
boolean xposedExsit = DangerProtector.isXposedExsit(this);  //检测xposed
boolean multipleApp = DangerProtector.isMultipleApp(this);  //检测多开
```

日志信息（检测到相关信息的日志）
```
08-20 16:31:24.849 1873-1873/com.example.androidplugin D/lucky: --------模拟器信息--------
08-20 16:31:24.849 1873-1873/com.example.androidplugin D/lucky: --------模拟器名字:MUMU模拟器--------
08-20 16:31:24.849 1873-1873/com.example.androidplugin D/lucky: --------模拟器识别的tag1:11111-111-1--------
08-20 16:31:24.849 1873-1873/com.example.androidplugin D/lucky: --------模拟器识别的tag2:00-000000000000000000000-1-0000000-000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000--------
08-20 16:31:24.959 1873-1873/com.example.androidplugin D/lucky: --------Root信息--------
08-20 16:31:24.959 1873-1873/com.example.androidplugin D/lucky: --------Root的tag:00000000000001011000001--------
08-20 16:31:24.974 1873-1873/com.example.androidplugin D/lucky: --------Xposed信息--------
08-20 16:31:24.974 1873-1873/com.example.androidplugin D/lucky: --------Xposed的tag:103--------
```
## 硬件通信库

引入方式

```java
implementation 'com.lucky.commplugin:commplugin:1.1.0'
```

初始化配置（必须）

```java
CommConfig commConfig = new CommConfig.Builder().builder();  //初始化配置对象

//可配置信息
int baudRate;   //波特率（默认115200）
int dataBits;   //数据位（默认8）
int stopBits;   //停止位（默认1）
int parity;     //校验位（默认0）
String classicUUID;  //经典蓝牙的UUID
String serverIP;  //局域网通信的ip
int serverPort;   //局域网通信的port
```

日志

```java
//过滤信息lucky
LogUtil.setDebug(false);  //关闭日志 (默认开启)
```

权限

```java
//蓝牙权限，6.0需要动态申请
<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
<!-- If your app targets Android 9 or lower, you can declare
     ACCESS_COARSE_LOCATION instead. -->
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />

<!-- true 表示手机必须支持BLE，否则无法安装！
	这里设为false, 运行后在Activity中检查-->
<uses-feature
    android:name="android.hardware.bluetooth_le"
    android:required="false" />
  
//网络权限，6.0以后需要动态申请
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />  
```

### USB通信

+ 封装了[usb开源库](https://github.com/mik3y/usb-serial-for-android)

+ USB连接关联Activity

  ```xml
  //关联某个activity,连接usb设备就启动activity,  根据xml下关联的配置文件进行匹配
   <intent-filter>
    <action android:name="android.hardware.usb.action.USB_DEVICE_ATTACHED" />
    </intent-filter>
    <meta-data
    android:name="android.hardware.usb.action.USB_DEVICE_ATTACHED"
    android:resource="@xml/device_filter" />
  
  device_filter.xml内容：
  <?xml version="1.0" encoding="utf-8"?>
  <resources>
      <!-- 0x0403 / 0x60??: FTDI -->
      <usb-device vendor-id="1027" product-id="24577" /> <!-- 0x6001: FT232R -->
      <usb-device vendor-id="1027" product-id="24592" /> <!-- 0x6010: FT2232H -->
      <usb-device vendor-id="1027" product-id="24593" /> <!-- 0x6011: FT4232H -->
      <usb-device vendor-id="1027" product-id="24596" /> <!-- 0x6014: FT232H -->
      <usb-device vendor-id="1027" product-id="24597" /> <!-- 0x6015: FT230X, FT231X, FT234XD -->
  
      <!-- 0x10C4 / 0xEA??: Silabs CP210x -->
      <usb-device vendor-id="4292" product-id="60000" /> <!-- 0xea60: CP2102 and other CP210x single port devices -->
      <usb-device vendor-id="4292" product-id="60016" /> <!-- 0xea70: CP2105 -->
      <usb-device vendor-id="4292" product-id="60017" /> <!-- 0xea71: CP2108 -->
  
      <!-- 0x067B / 0x2303: Prolific PL2303 -->
      <usb-device vendor-id="1659" product-id="8963" />
  
      <!-- 0x1a86 / 0x?523: Qinheng CH34x -->
      <usb-device vendor-id="6790" product-id="21795" /> <!-- 0x5523: CH341A -->
      <usb-device vendor-id="6790" product-id="29987" /> <!-- 0x7523: CH340 -->
  
      <!-- CDC driver -->
      <usb-device vendor-id="9025" />                   <!-- 0x2341 / ......: Arduino -->
      <usb-device vendor-id="5824" product-id="1155" /> <!-- 0x16C0 / 0x0483: Teensyduino  -->
      <usb-device vendor-id="1003" product-id="8260" /> <!-- 0x03EB / 0x2044: Atmel Lufa -->
      <usb-device vendor-id="7855" product-id="4"    /> <!-- 0x1eaf / 0x0004: Leaflabs Maple -->
      <usb-device vendor-id="3368" product-id="516"  /> <!-- 0x0d28 / 0x0204: ARM mbed -->
  </resources>
  ```

+ 操作步骤

1. 初始化配置

   ```java
   //设置波特率、数据位、停止位、校验位
   CommConfig commConfig = new CommConfig.Builder().baudRate(115200).dataBits(8).stopBits(1).parity(0).builder();
   ```

2. 初始化USB

   ```java
   UsbManagerClient.getInstance().initUsb(this, commConfig, new UsbStateListener() {
                   @Override
                   public void attach() {
                        //usb连接
                   }
   
                   @Override
                   public void detach() {
                        //usb断开
                   }
               });
   ```

3. 获取USB设备和建立连接

   ```java
               List<UsbSerialDriver> connectDevice = UsbManagerClient.getInstance().getConnectDevice();  //获取所有的连接设备
               if (connectDevice.size() > 0) {
                   UsbManagerClient.getInstance().openUsbConnection(connectDevice.get(0), new UsbConnectListener() {  //建立连接
                       @Override
                       public void connectSuccess() {
                           Toast.makeText(CommActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
                       }
   
                       @Override
                       public void connectFail(String message) {
                           Toast.makeText(CommActivity.this, "连接失败：" + message, Toast.LENGTH_SHORT).show();
                       }
                   });
               }
           }));
   
   ```

4. 发送数据

   ```java
   UsbManagerClient.getInstance().sendBuffer(String data);  //发送十六进制字符串
   UsbManagerClient.getInstance().sendBuffer(byte[] data);  //发送字节数组
   ```

5. 接收数据

   ```java
   //放在连接到USB设备成功后面去执行
   UsbManagerClient.getInstance().readDataAsync(new SerialInputOutputManager.Listener() {
                               @Override
                               public void onNewData(byte[] data) {
                                   //接收到数据
                               }
   
                               @Override
                               public void onRunError(Exception e) {
                                   //接收数据发生异常
                               }
                           });
   ```

6. 关闭连接释放资源

   ```java
   UsbManagerClient.getInstance().releaseUsb();
   ```

### 蓝牙通信

1. 初始化配置

   ```java
   //经典蓝牙客户端（服务端UUID）
   CommConfig commConfig = new CommConfig.Builder().classicUUID("").builder();
   
   //低功耗蓝牙客户端（服务端的主服务UUID、读UUID、写UUID）
   CommConfig commConfig = new CommConfig.Builder().bleServiceUUID("").bleNotifyUUID("").bleWriteUUID("").builder();
   
   //低功耗蓝牙服务端（可以自定义主服务UUID、读UUID、写UUID、描述信息UUID）
   CommConfig commConfig = new CommConfig.Builder().bleServiceUUID("").bleNotifyUUID("").bleWriteUUID("").blueDescNotifyUUID("").builder();
   
   ```

2. 蓝牙扫描（作为客户端需要做扫描操作）

   ```java
   /**
    * 扫描方案（获取蓝牙设备）
    * <code>
    * BluetoothAdapter.startDiscovery(){startScan_1}//可以扫描经典蓝牙和ble蓝牙两种
    * BluetoothAdapter.startLeScan(){startScan_2}//扫描低功耗蓝牙，在api21已经弃用，不过还是可以使用
    * BluetoothLeScanner.startScan(){startScan_3}//新的ble扫描方法
    * </code>
    * <p>
    * 官方地址：https://developer.android.com/guide/topics/connectivity/bluetooth
    *
    */
   ClassicClient.getInstance().startScan_1(new BlueScanListener() {
                   @Override
                   public void onScanResult(BluetoothDevice bluetoothDevice) {
                       ClassicClient.getInstance().stopScan_1();  //扫描到需要关闭扫描
                   }
               });
               
   ClassicClient.getInstance().startScan_2(new BlueScanListener() {
                   @Override
                   public void onScanResult(BluetoothDevice bluetoothDevice) {
                       
                   }
               });
               
   ClassicClient.getInstance().startScan_3(new BlueScanListener() {
                   @Override
                   public void onScanResult(BluetoothDevice bluetoothDevice) {
                       
                   }
               }); 
   ```

3. 使蓝牙可以被扫描到（作为服务端要能够被扫描到【非必须】）

   ```java
   ClassicServer.getInstance().enableDiscovery();
   ```

4. 初始化蓝牙

   ```java
   ClassicClient.getInstance().initBluetooth(Context context, CommConfig commConfig);
   ```

#### 经典蓝牙

+ 客户端

  1. 连接

     ```java
     ClassicClient.getInstance().connect(bluetoothDevice, new ClientConnectListener() {
                     @Override
                     public void connectSuccess() {
                         runOnUiThread(() -> Toast.makeText(CommActivity.this, "连接了", Toast.LENGTH_SHORT).show());
                         ClassicClient.getInstance().read(new ReadListener() {
                             @Override
                             public void readData(byte[] b) {
                                 runOnUiThread(() -> Toast.makeText(CommActivity.this, new String(b), Toast.LENGTH_SHORT).show());
                             }
     
                             @Override
                             public void readError(Exception e) {
                                 runOnUiThread(() -> Toast.makeText(CommActivity.this, "读取异常:" + e.getMessage(), Toast.LENGTH_SHORT).show());
                             }
                         });
                     }
     
                     @Override
                     public void connectFail(Exception e) {
                         runOnUiThread(() -> Toast.makeText(CommActivity.this, "连接异常" + e.getMessage(), Toast.LENGTH_SHORT).show());
                     }
                 });
     ```

  2. 写数据

     ```java
     ClassicClient.getInstance().write(String data) throws Exception  //写十六进制字符串
     ClassicClient.getInstance().write(byte[] data) throws Exception  //写字节数组
     ```

  3. 读取数据（放在蓝牙连接成功后）

     ```java
     ClassicClient.getInstance().read(new ReadListener() {
                             @Override
                             public void readData(byte[] b) {
                                 runOnUiThread(() -> Toast.makeText(CommActivity.this, new String(b), Toast.LENGTH_SHORT).show());
                             }
     
                             @Override
                             public void readError(Exception e) {
                                 runOnUiThread(() -> Toast.makeText(CommActivity.this, "读取异常:" + e.getMessage(), Toast.LENGTH_SHORT).show());
                             }
                         });
     ```

  4. 关闭蓝牙

     ```java
     ClassicClient.getInstance().close();
     ```

  5. 释放资源

     ```java
     ClassicClient.getInstance().release();
     ```

+ 服务端

  1. 等待连接

     ```java
     ClassicServer.getInstance().accept(new ServerAcceptListener() {
                     @Override
                     public void connectSuccess(Object object) {
                         BluetoothSocket bluetoothSocket = (BluetoothSocket) object;  //获取到客户端的蓝牙对象
                         
                     }
     
                     @Override
                     public void connectFail(Exception e) {
                         runOnUiThread(() -> Toast.makeText(CommActivity.this, "连接异常" + e.getMessage(), Toast.LENGTH_SHORT).show());
     
                     }
                 });
     ```

  2. 写入数据

     ```java
     ClassicServer.getInstance().write(String data) throws Exception  //写十六进制字符串
     ClassicServer.getInstance().write(byte[] data) throws Exception  //写字节数组
     ```

  3. 读取数据（放在连接成功之后）

     ```java
     ClassicServer.getInstance().read(new ReadListener() {
                             @Override
                             public void readData(byte[] b) {
                                 runOnUiThread(() -> Toast.makeText(CommActivity.this, new String(b), Toast.LENGTH_SHORT).show());
                             }
     
                             @Override
                             public void readError(Exception e) {
                                 runOnUiThread(() -> Toast.makeText(CommActivity.this, "读取异常:" + e.getMessage(), Toast.LENGTH_SHORT).show());
                             }
                         });
     ```

  4. 关闭连接

     ```java
     ClassicServer.getInstance().close();
     ```

  5. 释放资源

     ```java
     ClassicServer.getInstance().release();
     ```

#### 低功耗蓝牙

+ 客户端

  1. 连接

     ```java
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
     ```

  2. 写数据

     ```java
     BleClient.getInstance().write(String data) throws Exception  //写十六进制字符串
     BleClient.getInstance().write(byte[] data) throws Exception  //写字节数组
     ```

  3. 读数据

     ```java
     BleClient.getInstance().read(new ReadListener() {
                             @Override
                             public void readData(byte[] b) {
                                 runOnUiThread(() -> Toast.makeText(CommActivity.this, new String(b), Toast.LENGTH_SHORT).show());
                             }
     
                             @Override
                             public void readError(Exception e) {
                                 runOnUiThread(() -> Toast.makeText(CommActivity.this, "读取异常:" + e.getMessage(), Toast.LENGTH_SHORT).show());
                             }
                         });
     ```

  4. 关闭蓝牙

     ```java
     BleClient.getInstance().close();
     ```

  5. 释放资源

     ```java
     BleClient.getInstance().release();
     ```

+ 服务端

  1. 初始化蓝牙之后就自启动了（目前测试阶段）

## 局域网通信（暂略）

