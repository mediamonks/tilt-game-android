package org.hitlabnz.sensor_fusion_demo;

import android.hardware.Sensor;
import android.hardware.SensorManager;

/**
 * Class that tests availability of hardware sensors.
 * 
 * @author Alex
 *
 */
public class HardwareChecker implements SensorChecker {

	boolean gyroscopeIsAvailable = false;
	
	public HardwareChecker (SensorManager sensorManager) {
		if(sensorManager.getSensorList(Sensor.TYPE_GYROSCOPE).size() > 0) {
			gyroscopeIsAvailable = true;
		}
	}
	
	@Override
	public boolean IsGyroscopeAvailable() {
		return gyroscopeIsAvailable;
	}

}
