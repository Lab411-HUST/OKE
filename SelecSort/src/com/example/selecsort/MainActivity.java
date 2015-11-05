package com.example.selecsort;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {
	static Thread th;
	static int m = 0;
	int n;
	TextView tvv;
	boolean kill=true;
	long startTime, endTime;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		final EditText edtN = (EditText) findViewById(R.id.edt_N);
		edtN.setText("80000");
		tvv = (TextView) findViewById(R.id.tvv);
		final Button btn = (Button) findViewById(R.id.btn_N);
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				btn.setEnabled(false);
				n = Integer.parseInt(edtN.getText().toString());
				tvv.setText("Loading..........");
				try {
					sapxepMang();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
	}
	
	Handler hd = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			long t =  (long) msg.obj; 
			tvv.setText("Respone Time: "+String.valueOf(t)+ " milisecond");
			super.handleMessage(msg);
		}
		
		
	};
	
	public void sapxepMang() throws InterruptedException {

		th = new Thread() {

			@Override
			public void run() {
				super.run();
				while (kill) {
					m++;

					int[] a = new int[n];
					Random rand = new Random();

					for (int i = 0; i < n; i++) {
						a[i] = rand.nextInt();
						/* for(int i=0; i<n; i++) */
						// System.out.printf("\t" + a[i]);
					}
				//	System.out.println("\n");
					long startTime = System.currentTimeMillis();

					for (int j = 0; j < a.length; j++) {
						int max = a[j];
						int index = j;
						for (int k = j + 1; k < a.length; k++) {
							if (max < a[k]) {
								max = a[k];
								index = k;
							}
						}
						// Nếu chỉ số đã thay đổi, ta sẽ hoán vị
						if (index != j) {
							int temp = a[j];
							a[j] = a[index];
							a[index] = temp;
						}
					}

				//	System.out.println("\nSap xep:");
					for (int h = 0; h < n; h++) {
				//		System.out.printf("\t" + a[h]);
					}
				//	System.out.println("\n");

					long endTime = System.currentTimeMillis();
					long rt = endTime - startTime;
					
					Message mess = new Message();
					mess.obj = rt;
					hd.sendMessage(mess);
					System.out.println("thoi gian:" + rt);
					
					try {
						FileOutputStream file = new FileOutputStream((new File("/sdcard/ResponeTime.txt")),true);
						String tmp=String.valueOf(rt);
						file.write((tmp+"\n").getBytes());
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					/*if (m == 60)
						break;*/

				}
			}
		};
		th.start();

	}
	@Override
	protected void onDestroy() {

		kill = false;
		super.onDestroy();
	}
}
