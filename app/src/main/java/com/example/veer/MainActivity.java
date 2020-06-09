package com.example.veer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Formatter;
import java.util.Locale;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity implements LocationListener,SensorEventListener {

    private Sensor mGyro;
    private TextView angle,sugSpeed;
    private static final String TAG = "MainActivity";
    private SensorManager sensorManager;
    private static final double friction = .7;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Speedometer code
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        this.onLocationChanged(null);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1000);

        }
        else {
            doStuff();
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);
        this.updateSpeed(null);

        CheckBox chkUseMetricUnits = (CheckBox) this.findViewById(R.id.chkMetricUnits);
        chkUseMetricUnits.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MainActivity.this.updateSpeed(null);
            }
        } );


        //Gyroscope angle code
        sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        mGyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        angle = (TextView) findViewById(R.id.Gyro);
        sugSpeed = (TextView) findViewById(R.id.sugSpeed);
        if(mGyro != null) {
            sensorManager.registerListener(MainActivity.this, mGyro, SensorManager.SENSOR_DELAY_NORMAL);
            Log.d(TAG, "OnCreate: Registered Gyro listener");
        }
        else {
            angle.setText("Gyro not supported");
        }


        };

    public void onAccuracyChanged(Sensor sensor, int i)
    {

    }

    public void onSensorChanged(SensorEvent sensorEvent)
    {
        double angleHolder = sensorEvent.values[0] * 180/3.1428f;
        angleHolder = Math.round(angleHolder);
        angle.setText("Angle: "+ angleHolder);
        double tanAngleHolder = Math.tan(angleHolder);
        double radius = 50.0;
        double insideSqrt = (tanAngleHolder + friction) / (friction * tanAngleHolder);
        insideSqrt = insideSqrt * 9.8 * radius;
        double theSpeed = Math.sqrt(insideSqrt) * 2.237f;
        theSpeed = Math.round(theSpeed);
        if(angleHolder > 15)
        sugSpeed.setText("Suggested speed: " + theSpeed);
    }

    @SuppressLint("MissingPermission")
    public void doStuff()
    {
        LocationManager locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        if(locationManager != null)
        {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);
        }
        Toast.makeText(this,"Waiting for GPS connection!",Toast.LENGTH_SHORT).show();
    }

    public void finish()
    {
        super.finish();
        System.exit(0);
    }

    private void updateSpeed(CLocation location)
    {
        float nCurrentSpeed = 0;

        if(location != null)
        {
            location.setbUseMetricUnits(this.useMetricUnits());
            nCurrentSpeed = location.getSpeed();
        }

        Formatter fmt = new Formatter(new StringBuilder());
        fmt.format(Locale.US,"%5.1f", nCurrentSpeed);
        String strCurrentSpeed = fmt.toString();
        strCurrentSpeed = strCurrentSpeed.replace(" ", "0");

        String strUnits = "miles/hour";
        if(this.useMetricUnits())
        {
            strUnits = "meters/second";
        }

        TextView txtCurrentSpeed = (TextView)this.findViewById(R.id.txtCurrentSpeed);
        txtCurrentSpeed.setText(strCurrentSpeed + " " + strUnits);
    }

    private boolean useMetricUnits()
    {
        CheckBox chkUseMetricUnits = (CheckBox) this.findViewById(R.id.chkMetricUnits);
        return chkUseMetricUnits.isChecked();
    }

    @Override
    public void onLocationChanged(Location location) {
        if(location!= null)
        {
            CLocation myLocation = new CLocation(location,this.useMetricUnits());
            this.updateSpeed(myLocation);
        }
    }

    public void onProviderDisabled(String provider)
    {

    }
    public void onProviderEnabled(String provider)
    {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }



    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,@NonNull int[] grantResults)
    {
         if(requestCode == 1000)
         {
             if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
             {
                 doStuff();
             }
             else {
                 finish();
             }
         }
    }
}
