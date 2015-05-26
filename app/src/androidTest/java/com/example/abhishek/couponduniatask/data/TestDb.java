package com.example.abhishek.couponduniatask.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

/**
 * Created by Abhishek on 24-05-2015.
 */
public class TestDb extends AndroidTestCase {
    public static final String LOG_TAG = TestDb.class.getSimpleName();

    // Since we want each test to start with a clean slate
    void deleteTheDatabase() {
        mContext.deleteDatabase(DetailDbHelper.DATABASE_NAME);
    }

    /*
        This function gets called before each test is executed to delete the database.  This makes
        sure that we always have a clean test.
     */
    public void setUp() {
        deleteTheDatabase();
    }

    public void testWeatherTable() {
        DetailDbHelper dbHelper = new DetailDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues detailValues =  TestUtilities.createValues();

        long rowId = db.insert(DetailContract.RestaurantDetailEntry.TABLE_NAME, null, detailValues);
        assertTrue(rowId != -1);

        Cursor detailCursor = db.query(
                DetailContract.RestaurantDetailEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        assertTrue( "Error: No Records returned from query", detailCursor.moveToFirst());

        TestUtilities.validateCurrentRecord("testInsertReadDb weatherEntry failed to validate",
                detailCursor, detailValues);

        assertFalse( "Error: More than one record returned from weather query",
                detailCursor.moveToNext());


        db.close();
        detailCursor.close();
    }
}
