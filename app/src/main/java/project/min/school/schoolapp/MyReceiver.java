package project.min.school.schoolapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by Abdulqani on 5/24/2017.
 */

public class MyReceiver extends BroadcastReceiver {
    /*
        public abstract void onReceive (Context context, Intent intent)
            This method is called when the BroadcastReceiver is receiving an Intent broadcast.
            During this time you can use the other methods on BroadcastReceiver to view/modify
            the current result values.

        Parameters
            context : The Context in which the receiver is running.
            intent : The Intent being received.
    */
    @Override
    public void onReceive(Context context,Intent intent){
        // Receive the broadcast random number
        int receivedNumber = intent.getIntExtra("RandomNumber",-1);

        // Display the received random number
        Toast.makeText(context,"Broadcast Received : " + receivedNumber,Toast.LENGTH_SHORT).show();
    }


}