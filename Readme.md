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