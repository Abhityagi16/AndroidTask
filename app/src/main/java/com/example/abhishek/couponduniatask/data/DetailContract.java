package com.example.abhishek.couponduniatask.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Abhishek on 23-05-2015.
 */
public class DetailContract {

    public static final String CONTENT_AUTHORITY = "com.example.abhishek.couponduniatask";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_RESTAURANT_DETAIL = "restaurant_detail";

    public static final class RestaurantDetailEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_RESTAURANT_DETAIL).build();

        public static final String TABLE_NAME = "restaurant_detail";

        public static final String COLUMN_OUTLET_NAME = "outlet_name";

        public static final String COLUMN_NEIGHBOURHOOD_NAME = "neighbourhood_name";

        public static final String COLUMN_LOGOURL = "logourl";

        public static final String COLUMN_CATEGORIES = "categories";

        public static final String COLUMN_NUM_OF_COUPONS = "num_of_coupons";

        public static final String COLUMN_DISTANCE = "distance";

        public static Uri buildRestaurantDetailUri(long _id) {
            return ContentUris.withAppendedId(CONTENT_URI, _id);
        }
    }
}
