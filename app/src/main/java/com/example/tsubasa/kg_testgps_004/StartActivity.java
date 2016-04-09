package com.example.tsubasa.kg_testgps_004;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by Tsubasa on 2016/04/09.
 */
public class StartActivity extends AppCompatActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);

        Button btnStart = (Button)findViewById(R.id.button_start);
        btnStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Main画面を起動
                Intent intent = new Intent(StartActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}

