package net.czlz.ida_debugger;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
//import android.support.v7.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Pattern;
import net.czlz.ida_debugger.DebugServer;
import android.util.Log;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ServiceConnection {
    static final String TAG = "ida_debug";

    private Intent intent;
    private TextView ip;
    private Button startbutton;
    private TextView tv;
    private int wifiSleepPolicy = 0;
//    private Handler handler = new Handler() { // from class: net.czlz.ida_debugger.MainActivity.1
//        @Override // android.os.Handler
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            MainActivity.this.tv.setText(msg.obj.toString());
//        }
//    };

    public native String stringFromJNI();

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.BaseFragmentActivityGingerbread, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.wifiSleepPolicy = Settings.System.getInt(getContentResolver(), "wifi_sleep_policy", 0);
        this.tv = (TextView) findViewById(R.id.sample_text);
        this.ip = (TextView) findViewById(R.id.IP);
        this.startbutton = (Button) findViewById(R.id.start);
        if (getRoot()) {
            if (CPUtil.getCpuArchitecture()[0].toString().equalsIgnoreCase("ARM")) {
                this.startbutton.setEnabled(true);
                CopyFile();
            } else {
                this.tv.setText("IDA服务端不支持ARM以外的硬件设备");
            }
        } else {
            this.tv.setText("获取权限失败！");
        }
        this.ip.setText(getIP());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
    }

    private void CopyFile() {
        InputStream in = null;
        FileOutputStream out = null;
        String path = getFilesDir().getPath();
        File file = new File(path + "/android_server");
        try {
            try {
                File file_ = new File(path);
                if (!file_.exists()) {
                    file_.mkdirs();
                }
                if (file_.exists() && !file.exists()) {
                    file.createNewFile();
                    in = getAssets().open("android_server");
                    FileOutputStream out2 = new FileOutputStream(file);
                    try {
                        byte[] buf = new byte[1024];
                        while (true) {
                            int length = in.read(buf);
                            if (length == -1) {
                                break;
                            }
                            out2.write(buf, 0, length);
                        }
                        out2.flush();
                        RootUtil.RootCommand("chmod 777 " + path + "/android_server");
                        out = out2;
                    } catch (Exception e) {
                        e = e;
                        out = out2;
                        e.printStackTrace();
                        if (in != null) {
                            try {
                                in.close();
                            } catch (Exception e1) {
                                e1.printStackTrace();
                                return;
                            }
                        }
                        if (out != null) {
                            out.close();
                            return;
                        }
                        return;
                    } catch (Throwable th) {
                        th = th;
                        out = out2;
                        if (in != null) {
                            try {
                                in.close();
                            } catch (Exception e12) {
                                e12.printStackTrace();
                                throw th;
                            }
                        }
                        if (out != null) {
                            out.close();
                        }
                        throw th;
                    }
                }
                if (in != null) {
                    try {
                        in.close();
                    } catch (Exception e13) {
                        e13.printStackTrace();
                        return;
                    }
                }
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed dumping state", e);
            }
        } catch (Throwable th2) {
            Log.e(TAG, "Failed dumping state", th2);
        }
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 4) {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    static {
        System.loadLibrary("native-lib");
    }

    private boolean getRoot() {
        String str;
        Pattern p = Pattern.compile("uid=0");
        if (RootUtil.isDeviceRooted()) {
            String apkRoot = "chmod 777 " + getPackageCodePath();
            return (RootUtil.RootCommand(apkRoot) == -1 || (str = RootUtil.RootCommandR("id")) == null || str.equalsIgnoreCase("") || !p.matcher(str).find()) ? false : true;
        }
        return false;
    }

    private String getIP() {
        WifiManager wifiManager = (WifiManager) getSystemService("wifi");
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        String ip = (ipAddress & 255) + "." + ((ipAddress >> 8) & 255) + "." + ((ipAddress >> 16) & 255) + "." + ((ipAddress >> 24) & 255);
        return ip;
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.getIP /* 2131427415 */:
                this.ip.setText(getIP());
                return;
            case R.id.start /* 2131427416 */:
                StartServer();
                return;
            case R.id.exit /* 2131427417 */:
                if (isServiceRunning(this)) {
                    CloseServer();
                }
                System.exit(0);
                return;
            default:
                return;
        }
    }

    private void CloseServer() {
        unbindService(this);
        Settings.System.putInt(getContentResolver(), "wifi_sleep_policy", 2);
    }

    private void StartServer() {
        if (isServiceRunning(this)) {
            CloseServer();
            this.startbutton.setText("开启调试");
            return;
        }
        this.intent = new Intent(this, DebugServer.class);
        bindService(this.intent, this, 1);
        Settings.System.putInt(getContentResolver(), "wifi_sleep_policy", this.wifiSleepPolicy);
        this.startbutton.setText("关闭调试");
    }

    @Override // android.content.ServiceConnection
    public void onServiceConnected(ComponentName name, IBinder service) {
        DebugServer.Binder binder = (DebugServer.Binder) service;
        DebugServer server = binder.getServer();
        server.setCallBack(new DebugServer.CallBack() { // from class: net.czlz.ida_debugger.MainActivity.2
            @Override // net.czlz.ida_debugger.DebugServer.CallBack
            public void setData(String data) {
                Message msg = new Message();
                msg.obj = data;
//                MainActivity.this.handler.sendMessage(msg);
            }
        });
    }

    @Override // android.content.ServiceConnection
    public void onServiceDisconnected(ComponentName name) {
    }

    public static boolean isServiceRunning(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService("activity");
        List<ActivityManager.RunningServiceInfo> services = am.getRunningServices(100);
        for (ActivityManager.RunningServiceInfo info : services) {
            String name = info.service.getClassName();
            if ("net.czlz.ida_debugger.DebugServer".equals(name)) {
                return true;
            }
        }
        return false;
    }
}
