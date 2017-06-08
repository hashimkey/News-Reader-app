package project.min.school.schoolapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Abdulqani on 5/11/2017.
 */

public class Photos extends Fragment {

    int TAKE_PHOTO_CODE = 0;
    public static int count = 0;





    private Context mContext;
    private Random mRandom = new Random();
    private Button mButtonSendBroadcast;
    private TextView mTextView;


    final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1;


    Uri imageUri                      = null;
    static TextView imageDetails      = null;
    public  static ImageView showImg  = null;
    Photos CameraActivity = null;





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab2photos, container, false);



        // Here, we are making a folder named picFolder to store
        // pics taken by the camera using this application.
        final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/picFolder/";
        File newdir = new File(dir);
        newdir.mkdirs();


        Button capture = (Button) rootView.findViewById(R.id.btnCapture);
        capture.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setMessage("Are you sure, You want to take picture");
                alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        // Here, the counter will be incremented each time, and the
                        // picture taken by camera will be stored as 1.jpg,2.jpg
                        // and likewise.
                        count++;
                        String file = dir+count+".jpg";
                        File newfile = new File(file);
                        try {
                            newfile.createNewFile();
                        }
                        catch (IOException e)
                        {
                        }

                        Uri outputFileUri = Uri.fromFile(newfile);
                        //Implicit intent
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

                        startActivityForResult(cameraIntent, TAKE_PHOTO_CODE);



                    }
                });

                alertDialogBuilder.setNegativeButton("No", null);
                alertDialogBuilder.show();



            }
        });



       //Send Broadcast
// Get the application context
        mContext = getActivity().getApplicationContext();
        // Get the widgets reference from XML layout
        mButtonSendBroadcast = (Button)rootView.findViewById(R.id.btn_send_broadcast);
        mTextView = (TextView) rootView.findViewById(R.id.tv);

        // Set a click listener for button
        mButtonSendBroadcast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Generate a new random number
                int nextRandomNumber = mRandom.nextInt(100);
                // Display the random number in TextView
                mTextView.setText("Random Number : " + nextRandomNumber);
                // Initialize a new Intent object
                Intent intent = new Intent();
                // Set an action for the Intent
                intent.setAction("project.min.school.schoolapp");
                // Put an integer value Intent to broadcast it
                intent.putExtra("RandomNumber",nextRandomNumber);
                // Finally, send the broadcast
                getActivity().sendBroadcast(intent);
            }
        });





        return rootView;
    }




    @Override
    public   void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_PHOTO_CODE && resultCode == RESULT_OK) {
            Log.d("CameraDemo", "Pic saved");

        }
    }




}
