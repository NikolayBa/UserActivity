package edu.aubg.useractivity;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionApi;
import edu.aubg.useractivity.DBHelper;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, android.app.LoaderManager.LoaderCallbacks<Cursor> {

    public GoogleApiClient mApiClient;
    DBHelper dbHelper;
    ImageView imageView;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mApiClient.connect();

        dbHelper = new DBHelper(this);

        imageView = (ImageView)findViewById(R.id.imageDisplay);
        textView = (TextView)findViewById(R.id.textDisplay);
        /*dbHelper = new DBHelper(this);
        dbHelper.addData(3, System.currentTimeMillis());*/

        int lastActivtyname;
        long lastActivityStart;

        //get last activity
        Cursor data = dbHelper.getData();
        try {
            if (!data.moveToFirst())
                data.moveToFirst();
            lastActivtyname = data.getInt(1);
            lastActivityStart = data.getLong(2);
        } finally {
            data.close();
        }
        setImage(lastActivtyname);
        setText(lastActivtyname);

        getLoaderManager().initLoader(100, null, this);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Intent intent = new Intent( this, ActivityRecognizedService.class );
        PendingIntent pendingIntent = PendingIntent.getService( this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT );
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates( mApiClient, 3000, pendingIntent );
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return null;
    }


    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor data)
    {
        int lastActivtyname;
        long lastActivityStart;

        //get last activity
        data = dbHelper.getData();
        try {
            if (!data.moveToFirst())
                data.moveToFirst();
            lastActivtyname = data.getInt(1);
            lastActivityStart = data.getLong(2);
        } finally {
            data.close();
        }
        setImage(lastActivtyname);
        setText(lastActivtyname);
    }


    private void setImage(int activityType) {
        switch (activityType) {
            case DetectedActivity.IN_VEHICLE:
                imageView.setImageResource(R.drawable.driving);
                break;
            case DetectedActivity.WALKING:
                imageView.setImageResource(R.drawable.walking);
                break;
            case DetectedActivity.RUNNING:
                imageView.setImageResource(R.drawable.running);
                break;
            case DetectedActivity.STILL:
                imageView.setImageResource(R.drawable.still);
                break;
        }
    }

    private void setText(int activityType) {
        switch (activityType) {
            case DetectedActivity.IN_VEHICLE:
                textView.setText("You are now driving");
                break;
            case DetectedActivity.WALKING:
                textView.setText("You are now walking");
                break;
            case DetectedActivity.RUNNING:
                textView.setText("You are now running");
                break;
            case DetectedActivity.STILL:
                textView.setText("You are now still");
                break;
        }
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {

    }
}
