package com.example.abhishek.couponduniatask.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.example.abhishek.couponduniatask.data.DetailContract.RestaurantDetailEntry;


public class DetailProvider extends ContentProvider {

    private DetailDbHelper mOpenHelper;

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        if(selection == null) selection = "1";

        int rowsDeleted = db.delete(RestaurantDetailEntry.TABLE_NAME, selection, selectionArgs);

        if(rowsDeleted != 0)
            getContext().getContentResolver().notifyChange(uri, null);
        db.close();
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        Uri returnUri;

        long _id = db.insert(RestaurantDetailEntry.TABLE_NAME, null, values);
        if(_id > 0)
            returnUri =  RestaurantDetailEntry.buildRestaurantDetailUri(_id);
        else
            throw new android.database.SQLException("Failed to insert row into " + uri);

        getContext().getContentResolver().notifyChange(uri, null);
        db.close();
        return returnUri;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new DetailDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor retCursor;

        retCursor = mOpenHelper.getReadableDatabase().query(
                RestaurantDetailEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        int rowsUpdated = db.update(
                RestaurantDetailEntry.TABLE_NAME, values, selection, selectionArgs);

        if(rowsUpdated != 0)
            getContext().getContentResolver().notifyChange(uri, null);
        db.close();
        return  rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        db.beginTransaction();
        int returnCount = 0;

        try {
            for(ContentValues value : values) {
                long _id = db.insert(RestaurantDetailEntry.TABLE_NAME, null, value);
                if(_id != 1)
                    returnCount ++;
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnCount;
    }
}
