package com.jna.learnguitar;

import android.content.Context;
import android.graphics.Canvas;
import android.os.SystemClock;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

/**
 * Created by adamtobey on 7/8/14.
 */
public abstract class AbstractLayeredSurfaceView extends SurfaceView implements Runnable {
    protected SurfaceHolder holder;
    protected Canvas canvas;
    protected ArrayList<SurfaceViewRenderLayer> layers;

    private long previousUptime = 0l;
    private long currentUptime;
    private int millisDiff;
    private Thread renderThread;

    protected AbstractLayeredSurfaceView(Context context){
        super(context);

        holder = getHolder();

        initializeLayers();
    }

    protected abstract void initializeLayers();

    @Override
    public void run(){
        currentUptime = SystemClock.elapsedRealtime();
        millisDiff = (int)(currentUptime-previousUptime);
        onTick(millisDiff);
        draw(millisDiff);
        previousUptime = currentUptime;
    }

    protected void draw(int elapsedMillis){
        if (holder.getSurface().isValid()) {
            canvas = holder.lockCanvas();
            for (SurfaceViewRenderLayer layer : layers) {
                if (layer.getNeedsUpdate()) {
                    layer.render(canvas, elapsedMillis);
                }
            }
            holder.unlockCanvasAndPost(canvas);
            canvas = null;
        }
    }

    public void play(){
        previousUptime = SystemClock.elapsedRealtime();
        renderThread = new Thread(this);
        renderThread.start();
    }

    public void pause(){
        try {
            renderThread.join();
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    public void onCreate(){
        for (SurfaceViewRenderLayer layer : layers){
            layer.onCreate();
        }
    }

    public void onPause(){
        for (SurfaceViewRenderLayer layer : layers){
            layer.onPause();
        }
    }

    public void onDestroy(){
        for (SurfaceViewRenderLayer layer : layers){
            layer.onDestroy();
        }
    }

    public void onResume(){
        for (SurfaceViewRenderLayer layer : layers){
            layer.onResume();
        }
    }

    public void onStart(){
        for (SurfaceViewRenderLayer layer : layers){
            layer.onStart();
        }
    }

    public void onStop(){
        for (SurfaceViewRenderLayer layer : layers){
            layer.onStop();
        }
    }

    protected void onTick(int elapsedMillis){

    }

}
