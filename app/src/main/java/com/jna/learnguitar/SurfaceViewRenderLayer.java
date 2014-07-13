package com.jna.learnguitar;

import android.graphics.Canvas;

/**
 * Created by adamtobey on 7/8/14.
 */
public abstract class SurfaceViewRenderLayer{
    private boolean needsUpdate = true;

    public abstract void render(Canvas canvas, int elapsedNanos);
    public void setNeedsUpdate(boolean needsUpdate){
        this.needsUpdate = needsUpdate;
    }
    public boolean getNeedsUpdate(){
        return needsUpdate;
    }

    public void onCreate(){

    }

    public void onPause(){

    }

    public void onDestroy(){

    }

    public void onResume(){

    }

    public void onStart(){

    }

    public void onStop(){

    }

    public void onTick(){

    }
}
