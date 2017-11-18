package com.tao.mschelper;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ServiceConnection sc = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d("MSC", "Service Connected");

                DisplayMetrics dm = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(dm);
                int w = (int)(dm.widthPixels * dm.density);
                int h = (int)(dm.heightPixels * dm.density);
                w = dm.widthPixels;
                h = dm.heightPixels;

                MscService.MscBinder binder = (MscService.MscBinder)service;
                binder.init(GlobleUtil.getInt("Hour", 10),
                        GlobleUtil.getInt("Minute", 0),
                        GlobleUtil.getInt("HitCount", 3),
                        GlobleUtil.getInt("Inteval", 100), w, h);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d("MSC", "Service Disconnected");
            }
        };

        final TimePicker tpTime = (TimePicker) findViewById(R.id.tpTime);
        tpTime.setIs24HourView(true);
        tpTime.setCurrentHour(GlobleUtil.getInt("Hour", 10));
        tpTime.setCurrentMinute(GlobleUtil.getInt("Minute", 0));
        final EditText etHitCount = (EditText)findViewById(R.id.etHitCount);
        etHitCount.setText(GlobleUtil.getString("HitCount", "3"));
        final EditText etInteval = (EditText)findViewById(R.id.etInteval);
        etInteval.setText(GlobleUtil.getString("Inteval", "100"));

        findViewById(R.id.btStart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hitCount = Integer.parseInt(etHitCount.getText().toString());
                if (hitCount == 0) {
                    Toast.makeText(MainActivity.this, R.string.promtHitCount, Toast.LENGTH_LONG).show();
                    return;
                }
                int inteval = Integer.parseInt(etInteval.getText().toString());
                if (inteval == 0) {
                    Toast.makeText(MainActivity.this, R.string.promtInteval, Toast.LENGTH_LONG).show();
                    return;
                }
                GlobleUtil.putInt("Hour", tpTime.getCurrentHour());
                GlobleUtil.putInt("Minute", tpTime.getCurrentMinute());
                GlobleUtil.putInt("HitCount", hitCount);
                GlobleUtil.putInt("Inteval", inteval);

                Intent intent = new Intent(MainActivity.this, MscService.class);
                try {
                    unbindService(sc);
                } catch (Exception e) {

                }

                Log.d("MSC", "Create Service");
                bindService(intent, sc, Context.BIND_AUTO_CREATE);
            }
        });
    }
}
