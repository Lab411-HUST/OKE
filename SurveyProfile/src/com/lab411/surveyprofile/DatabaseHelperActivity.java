package com.lab411.surveyprofile;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelperActivity extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "contextManager";

	private static final String TABLE_CONTEXT = "contexts";

	private static final String KEY_CONTEXT = "context";

	private static final String KEY_ID = "id";
	private static final String KEY_NAME = "name";
	private static final String KEY_AGE = "age";
	private static final String KEY_SEX = "sex";
	private static final String KEY_GAME = "game";
	private static final String KEY_MUSIC = "music";
	private static final String KEY_IMAGE = "image";
	private static final String KEY_CHAT = "chat";
	private static final String KEY_NETWORK = "wifi_3G";
	private static final String KEY_TIME_CALL = "time_call";

	private static final String CREATE_TABLE_CONTEXT = "CREATE TABLE "
			+ TABLE_CONTEXT + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME
			+ " TEXT," + KEY_AGE + " TEXT," + KEY_SEX + " TEXT," + KEY_GAME
			+ " TEXT," + KEY_MUSIC + " TEXT," + KEY_IMAGE + " TEXT," + KEY_CHAT
			+ " TEXT," + KEY_NETWORK + " TEXT," + KEY_TIME_CALL + " TEXT"
			+ ")";

	public DatabaseHelperActivity(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(CREATE_TABLE_CONTEXT);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTEXT);
		onCreate(db);
	}

	// public void insertContext(String name) {
	// SQLiteDatabase db = this.getWritableDatabase();
	//
	// ContentValues values = new ContentValues();
	// values.put(KEY_NAME, name);// column name, column value
	//
	// // Inserting Row
	// db.insert(TABLE_CONTEXT, null, values);// tableName, nullColumnHack,
	// // CotentValues
	// db.close(); // Closing database connection
	// }

	public void addContextAware(ContextAware c) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_NAME, c.getName());
		values.put(KEY_AGE, c.getAge());
		values.put(KEY_SEX, c.getSex());
		values.put(KEY_GAME, c.getGame());
		values.put(KEY_MUSIC, c.getMusic());
		values.put(KEY_IMAGE, c.getImage());
		values.put(KEY_CHAT, c.getChat());
		values.put(KEY_NETWORK, c.getNetwork());
		values.put(KEY_TIME_CALL, c.getTime_call());

		db.insert(TABLE_CONTEXT, null, values);
		db.close();

	}

	public ContextAware getContext(long c_id) {
		SQLiteDatabase db = this.getReadableDatabase();
		String selectQuery = "SELECT * FROM " + TABLE_CONTEXT + " WHERE "
				+ KEY_ID + " = " + c_id;
		Cursor c = db.rawQuery(selectQuery, null);
		if (c != null)
			c.moveToFirst();
		ContextAware ca = new ContextAware();
		ca.setId(c.getColumnIndex(KEY_ID));
		ca.setName(c.getString(c.getColumnIndex(KEY_NAME)));
		ca.setAge(c.getString(c.getColumnIndex(KEY_AGE)));
		ca.setSex(c.getString(c.getColumnIndex(KEY_SEX)));
		ca.setGame(c.getString(c.getColumnIndex(KEY_GAME)));
		ca.setMusic(c.getString(c.getColumnIndex(KEY_MUSIC)));
		ca.setImage(c.getString(c.getColumnIndex(KEY_IMAGE)));
		ca.setChat(c.getString(c.getColumnIndex(KEY_CHAT)));
		ca.setNetwork(c.getString(c.getColumnIndex(KEY_NETWORK)));
		ca.setTime_call(c.getString(c.getColumnIndex(KEY_TIME_CALL)));
		return ca;
	}

	public List<ContextAware> getAllContextAware() {
		List<ContextAware> ctl = new ArrayList<ContextAware>();
		String selectQuery = "SELECT * FROM " + TABLE_CONTEXT;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);
		if (c.moveToFirst()) {
			do {
				ContextAware ca = new ContextAware();
				ca.setId(c.getColumnIndex(KEY_ID));
				ca.setName(c.getString(c.getColumnIndex(KEY_NAME)));
				ca.setAge(c.getString(c.getColumnIndex(KEY_AGE)));
				ca.setSex(c.getString(c.getColumnIndex(KEY_SEX)));
				ca.setGame(c.getString(c.getColumnIndex(KEY_GAME)));
				ca.setMusic(c.getString(c.getColumnIndex(KEY_MUSIC)));
				ca.setImage(c.getString(c.getColumnIndex(KEY_IMAGE)));
				ca.setChat(c.getString(c.getColumnIndex(KEY_CHAT)));
				ca.setNetwork(c.getString(c.getColumnIndex(KEY_NETWORK)));
				ca.setTime_call(c.getString(c.getColumnIndex(KEY_TIME_CALL)));

				ctl.add(ca);
			} while (c.moveToNext());
		}
		return ctl;
	}

	public int getContextCount() {
		String countQuery = "SELECT  * FROM " + TABLE_CONTEXT;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);

		int count = cursor.getCount();
		cursor.close();

		// return count
		return count;
	}

	/*
	 * Updating a todo
	 */
	public int updateContext(ContextAware ca) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_CONTEXT, ca.getName());
		values.put(KEY_CONTEXT, ca.getAge());
		values.put(KEY_CONTEXT, ca.getSex());
		values.put(KEY_CONTEXT, ca.getGame());
		values.put(KEY_CONTEXT, ca.getMusic());
		values.put(KEY_CONTEXT, ca.getImage());
		values.put(KEY_CONTEXT, ca.getChat());
		values.put(KEY_CONTEXT, ca.getNetwork());
		values.put(KEY_CONTEXT, ca.getTime_call());

		// values.put(KEY_STATUS, todo.getStatus());

		// updating row
		return db.update(TABLE_CONTEXT, values, KEY_ID + " = ?",
				new String[] { String.valueOf(ca.getId()) });
	}

	/*
	 * Deleting a todo
	 */
	public void deleteToDo(long tado_id) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_CONTEXT, KEY_ID + " = ?",
				new String[] { String.valueOf(tado_id) });
	}
}
