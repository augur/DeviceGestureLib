package info.augury.devicegesturelib;

import android.hardware.Sensor;

/**
 * Contract of class, that reads accelerometer and passes its data at different
 * rates to multiple {@link IProcessedDataListener}   
 */
interface ISensorEventProcessor {
	
	public void onAccuracyChanged(Sensor sensor, int accuracy);
	public void onSensorChanged(float[] values, long timestamp);	
	
	void addListener(IProcessedDataListener listener, long period);
	void removeListener(IProcessedDataListener listener);
	void pause();
	void unpause();
	void close();

}
