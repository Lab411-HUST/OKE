package com.example.demofreeze;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
	Button btn,btn2, btn3,btn4;
	EditText txtPackage;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtPackage=(EditText)findViewById(R.id.editText1);
        btn=(Button)findViewById(R.id.button1);
        btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				Utils.runCmd("am force-stop "+ txtPackage.getText().toString());
				if (Utils.runCmd("pm block " + txtPackage.getText().toString()
						+ ";pm disable " + txtPackage.getText().toString())) {
					Toast.makeText(getApplicationContext(), "Đã tắt "+txtPackage.getText().toString(), Toast.LENGTH_SHORT).show();
				}else {
					Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
				}
			}
		});
        btn2=(Button)findViewById(R.id.button2);
        btn2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (Utils.runCmd("pm unblock " + txtPackage.getText().toString()
						+ ";pm enable " + txtPackage.getText().toString())) {
					Toast.makeText(getApplicationContext(), "Mở lại "+txtPackage.getText().toString(), Toast.LENGTH_SHORT).show();
				}
			}
		});
        btn3=(Button)findViewById(R.id.button3);
        btn3.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Utils.runCmd("am force-stop "+ txtPackage.getText().toString());
			}
		});
        btn4=(Button)findViewById(R.id.button4);
        btn4.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Utils.runCmd("am start "+ txtPackage.getText().toString());
			}
		});
    }
}
