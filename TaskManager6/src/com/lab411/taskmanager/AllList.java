package com.lab411.taskmanager;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class AllList extends MyActivity {
	Context context;
	private final static int MENU_ITEM2 = 1, MENU_ITEM3 = 2;
	private MainListItem item;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected int makeActivityNo() {
		return 0;
	}

	@Override
	protected String ruleOfButton() {
		return "Clean Processes";
	}

//	private void ShortcutIcon() {
//		Intent shortcutIntent = new Intent(getApplicationContext(),
//				CleanProcesses.class);
//		shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//		Intent addIntent = new Intent();
//		addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
//		addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "Clean Processes");
//		addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
//				Intent.ShortcutIconResource.fromContext(
//						getApplicationContext(), R.drawable.icon_clean));
//		addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
//		getApplicationContext().sendBroadcast(addIntent);
//		Toast.makeText(getApplicationContext(), "Shortcut Created!",
//				Toast.LENGTH_SHORT).show();
//	}
	
	public void uninstallApp(){
//		Intent uninstallIntent=new Intent(Intent.ACTION_DELETE);
//    	uninstallIntent.setData(Uri.parse(item.processName));
//    	context.startActivity(uninstallIntent);
		Uri packageURI = Uri.parse("package:"+item.processName);
    	Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
    	context.startActivity(uninstallIntent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, MENU_ITEM2, 0, StringRes.uninstall);
		menu.add(0, MENU_ITEM3, 0, StringRes.exit);
		// item2.setIcon(R.drawable.two);
		// item3.setIcon(R.drawable.three);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case MENU_ITEM2:
			uninstallApp();
			break;
		case MENU_ITEM3:
			finish();
			break;
		}
		return true;
	}
}