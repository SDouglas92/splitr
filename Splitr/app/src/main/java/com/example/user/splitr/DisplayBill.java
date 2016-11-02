package com.example.user.splitr;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.InputStream;
import java.net.URL;
import java.util.Locale;

/**
 * Created by user on 02/11/2016.
 */
public class DisplayBill extends AppCompatActivity {

    TextRecognizer textRecognizer;
    ImageView iv;
    String mCurrentPhotoPath;
    private GraphicOverlay<OcrGraphic> mGraphicOverlay;
    Frame mFrame;
    private GestureDetector gestureDetector;
    private TextToSpeech tts;


    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.capture_image);
        iv = (ImageView) findViewById(R.id.imageView);
        mGraphicOverlay = (GraphicOverlay) findViewById(R.id.graphicOverlay);
        Bundle bundle = getIntent().getExtras();
        if (bundle.getString("Photo Path") != null) {
            mCurrentPhotoPath = bundle.getString("Photo Path");
        }
        Log.d("Display Bill", mCurrentPhotoPath);
        setupTextRecognizer();
        Log.d("OnCreate firing", "On create");
        setPic();

        gestureDetector = new GestureDetector(this, new CaptureGestureListener());

        TextToSpeech.OnInitListener listener =
                new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(final int status) {
                        if (status == TextToSpeech.SUCCESS) {
                            Log.d("OnInitListener", "Text to speech engine started successfully.");
                            tts.setLanguage(Locale.US);
                        } else {
                            Log.d("OnInitListener", "Error starting the text to speech engine.");
                        }
                    }
                };
        tts = new TextToSpeech(this.getApplicationContext(), listener);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {

        boolean c = gestureDetector.onTouchEvent(e);

        return c || super.onTouchEvent(e);
    }


    @SuppressLint("InlinedApi")
    private void setupTextRecognizer() {
        Context context = getApplicationContext();


        textRecognizer = new TextRecognizer.Builder(context).build();
        textRecognizer.setProcessor(new OcrDetectorProcessor(mGraphicOverlay));

        if (!textRecognizer.isOperational()) {

            Log.w("Tag", "Detector dependencies are not yet available.");

            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {
                Toast.makeText(this, R.string.low_storage_error, Toast.LENGTH_LONG).show();
                Log.w("Tag", getString(R.string.low_storage_error));
            }
        }


    }

    public void setPic() {

        Bitmap mBitmap = null;
        // Get the dimensions of the View
        int targetW = iv.getWidth();
        int targetH = iv.getHeight();

        Log.d("Set Pic firing", Integer.toString(targetW));
        Log.d("Set Pic firing", Integer.toString(targetH));

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        Log.d("Set Pic firing", Integer.toString(photoW));
        Log.d("Set Pic firing", Integer.toString(photoH));


        // Determine how much to scale down the image
//        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
//        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        try (InputStream is = new URL(mCurrentPhotoPath).openStream()) {
            mBitmap = BitmapFactory.decodeStream(is, null, bmOptions);
        } catch (Exception e) {

        }
        Log.d("Set Pic:", mCurrentPhotoPath);
        iv.setImageBitmap(mBitmap);

        mFrame = new Frame.Builder().setBitmap(mBitmap).build();
        textRecognizer.receiveFrame(mFrame);
    }

    private boolean onTap(float rawX, float rawY) {
        OcrGraphic graphic = mGraphicOverlay.getGraphicAtLocation(rawX, rawY);
        TextBlock text = null;
        if (graphic != null) {
            text = graphic.getTextBlock();
            if (text != null && text.getValue() != null) {
                Log.d("TAG", "text data is being spoken! " + text.getValue());
                // Speak the string.
                tts.speak(text.getValue(), TextToSpeech.QUEUE_ADD, null, "DEFAULT");
            } else {
                Log.d("TAG", "text data is null");
            }
        } else {
            Log.d("TAG", "no text detected");
        }
        return text != null;
    }


    private class CaptureGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return onTap(e.getRawX(), e.getRawY()) || super.onSingleTapConfirmed(e);
        }
    }
}
