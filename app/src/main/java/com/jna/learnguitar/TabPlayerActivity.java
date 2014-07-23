package com.jna.learnguitar;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class TabPlayerActivity extends Activity {
    private final String tag = "com.jna.LearnGuitar.TabPlayerActivity";

    public static final char blankChar = '-';

    private PlaybackThread playbackThread;

    private TabScrollerSurfaceView tabScroller;
    private FingeringDiagramSurfaceView fingeringDiagram;
    private FrameLayout fingeringDiagramContainer;
    private FrameLayout tabScrollerContainer;
    private ImageButton previousButton;
    private ImageButton playPauseButton;
    private ImageButton stopButton;
    private ImageButton nextButton;
    private Drawable previousIcon;
    private Drawable playIcon;
    private Drawable pauseIcon;
    private Drawable stopIcon;
    private Drawable nextIcon;

    private DisplayMetrics displayMetrics;

    private ArrayList<char[]> tab;
    private final int minFrameTime = 1000/30; //1000ms/nfps

    public class PlaybackThread extends Thread {

        private int lpm; //tempo, lines per minute
        private boolean running = false;
        private float line;
        private long previousTime;

        public void run(){
            previousTime = SystemClock.elapsedRealtime();
            long currentTime;
            int timeDiffMillis = 0;
            double timeDiffMins;
            int renderTime = 0;
            while (true){
                if (running){
                    currentTime = SystemClock.elapsedRealtime();
                    timeDiffMillis = (int)(currentTime - previousTime);
                    timeDiffMins = timeDiffMillis/60000d;
                    line += timeDiffMins*lpm;
                    fingeringDiagram.draw(timeDiffMillis);
                    tabScroller.draw(timeDiffMillis);
                    renderTime = (int)(SystemClock.elapsedRealtime() - currentTime);
                    previousTime = currentTime;
                } else {
                    previousTime = SystemClock.elapsedRealtime() + minFrameTime;
                }
                if (renderTime < minFrameTime) {
                    try {
                        Thread.sleep(minFrameTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public float getLineNumber(){
            return line;
        }

        public int getDisplayedLineNumber(){
            return (int)Math.ceil(line);
        }

        public char[] getDisplayedLine(){
            return tab.get(getDisplayedLineNumber());
        }

        public char[][] getLines(int first, int last){
            char[][] lines = new char[last-first][6];
            for (int i = 0; i < last - first; i++){
                lines[i] = tab.get(i + first - 1);
            }
            return lines;
        }

        public char[][] getAllLines(){
            return getLines(1, getTabLength());
        }

        public int getTabLength(){
            return tab.size();
        }

        public void startPlayback(){
            running = true;
        }

        public void pausePlayback(){
            running = false;
        }

        public void stopPlayback(){
            pausePlayback();
            line = 0;
        }

        public void setTempo(int lpm){
            this.lpm = lpm;
        }

        public int getTempo(){
            return lpm;
        }

}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_player);

        fingeringDiagramContainer = (FrameLayout) findViewById(R.id.fingeringDiagramContainer);
        tabScrollerContainer = (FrameLayout) findViewById(R.id.tabScrollerContainer);



        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        initPlaybackButtons();

        try {
            InputStream inputStream;
            AssetManager assetManager;
            assetManager = getAssets();
            inputStream = assetManager.open(getIntent().getExtras().getString("com.jna.LearnGuitar.filename"));
            byte[] buffer = new byte[6];

            while (true) {
                int bytes = 0;
                bytes = inputStream.read(buffer);
                if (bytes == -1){
                    break;
                }
                char[] append = new char[6];
                for (int i = 0; i < bytes; i++) {
                    append[i] = (char) buffer[i];
                }
                tab.add(append);
                inputStream.read(); //skip line breaks
            }
            assetManager.close();
            inputStream.close();
        } catch (IOException e){
            e.printStackTrace();
        }

        //init tabLines

        tabScroller = new TabScrollerSurfaceView(this, displayMetrics, playbackThread);
        fingeringDiagram = new FingeringDiagramSurfaceView(this, displayMetrics, playbackThread);

        tabScroller.onCreate();
        fingeringDiagram.onCreate();

        tabScrollerContainer.addView(tabScroller);
        fingeringDiagramContainer.addView(fingeringDiagram);
    }

    private void initPlaybackButtons(){
        playPauseButton = (ImageButton) findViewById(R.id.playbackPlayPauseButton);
        stopButton = (ImageButton) findViewById(R.id.playbackStopButton);
        nextButton = (ImageButton) findViewById(R.id.playbackNextButton);
        previousButton = (ImageButton) findViewById(R.id.playbackPreviousButton);

        final int padding = (int) (5*displayMetrics.density);

        playIcon = new Drawable() {
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            Path path = new Path();
            {
                paint.setARGB(255, 0, 0, 0);
                paint.setStrokeWidth(3*displayMetrics.density);
            }

            @Override
            public void draw(Canvas canvas) {
                path.moveTo(padding, padding);
                path.lineTo(padding, canvas.getHeight() - padding);
                path.lineTo(canvas.getWidth() - padding, canvas.getHeight()/2);
                path.close();
                canvas.drawPath(path, paint);
            }

            @Override
            public void setAlpha(int alpha) {
                paint.setAlpha(alpha);
            }

            @Override
            public void setColorFilter(ColorFilter cf) {}

            @Override
            public int getOpacity() {
                return paint.getAlpha();
            }
        };

        pauseIcon = new Drawable() {
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            Path leftPath = new Path();
            Path rightPath;
            {
                paint.setARGB(255, 0, 0, 0);
                paint.setStrokeWidth(3*displayMetrics.density);
            }
            private int paddedWidth;

            @Override
            public void draw(Canvas canvas) {
                paddedWidth = canvas.getWidth() - 2*padding;
                leftPath.moveTo(padding, padding);
                leftPath.lineTo(padding, canvas.getHeight() - padding);
                leftPath.lineTo(padding + paddedWidth/3, canvas.getHeight() - padding);
                leftPath.lineTo(padding + paddedWidth/3, padding);
                leftPath.close();
                rightPath = new Path(leftPath);
                rightPath.offset(paddedWidth*2f/3f, 0);

                canvas.drawPath(leftPath, paint);
                canvas.drawPath(rightPath, paint);
            }

            @Override
            public void setAlpha(int alpha) {
                paint.setAlpha(alpha);
            }

            @Override
            public void setColorFilter(ColorFilter cf) {

            }

            @Override
            public int getOpacity() {
                return paint.getAlpha();
            }
        };

        stopIcon = new Drawable() {
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            Path path = new Path();
            {
                paint.setARGB(255, 0, 0, 0);
                paint.setStrokeWidth(3*displayMetrics.density);
            }

            @Override
            public void draw(Canvas canvas) {
                path.moveTo(padding, padding);
                path.lineTo(padding, canvas.getHeight() - padding);
                path.lineTo(canvas.getWidth() - padding, canvas.getHeight() - padding);
                path.lineTo(canvas.getWidth() - padding, padding);
                path.close();
                canvas.drawPath(path, paint);
            }

            @Override
            public void setAlpha(int alpha) {
                paint.setAlpha(alpha);
            }

            @Override
            public void setColorFilter(ColorFilter cf) {

            }

            @Override
            public int getOpacity() {
                return paint.getAlpha();
            }
        };

        nextIcon = new Drawable() {
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            Path leftPath = new Path();
            Path rightPath;
            {
                paint.setARGB(255, 0, 0, 0);
                paint.setStrokeWidth(3*displayMetrics.density);
            }
            private int paddedWidth;
            private int paddedHeight;

            @Override
            public void draw(Canvas canvas) {
                paddedWidth = canvas.getWidth() - 2*padding;
                paddedHeight = canvas.getHeight() - 2*padding;
                leftPath.moveTo(padding, padding);
                leftPath.lineTo(padding, canvas.getHeight() - padding);
                leftPath.lineTo(padding + paddedWidth/2f, padding + paddedHeight/2f);
                leftPath.close();
                rightPath = new Path(leftPath);
                rightPath.offset(paddedWidth/2f, 0);
            }

            @Override
            public void setAlpha(int alpha) {
                paint.setAlpha(alpha);
            }

            @Override
            public void setColorFilter(ColorFilter cf) {

            }

            @Override
            public int getOpacity() {
                return paint.getAlpha();
            }
        };

        previousIcon = new Drawable() {
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            Path leftPath = new Path();
            Path rightPath;
            {
                paint.setARGB(255, 0, 0, 0);
                paint.setStrokeWidth(3*displayMetrics.density);
            }
            private int paddedWidth;
            private int paddedHeight;

            @Override
            public void draw(Canvas canvas) {
                paddedWidth = canvas.getWidth() - 2*padding;
                paddedHeight = canvas.getHeight() - 2*padding;
                leftPath.moveTo(padding + paddedWidth/2f, padding);
                leftPath.lineTo(padding, padding + paddedHeight/2f);
                leftPath.lineTo(padding + paddedWidth/2f, canvas.getHeight() - padding);
                leftPath.close();
                rightPath = new Path(leftPath);
                rightPath.offset(paddedWidth/2f, 0);
            }

            @Override
            public void setAlpha(int alpha) {
                paint.setAlpha(alpha);
            }

            @Override
            public void setColorFilter(ColorFilter cf) {

            }

            @Override
            public int getOpacity() {
                return paint.getAlpha();
            }
        };

        playPauseButton.setImageDrawable(playIcon);
        playPauseButton.setTag("Play icon");
        stopButton.setImageDrawable(stopIcon);
        nextButton.setImageDrawable(nextIcon);
        previousButton.setImageDrawable(previousIcon);

        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playPauseButton.getTag().equals("Play icon")){
                    playPauseButton.setImageDrawable(pauseIcon);
                    playPauseButton.setTag("Pause icon");
                    playbackThread.startPlayback();
                } else {
                    playPauseButton.setImageDrawable(playIcon);
                    playPauseButton.setTag("Play icon");
                    playbackThread.pausePlayback();
                }
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playbackThread.stopPlayback();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        tabScroller.onStart();
        fingeringDiagram.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        tabScroller.onResume();
        fingeringDiagram.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        tabScroller.onPause();
        fingeringDiagram.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        tabScroller.onStop();
        fingeringDiagram.onStop();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        tabScroller.onDestroy();
        fingeringDiagram.onDestroy();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.tab_player, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
