package com.jna.learnguitar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.provider.ContactsContract;
import android.util.DisplayMetrics;

/**
 * Created by adamtobey on 7/8/14.
 */
public class FingeringDiagramSurfaceView extends AbstractLayeredSurfaceView {

    private DisplayMetrics displayMetrics;
    private TabPlayerActivity.PlaybackThread playbackThread;

    private final int numFrets = 9;
    private final float smallestScaleLength;
    private final float heightIncrement = getHeight()/7f;
    private final int neckTopBarWidthDp = 5;
    private final int neckTopBarWidthPx;
    private final int stringThicknessDp = 4;
    private final int stringThicknessPx;
    private final int dotRadiusDp = 5;
    private final int dotRadiusPx;
    private final int indicatorsRadiusDp = 10;
    private final int indicatorsRadiusPx;
    private final int indicatorsStrokeWidthDp = 2;
    private final int indicatorsStrokeWidthPx;
    private final int indicatorsTextSizeSp = 10;
    private final int indicatorsTextSizePx;

    private float[] fretLocations;

    public FingeringDiagramSurfaceView(Context c, DisplayMetrics displayMetrics, TabPlayerActivity.PlaybackThread playbackThread){
        super(c);
        this.playbackThread = playbackThread;
        this.displayMetrics = displayMetrics;

        stringThicknessPx = (int)(stringThicknessDp * displayMetrics.density);
        neckTopBarWidthPx = (int) (neckTopBarWidthDp * displayMetrics.density);
        dotRadiusPx = (int)(dotRadiusDp*displayMetrics.density);
        indicatorsRadiusPx = (int) (indicatorsRadiusDp * displayMetrics.density);
        indicatorsStrokeWidthPx = (int) (indicatorsStrokeWidthDp * displayMetrics.density);
        indicatorsTextSizePx = (int) (indicatorsTextSizeSp * displayMetrics.scaledDensity);

        smallestScaleLength = (getWidth() - stringThicknessPx)*generateFretPositionDecimal(12)/generateFretPositionDecimal(numFrets);

        fretLocations = generateFretPositions();
    }

    private float generateFretPositionDecimal(int fret){ // from bottom
        return 2^(fret/12)-1;
    }

    private float[] generateFretPositions(){
        float[] frets = new float[numFrets];
        for (int fret = 1; fret <= numFrets; fret++){
            frets[frets.length - fret] = (smallestScaleLength)*generateFretPositionDecimal(fret);
        }
        return frets;
    }

    private float getFretX(int fret){
        return fretLocations[fret + 1];
    }

    private float getFretsCenterX(int fret1, int fret2){
        return (getFretX(fret1) + getFretX(fret2))/2f;
    }

    private float getStringY(int string){
        return string*heightIncrement;
    }

    public void initializeLayers(){

        SurfaceViewRenderLayer neck = new SurfaceViewRenderLayer() {
            private Paint brownPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            private Paint whitePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            private Paint grayPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            private Rect bounds = new Rect(0, 0, getWidth(), getHeight());
            private Rect stringBounds = new Rect(0, 0, getWidth(), stringThicknessPx);
            private Rect neckTopBarBounds = new Rect(getWidth()-neckTopBarWidthPx, 0, getWidth(), getHeight());
            {
                brownPaint.setARGB(255, 78, 57, 17);
                whitePaint.setARGB(255, 255, 255, 255);
                grayPaint.setARGB(255, 217, 217, 217);
            }

            @Override
            public void render(Canvas canvas, int elapsedNanos) {
                //background
                canvas.drawRect(bounds, brownPaint);

                //white bar at top of neck
                canvas.drawRect(neckTopBarBounds, whitePaint);

                //white dots on neck
                float halfHeight = getHeight()/2f;
                float oneThirdHeight = getHeight()/3f;
                float twoThirdsHeight = getHeight() - oneThirdHeight;
                switch (numFrets) {
                    case 3:
                        canvas.drawCircle(getFretsCenterX(2, 3), halfHeight, dotRadiusPx, whitePaint);
                    case 5:
                        canvas.drawCircle(getFretsCenterX(4, 5), halfHeight, dotRadiusPx, whitePaint);
                    case 7:
                        canvas.drawCircle(getFretsCenterX(6, 7), halfHeight, dotRadiusPx, whitePaint);
                    case 9:
                        canvas.drawCircle(getFretsCenterX(8, 9), halfHeight, dotRadiusPx, whitePaint);
                    case 12:
                        canvas.drawCircle(getFretsCenterX(11, 12), oneThirdHeight, dotRadiusPx, whitePaint);
                        canvas.drawCircle(getFretsCenterX(11, 12), twoThirdsHeight, dotRadiusPx, whitePaint);
                    case 15:
                        canvas.drawCircle(getFretsCenterX(14, 15), halfHeight, dotRadiusPx, whitePaint);
                    case 17:
                        canvas.drawCircle(getFretsCenterX(16, 17), halfHeight, dotRadiusPx, whitePaint);
                }

                //strings
                for (int string = 1; string <= 6; string++){
                    stringBounds.offsetTo(0, (int) getStringY(string));
                    canvas.drawRect(stringBounds, grayPaint);
                }

                setNeedsUpdate(false);
            }
        };

        SurfaceViewRenderLayer indicators = new SurfaceViewRenderLayer() {

            char[] locations = playbackThread.getDisplayedLine();
            int[] fingerSuggestions = DataProvider.getFingerSuggestions(locations);
            Rect textBounds = new Rect();
            Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            Paint strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            {
                fillPaint.setARGB(150, 255, 255, 255);
                strokePaint.setARGB(255, 0, 0, 0);
                strokePaint.setStrokeWidth(indicatorsStrokeWidthPx);
                textPaint.setTextSize(indicatorsTextSizePx); //TODO make sure the size is supposed to be in px and not sp
            }

            @Override
            public void render(Canvas canvas, int elapsedNanos) {
                int location = 0;
                for (int string = 1; string <= 6; string++){
                    if (locations[string - 1] != TabPlayerActivity.blankChar){
                        try{
                            location = Integer.parseInt("" + locations[string - 1]);
                        } catch (NumberFormatException e){
                            e.printStackTrace();
                        }
                        canvas.drawCircle(getFretX(location), getStringY(string), indicatorsRadiusPx, fillPaint);
                        canvas.drawCircle(getFretX(location), getStringY(string), indicatorsRadiusPx, strokePaint);
                        if (fingerSuggestions != null){
                            textPaint.getTextBounds("" + fingerSuggestions, 0, 1, textBounds);
                            canvas.drawText("" + fingerSuggestions[string - 1], getFretX(location) - textBounds.width()/2, getStringY(string) - textBounds.height()/2, textPaint);
                        }
                    }
                }
            }
        };

        layers.add(neck);
        layers.add(indicators);
    }

}
