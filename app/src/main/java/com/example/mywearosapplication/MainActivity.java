package com.example.mywearosapplication;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends WearableActivity implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;

    private Location targetLocation;

    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;
    private float[] mR = new float[9];
    private float[] mOrientation = new float[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_GAME);

        // USP10
        targetLocation = new Location("");//provider name is unnecessary
//        targetLocation.setLatitude(26.090007d);//your coords of course
        targetLocation.setLatitude(26.120898d);//your coords of course
//        targetLocation.setLongitude(-80.367247d);
        targetLocation.setLongitude(-80.313198d);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Enables Always-on
        setAmbientEnabled();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == mAccelerometer) {
            System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);
            mLastAccelerometerSet = true;
        } else if (event.sensor == mMagnetometer) {
            System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.length);
            mLastMagnetometerSet = true;
        }

        if(mLastAccelerometerSet && mLastMagnetometerSet) {
            SensorManager.getRotationMatrix(mR, null, mLastAccelerometer, mLastMagnetometer);
            SensorManager.getOrientation(mR, mOrientation);
            float azimuthInRadians = mOrientation[0];
            float azimuthInDegrees = (float)(Math.toDegrees(azimuthInRadians)+360)%360;

//            ImageView imageView1 = findViewById(R.id.orientation_pointer);
//            imageView1.setRotation(azimuthInDegrees);

            Location loc = new Location("");
            loc.setBearing(azimuthInDegrees);

            float rotation = (360 + ((loc.bearingTo(targetLocation) + 360) % 360) - loc.getBearing()) % 360;

            ImageView imageView2 = findViewById(R.id.orientation_to_pointer);
            imageView2.setRotation(rotation);

            TextView textView2 = findViewById(R.id.instruction_text);

            textView2.setText("" + azimuthInDegrees);

//            if((rotation > 0 && rotation < 30) || (rotation > 330)) {
//                textView2.setText("Straight");
//            }
//            else if(rotation > 180) {
//                textView2.setText("Turn Left");
//            }
//            else {
//                textView2.setText("Turn Right");
//            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do nothing
    }
}
