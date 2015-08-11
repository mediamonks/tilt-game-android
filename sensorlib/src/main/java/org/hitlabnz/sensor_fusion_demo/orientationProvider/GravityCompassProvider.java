package org.hitlabnz.sensor_fusion_demo.orientationProvider;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

/**
 * The orientation provider that delivers the current orientation from the {@link Sensor#TYPE_GRAVITY
 * Gravity} and {@link Sensor#TYPE_MAGNETIC_FIELD Compass}.
 * 
 * @author Alexander Pacha
 * 
 */
public class GravityCompassProvider extends OrientationProvider {

    /**
     * Compass values
     */
    private float[] magnitudeValues = new float[3];

    /**
     * Gravity values
     */
    private float[] gravityValues = new float[3];

    /**
     * Initialises a new GravityCompassProvider
     * 
     * @param sensorManager The android sensor manager
     */
    public GravityCompassProvider(SensorManager sensorManager) {
        super(sensorManager);

        //Add the compass and the gravity sensor
        sensorList.add(sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY));
        sensorList.add(sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD));
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // we received a sensor event. it is a good practice to check
        // that we received the proper event
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            magnitudeValues = event.values.clone();
        } else if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {
            gravityValues = event.values.clone();
        }

        if (magnitudeValues != null && gravityValues != null) {
            float[] i = new float[16];

            // Fuse gravity-sensor (virtual sensor) with compass
            SensorManager.getRotationMatrix(currentOrientationRotationMatrix.matrix, i, gravityValues, magnitudeValues);
            // Transform rotation matrix to quaternion
            currentOrientationQuaternion.setRowMajor(currentOrientationRotationMatrix.matrix);
        }
    }
}
