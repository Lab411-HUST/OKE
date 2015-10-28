package com.lab411.taskmanager;

import java.util.List;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class ListviewListener implements OnItemClickListener {
	private Context context;
	private ActivityManager activityManager;
	private List<MainListItem> items;
	private MainListItem item;
	private int kind;
	private KillMyProcess killMyProcess;

	public ListviewListener(Context context, ActivityManager activityManager, List<MainListItem> items, KillMyProcess killMyProcess) {
		this.context = context;
		this.activityManager = activityManager;
		this.items = items;
		this.killMyProcess = killMyProcess;
	}

	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		this.item = returnItem((String)view.getTag());
    	if(item.processName.equals(context.getPackageName())) {
    		showYesNoDialog(context, new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int which) {
    				killMyProcess.finishApp();
    			}
    		});
    	}
    	else {
        	CharSequence[] functions = { "Disable", "Open", "Uninstall"};
    		showDialog(context, functions, new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int which) {
    				kind = which;
    				if(which == 0) {
    					showYesNoDialog(context, new DialogInterface.OnClickListener() {
    		    			public void onClick(DialogInterface dialog, int which) {
    		    				killTask();
    		    			}
    		    		});
    				}
    				else if(which == 1) {
    					startApp();
    				}
    				else if(which ==2){
    					uninstallApp();
    				}
    			}
    		});
    	}
	}

    private MainListItem returnItem(String tag) {
    	for(MainListItem item: items) {
    		if(item.processName.equals(tag)) {
    			return item;
    		}
    	}
    	return null;
    }

    private void showYesNoDialog(Context context, DialogInterface.OnClickListener listener) {
    	AlertDialog.Builder ad = new AlertDialog.Builder(context);
    	ad.setTitle("Progress will be disable?");
    	ad.setMessage(item.label + " will be disable?");
    	ad.setPositiveButton("OK", listener);
    	ad.setNegativeButton("Cancel", null);
    	ad.show();
    }

    private void showDialog(Context context, CharSequence[] functions, DialogInterface.OnClickListener listener) {
    	AlertDialog.Builder ad = new AlertDialog.Builder(context);
    	ad.setItems(functions, listener);
    	ad.show();
    }

    private void killTask() {
    	int time = 0;
    	if(!item.classNames.isEmpty()) {
    		for(ClassListItem name: item.classNames) {
    			Intent intent = new Intent();
    			intent.setClassName(item.processName, name.className);
    			try{
    				time = 1000;
    				context.stopService(intent);
    			}
    			catch(SecurityException e) {
    				makeToast(item.label + " has been disble!");
    			}
    		}
    	}
    	final int sleepTime = time;
    	makeToast(item.label + " has been disable from background!");
    	Thread thread = new Thread() {
    		public void run() {
    			try {
    				Thread.sleep(sleepTime);
    			}
    			catch (InterruptedException e) {
    				e.printStackTrace();
    			}
//    			android.os.Process.killProcess(android.os.Process.myPid());
    			activityManager.killBackgroundProcesses(item.processName);
    		}
    	};
    	thread.start();
    }

    private void startApp() {
    	if(item.mainClassName.equals("")) {
    	}
    	else {
    		Intent intent = new Intent();
    		intent.setComponent(new ComponentName(item.processName, item.mainClassName));
    		context.startActivity(intent);
    	}
    }
    private void makeToast(String text) {
    	if(kind != 2) {
	    	Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    	}
    }
    
    private void uninstallApp(){
    	Uri packageURI = Uri.parse("package:"+item.processName);
    	Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
    	context.startActivity(uninstallIntent);
//    	Intent uninstallIntent=new Intent(Intent.ACTION_DELETE);
//    	uninstallIntent.setData(Uri.parse("package:com.lab411.taskmanager"));
//    	context.startActivity(uninstallIntent);

    }
}