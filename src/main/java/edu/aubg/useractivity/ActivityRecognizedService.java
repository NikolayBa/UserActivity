package edu.aubg.useractivity;

import android.app.Activity;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.ImageView;
import android.widget.Toast;
import edu.aubg.useractivity.DBHelper;


import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;
import java.util.concurrent.TimeUnit;

import android.os.Handler;

public class ActivityRecognizedService extends IntentService {
    Handler mHandler = new Handler();
    DBHelper dbHelper;
    String message;
    MainActivity mActivity;

    @Override
    public void onCreate() {
        super.onCreate();
        dbHelper = new DBHelper(this);
        mActivity = new MainActivity();
    }

    String lastActivity = "";
    public ActivityRecognizedService() {
        super("ActivityRecognizedService");
    }

    public ActivityRecognizedService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            handleDetectedActivities( result.getProbableActivities() );
        }
    }

    private void handleDetectedActivities(List<DetectedActivity> probableActivities) {
        for( final DetectedActivity activity : probableActivities ) {
            switch( activity.getType() ) {
                case DetectedActivity.IN_VEHICLE: {
                    Log.e( "ActivityRecogition", "In Vehicle: " + activity.getConfidence() );
                    message = "In Vehicle ";


                    break;
                }
                case DetectedActivity.ON_FOOT: {
                    Log.e( "ActivityRecogition", "On Foot: " + activity.getConfidence() );
                    message = "On foot ";
                    break;
                }
                case DetectedActivity.RUNNING: {
                    Log.e( "ActivityRecogition", "Running: " + activity.getConfidence() );
                    message = "Running ";


                    break;
                }
                case DetectedActivity.STILL: {
                    Log.e( "ActivityRecogition", "Still: " + activity.getConfidence() );

                    break;
                }
                case DetectedActivity.TILTING: {
                    Log.e( "ActivityRecogition", "Tilting: " + activity.getConfidence() );
                    message = "Titlting  ";
                    break;
                }
                case DetectedActivity.WALKING: {
                    Log.e( "ActivityRecogition", "Walking: " + activity.getConfidence() );
                    message = "Walking :";

                    break;
                }
                case DetectedActivity.UNKNOWN: {
                    Log.e( "ActivityRecogition", "Unknown: " + activity.getConfidence() );
                    String message = "UNKNOWN";
                    break;
                }
            }

            if( activity.getConfidence() >= 75 ) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

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
                        if(activity.getType() != lastActivtyname) {
                            long difference = System.currentTimeMillis() - lastActivityStart;

                            long minutes = TimeUnit.MILLISECONDS.toMinutes(difference);
                            long seconds = TimeUnit.MILLISECONDS.toSeconds(difference) - TimeUnit.MINUTES.toSeconds(minutes);

                            String activityname = Integer.toString(activity.getType());
                            String duration = message + " for " + minutes + " min " + seconds + " seconds.";
                            Log.e( "ActivityRecogition", activityname );
                            Toast.makeText(getApplicationContext(), duration, Toast.LENGTH_LONG).show();
                            dbHelper.addData(activity.getType(), System.currentTimeMillis());
                        }
                    }
                });
            }

        }
    }
}