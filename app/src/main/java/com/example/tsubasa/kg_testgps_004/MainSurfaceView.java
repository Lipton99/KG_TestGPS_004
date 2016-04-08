package com.example.tsubasa.kg_testgps_004;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Tsubasa on 2016/04/04.
 */
public class MainSurfaceView implements SurfaceHolder.Callback, Runnable {
    private SurfaceHolder holder;
    private static final String TAG = "MainSurfaceView";
    private Thread thread;

    public MainSurfaceView(Context context, SurfaceView sv) {
        holder = sv.getHolder();
        holder.addCallback(this);
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        Log.d(TAG, "surfaceChanged");
    }

    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        Log.d(TAG, "surfaceCreated");
        Canvas canvas = arg0.lockCanvas();
        canvas.drawColor(Color.BLACK);
        arg0.unlockCanvasAndPost(canvas);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        Log.d(TAG, "surfaceDestroyed");
        thread = null;
    }

    @Override
    public void run() {
        Log.d(TAG, "run");
    }
}
