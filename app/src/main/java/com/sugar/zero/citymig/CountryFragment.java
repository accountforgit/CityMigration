package com.sugar.zero.citymig;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by lunix on 1/28/17.
 */
public class CountryFragment extends Fragment {

    Context context;
    MainActivity activity;
    public boolean isopen=true;
    private String city;

    public  CountryFragment(){}

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View countryLayout = inflater.inflate(R.layout.country, container);
        View countryButton = countryLayout.findViewById(R.id.country);
        activity=(MainActivity)getActivity();
        if (countryLayout.getId()==R.id.list1) {
            city=activity.firstCity;
        }
        else {
            city=activity.secondCity ;
        }
        ((TextView)countryLayout.findViewById(R.id.choosed)).setText(city+"");
        context=getContext();
        final LinearLayout country_cities =(LinearLayout)countryLayout.findViewById(R.id.countries);
        SQLiteDatabase database = new DataBase(getContext()).getReadableDatabase();
        Cursor cursor = database.rawQuery("select * from " + DataBase.COUNTRY_TABLE_NAME, null);
        cursor.moveToFirst();
        if(country_cities.getChildCount()==0)
            for (int i=0;i<cursor.getCount();i++){
                View countryItem = inflater.inflate(R.layout.country_item,null);
                final String country_name=cursor.getString(1);
                final LinearLayout viewById =(LinearLayout)countryItem.findViewById(R.id.country_cities);
                ((TextView)countryItem.findViewById(R.id.country)).setText(country_name);
                countryItem.findViewById(R.id.country).setOnClickListener(new View.OnClickListener() {
                    boolean b=true;
                    @Override
                    public void onClick(View v) {
                        if(b){
                            viewById.setVisibility(LinearLayout.VISIBLE);
                            b=false;
                        }
                        else{
                            viewById.setVisibility(LinearLayout.GONE);
                            b=true;
                        }
                        if(viewById.getChildCount()==0){
                            Cursor cities = new DataBase(context).getReadableDatabase().rawQuery("select * from " +
                                    DataBase.TABLE_NAME + " where country=? order by name", new String[]{country_name});
                            cities.moveToFirst();
                            for (int i=0;i<cities.getCount();i++){
                                final String city=cities.getString(1);
                                CardView cardView = new CardView(context);
                                TextView textView = (TextView)inflater.inflate(R.layout.city_view,null);
                                textView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        country_cities.setVisibility(LinearLayout.GONE);
                                        isopen=true;
                                        Log.d("tag", "fffffffffffffff");
                                        ((TextView)countryLayout.findViewById(R.id.choosed)).setText(city);
                                        if (countryLayout.getId()==R.id.list1) {
                                            (activity).firstCity = city;
                                        }
                                        else {
                                            activity.secondCity = city;
                                        }
                                    }
                                });
                                textView.setText(city);
                                textView.setBackgroundColor(new int[]{Color.WHITE,Color.parseColor("#BACEE9")}[cities.getInt(4)]);
                                cardView.addView(textView);
                                viewById.addView(cardView);
                                cities.moveToNext();
                            }
                        }
                    }
                });
                country_cities.addView(countryItem);
                cursor.moveToNext();
            }
        countryButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(isopen){
                    country_cities.setVisibility(LinearLayout.VISIBLE);
                    isopen=false;
                }
                else {
                    country_cities.setVisibility(LinearLayout.GONE);
                    isopen=true;
                }
            }
        });
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}