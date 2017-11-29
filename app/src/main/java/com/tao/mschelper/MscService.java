package com.tao.mschelper;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import java.util.Calendar;
import java.util.Random;
import java.util.TimeZone;

public class  MscService extends Service {
    class MscBinder extends Binder {
        void init(final MainActivity.MscConn conn, int deviation,
                  final int hour, final int minute, final int count,
                  final int delay, final int inteval, final int w, final int h) {
            GlobleUtil.log("Service init " +
                    "(Deviation:" + deviation +
                    ")(Hour:" + hour +
                    ")(Minute:" + minute +
                    ")(Count:" + count +
                    ")(Delay:" + delay +
                    ")(Inteval:" + inteval +
                    ")(Width:" + w +
                    ")(Height:" + h + ")");

            final int startTime = (hour * 60 + minute) * 60 + deviation;

            Calendar cal = Calendar.getInstance();
            cal.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
            int cHour = cal.get(Calendar.HOUR);
            if (cal.get(Calendar.AM_PM) != 0) {
                cHour += 12;
            }
            int cMinute = cal.get(Calendar.MINUTE);
            int cSecond = cal.get(Calendar.SECOND);
            int millis = startTime - (cHour * 60  + cMinute) * 60 - cSecond - 1;
            GlobleUtil.log("Start tap after " + (millis + 1) + " '");

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
                    int cHour = cal.get(Calendar.HOUR);
                    if (cal.get(Calendar.AM_PM) != 0) {
                        cHour += 12;
                    }
                    int cMinute = cal.get(Calendar.MINUTE);
                    int cSecond = cal.get(Calendar.SECOND);
                    GlobleUtil.log("Current time " + cHour + ":" + cMinute + ":" + cSecond);
                    if ((cHour * 60 + cMinute) * 60 + cSecond == startTime) {
                        Random r = new Random();
                        int x = w * (r.nextInt(8) + 1) / 10;
                        int y = h - 30 - (r.nextInt(6) - 3);
                        try {
                            if (delay != 0) {
                                GlobleUtil.log("Delay " + delay);
                                Thread.sleep(delay);
                            }
                            for (int i = 0; i < count; i++) {
                                ShellUtil.tap(x + r.nextInt(6) - 3, y + r.nextInt(6) - 3);
                                Thread.sleep(inteval + r.nextInt(inteval / 4) - inteval / 8);
                            }
                        } catch (Exception e) {
                            GlobleUtil.log(e.toString());
                        }
                        GlobleUtil.log("Service Finished");
                        conn.finished();
                        return;
                    }
                    handler.postDelayed(this, 30);
                }
            }, millis * 1000);
        }
    }

    MscBinder binder;

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        binder = new MscBinder();
    }
}
