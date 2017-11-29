package com.tao.mschelper;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.OutputStreamWriter;

/**
 * Created by Tao on 2017/10/25.
 */

public class GlobleUtil {
    public static void log(String str) {
        java.util.Date today = new java.util.Date();
        java.text.SimpleDateFormat dateTimeFormat = new java.text.SimpleDateFormat("mm:ss:SSS");
        printLog("[" + dateTimeFormat.format(today) + "]" + str.toString());
    }

    public static void printLog(Object o) {
        BufferedWriter bw = null;
        try {
            String logPath = Environment.getExternalStorageDirectory() + "/Download/MscHelper/";
            File file = new File(logPath);
            if (!file.exists()) {
                file.mkdirs();
            }

            String filePath = logPath + "msc.log";
            FileOutputStream fos = new FileOutputStream(filePath, true);
            bw = new BufferedWriter(new OutputStreamWriter(fos));
            bw.write("\n" + o.toString());
        } catch (Exception e) {
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (Exception e) {
            }
        }
    }

    public static String getString(String key, String defValue) {
        BufferedReader reader = null;
        try {
            File file = new File(Environment.getExternalStorageDirectory(), "/Download/MscHelper/msc.txt");
            reader = new BufferedReader(new FileReader(file));
            String l = null;
            while ((l = reader.readLine()) != null) {
                if (l.indexOf(key) == 0) {
                    return l.substring(key.length() + 1);
                }
            }
        } catch (Exception e) {
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                }
            }
        }

        return defValue;
    }

    public static void putString(String key, String value) {
        StringBuffer strContent = new StringBuffer();
        File file = new File(Environment.getExternalStorageDirectory(), "/Download/MscHelper/msc.txt");
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String l = null;
            while ((l = reader.readLine()) != null) {
                if (l.indexOf(key) != 0) {
                    if (strContent.length() > 0) {
                        strContent.append('\n');
                    }
                    strContent.append(l);
                }
            }
        } catch (Exception e) {
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                }
            }
        }

        if (strContent.length() > 0) {
            strContent.append('\n');
        }
        strContent.append(key);
        strContent.append(':');
        strContent.append(value);

        FileWriter writer = null;
        try {
            File filePath = new File(Environment.getExternalStorageDirectory(), "/Download/MscHelper");
            if (!filePath.exists()) {
                filePath.mkdirs();
            }
            writer = new FileWriter(file);
            writer.write(strContent.toString());
        } catch (Exception e) {
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception e) {
                }
            }
        }
    }

    public static boolean getBoolean(String key, boolean defValue) {
        return Boolean.parseBoolean(getString(key, Boolean.toString(defValue)));
    }

    public static void putBoolean(String key, boolean value) {
        putString(key, Boolean.toString(value));
    }

    public static int getInt(String key, int defValue) {
        return Integer.parseInt(getString(key, Integer.toString(defValue)));
    }

    public static void putInt(String key, int value) {
        putString(key, Integer.toString(value));
    }

    public static float getFloat(String key, float defValue) {
        return Float.parseFloat(getString(key, Float.toString(defValue)));
    }

    public static void putFloat(String key, float value) {
        putString(key, Float.toString(value));
    }
}
