package com.lab411.surveyprofile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends Activity {

	ContextAware ca;

	final String arr1[] = { "", "Ở mức cao", "Bình thường", "Không" };
	final String arr2[] = { "", "Mỗi ngày vài giờ", "Thỉnh thoảng", "Không" };
	String arr3[] = { "", "Nhiều giờ mỗi ngày", "Thỉnh thoảng", "Không" };
	String arr4[] = { "", "Thường xuyên", "Thỉnh thoảng", "Không" };
	String arr5[] = { "", "Dưới 18 tuổi", "20- 40 tuổi", "40- 60 tuổi",
			"Lớn hơn 60 tuổi" };
	String arr6[] = { "", "Nam", "Nữ" };
	String arr7[] = { "", ">1h", "15p-1h", "Ít hơn" };
	private TextView selection1, selection2, selection3, selection4,
			selection5, selection6, selection7, selection8;
	private Spinner spinner1, spinner2, spinner3, spinner4, spinner5, spinner6,
			spinner7, spinner8;
	private EditText selection0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// DatabaseHelperActivity db=new DatabaseHelperActivity(this);
		// Log.d("Insert: ", "Inserthjing ..");
		// db.addContextAware(new ContextAware("ToiNK", "23", "Nam", "High",
		// "Medium", "Low", "High", "High", "Low"));

		selection0 = (EditText) findViewById(R.id.selection0);
		selection1 = (TextView) findViewById(R.id.selection1);
		selection2 = (TextView) findViewById(R.id.selection2);
		selection3 = (TextView) findViewById(R.id.selection3);
		selection4 = (TextView) findViewById(R.id.selection4);
		selection5 = (TextView) findViewById(R.id.selection5);
		selection6 = (TextView) findViewById(R.id.selection6);
		selection7 = (TextView) findViewById(R.id.selection7);
		selection8 = (TextView) findViewById(R.id.selection8);
		spinner1 = (Spinner) findViewById(R.id.spinner1);
		spinner2 = (Spinner) findViewById(R.id.spinner2);
		spinner3 = (Spinner) findViewById(R.id.spinner3);
		spinner4 = (Spinner) findViewById(R.id.spinner4);
		spinner5 = (Spinner) findViewById(R.id.spinner5);
		spinner6 = (Spinner) findViewById(R.id.spinner6);
		spinner7 = (Spinner) findViewById(R.id.spinner7);
		spinner8 = (Spinner) findViewById(R.id.spinner8);

		ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, arr5);
		adapter1.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
		spinner1.setAdapter(adapter1);

		ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, arr6);
		adapter2.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
		spinner2.setAdapter(adapter2);

		ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, arr2);
		adapter3.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
		spinner3.setAdapter(adapter3);

		ArrayAdapter<String> adapter4 = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, arr2);
		adapter4.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
		spinner4.setAdapter(adapter4);

		ArrayAdapter<String> adapter5 = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, arr1);
		adapter5.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
		spinner5.setAdapter(adapter5);

		ArrayAdapter<String> adapter6 = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, arr4);
		adapter6.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
		spinner6.setAdapter(adapter6);

		ArrayAdapter<String> adapter7 = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, arr3);
		adapter7.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
		spinner7.setAdapter(adapter7);

		ArrayAdapter<String> adapter8 = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, arr7);
		adapter8.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
		spinner8.setAdapter(adapter8);

		spinner1.setOnItemSelectedListener(new OnItemSelectedListener1() {
		});
		spinner2.setOnItemSelectedListener(new OnItemSelectedListener2() {
		});
		spinner3.setOnItemSelectedListener(new OnItemSelectedListener3() {
		});
		spinner4.setOnItemSelectedListener(new OnItemSelectedListener4() {
		});
		spinner5.setOnItemSelectedListener(new OnItemSelectedListener5() {
		});
		spinner6.setOnItemSelectedListener(new OnItemSelectedListener6() {
		});
		spinner7.setOnItemSelectedListener(new OnItemSelectedListener7() {
		});
		spinner8.setOnItemSelectedListener(new OnItemSelectedListener8() {
		});

	}

	private class OnItemSelectedListener1 implements
			android.widget.AdapterView.OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub
			selection1.setText(arr5[position]);
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub
			// selection1.setText("select here");
		}

	}

	private class OnItemSelectedListener2 implements
			android.widget.AdapterView.OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub
			selection2.setText(arr6[position]);
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub
		}

	}

	private class OnItemSelectedListener3 implements
			android.widget.AdapterView.OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub
			selection3.setText(arr2[position]);
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub
		}

	}

	private class OnItemSelectedListener4 implements
			android.widget.AdapterView.OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub
			selection4.setText(arr2[position]);
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub
		}

	}

	private class OnItemSelectedListener5 implements
			android.widget.AdapterView.OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub
			selection5.setText(arr1[position]);
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub
		}

	}

	private class OnItemSelectedListener6 implements
			android.widget.AdapterView.OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub
			selection6.setText(arr4[position]);
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub
		}

	}

	private class OnItemSelectedListener7 implements
			android.widget.AdapterView.OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub
			selection7.setText(arr3[position]);
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub
		}

	}

	private class OnItemSelectedListener8 implements
			android.widget.AdapterView.OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub
			selection8.setText(arr7[position]);
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.save:
			saveProfile();
			return true;
		case R.id.clear:
			clear();
			return true;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void saveProfile() {
		DatabaseHelperActivity db = new DatabaseHelperActivity(this);
			db.addContextAware(new ContextAware(
					selection0.getText().toString(), selection1.getText()
							.toString(), selection2.getText().toString(),
					selection3.getText().toString(), selection4.getText()
							.toString(), selection5.getText().toString(),
					selection6.getText().toString(), selection7.getText()
							.toString(), selection8.getText().toString()));
		// db.addContextAware(new ContextAware("ToiNK", "23", "Nam", "High",
		// "Medium", "Low", "High", "High", "Low"));
		clear();
		Intent i=new Intent(MainActivity.this, ProcessProfile.class);
		startActivity(i);
	}

	public void clear() {
		selection0.setText("");
		selection1.setText("");
		selection2.setText("");
		selection3.setText("");
		selection4.setText("");
		selection5.setText("");
		selection6.setText("");
		selection7.setText("");
		selection8.setText("");
		spinner1.setSelection(0, true);
		spinner2.setSelection(0);
		spinner3.setSelection(0);
		spinner4.setSelection(0);
		spinner5.setSelection(0);
		spinner6.setSelection(0);
		spinner7.setSelection(0);
		spinner8.setSelection(0);
	}
}
