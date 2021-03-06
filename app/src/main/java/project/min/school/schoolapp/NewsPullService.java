package project.min.school.schoolapp;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

/**
 * Created by Abdulqani on 5/18/2017.
 *
 *
 */

public class NewsPullService extends IntentService {

    private static final String TAG = "PollService";

    public static Intent newIntent(Context context) {
        return new Intent(context, NewsPullService.class);
    }
    public NewsPullService() {
        super(TAG);
    }
    @Override
    protected void onHandleIntent(Intent intent) {


        if (!isNetworkAvailableAndConnected()) {
            return;
        }
        Log.i(TAG, "Received an intent: " + intent);

    }

    private boolean isNetworkAvailableAndConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;
        boolean isNetworkConnected = isNetworkAvailable && cm.getActiveNetworkInfo().isConnected();
        return isNetworkConnected;
    }
}