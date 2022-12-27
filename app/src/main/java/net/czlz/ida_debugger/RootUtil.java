package net.czlz.ida_debugger;

import android.os.Build;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;

/* loaded from: classes.dex */
public class RootUtil {
    public static boolean isDeviceRooted() {
        return checkRootMethod1() || checkRootMethod2() || checkRootMethod3() || checkRootMethod4();
    }

    private static boolean checkRootMethod1() {
        String buildTags = Build.TAGS;
        return buildTags != null && buildTags.contains("test-keys");
    }

    private static boolean checkRootMethod2() {
        return new File("/system/app/Superuser.apk").exists();
    }

    private static boolean checkRootMethod3() {
        String[] paths = {"/sbin/su", "/system/bin/su", "/system/xbin/su", "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su", "/system/bin/failsafe/su", "/data/local/su"};
        for (String path : paths) {
            if (new File(path).exists()) {
                return true;
            }
        }
        return false;
    }

    private static boolean checkRootMethod4() {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(new String[]{"/system/xbin/which", "su"});
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            if (in.readLine() == null) {
                if (process != null) {
                    process.destroy();
                }
                return false;
            } else if (process != null) {
                process.destroy();
                return true;
            } else {
                return true;
            }
        } catch (Throwable th) {
            if (process != null) {
                process.destroy();
            }
            return false;
        }
    }

    public static int RootCommand(String command) {
        int i = 0;
        Process localProcess = null;
        DataOutputStream localDataOutputStream = null;
        try {
            try {
                localProcess = Runtime.getRuntime().exec("su");
                OutputStream localObject = localProcess.getOutputStream();
                DataOutputStream localDataOutputStream2 = new DataOutputStream(localObject);
                try {
                    String str = String.valueOf(command);
                    String localObject2 = str + "\n";
                    localDataOutputStream2.writeBytes(localObject2);
                    localDataOutputStream2.flush();
                    localDataOutputStream2.writeBytes("exit\n");
                    localDataOutputStream2.flush();
                    localProcess.waitFor();
                    int result = localProcess.exitValue();
                    i = Integer.valueOf(result).intValue();
                    if (localDataOutputStream2 != null) {
                        try {
                            localDataOutputStream2.close();
                        } catch (Exception e) {
                        }
                    }
                    localProcess.destroy();
                    localDataOutputStream = localDataOutputStream2;
                } catch (Exception e2) {
                    localDataOutputStream = localDataOutputStream2;
                    e2.printStackTrace();
                    i = -1;
                    if (localDataOutputStream != null) {
                        try {
                            localDataOutputStream.close();
                        } catch (Exception e3) {
                        }
                    }
                    localProcess.destroy();
                    return i;
                } catch (Throwable th) {
                    th = th;
                    localDataOutputStream = localDataOutputStream2;
                    if (localDataOutputStream != null) {
                        try {
                            localDataOutputStream.close();
                        } catch (Exception e4) {
                            throw th;
                        }
                    }
                    localProcess.destroy();
                    throw th;
                }
            } catch (Exception e5) {

            }
            return i;
        } catch (Throwable th2) {

        }
        return i;
    }

    public static String RootCommandR(String command) {
        String str = null;
        Process localProcess = null;
        DataOutputStream localDataOutputStream = null;
        BufferedReader mReader = null;
        try {
            try {
                localProcess = Runtime.getRuntime().exec("su");
                OutputStream localObject = localProcess.getOutputStream();
                DataOutputStream localDataOutputStream2 = new DataOutputStream(localObject);
                try {
                    String str2 = String.valueOf(command);
                    String localObject2 = str2 + "\n";
                    localDataOutputStream2.writeBytes(localObject2);
                    localDataOutputStream2.flush();
                    localDataOutputStream2.writeBytes("exit\n");
                    localDataOutputStream2.flush();
                    localProcess.waitFor();
                    localProcess.exitValue();
                    BufferedReader mReader2 = new BufferedReader(new InputStreamReader(localProcess.getInputStream()));
                    try {
                        StringBuffer mRespBuff = new StringBuffer();
                        char[] buff = new char[1024];
                        while (true) {
                            int ch = mReader2.read(buff);
                            if (ch == -1) {
                                break;
                            }
                            mRespBuff.append(buff, 0, ch);
                        }
                        str = mRespBuff.toString();
                        if (localDataOutputStream2 != null) {
                            try {
                                localDataOutputStream2.close();
                            } catch (Exception e) {
                            }
                        }
                        if (mReader2 != null) {
                            mReader2.close();
                        }
                        localProcess.destroy();
                    } catch (Exception e2) {
                        mReader = mReader2;
                        localDataOutputStream = localDataOutputStream2;
                        e2.printStackTrace();
                        str = "";
                        if (localDataOutputStream != null) {
                            try {
                                localDataOutputStream.close();
                            } catch (Exception e3) {
                            }
                        }
                        if (mReader != null) {
                            mReader.close();
                        }
                        localProcess.destroy();
                        return str;
                    } catch (Throwable th) {
                        th = th;
                        mReader = mReader2;
                        localDataOutputStream = localDataOutputStream2;
                        if (localDataOutputStream != null) {
                            try {
                                localDataOutputStream.close();
                            } catch (Exception e4) {
                                throw th;
                            }
                        }
                        if (mReader != null) {
                            mReader.close();
                        }
                        localProcess.destroy();
                        throw th;
                    }
                } catch (Exception e5) {

                } catch (Throwable th2) {

                }
            } catch (Throwable th3) {

            }
        } catch (Exception e6) {
            
        }
        return str;
    }
}
