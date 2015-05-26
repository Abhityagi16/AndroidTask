package com.example.abhishek.couponduniatask;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.audiofx.EnvironmentalReverb;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.util.LruCache;
import android.support.v4.widget.CursorAdapter;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.abhishek.couponduniatask.data.DetailContract;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Abhishek on 25-05-2015.
 */
public class DetailAdapter extends CursorAdapter {

    private LruCache<String, Bitmap> mMemoryCache;

    public DetailAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize);
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }


    private String convertCursorRowToUXFormat(Cursor cursor) {
        int idx_name = cursor.getColumnIndex(DetailContract.RestaurantDetailEntry.COLUMN_OUTLET_NAME);
        int idx_neighbourhood = cursor.getColumnIndex(
                DetailContract.RestaurantDetailEntry.COLUMN_NEIGHBOURHOOD_NAME);
        int idx_distance = cursor.getColumnIndex(DetailContract.RestaurantDetailEntry.COLUMN_DISTANCE);
        String name = cursor.getString(idx_name);
        String neighbour = cursor.getString(idx_neighbourhood);
        double distance = cursor.getDouble(idx_distance);

        return String.format("%s - %s - %s", name, neighbour, distance);
    }

    public static class ViewHolder {
        public final ImageView imageView;
        public final TextView nameView;
        public final TextView offersView;
        public final TextView localityView;
        public final TextView categoryView;

        public ViewHolder(View view) {
            imageView = (ImageView) view.findViewById(R.id.list_item_icon);
            nameView = (TextView) view.findViewById(R.id.list_item_name);
            offersView = (TextView) view.findViewById(R.id.list_item_offers);
            localityView = (TextView) view.findViewById(R.id.list_item_locality);
            categoryView = (TextView) view.findViewById(R.id.list_item_categories);
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_names, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        String logoUrl = cursor.getString(RestaurantListFragment.COLUMN_DETAIL_LOGOURL);
        Bitmap img = getBitmapFromMemCache(logoUrl);
        if(img != null) {
            viewHolder.imageView.setImageBitmap(img);
            Log.v("In Detail Adapter", "Recieved from cache");
        }
        else
            new DownloadImageTask(viewHolder.imageView).execute(logoUrl);

        String name = cursor.getString(RestaurantListFragment.COLUMN_DETAIL_NAME);
        viewHolder.nameView.setText(name);

        int offers = cursor.getInt(RestaurantListFragment.COLUMN_DETAIL_COUPONS);
        viewHolder.offersView.setText(offers + " Offers");

        String[] cat = cursor.getString(RestaurantListFragment.COLUMN_DETAIL_CATEGORIES).split(",");
        String categories = "";
        for(String s : cat) {
            if(s.equals("Restaurant"))
                continue;
            categories += "\u2022 " + s + " ";
        }
        viewHolder.categoryView.setText(categories);

        String locality = "" + cursor.getInt(RestaurantListFragment.COLUMN_DETAIL_DISTANCE) + "m " +
                cursor.getString(RestaurantListFragment.COLUMN_DETAIL_NEIGHBOUR);
        viewHolder.localityView.setText(locality);

    }

//    public static File getDiskCacheDir(Context context, String uniqueName) {
//        // Check if media is mounted or storage is built-in, if so, try and use external cache dir
//        // otherwise use internal cache dir
//        final String cachePath =
//                Environment.isMEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
//                        !Environment.isExternalStorageRemovable() ? getExternalCacheDir(context).getPath() :
//                        context.getCacheDir().getPath();
//
//        return new File(cachePath + File.separator + uniqueName);
//    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public DownloadImageTask(ImageView view) {
            imageView = view;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap img = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                img = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            addBitmapToMemoryCache(urldisplay, img);
            return img;
        }

        @Override
        protected void onPostExecute(Bitmap img) {
            imageView.setImageBitmap(img);
        }
    }
}
