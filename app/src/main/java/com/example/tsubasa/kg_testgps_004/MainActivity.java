package com.example.tsubasa.kg_testgps_004;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private SurfaceView surfaceView;
    private RPGView mainRPGView;

    private LocationManager locationManager;    // ロケーションマネージャ
    private TextView latitude_Label;
    private TextView longitude_Label;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        surfaceView = (SurfaceView)findViewById(R.id.surfaceViewMain);
        mainRPGView = new RPGView(this, surfaceView);

        // ロケーションマネージャのインスタンスを取得する
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        latitude_Label = (TextView) findViewById(R.id.latitude_Label);
        longitude_Label = (TextView) findViewById(R.id.longitude_Label);

        // ボタンを定義
        Button button_up = (Button) findViewById(R.id.button_up);
        Button button_down = (Button) findViewById(R.id.button_down);
        Button button_left = (Button) findViewById(R.id.button_left);
        Button button_right = (Button) findViewById(R.id.button_right);

        // ボタンに OnClickListener インターフェースを実装する
        button_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainRPGView.onTouchButtonEvent("up");
                //Toast.makeText(MainActivity.this, "「↑」クリックされました！", Toast.LENGTH_LONG).show();
            }
        });
        button_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainRPGView.onTouchButtonEvent("down");
                //Toast.makeText(MainActivity.this, "「↓」クリックされました！", Toast.LENGTH_LONG).show();
            }
        });
        button_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainRPGView.onTouchButtonEvent("left");
                //Toast.makeText(MainActivity.this, "「←」クリックされました！", Toast.LENGTH_LONG).show();
            }
        });
        button_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainRPGView.onTouchButtonEvent("right");
               //Toast.makeText(MainActivity.this, "「→」クリックされました！", Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    protected void onResume() {
        if(locationManager != null)
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        super.onResume();
        }
    @Override
    public void onStart() {
        super.onStart();
        // 位置情報の更新を受け取るように設定
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);
    }
    @Override
    protected void onPause() {
        if(locationManager != null) {
            locationManager.removeUpdates(this);
            }
        super.onPause();
        }
    @Override
    public void onStop() {
        super.onStop();
        // 位置情報の更新を止める
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
    // 例としてラベルに取得した位置を表示
        latitude_Label.setText(Double.toString(location.getLatitude()));
        longitude_Label.setText(Double.toString(location.getLongitude()));
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }
}
