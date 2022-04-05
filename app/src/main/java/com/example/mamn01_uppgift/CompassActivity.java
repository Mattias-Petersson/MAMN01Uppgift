package com.example.mamn01_uppgift;

import static java.lang.Math.abs;

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
import java.util.Random;

public class CompassActivity extends AppCompatActivity {

    private ImageView compassImage;
    private TextView compassText;
    private TextView calibrationText;

    private int counter = 0;
    private final int averageCount = 7;
    private double[] angles = new double[averageCount];
    private double angle;
    Random rand = new Random();

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
        calibrationText = findViewById(R.id.textCalibrating);


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
        angles[counter % averageCount] = floatOrientation[0];
        counter++;
        double x = 0;
        double y = 0;
        for (double a : angles) {
            x += Math.cos(a);
            y += Math.sin(a);
        }
        double toDegrees = (Math.toDegrees(Math.atan2(y, x)) + 360) % 360;
        angle = Math.round(toDegrees * 100) / 100.00;

    }

    private void update() {
        updateOrientation();
        formatAngle();
        updateUI();
    }

    /*
    Sets the background color to gray if they're pointing north. Sets text in degrees.
    If the phone is moving too much and picking up too many wildly different sensor-values,
    the average method in formatAngle will produce unpredictable results,
     stabilityText aims to tell the user that they need to stabilize before the compass will behave
     as it should again.
     */
    private void updateUI() {
        compassText.setText(String.valueOf(angle) + "°");
        int c = (angle < 15 || angle > 345) ? Color.GRAY : Color.GRAY - 1000;
        this.getWindow().getDecorView().setBackgroundColor(c);

        String stabilityText = Math.abs((angles[0] + angles[averageCount - 1]) / 2 - angles[rand.nextInt(averageCount - 1) + 1]) > 0.1 ?
                "Calibrating, try holding the phone more stable" : "";
        calibrationText.setText(stabilityText);
    }

    private void updateOrientation() {
        SensorManager.getRotationMatrix(floatRotationMatrix, null, floatGravity, floatGeoMagnetic);
        SensorManager.getOrientation(floatRotationMatrix, floatOrientation);
        compassImage.setRotation((float) (-angle));
    }

}
