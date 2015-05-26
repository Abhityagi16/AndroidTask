package com.example.abhishek.couponduniatask.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.location.Location;
import android.net.Uri;
import android.util.Log;

import com.example.abhishek.couponduniatask.data.DetailContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Created by Abhishek on 26-05-2015.
 */
public class FetchDetailService extends IntentService {

    private static final String LOG_TAG = FetchDetailService.class.getSimpleName();
    public static final String LOCATION_EXTRA = "loc";

    private double[] mLocation;

    public FetchDetailService() {
        super("FetchDetailService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mLocation = intent.getDoubleArrayExtra(LOCATION_EXTRA);
        HttpURLConnection urlConnection = null;
        String detailJsonStr = null;
        BufferedReader reader = null;

        try {
            final String BASE_URL = "http://staging.couponapitest.com";
            final String PARAM = "task_data.txt";

            Uri builtUri = Uri.parse(BASE_URL).buildUpon().appendPath(PARAM).build();

            URL url = new URL(builtUri.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();

            if(inputStream == null) return;

            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if(buffer.length() == 0) return;

            detailJsonStr = buffer.toString();
            final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
            getDataFromJson(detailJsonStr);
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            return;
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("RestaurantListFragment", "Error closing stream", e);
                }
            }
        }

        return;
    }

    private void getDataFromJson(String detailJsonStr) throws JSONException {

        final String REST_DATA = "data";
        final String REST_NAME = "OutletName";
        final String REST_NEIGHBOURHOOD_NAME = "NeighbourhoodName";
        final String REST_LATITUDE = "Latitude";
        final String REST_LONGITUDE = "Longitude";
        final String REST_LOGOURL = "LogoURL";
        final String REST_NUM_OF_COUPONS = "NumCoupons";
        final String REST_CATEGORIES = "Categories";
        final String REST_CATEGORIES_NAME = "Name";

        try {
            JSONObject jsonObject = new JSONObject(detailJsonStr);
            JSONObject dataObject = jsonObject.getJSONObject(REST_DATA);
            JSONArray dataArray = dataObject.names();

            Vector<ContentValues> cvVector = new Vector<ContentValues>();

            for (int i=0; i<dataArray.length(); i++) {
                String outletName, neighbourhoodName;
                double latitude, longitude;
                String logoUrl;
                int numOfCoupons;
                double currentLatitude = mLocation[0];
                double currentLongitude = mLocation[1];

                JSONObject detailJson = dataObject.getJSONObject(dataArray.getString(i));
                JSONArray categoryArray = detailJson.getJSONArray(REST_CATEGORIES);

                String categories;
                StringBuffer buffer = new StringBuffer();

                outletName = detailJson.getString(REST_NAME);
                neighbourhoodName = detailJson.getString(REST_NEIGHBOURHOOD_NAME);
                latitude = detailJson.getDouble(REST_LATITUDE);
                longitude = detailJson.getDouble(REST_LONGITUDE);
                logoUrl = detailJson.getString(REST_LOGOURL);
                numOfCoupons = detailJson.getInt(REST_NUM_OF_COUPONS);

                StringBuffer buff = new StringBuffer();
                for (int index=0; index<logoUrl.length(); index++) {
                    if(logoUrl.charAt(index) == '\\')
                        continue;
                    buff.append(logoUrl.charAt(index));
                }
                String logoAddress = buff.toString();

                float[] dist = new float[3];
                Location.distanceBetween(currentLatitude, currentLongitude,
                        latitude, longitude, dist);
                int distance =  Math.round(dist[0]);

                for(int j=0; j<categoryArray.length(); j++) {
                    JSONObject categoryJson = categoryArray.getJSONObject(j);
                    String name = categoryJson.getString(REST_CATEGORIES_NAME);
                    if(j != 0)
                        buffer.append(",");
                    buffer.append(name);

                }
                categories = buffer.toString();
                ContentValues values = new ContentValues();

                values.put(DetailContract.RestaurantDetailEntry.COLUMN_OUTLET_NAME, outletName);
                values.put(DetailContract.RestaurantDetailEntry.COLUMN_LOGOURL, logoAddress);
                values.put(DetailContract.RestaurantDetailEntry.COLUMN_NEIGHBOURHOOD_NAME, neighbourhoodName);
                values.put(DetailContract.RestaurantDetailEntry.COLUMN_DISTANCE, distance);
                values.put(DetailContract.RestaurantDetailEntry.COLUMN_NUM_OF_COUPONS, numOfCoupons);
                values.put(DetailContract.RestaurantDetailEntry.COLUMN_CATEGORIES, categories);

                cvVector.add(values);

            }
            // Delete previously stored data so as to prevent creating infinite history
            this.getContentResolver().delete(DetailContract.RestaurantDetailEntry.CONTENT_URI, null, null);

            if(cvVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cvVector.size()];
                cvVector.toArray(cvArray);
                this.getContentResolver().bulkInsert(
                        DetailContract.RestaurantDetailEntry.CONTENT_URI, cvArray);
            }

            String sortOrder = DetailContract.RestaurantDetailEntry.COLUMN_DISTANCE + " ASC";

            Cursor cur = this.getContentResolver().query(
                    DetailContract.RestaurantDetailEntry.CONTENT_URI, null, null, null, sortOrder);

            cvVector = new Vector<ContentValues>(cur.getCount());
            if(cur.moveToFirst()) {
                do {
                    ContentValues cv = new ContentValues();
                    DatabaseUtils.cursorRowToContentValues(cur, cv);
                    cvVector.add(cv);
                } while(cur.moveToNext());
            }

            Log.d(LOG_TAG, "FetchDetailService complete. " + cvVector.size() + " values inserted");


        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error in Service");
            e.printStackTrace();
        }
    }
}
