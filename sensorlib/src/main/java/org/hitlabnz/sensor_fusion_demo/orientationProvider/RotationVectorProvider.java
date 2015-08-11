package org.hitlabnz.sensor_fusion_demo.orientationProvider;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

/**
 * The orientation provider that delivers the current orientation from the {@link Sensor#TYPE_ROTATION_VECTOR Android
 * Rotation Vector sensor}.
 * 
 * @author Alexander Pacha
 * 
 */
public class RotationVectorProvider extends OrientationProvider {

    /**
     * Initialises a new RotationVectorProvider
     * 
     * @param sensorManager The android sensor manager
     */
    public RotationVectorProvider(SensorManager sensorManager) {
        super(sensorManager);

        //The rotation vector sensor that is being used for this provider to get device orientation
        sensorList.add(sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR));
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // we received a sensor event. it is a good practice to check
        // that we received the proper event
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            // convert the rotation-vector to a 4x4 matrix. the matrix
            // is interpreted by Open GL as the inverse of the
            // rotation-vector, which is what we want.

            try {
                SensorManager.getRotationMatrixFromVector(currentOrientationRotationMatrix.matrix, event.values);
            } catch (IllegalArgumentException e) {
                if (event.values.length > 3) {
                    // Note 3 bug
                    float[] newVector = new float[] {
                            event.values[0],
                            event.values[1],
                            event.values[2]
                    };
                    SensorManager.getRotationMatrixFromVector(currentOrientationRotationMatrix.matrix, newVector);
                }
            }

            // Get Quaternion
            float[] q = new float[4];
            // Calculate angle. Starting with API_18, Android will provide this value as event.values[3], but if not, we have to calculate it manually.
            try {
                SensorManager.getQuaternionFromVector(q, event.values);
            } catch (IllegalArgumentException e) {
                if (event.values.length > 4) {
                    // Note 3 bug
                    float[] newVector = new float[] {
                            event.values[0],
                            event.values[1],
                            event.values[2],
                            event.values[3]
                    };
                    SensorManager.getQuaternionFromVector(q, newVector);
                }
            }


//            SensorManager.getQuaternionFromVector(q, event.values);
            currentOrientationQuaternion.setXYZW(q[1], q[2], q[3], -q[0]);
        }
    }
}
