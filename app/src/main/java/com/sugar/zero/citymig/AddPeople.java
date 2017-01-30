package com.sugar.zero.citymig;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class AddPeople extends Service {
    public AddPeople() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase database = new DataBase(getApplicationContext()).getReadableDatabase();
                while (database.inTransaction());
                database.beginTransaction();
                Cursor cursor = database.rawQuery("select * from " + DataBase.TABLE_NAME, null);
                cursor.moveToFirst();
                float mult[]=new float[]{1.05f,1.1f};
                for(int i=0;i<cursor.getCount();i++){
                    String name = cursor.getString(1);
                    int count=cursor.getInt(3);
                    Log.d("tag", ""+count+" "+name);
                    database.execSQL("update " + DataBase.TABLE_NAME + " set count=" + ((int) count * mult[cursor.getInt(4)]) + " where name=?"
                            , new String[]{name});
                    int nif[]=new int[]{5*1000000,10*1000000,20*1000000,50*1000000,80*1000000,100*1000000};
                    for(int j=0;j<nif.length;j++){
                        if(count<nif[j] && ((int) count * mult[cursor.getInt(4)])>=nif[j]){
                            NotificationCompat.Builder mBuilder =
                                    (NotificationCompat.Builder) new NotificationCompat.Builder(getApplicationContext())
                                            .setSmallIcon(android.R.drawable.title_bar_tall)
                                            .setContentTitle("Greeeet!!!!")
                                            .setContentText("In city "+name+" more "+nif[j]+" people.");
                            NotificationManager mNotificationManager =
                                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            mBuilder.setContentIntent(null);
                            mNotificationManager.notify(cursor.getInt(0), mBuilder.build());
                        }

                    }
                    cursor.moveToNext();
                }
                database.setTransactionSuccessful();
                database.endTransaction();

            }
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }
}
