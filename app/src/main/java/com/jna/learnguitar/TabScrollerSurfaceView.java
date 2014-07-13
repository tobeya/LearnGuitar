package com.jna.learnguitar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.DisplayMetrics;
import android.util.Log;

/**
 * Created by adamtobey on 7/8/14.
 */
public class TabScrollerSurfaceView extends AbstractLayeredSurfaceView {
    private final String tag = "LearnGuitar/TabScrollerSurfaceView";

    Picture currentTabRender;
    float tabScrollRate; //pixels per millisecond
    private Canvas currentTabRenderCanvas;
    private Paint textPaint;
    DisplayMetrics displayMetrics;
    private final float preferredTextHeightSp = 12;
    private final float preferredTextHeightPx;
    private float actualTextHeightPx;
    private final float preferredTextPaddingDp = 3;
    private final float preferredTextPaddingPx;
    private String[] tabLines;

    public TabScrollerSurfaceView(Context context, String[] tabLines, DisplayMetrics displayMetrics){
        super(context);
        this.tabLines = tabLines;
        this.displayMetrics = displayMetrics;
        preferredTextHeightPx = preferredTextHeightSp * displayMetrics.scaledDensity;
        preferredTextPaddingPx = preferredTextPaddingDp * displayMetrics.density;
    }

    protected void initializeLayers(){
        SurfaceViewRenderLayer background = new SurfaceViewRenderLayer() {

            @Override
            public void render(Canvas canvas, int elapsedMillis) {
                canvas.drawRGB(255, 255, 215);
                setNeedsUpdate(false);
            }

            @Override
            public void onCreate(){
                setNeedsUpdate(true);
            }
        };

        SurfaceViewRenderLayer tab = new SurfaceViewRenderLayer() {
            private RectF tabBounds;
            {
                tabBounds = new RectF(0, currentTabRender.getWidth(), 0, currentTabRender.getHeight());
                tabBounds.offset(getWidth()/3f, 0f);
            }

            @Override
            public void render(Canvas canvas, int elapsedMillis) {

                tabBounds.offset(elapsedMillis*tabScrollRate, 0);

                canvas.drawPicture(currentTabRender);
            }
        };

        SurfaceViewRenderLayer shadowOverlay = new SurfaceViewRenderLayer() {
            private Rect bounds = new Rect(0, 0, getWidth(), getHeight());
            private Paint shadowPaint;
            private Paint gradientPaint;
            private final float strokeWidth = 5f*displayMetrics.density;
            {
                shadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                shadowPaint.setStrokeWidth(strokeWidth);
                shadowPaint.setARGB(100, 0, 0, 0);
                gradientPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                Shader gradientShader = new LinearGradient(0, 0, getWidth(), 0, new int[]{Color.argb(75, 0, 0, 0), Color.argb(0, 0, 0, 0), Color.argb(75, 0, 0, 0)}, new float[]{0f, 0.5f, 1.0f}, Shader.TileMode.CLAMP);
                gradientPaint.setShader(gradientShader);
            }

            @Override
            public void render(Canvas canvas, int elapsedNanos) {
                canvas.drawRect(bounds, shadowPaint);
                canvas.drawRect(bounds, gradientPaint);
                setNeedsUpdate(false);
            }
        };

        SurfaceViewRenderLayer playHead = new SurfaceViewRenderLayer() {
            private Rect bounds;
            private Paint redPaint;
            {
                bounds = new Rect(0, 0, (int)(2*displayMetrics.density), getHeight());
                bounds.offset(getWidth()/3, 0);
                redPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                redPaint.setARGB(255, 255, 0, 0);
            }
            @Override
            public void render(Canvas canvas, int elapsedNanos) {
                canvas.drawRect(bounds, redPaint);
            }
        };

        layers.add(background);
        layers.add(tab);
        layers.add(shadowOverlay);
        layers.add(playHead);
    }

    @Override
    public void onCreate(){
        generateTabView();
    }

    private void generateTabView(){
        int height = getHeight();
        float heightIncrement = height/6f;
        if (preferredTextHeightPx + preferredTextPaddingPx > heightIncrement){
            actualTextHeightPx = heightIncrement - preferredTextPaddingPx;
            Log.w(tag, "Tab text resized to " + actualTextHeightPx);
        }

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(actualTextHeightPx); //TODO make sure the text size is supposed to be in px
        Rect textBounds = new Rect();
        textPaint.getTextBounds(tabLines[0], 0, tabLines[0].length(), textBounds);
        int width = textBounds.width();

        currentTabRenderCanvas = currentTabRender.beginRecording(width, height);

        for (int line = 0; line < 6; line++) {
            currentTabRenderCanvas.drawText(tabLines[line], 0f, heightIncrement*line, textPaint);
        }

        currentTabRender.endRecording();
        currentTabRenderCanvas = null;
    }
}
