package com.example.abhishek.couponduniatask;

import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.example.abhishek.couponduniatask.data.DetailContract;
import com.example.abhishek.couponduniatask.service.FetchDetailService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;


/**
 * A placeholder fragment containing a simple view.
 */
public class RestaurantListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private Location mLocation;
    private GoogleApiClient mGoogleApiClient;
    private static final int FRAGMENT_LOADER = 0;
    private DetailAdapter mDetailsAdapter;

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }
    @Override
    public void onConnected(Bundle bundle) {
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        Log.v("Fragment", ""+mLocation.getLatitude());
//        FetchDetailTask fetchDetail = new FetchDetailTask(getActivity(), mLocation);
//        fetchDetail.execute();
        Intent intent = new Intent(getActivity(), FetchDetailService.class);
        double[] intentExtra = {mLocation.getLatitude(), mLocation.getLongitude()};
        intent.putExtra(FetchDetailService.LOCATION_EXTRA, intentExtra);
        getActivity().startService(intent);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    private static final String[] DETAIL_COLUMNS = {
            DetailContract.RestaurantDetailEntry.TABLE_NAME + "." + DetailContract.RestaurantDetailEntry._ID,
            DetailContract.RestaurantDetailEntry.COLUMN_OUTLET_NAME,
            DetailContract.RestaurantDetailEntry.COLUMN_LOGOURL,
            DetailContract.RestaurantDetailEntry.COLUMN_NUM_OF_COUPONS,
            DetailContract.RestaurantDetailEntry.COLUMN_CATEGORIES,
            DetailContract.RestaurantDetailEntry.COLUMN_NEIGHBOURHOOD_NAME,
            DetailContract.RestaurantDetailEntry.COLUMN_DISTANCE
    };

    static final int COLUMN_DETAIL_ID = 0;
    static final int COLUMN_DETAIL_NAME = 1;
    static final int COLUMN_DETAIL_LOGOURL = 2;
    static final int COLUMN_DETAIL_COUPONS = 3;
    static final int COLUMN_DETAIL_CATEGORIES = 4;
    static final int COLUMN_DETAIL_NEIGHBOUR = 5;
    static final int COLUMN_DETAIL_DISTANCE = 6;

    public RestaurantListFragment() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildGoogleApiClient();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mDetailsAdapter = new DetailAdapter(getActivity(), null, 0);
        ListView listView = (ListView) rootView.findViewById(R.id.listview_names);
        listView.setAdapter(mDetailsAdapter);
        return rootView;
    }

    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(FRAGMENT_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = DetailContract.RestaurantDetailEntry.COLUMN_DISTANCE + " ASC";

        return new CursorLoader(getActivity(),
                DetailContract.RestaurantDetailEntry.CONTENT_URI,
                DETAIL_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mDetailsAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mDetailsAdapter.swapCursor(null);
    }
}
