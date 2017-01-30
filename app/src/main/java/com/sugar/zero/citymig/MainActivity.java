package com.sugar.zero.citymig;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    CountryFragment list1;
    CountryFragment list2;
    String firstCity="";
    String secondCity="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(savedInstanceState!=null) {
            if (savedInstanceState.get("firstCity") != null)
                firstCity=savedInstanceState.get("firstCity").toString();
            if (savedInstanceState.get("secondCity") != null)
                secondCity=savedInstanceState.get("secondCity").toString();
        }
        list1=new CountryFragment();
        list2=new CountryFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.list1, list1).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.list2, list2).commit();
    }

    public void OnMigrateClick(View v){
        if(!firstCity.equals("") && !secondCity.equals("")){
            String count = ((EditText) findViewById(R.id.migrate_count)).getText().toString();
            if(!count.equals("")){
                SQLiteDatabase database = new DataBase(getApplicationContext()).getReadableDatabase();
                while (database.inTransaction());
                database.beginTransaction();
                Cursor cursor = database.rawQuery("select * from " + DataBase.TABLE_NAME + " where name=?", new String[]{firstCity});
                cursor.moveToFirst();
                int anInt = cursor.getInt(3);
                if(anInt>=Integer.parseInt(count)){
                    database.execSQL("update " + DataBase.TABLE_NAME + " set count=" + (cursor.getInt(3) - Integer.parseInt(count)) + " where name=?"
                            , new String[]{firstCity});
                    Cursor cursor1 = database.rawQuery("select * from " + DataBase.TABLE_NAME + " where name=?", new String[]{secondCity});
                    cursor1.moveToFirst();
                    database.execSQL("update " + DataBase.TABLE_NAME + " set count=" + (cursor1.getInt(3) + Integer.parseInt(count)) + " where name=?"
                            , new String[]{secondCity});
                    ContentValues contentValues=new ContentValues();
                    contentValues.put("fcity",firstCity);
                    contentValues.put("scity",secondCity);
                    contentValues.put("count", Integer.parseInt(count));
                    long insert = database.insert(DataBase.MIGRATION_TABLE_NAME, null, contentValues);
                    Log.d("tag",insert+"");
                    Toast.makeText(MainActivity.this, "Migrateted", Toast.LENGTH_SHORT).show();
                }
                database.setTransactionSuccessful();
                database.endTransaction();
            }
        }
        else
            Toast.makeText(MainActivity.this, "choose city", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d("tag","fsdfa");
        outState.putString("firstCity", firstCity);
        Log.d("tag", firstCity);
        outState.putString("secondCity", secondCity);
        Log.d("tag", secondCity);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        startActivity(new Intent(getApplicationContext(),HistoryActivity.class));
        return true;
    }
}
