package com.example.abhishek.couponduniatask.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

/**
 * Created by Abhishek on 26-05-2015.
 */
public class TestProvider extends AndroidTestCase {
    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    public void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(
                DetailContract.RestaurantDetailEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                DetailContract.RestaurantDetailEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Weather table during delete", 0, cursor.getCount());
        cursor.close();
    }

    public void deleteAllRecordsFromDB() {
        DetailDbHelper dbHelper = new DetailDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete(DetailContract.RestaurantDetailEntry.TABLE_NAME, null, null);
        db.close();
    }
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecordsFromDB();
    }
    public void testBasicWeatherQuery() {

        DetailDbHelper dbHelper = new DetailDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues detailValues = TestUtilities.createValues();

        long rowId = db.insert(DetailContract.RestaurantDetailEntry.TABLE_NAME, null, detailValues);
        assertTrue("Unable to Insert WeatherEntry into the Database", rowId != -1);

        db.close();

        // Test the basic content provider query
        Cursor cursor = mContext.getContentResolver().query(
                DetailContract.RestaurantDetailEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicQuery", cursor, detailValues);
    }

    static ContentValues[] createBulkInsertValues() {
        ContentValues[] returnContentValues = new ContentValues[10];

        for ( int i = 0; i < 10; i++) {
            ContentValues values = new ContentValues();
            values.put(DetailContract.RestaurantDetailEntry.COLUMN_OUTLET_NAME, "Pizzaria " + i);
            values.put(DetailContract.RestaurantDetailEntry.COLUMN_NEIGHBOURHOOD_NAME, "Andheri West " + i);
            values.put(DetailContract.RestaurantDetailEntry.COLUMN_LOGOURL, "www.abc.com " + i);
            values.put(DetailContract.RestaurantDetailEntry.COLUMN_DISTANCE, 100 + i);
            values.put(DetailContract.RestaurantDetailEntry.COLUMN_CATEGORIES, "Italian, Continental " + i);
            values.put(DetailContract.RestaurantDetailEntry.COLUMN_NUM_OF_COUPONS, 4 + i);
            returnContentValues[i] = values;
        }
        return returnContentValues;
    }

    public void testBulkInsert() {

        ContentValues[] bulkInsertContentValues = createBulkInsertValues();

        // Register a content observer for our bulk insert.
        TestUtilities.TestContentObserver observer = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(DetailContract.RestaurantDetailEntry.CONTENT_URI, true, observer);

        int insertCount = mContext.getContentResolver().bulkInsert(
                DetailContract.RestaurantDetailEntry.CONTENT_URI, bulkInsertContentValues);

        mContext.getContentResolver().unregisterContentObserver(observer);

        assertEquals(insertCount, 10);

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                DetailContract.RestaurantDetailEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order == by DATE ASCENDING
        );

        // we should have as many records in the database as we've inserted
        assertEquals(cursor.getCount(), 10);

        // and let's make sure they match the ones we created
        cursor.moveToFirst();
        for ( int i = 0; i < 10; i++, cursor.moveToNext() ) {
            TestUtilities.validateCurrentRecord("testBulkInsert.  Error validating Entry " + i,
                    cursor, bulkInsertContentValues[i]);
        }
        cursor.close();
    }
}
