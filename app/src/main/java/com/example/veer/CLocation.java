package com.example.veer;

import android.location.Location;

public class CLocation extends Location {

    private boolean bUseMetricUnits = false;

    public CLocation(Location location)
    {
        this(location,true);
    }
    public CLocation(Location location,boolean bUseMetricUnits)
    {
        super(location);
        this.bUseMetricUnits = bUseMetricUnits;
    }

    public boolean getUseMetricUnits()
    {
        return this.bUseMetricUnits;
    }

    public void setbUseMetricUnits(boolean bUseMetricUnits)
    {
        this.bUseMetricUnits = bUseMetricUnits;
    }

    public float distanceTo(Location dest)
    {
        float nDistance = super.distanceTo(dest);
        if(!this.getUseMetricUnits())
        {
            nDistance = nDistance * 3.28083989501312f;
        }
        return nDistance;

    }

    public float getAccuracy()
    {
        float nAccuracy = super.getAccuracy();
        if(!this.getUseMetricUnits())
        {
            nAccuracy = nAccuracy * 3.28083989501312f;
        }
        return nAccuracy;

    }

    public double getAltitude()
    {
        double nAltitude = super.getAltitude();
        if(!this.getUseMetricUnits())
        {
            nAltitude = nAltitude * 3.28083989501312d;
        }
        return nAltitude;
    }

    public float getSpeed()
    {
        float nSpeed = super.getSpeed();
        if(!this.getUseMetricUnits())
        {
            nSpeed = super.getSpeed() * 2.23693629f;
        }
        return nSpeed;
    }
}
