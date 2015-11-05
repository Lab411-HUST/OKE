package com.example.demofreeze;

import java.io.PrintWriter;

import android.util.Log;

public class Utils {

	public static boolean runCmd(String cmd) {
		try {
			boolean root = true;
			Process process = null;
			if (root) {
				process = Runtime.getRuntime().exec("su");
				PrintWriter pw = new PrintWriter(process.getOutputStream());
				pw.println(cmd);
				pw.flush();
				pw.close();
				process.waitFor();
				Log.v(cmd, "CMD");
				Log.d("Authorities", "root");
			} else {
				process = Runtime.getRuntime().exec(cmd);
				process.waitFor();
				Log.d("Authorities", "no root");
			}
			if (process!=null) {
				return process.exitValue()!=0 ? false : true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
