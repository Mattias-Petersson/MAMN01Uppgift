package com.example.mamn01_uppgift;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;

public class CompassActivity extends AppCompatActivity {

    private ImageView compassImage;
    private TextView compassText;

    private int counter = 0;
    private final int averageCount = 3;
    private double[] angles = new double[averageCount];
    private double angle;

    private SensorManager sensorManager;
    private Sensor sensorAccelerometer;
    private Sensor sensorMagneticField;

    private float[] floatGravity = new float[3];
    private float[] floatGeoMagnetic = new float[3];
    private float[] floatOrientation = new float[3];
    private float[] floatRotationMatrix = new float[9];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);
        compassImage = findViewById(R.id.imageView);
        compassText = findViewById(R.id.textDegrees);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorMagneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        SensorEventListener sensorEventListenerAccelerometer = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                floatGravity = event.values;
                update();
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };

        SensorEventListener sensorEventListenerMagneticField = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                floatGeoMagnetic = event.values;
                update();

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };
        sensorManager.registerListener(sensorEventListenerAccelerometer, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(sensorEventListenerMagneticField, sensorMagneticField, SensorManager.SENSOR_DELAY_NORMAL);
    }

    /*
    Takes an average of the last averageCount values read. Converts it into degrees and cuts off to two decimal places.
     */
    private void formatAngle() {
        angles[counter% averageCount] = floatOrientation[0];
        counter++;
        double toDegrees = (Math.toDegrees(Arrays.stream(angles).average().orElse(1.00)) + 360) % 360.0;
        angle = Math.round(toDegrees * 100) / 100.00;

    }
    private void update() {
        formatAngle();
        updateOrientation();
        updateColor();
        compassText.setText(String.valueOf(angle));
    }
    private void updateColor() {
        int c = (angle < 15 || angle > 345) ? Color.GRAY : Color.BLACK;
        this.getWindow().getDecorView().setBackgroundColor(c);
    }

    private void updateOrientation() {
        SensorManager.getRotationMatrix(floatRotationMatrix, null, floatGravity, floatGeoMagnetic);
        SensorManager.getOrientation(floatRotationMatrix, floatOrientation);
        compassImage.setRotation((float)(-angle));
    }

}
