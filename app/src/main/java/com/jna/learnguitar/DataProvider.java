package com.jna.learnguitar;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by adamtobey on 7/7/14.
 */
public class DataProvider {

    public static List<String> getListViewHeadersByTag(String tag){
        List<String> headers = new ArrayList<String>();
        if (tag.equals("Scales")){
            headers.add("Major");
            headers.add("Minor");
            headers.add("Pentatonic");
            headers.add("Chromatic");
        } else if (tag.equals("Chords")){
            headers.add("Major");
            headers.add("Minor");
            headers.add("Diminished");
            headers.add("Seventh");
        } else if (tag.equals("Tabs")){
            headers.add("Beginner");
            headers.add("Easy");
            headers.add("Medium");
            headers.add("Hard");
        } else if (tag.equals("Tutorials")){
            headers.add("Beginner");
            headers.add("Easy");
            headers.add("Medium");
            headers.add("Hard");
        } else {
            Log.w("LearnGuitar", "getListViewHeadersByTag method returned null for tag " + tag);
            return null;
        }
        return headers;
    }

    public static HashMap<String, List<String>> getListViewChildrenByTag(String tag, List<String> headers){
        HashMap<String, List<String>> children = new HashMap<String, List<String>>();

        if (headers == null){
            headers = getListViewHeadersByTag(tag);
        }

        if (tag.equals("Scales")){
            List<String> major_minor = new ArrayList<String>();
            major_minor.add("C");
            major_minor.add("C#");
            major_minor.add("D");
            major_minor.add("D#");
            major_minor.add("E");
            major_minor.add("F");
            major_minor.add("F#");
            major_minor.add("G");
            major_minor.add("G#");
            major_minor.add("A");
            major_minor.add("A#");
            major_minor.add("B");

            List<String> pentatonic = new ArrayList<String>();
            pentatonic.add("Major");
            pentatonic.add("Minor");
            pentatonic.add("Suspended");
            pentatonic.add("Blues Major");
            pentatonic.add("Blues Minor");

            List<String> chromatic = new ArrayList<String>();
            chromatic.add("One Octave");
            chromatic.add("Two Octave");
            chromatic.add("Three Octave");

            children.put(headers.get(0), major_minor);
            children.put(headers.get(1), major_minor);
            children.put(headers.get(2), pentatonic);
            children.put(headers.get(3), chromatic);
        } else if (tag.equals("Chords")) {
            List<String> all = new ArrayList<String>();
            all.add("C");
            all.add("C#");
            all.add("D");
            all.add("D#");
            all.add("E");
            all.add("F");
            all.add("F#");
            all.add("G");
            all.add("G#");
            all.add("A");
            all.add("A#");
            all.add("B");

            children.put(headers.get(0), all);
            children.put(headers.get(1), all);
            children.put(headers.get(2), all);
            children.put(headers.get(3), all);
        } else if (tag.equals("Tabs")){
            List<String> empty = new ArrayList<String>();
            empty.add("Tab");

            children.put(headers.get(0), empty);
            children.put(headers.get(1), empty);
            children.put(headers.get(2), empty);
            children.put(headers.get(3), empty);
        } else if (tag.equals("Tutorials")){
            List<String> all = new ArrayList<String>();
            all.add("Tutorial");

            children.put(headers.get(0), all);
            children.put(headers.get(1), all);
            children.put(headers.get(2), all);
            children.put(headers.get(3), all);
        } else {
            Log.w("LearnGuitar", "getListViewChildrenByTag method returned null for tag " + tag);
            return null;
        }
        return children;
    }

}
