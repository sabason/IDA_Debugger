package net.czlz.ida_debugger;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
//import android.support.annotation.Nullable;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/* loaded from: classes.dex */
public class DebugServer extends Service {
    private CallBack callback;
    private Thread debugThread = null;
    private Process localProcess = null;
    private Binder binder = new Binder();

    /* loaded from: classes.dex */
    public interface CallBack {
        void setData(String str);
    }

    public void setCallBack(CallBack callback) {
        this.callback = callback;
    }

    public DebugServer() {
        Debug();
    }

    /* loaded from: classes.dex */
    public class Binder extends android.os.Binder {
        public Binder() {
        }

        public DebugServer getServer() {
            return DebugServer.this;
        }
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        if (this.debugThread != null && !this.debugThread.isAlive()) {
            this.debugThread.start();
        }
    }

    public void Debug() {
        this.debugThread = new Thread(new Runnable() { // from class: net.czlz.ida_debugger.DebugServer.1
            @Override // java.lang.Runnable
            public void run() {
                DebugServer.this.sendMessage("正在启动调试器进程！");
                RootUtil.RootCommand("setenforce 0");
                RootUtil.RootCommand("setprop service.adb.tcp.port 5555");
                RootUtil.RootCommand("stop adbd");
                RootUtil.RootCommand("start adbd");
                String cmd = DebugServer.this.getFilesDir().getPath() + "/android_server";
                DataOutputStream localDataOutputStream = null;
                try {

                    try {
                        DebugServer.this.localProcess = Runtime.getRuntime().exec("su");
                        OutputStream localObject = DebugServer.this.localProcess.getOutputStream();
                        DataOutputStream localDataOutputStream2 = new DataOutputStream(localObject);
                        try {
                            String str = String.valueOf(cmd);
                            String localObject2 = str + "\n";
                            localDataOutputStream2.writeBytes(localObject2);
                            localDataOutputStream2.flush();
                            localDataOutputStream2.writeBytes("exit\n");
                            localDataOutputStream2.flush();
                            if (localDataOutputStream2 != null) {
                                try {
                                    localDataOutputStream2.close();
                                    localDataOutputStream = null;
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    localDataOutputStream = localDataOutputStream2;
                                }
                            } else {
                                localDataOutputStream = localDataOutputStream2;
                            }
                        } catch (Exception e2) {
                            Log.e("ida_debug", "error", e2);
                            localDataOutputStream = localDataOutputStream2;
                            DebugServer.this.sendMessage(e2.getMessage());
                            if (localDataOutputStream != null) {
                                try {
                                    localDataOutputStream.close();
                                    localDataOutputStream = null;
                                } catch (IOException e3) {
                                    e3.printStackTrace();
                                }
                            }
                            DebugServer.this.sendMessage("调试器已成功启动！");
                        } catch (Throwable th) {
                            th = th;
                            localDataOutputStream = localDataOutputStream2;
                            if (localDataOutputStream != null) {
                                try {
                                    localDataOutputStream.close();
                                } catch (IOException e4) {
                                    e4.printStackTrace();
                                }
                            }
                            throw th;
                        }
                    } catch (Exception e5) {
                        Log.e("ida_debug", "error", e5);
                    }
                    DebugServer.this.sendMessage("调试器已成功启动！");
                } catch (Throwable th2) {
                    Log.e("ida_debug", "error", th2);
                }
            }
        });
    }

    public void sendMessage(String msg) {
        if (this.callback != null) {
            this.callback.setData(msg);
        }
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override // android.app.Service
    public void onDestroy() {
        super.onDestroy();
        if (this.localProcess != null) {
            this.localProcess.destroy();
            this.localProcess = null;
        }
        if (this.debugThread != null && this.debugThread.isAlive()) {
            this.debugThread.destroy();
        }
        sendMessage("调试器进程已退出！");
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return this.binder;
    }
}
