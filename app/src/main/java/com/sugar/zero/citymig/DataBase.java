package com.sugar.zero.citymig;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.JsonReader;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

/**
 * Created by lunix on 1/28/17.
 */
public class DataBase extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "cities.db";

    private Context context=null;
    public static final String TABLE_NAME="cities";
    public static final String COUNTRY_TABLE_NAME="country";
    public static final String MIGRATION_TABLE_NAME="migration";
    private static final String NAME="name";
    private static final String COUNTRY="country";
    private static final String COUNT="count";
    private static final String ISCAPITAL="capital";
    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES="CREATE TABLE " + TABLE_NAME + " (" +
            "_id INTEGER PRIMARY KEY," +
            NAME + TEXT_TYPE + COMMA_SEP +
            COUNTRY + TEXT_TYPE + COMMA_SEP +
            COUNT+INT_TYPE+COMMA_SEP+
            ISCAPITAL+INT_TYPE+
            " )";
    private static final String SQL_CREATE_COUNTRY="CREATE TABLE " + COUNTRY_TABLE_NAME + " (" +
            "_id INTEGER PRIMARY KEY," +
            NAME + TEXT_TYPE+
            " )";
    private static final String SQL_CREATE_MIGRATION="CREATE TABLE " + MIGRATION_TABLE_NAME + " (" +
            "_id INTEGER PRIMARY KEY," +
            "fcity" + TEXT_TYPE+COMMA_SEP+
            "scity"+TEXT_TYPE+COMMA_SEP+
            "count"+INT_TYPE+
            " )";


    public DataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context=context;
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
        db.execSQL(SQL_CREATE_COUNTRY);
        db.execSQL(SQL_CREATE_MIGRATION);
        try {
            InputStream open = context.getAssets().open("data.txt");
            Scanner a=new Scanner(open);
            String text="";
            while (a.hasNextLine())
                text+=a.nextLine();
            JSONArray jsonArray=new JSONObject(text).getJSONArray("countries");
            for (int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String country=jsonObject.getString("name");
                JSONArray cities = jsonObject.getJSONArray("cities");
                for (int j=0;j<cities.length();j++){
                    JSONObject city = cities.getJSONObject(j);
                    String name = city.getString("name");
                    int people = city.getInt("people");
                    ContentValues contentValues=new ContentValues();
                    contentValues.put(NAME,name);
                    contentValues.put(COUNTRY,country);
                    contentValues.put(COUNT,people);
                    contentValues.put(ISCAPITAL, 0);
                    long insert = db.insert(TABLE_NAME, null, contentValues);
                    Log.d("tag",insert+"");
                }
                String capital_name= jsonObject.getJSONObject("capital").getString("name");
                int capital_people= jsonObject.getJSONObject("capital").getInt("people");
                ContentValues contentValues=new ContentValues();
                contentValues.put(NAME,capital_name);
                contentValues.put(COUNTRY,country);
                contentValues.put(COUNT,capital_people);
                contentValues.put(ISCAPITAL, 1);
                long insert = db.insert(TABLE_NAME, null, contentValues);
                ContentValues contentValues1=new ContentValues();
                contentValues1.put(NAME, country);
                long insert1 = db.insert(COUNTRY_TABLE_NAME, null, contentValues1);
                Log.d("tag", insert1 + "");
                PendingIntent pendingIntent=PendingIntent.getService(context, 0, new Intent(context,AddPeople.class),0);
                ((AlarmManager) context.getSystemService(Context.ALARM_SERVICE)).setRepeating(AlarmManager.RTC,
                        System.currentTimeMillis() + 60 * 1000,60*1000,pendingIntent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}