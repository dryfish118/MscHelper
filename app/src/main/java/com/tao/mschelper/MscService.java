package com.tao.mschelper;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.orhanobut.logger.Logger;

import java.util.Calendar;
import java.util.Random;
import java.util.TimeZone;

public class  MscService extends Service {
    class MscBinder extends Binder {
        void init(final MainActivity.MscConn conn, final int hour, final int minute, final int count,
                  final int delay, final int inteval, final int w, final int h) {
            Logger.i("Service init " +
                    "(Hour:" + hour +
                    ")(Minute:" + minute +
                    ")(Count:" + count +
                    ")(Delay:" + delay +
                    ")(Inteval:" + inteval +
                    ")(Width:" + w +
                    ")(Height:" + h + ")");

            Calendar cal = Calendar.getInstance();
            cal.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
            int cHour = cal.get(Calendar.HOUR);
            if (cal.get(Calendar.AM_PM) != 0) {
                cHour += 12;
            }
            int cMinute = cal.get(Calendar.MINUTE);
            int cSecond = cal.get(Calendar.SECOND);
            int millis = (hour * 60  + minute) * 60 - (cHour * 60  + cMinute) * 60 - cSecond - 1;
            Logger.i("Start tap after " + (millis + 1) + " '");

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
                    Logger.i("Current time " + cHour + ":" + cMinute + ":" + cSecond);
                    if (cHour == hour && cMinute == minute) {
                        Random r = new Random();
                        int x = w * (r.nextInt(8) + 1) / 10;
                        int y = h - 30 - (r.nextInt(6) - 3);
                        try {
                            if (delay != 0) {
                                Logger.i("Delay " + delay);
                                Thread.sleep(delay);
                            }
                            for (int i = 0; i < count; i++) {
                                ShellUtil.tap(x + r.nextInt(6) - 3, y + r.nextInt(6) - 3);
                                Thread.sleep(inteval + r.nextInt(inteval / 4) - inteval / 8);
                            }
                        } catch (Exception e) {
                            Logger.i(e.toString());
                        }
                        Logger.i("Service Finished");
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
