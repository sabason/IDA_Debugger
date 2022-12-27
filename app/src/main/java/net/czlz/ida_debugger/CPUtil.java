package net.czlz.ida_debugger;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

/* loaded from: classes.dex */
public class CPUtil {
    private static Object[] mArmArchitecture = new Object[3];

    static {
        mArmArchitecture[1] = -1;
    }

    public static Object[] getCpuArchitecture() {
        InputStream is;
        InputStreamReader ir;
        BufferedReader br;

        if (((Integer) mArmArchitecture[1]).intValue() != -1) {
            return mArmArchitecture;
        }
        try {
            is = new FileInputStream("/proc/cpuinfo");
            ir = new InputStreamReader(is);
            br = new BufferedReader(ir);
            while (true) {
                String line = br.readLine();
                if (line == null) {
                    break;
                }
                String[] pair = line.split(":");
                if (pair.length == 2) {
                    String key = pair[0].trim();
                    String val = pair[1].trim();
                    if (key.compareTo("Processor") == 0) {
                        String n = "";
                        for (int i = val.indexOf("ARMv") + 4; i < val.length(); i++) {
                            String temp = val.charAt(i) + "";
                            if (!temp.matches("\\d")) {
                                break;
                            }
                            n = n + temp;
                        }
                        mArmArchitecture[0] = "ARM";
                        mArmArchitecture[1] = Integer.valueOf(Integer.parseInt(n));
                    } else if (key.compareToIgnoreCase("Features") == 0) {
                        if (val.contains("neon")) {
                            mArmArchitecture[2] = "neon";
                        }
                    } else if (key.compareToIgnoreCase("model name") == 0) {
                        if (val.contains("Intel")) {
                            mArmArchitecture[0] = "INTEL";
                            mArmArchitecture[2] = "atom";
                        }
                    } else if (key.compareToIgnoreCase("cpu family") == 0) {
                        mArmArchitecture[1] = Integer.valueOf(Integer.parseInt(val));
                    }
                }
            }
            br.close();
            ir.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable th) {
            throw th;
        }
        return mArmArchitecture;
    }
//
//    public static Object[] mArmArchitecture = {-1, -1, -1};
//
//    public static Object[] getCpuArchitecture() {
//        if (((Integer) mArmArchitecture[1]).intValue() != -1) {
//            return mArmArchitecture;
//        }
//        try {
//            InputStream is = new FileInputStream("/proc/cpuinfo");
//            InputStreamReader ir = new InputStreamReader(is);
//            BufferedReader br = new BufferedReader(ir);
//            while (true) {
//                String line = br.readLine();
//                if (line == null) {
//                    break;
//                }
//                String[] pair = line.split(":");
//                if (pair.length == 2) {
//                    String key = pair[0].trim();
//                    String val = pair[1].trim();
//                    if (key.compareTo("Processor") == 0) {
//                        String n = "";
//                        for (int i = val.indexOf("ARMv") + 4; i < val.length(); i++) {
//                            String temp = val.charAt(i) + "";
//                            if (!temp.matches("\\d")) {
//                                break;
//                            }
//                            n = n + temp;
//                        }
//                        mArmArchitecture[0] = "ARM";
//                        mArmArchitecture[1] = Integer.valueOf(Integer.parseInt(n));
//                    } else if (key.compareToIgnoreCase("Features") == 0) {
//                        if (val.contains("neon")) {
//                            mArmArchitecture[2] = "neon";
//                        }
//                    } else if (key.compareToIgnoreCase("model name") == 0) {
//                        if (val.contains("Intel")) {
//                            mArmArchitecture[0] = "INTEL";
//                            mArmArchitecture[2] = "atom";
//                        }
//                    } else if (key.compareToIgnoreCase("cpu family") == 0) {
//                        mArmArchitecture[1] = Integer.valueOf(Integer.parseInt(val));
//                    }
//                }
//            }
//            br.close();
//            ir.close();
//            is.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return mArmArchitecture;
//    }
}
