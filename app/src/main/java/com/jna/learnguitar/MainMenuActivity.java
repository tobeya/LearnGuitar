package com.jna.learnguitar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class MainMenuActivity extends Activity {

    private Button scalesButton;
    private Button tabsButton;
    private Button tutorialsButton;
    private Button chordsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        scalesButton = (Button)findViewById(R.id.mainMenuScalesButton);
        tabsButton = (Button)findViewById(R.id.MainMenuTabsButton);
        tutorialsButton = (Button)findViewById(R.id.mainMenuTutorialsButton);
        chordsButton = (Button)findViewById(R.id.mainMenuChordsButton);

        View.OnClickListener buttonListener = new View.OnClickListener(){
            public void onClick(View v){
                String tag = (String) v.getTag();

                //Intent displayListIntent = new Intent(Intent.ACTION_VIEW);
                Intent displayListIntent = new Intent(getApplicationContext(), DisplayListActivity.class);
                //ComponentName name = new ComponentName("com.jna.learnguitar", "DisplayListActivity.java");
                //displayListIntent.setComponent(name);
                displayListIntent.putExtra("com.jna.learnguitar.listTag", tag);
                startActivity(displayListIntent);
            }
        };

        scalesButton.setOnClickListener(buttonListener);
        tabsButton.setOnClickListener(buttonListener);
        tutorialsButton.setOnClickListener(buttonListener);
        chordsButton.setOnClickListener(buttonListener);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
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
