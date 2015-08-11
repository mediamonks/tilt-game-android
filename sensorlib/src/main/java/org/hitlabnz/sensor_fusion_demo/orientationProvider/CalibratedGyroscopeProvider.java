package org.hitlabnz.sensor_fusion_demo.orientationProvider;

import org.hitlabnz.sensor_fusion_demo.representation.Quaternion;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

/**
 * The orientation provider that delivers the relative orientation from the {@link Sensor#TYPE_GYROSCOPE
 * Gyroscope}. This sensor does not deliver an absolute orientation (with respect to magnetic north and gravity) but
 * only a relative measurement starting from the point where it started.
 * 
 * @author Alexander Pacha
 * 
 */
public class CalibratedGyroscopeProvider extends OrientationProvider {

    /**
     * Constant specifying the factor between a Nano-second and a second
     */
    private static final float NS2S = 1.0f / 1000000000.0f;

    /**
     * The quaternion that stores the difference that is obtained by the gyroscope.
     * Basically it contains a rotational difference encoded into a quaternion.
     * 
     * To obtain the absolute orientation one must add this into an initial position by
     * multiplying it with another quaternion
     */
    private final Quaternion deltaQuaternion = new Quaternion();

    /**
     * The time-stamp being used to record the time when the last gyroscope event occurred.
     */
    private long timestamp;

    /**
     * This is a filter-threshold for discarding Gyroscope measurements that are below a certain level and
     * potentially are only noise and not real motion. Values from the gyroscope are usually between 0 (stop) and
     * 10 (rapid rotation), so 0.1 seems to be a reasonable threshold to filter noise (usually smaller than 0.1) and
     * real motion (usually > 0.1). Note that there is a chance of missing real motion, if the use is turning the
     * device really slowly, so this value has to find a balance between accepting noise (threshold = 0) and missing
     * slow user-action (threshold > 0.5). 0.1 seems to work fine for most applications.
     * 
     */
    private static final double EPSILON = 0.1f;

    /**
     * Value giving the total velocity of the gyroscope (will be high, when the device is moving fast and low when
     * the device is standing still). This is usually a value between 0 and 10 for normal motion. Heavy shaking can
     * increase it to about 25. Keep in mind, that these values are time-depended, so changing the sampling rate of
     * the sensor will affect this value!
     */
    private double gyroscopeRotationVelocity = 0;

    /**
     * Initialises a new CalibratedGyroscopeProvider
     * 
     * @param sensorManager The android sensor manager
     */
    public CalibratedGyroscopeProvider(SensorManager sensorManager) {
        super(sensorManager);

        //Add the gyroscope
        sensorList.add(sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE));
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // we received a sensor event. it is a good practice to check
        // that we received the proper event
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {

            // This timestep's delta rotation to be multiplied by the current rotation
            // after computing it from the gyro sample data.
            if (timestamp != 0) {
                final float dT = (event.timestamp - timestamp) * NS2S;
                // Axis of the rotation sample, not normalized yet.
                float axisX = event.values[0];
                float axisY = event.values[1];
                float axisZ = event.values[2];

                // Calculate the angular speed of the sample
                gyroscopeRotationVelocity = Math.sqrt(axisX * axisX + axisY * axisY + axisZ * axisZ);

                // Normalize the rotation vector if it's big enough to get the axis
                if (gyroscopeRotationVelocity > EPSILON) {
                    axisX /= gyroscopeRotationVelocity;
                    axisY /= gyroscopeRotationVelocity;
                    axisZ /= gyroscopeRotationVelocity;
                }

                // Integrate around this axis with the angular speed by the timestep
                // in order to get a delta rotation from this sample over the timestep
                // We will convert this axis-angle representation of the delta rotation
                // into a quaternion before turning it into the rotation matrix.
                double thetaOverTwo = gyroscopeRotationVelocity * dT / 2.0f;
                double sinThetaOverTwo = Math.sin(thetaOverTwo);
                double cosThetaOverTwo = Math.cos(thetaOverTwo);
                deltaQuaternion.setX((float) (sinThetaOverTwo * axisX));
                deltaQuaternion.setY((float) (sinThetaOverTwo * axisY));
                deltaQuaternion.setZ((float) (sinThetaOverTwo * axisZ));
                deltaQuaternion.setW(-(float) cosThetaOverTwo);

                // Matrix rendering in CubeRenderer does not seem to have this problem.
                synchronized (syncToken) {
                    // Move current gyro orientation if gyroscope should be used
                    deltaQuaternion.multiplyByQuat(currentOrientationQuaternion, currentOrientationQuaternion);
                }

                Quaternion correctedQuat = currentOrientationQuaternion.clone();
                // We inverted w in the deltaQuaternion, because currentOrientationQuaternion required it.
                // Before converting it back to matrix representation, we need to revert this process
                correctedQuat.w(-correctedQuat.w());

                synchronized (syncToken) {
                    // Set the rotation matrix as well to have both representations
                    SensorManager.getRotationMatrixFromVector(currentOrientationRotationMatrix.matrix,
                            correctedQuat.ToArray());
                }
            }
            timestamp = event.timestamp;
        }
    }
}
