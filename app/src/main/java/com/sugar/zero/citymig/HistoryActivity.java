package com.sugar.zero.citymig;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class HistoryActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        ListView listView= (ListView) findViewById(R.id.listview);
        SQLiteDatabase database = new DataBase(getApplicationContext()).getReadableDatabase();
        Cursor cursor = database.rawQuery("select * from " + DataBase.MIGRATION_TABLE_NAME, null);
        HistoryItem items[]=new HistoryItem[cursor.getCount()];
        cursor.moveToFirst();
        for (int i=0;i<cursor.getCount();i++){
            items[i]=new HistoryItem(cursor.getString(1),cursor.getString(2),cursor.getInt(3));
            cursor.moveToNext();
        }
        listView.setAdapter(new CustomAdapter(getApplicationContext(),R.layout.history_item_layout,items));

    }

    class CustomAdapter extends ArrayAdapter<String>{
        private HistoryItem items[];

        public CustomAdapter(Context context, int resource,HistoryItem items[]) {
            super(context, resource);
            this.items=items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View headView = getLayoutInflater().inflate(R.layout.history_item_layout, parent, false);
            ((TextView)headView.findViewById(R.id.fcity)).setText(items[position].fcity);
            ((TextView)headView.findViewById(R.id.scity)).setText(items[position].scity);
            ((TextView)headView.findViewById(R.id.count)).setText(items[position].count+"");

            return headView;
        }

        @Override
        public int getCount() {
            return items.length;
        }
    }
    class HistoryItem{

        private String fcity;
        private String scity;
        private int count;

        HistoryItem(String fcity, String scity, int count) {
            this.fcity = fcity;
            this.scity = scity;
            this.count = count;
        }
    }
}
