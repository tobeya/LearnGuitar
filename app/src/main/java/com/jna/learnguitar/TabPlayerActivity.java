package com.jna.learnguitar;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import java.io.IOException;
import java.io.InputStream;

public class TabPlayerActivity extends Activity {
    private final String tag = "com.jna.LearnGuitar.TabPlayerActivity";

    private TabScrollerSurfaceView tabScroller;
    private TabScrollerSurfaceView fingeringDiagram;
    private FrameLayout fingeringDiagramContainer;
    private FrameLayout tabScrollerContainer;
    private String[] tabLines;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_player);

        fingeringDiagramContainer = (FrameLayout) findViewById(R.id.fingeringDiagramContainer);
        tabScrollerContainer = (FrameLayout) findViewById(R.id.tabScrollerContainer);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        StringBuilder entireTab = new StringBuilder();
        try {
            InputStream inputStream;
            AssetManager assetManager;
            assetManager = getAssets();
            inputStream = assetManager.open(getIntent().getExtras().getString("com.jna.LearnGuitar.filename"));
            byte[] buffer = new byte[64];
            char[] append = new char[64];
            while (true) {
                int bytes = 0;
                bytes = inputStream.read(buffer);
                if (bytes == -1){
                    break;
                }
                for (int i = 0; i < bytes; i++) {
                    append[i] = (char) buffer[i];
                }
                entireTab.append(append);
                if (bytes < buffer.length){
                    break;
                }
            }
            assetManager.close();
            inputStream.close();
        } catch (IOException e){
            e.printStackTrace();
        }

        tabLines = entireTab.toString().split("" + (char)10);
        if (tabLines.length != 6){
            Log.e(tag, "More/fewer than six line breaks in tab file");
        }

        //init tabLines

        tabScroller = new TabScrollerSurfaceView(this, tabLines, displayMetrics);

        tabScrollerContainer.addView(tabScroller);

        tabScroller.onCreate();
        fingeringDiagram.onCreate();
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
