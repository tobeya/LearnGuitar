package com.jna.learnguitar;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;

import java.util.HashMap;
import java.util.List;

public class DisplayListActivity extends Activity {

    private ExpandableListAdapter listAdapter;
    private ExpandableListView listView;

    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    private String tag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_list);

        tag = (String) getIntent().getExtras().get("com.jna.learnguitar.listTag");

        listView = (ExpandableListView) findViewById(R.id.expListView);

        prepareListData();

        Log.w("LearnGuitar", "Tag=" + tag);
        Log.w("LearnGuitar", "First header=" + listDataHeader.get(0));
        Log.w("LearnGuitar", "First child=" + listDataChild.get(listDataHeader.get(0)).get(0));
        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild, tag);
        listView.setAdapter(listAdapter);
    }

    private void prepareListData(){
        listDataHeader = DataProvider.getListViewHeadersByTag(tag);
        listDataChild = DataProvider.getListViewChildrenByTag(tag, listDataHeader);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.display_list, menu);
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
