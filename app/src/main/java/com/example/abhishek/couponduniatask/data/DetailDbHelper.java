package com.example.abhishek.couponduniatask.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.abhishek.couponduniatask.data.DetailContract.RestaurantDetailEntry;

/**
 * Created by Abhishek on 23-05-2015.
 */
public class DetailDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;

    static final String DATABASE_NAME = "restaurantDetail.db";

    public DetailDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_RESTAURANT_DETAIL_TABLE = "CREATE TABLE " + RestaurantDetailEntry.TABLE_NAME + " (" +
                RestaurantDetailEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                RestaurantDetailEntry.COLUMN_OUTLET_NAME + " TEXT NOT NULL, " +
                RestaurantDetailEntry.COLUMN_NEIGHBOURHOOD_NAME + " TEXT NOT NULL, " +
                RestaurantDetailEntry.COLUMN_CATEGORIES + " TEXT NOT NULL, " +
                RestaurantDetailEntry.COLUMN_LOGOURL + " TEXT NOT NULL, " +
                RestaurantDetailEntry.COLUMN_NUM_OF_COUPONS + " INTEGER NOT NULL, " +
                RestaurantDetailEntry.COLUMN_DISTANCE + " INTEGER NOT NULL);";

        db.execSQL(SQL_CREATE_RESTAURANT_DETAIL_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL("DROP TABLE IF EXIST " + RestaurantDetailEntry.TABLE_NAME);
        onCreate(db);
    }
}
