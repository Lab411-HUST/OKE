package com.lab411.taskmanager;

import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class CleanProcesses extends Activity {
	private List<MainListItem> items;
	private Context context;
	private KillMyProcess killMyProcess;
	private ActivityManager activityManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Toast.makeText(getApplicationContext(), "xxx", Toast.LENGTH_SHORT).show();
		boolean type = true;
		for (MainListItem item : items) {
			if (item.status) {
				if (item.processName.equals(context.getPackageName()))
					type = false;
				else
					killTask(item);
			}
		}
		if (!type) {
			Thread thread = new Thread() {
				public void run() {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					killMyProcess.finishApp();
				}
			};
			thread.start();
		}
	}

	private void killTask(final MainListItem item) {
		int time = 0;
		if (!item.classNames.isEmpty()) {
			for (ClassListItem name : item.classNames) {
				Intent intent = new Intent();
				intent.setClassName(item.processName, name.className);
				try {
					time = 1000;
					context.stopService(intent);
				} catch (SecurityException e) {
					Toast.makeText(context, item.label + " has been disable!",
							Toast.LENGTH_SHORT).show();
				}
			}
		}
		final int sleepTime = time;
		Thread thread = new Thread() {
			public void run() {
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				activityManager.killBackgroundProcesses(item.processName);
			}
		};
		thread.start();
	}

}
