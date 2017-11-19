package com.tao.mschelper;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

public class MainActivity extends Activity {
    class MscConn implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("MSC", "Service Connected");

            final SharedPreferences sp = getSharedPreferences("MSC", MODE_PRIVATE);

            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);

            MscService.MscBinder binder = (MscService.MscBinder)service;
            binder.init(this, sp.getInt("Hour", 10),
                    sp.getInt("Minute", 0),
                    sp.getInt("HitCount", 1),
                    sp.getInt("Delay", 0),
                    sp.getInt("Inteval", 100),
                    dm.widthPixels, dm.heightPixels);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("MSC", "Service Disconnected");
        }

        void finished() {
            stopService();
        }
    }

    ServiceConnection sc = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sc = new MscConn();

        final SharedPreferences sp = getSharedPreferences("MSC", MODE_PRIVATE);

        final TimePicker tpTime = (TimePicker) findViewById(R.id.tpTime);
        tpTime.setIs24HourView(true);
        tpTime.setCurrentHour(sp.getInt("Hour", 10));
        tpTime.setCurrentMinute(sp.getInt("Minute", 0));

        final EditText etHitCount = (EditText)findViewById(R.id.etHitCount);
        etHitCount.setText(String.valueOf(sp.getInt("HitCount", 1)));

        final EditText etDelay = (EditText)findViewById(R.id.etDelay);
        etDelay.setText(String.valueOf(sp.getInt("Delay", 0)));

        final EditText etInteval = (EditText)findViewById(R.id.etInteval);
        etInteval.setText(String.valueOf(sp.getInt("Inteval", 100)));

        final Button btStart = (Button) findViewById(R.id.btStart);
        btStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MainActivity.this.getResources().getText(R.string.stopservice).equals(btStart.getText().toString())) {
                    Log.d("MSC", "Service Stopped");
                    stopService();
                } else {
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

                    SharedPreferences.Editor et = sp.edit();
                    et.putInt("Hour", tpTime.getCurrentHour());
                    et.putInt("Minute", tpTime.getCurrentMinute());
                    et.putInt("HitCount", hitCount);
                    et.putInt("Delay", Integer.parseInt(etDelay.getText().toString()));
                    et.putInt("Inteval", inteval);
                    et.apply();

                    Log.d("MSC", "Create Service");
                    Intent intent = new Intent(MainActivity.this, MscService.class);
                    if (bindService(intent, sc, Context.BIND_AUTO_CREATE)) {
                        btStart.setText(R.string.stopservice);
                    }
                }
            }
        });
    }

    void stopService() {
        try {
            unbindService(sc);
            ((Button) findViewById(R.id.btStart)).setText(R.string.startservice);
        } catch (Exception e) {
            Log.d("MSC", e.toString());
        }
    }
}
