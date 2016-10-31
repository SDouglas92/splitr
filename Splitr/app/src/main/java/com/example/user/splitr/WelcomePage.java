package com.example.user.splitr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by user on 29/10/2016.
 */

public class WelcomePage extends AppCompatActivity {
    ImageButton mOpenImageCapture;
    TextView mInstructions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_page);

        mOpenImageCapture = (ImageButton) findViewById(R.id.openCaptureButton);
        mInstructions= (TextView) findViewById(R.id.instructionsText);

        mOpenImageCapture.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Log.d("Spliter:","Welcome Button onclick fired");
                Intent intent = new Intent(WelcomePage.this, CaptureImage.class);
                startActivity(intent);
            }
        });
    }


}
