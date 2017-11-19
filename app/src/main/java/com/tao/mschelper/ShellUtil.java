package com.tao.mschelper;

import android.graphics.Point;
import android.util.Log;

import com.orhanobut.logger.Logger;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Random;


/**
 * Created by Tao on 2017/10/27.
 */

public class ShellUtil {
    private static final Random r = new Random();

    private static void execCmd(String cmd) {
        //XposedBridge.log("ShellUtil: " + cmd);
        try {
            // 申请获取root权限，这一步很重要，不然会没有作用
            Process process = Runtime.getRuntime().exec("su");
            // 获取输出流
            OutputStream outputStream = process.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(
                    outputStream);
            dataOutputStream.writeBytes(cmd);
            dataOutputStream.flush();
            dataOutputStream.close();
            outputStream.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static void tap(int x, int y) {
        String cmd = "input tap " + x + " " + y;
        Logger.i(cmd);
        execCmd(cmd);
    }

    public static void swipe(ArrayList<Point> pnts) {
        execCmd("sendevent /dev/input/event5 3 57 " + r.nextInt(1000));
        for (int i = 0; i < pnts.size(); i++) {
            if (i == 0) {
                execCmd("sendevent /dev/input/event5 3 53 " + pnts.get(i).x);
                execCmd("sendevent /dev/input/event5 3 54 " + pnts.get(i).y);
                String cmd = "sendevent /dev/input/event5 3 58 " + (r.nextInt(5) + 88);
                execCmd(cmd);
                cmd = "sendevent /dev/input/event5 3 48 " + ((r.nextInt(7) + 3) * 2);
                execCmd(cmd);
                execCmd("sendevent /dev/input/event5 0 0 0");
            } else {
                int x1 = pnts.get(i - 1).x;
                int y1 = pnts.get(i - 1).y;
                int x2 = pnts.get(i).x;
                int y2 = pnts.get(i).y;
                int cx = x2 - x1;
                int cy = y2 - y1;
                if (cx != 0 || cy != 0) {
                    int step = Math.max(Math.abs(cx / 100), Math.abs(cy / 100));
                    if (step > 0) {
                        int stepX = cx / step;
                        int stepY = cy / step;
                        for (int j = 1; j < step; j++) {
                            int x = x1 + stepX * j;
                            if (x != x1) {
                                execCmd("sendevent /dev/input/event5 3 53 " + x);
                            }
                            int y = y1 + stepY * j;
                            if (y != y1) {
                                execCmd("sendevent /dev/input/event5 3 54 " + y);
                            }
                            execCmd("sendevent /dev/input/event5 0 0 0");
                        }
                    }
                    if (x2 != x1) {
                        execCmd("sendevent /dev/input/event5 3 53 " + x2);
                    }
                    if (y2 != y1) {
                        execCmd("sendevent /dev/input/event5 3 54 " + y2);
                    }
                    execCmd("sendevent /dev/input/event5 0 0 0");
                }
            }
        }
        execCmd("sendevent /dev/input/event5 3 57 -1");
        execCmd("sendevent /dev/input/event5 0 0 0");
    }

    public static void line(int x1, int y1, int x2, int y2) {
        execCmd("input swipe " + x1 + " " + y1 + " " + x2 + " " + y2);
    }
}
