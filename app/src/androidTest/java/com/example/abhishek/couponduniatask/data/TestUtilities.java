package com.example.abhishek.couponduniatask.data;

import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import java.util.Map;
import java.util.Set;

/**
 * Created by Abhishek on 24-05-2015.
 */
public class TestUtilities extends AndroidTestCase {


    static ContentValues createValues() {
        ContentValues values = new ContentValues();
        values.put(DetailContract.RestaurantDetailEntry.COLUMN_OUTLET_NAME, "Pizzaria");
        values.put(DetailContract.RestaurantDetailEntry.COLUMN_NEIGHBOURHOOD_NAME, "Andheri West");
        values.put(DetailContract.RestaurantDetailEntry.COLUMN_LOGOURL, "www.abc.com");
        values.put(DetailContract.RestaurantDetailEntry.COLUMN_DISTANCE, 100);
        values.put(DetailContract.RestaurantDetailEntry.COLUMN_CATEGORIES, "Italian, Continental");
        values.put(DetailContract.RestaurantDetailEntry.COLUMN_NUM_OF_COUPONS, 4);
        return values;
    }

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }
}
