package com.example.user.splitr;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

/**
 * Created by user on 29/10/2016.
 */

public class WelcomePage extends AppCompatActivity {
    ImageButton mOpenImageCapture;
    TextView mInstructions;
    private static final int RC_HANDLE_CAMERA_PERM = 2;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_page);

        mOpenImageCapture = (ImageButton) findViewById(R.id.openCaptureButton);
        mInstructions= (TextView) findViewById(R.id.instructionsText);

        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission();}
        clearTempFolder();


        mOpenImageCapture.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Log.d("Spliter:","Welcome Button onclick fired");
                Intent intent = new Intent(WelcomePage.this, CameraCaptureActivity.class);
                startActivity(intent);
            }
        });
    }

    private void requestCameraPermission() {
        Log.w("TAG", "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

        Toast.makeText(getApplicationContext(),R.string.permission_camera_rationale, Toast.LENGTH_LONG).show();


    }

    private void clearTempFolder(){
        Log.d("Clearing temp folder", "Clearing temp folder");
        File dir = new File(Environment.getExternalStorageDirectory()+"/Android/data/com.example.user.splitr/files/Pictures");


        if (dir.isDirectory())
        {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++)
            {
                new File(dir, children[i]).delete();

            }

        }
    }


}
