package com.lab411.taskmanager;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

abstract public class MyActivity extends Activity{
	private ActivityManager activityManager;
	private SharedPreferences pref, option;
	private RunApp runApp;
	private List<MainListItem> items;
	private ListArrayAdapter adapter;
	private ListView listView;
	private TextView textView1, textView2;;
    private boolean type = false;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private KillAllListener killAllListener;
    private KillMyProcess killMyProcess;

//    protected TextView textView0;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getSharedPreferences("pref", MODE_PRIVATE);
        option = getSharedPreferences("option", MODE_PRIVATE);
    	textView1 = makeTextView("Avaiable RAM", 20);
    	textView2 = makeTextView("", 20);
//    	textView2.setText("hahaha");

    	activityManager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
    	items = new ArrayList<MainListItem>();
    	adapter = new ListArrayAdapter(this, items, pref);

    	runApp = new RunApp(this, activityManager, textView1, items, adapter, pref, option, makeActivityNo());
    	runApp.start();

    	sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
    	List<Sensor> list = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
    	if (list.size() > 0) accelerometer = list.get(0);

    	killAllListener = new KillAllListener(this, activityManager, items, accelerometer);
    	killMyProcess = new KillMyProcess(sensorManager, killAllListener);
    	killAllListener.addKillMyProcess(killMyProcess);

    	listView = makeListView();
    	setContentView(makeLayout());
    }

    @Override
    public void onResume() {
    	super.onResume();
    }

    @Override
    public void onRestart() {
    	super.onRestart();
    	runApp = new RunApp(this, activityManager, textView1, items, adapter, pref, option, makeActivityNo());
    	runApp.start();
    }

    @Override
    public void onStop() {
    	super.onStop();
    	runApp.stopThread();
        sensorManager.unregisterListener(killAllListener);
    	if(type) finish();
    }
    private LinearLayout makeLayout() {
    	LinearLayout layout = new LinearLayout(this);
    	String str = ruleOfButton();

        layout.setBackgroundColor(Color.BLACK);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(textView1);
        layout.addView(textView2);
        if(!str.equals("null")) layout.addView(makeButton(str));
        layout.addView(listView);
        return layout;
    }

    private Button makeButton(String str) {
    	Button button = new Button(this);
    	button.setText(str);
    	button.setOnClickListener(killAllListener);
    	return button;
    }

    private TextView makeTextView(String text, int size) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextSize(size);
        textView.setTextColor(Color.WHITE);
        return textView;
    }

    private ListView makeListView() {
        listView = new ListView(this);
        listView.setScrollingCacheEnabled(false);
        listView.setAdapter(adapter);
        listView.setItemsCanFocus(true);
        listView.setOnItemClickListener(new ListviewListener(this, activityManager, items, killMyProcess));
        return listView;
    }

    abstract protected int makeActivityNo();
    abstract protected String ruleOfButton();
}