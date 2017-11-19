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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

public class MainActivity extends Activity {
    class MscConn implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("MSC", "Service Connected");

            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);

            MscService.MscBinder binder = (MscService.MscBinder)service;
            binder.init(this, GlobleUtil.getInt("Hour", 10),
                    GlobleUtil.getInt("Minute", 0),
                    GlobleUtil.getInt("HitCount", 1),
                    GlobleUtil.getInt("Delay", 0),
                    GlobleUtil.getInt("Inteval", 100),
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

        final TimePicker tpTime = (TimePicker) findViewById(R.id.tpTime);
        tpTime.setIs24HourView(true);
        tpTime.setCurrentHour(GlobleUtil.getInt("Hour", 10));
        tpTime.setCurrentMinute(GlobleUtil.getInt("Minute", 0));

        final EditText etHitCount = (EditText)findViewById(R.id.etHitCount);
        etHitCount.setText(GlobleUtil.getString("HitCount", "1"));

        final EditText etDelay = (EditText)findViewById(R.id.etDelay);
        etDelay.setText(GlobleUtil.getString("Delay", "0"));

        final EditText etInteval = (EditText)findViewById(R.id.etInteval);
        etInteval.setText(GlobleUtil.getString("Inteval", "100"));

        final Button btStart = (Button) findViewById(R.id.btStart);
        btStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MainActivity.this.getResources().getText(R.string.stopservice).equals(btStart.getText().toString())) {
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
                    GlobleUtil.putInt("Hour", tpTime.getCurrentHour());
                    GlobleUtil.putInt("Minute", tpTime.getCurrentMinute());
                    GlobleUtil.putInt("HitCount", hitCount);
                    GlobleUtil.putInt("Delay", Integer.parseInt(etDelay.getText().toString()));
                    GlobleUtil.putInt("Inteval", inteval);

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

        }
    }
}
