package info.augury.devicegesturelib;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Communicates with Android sensors, reads accelerometer data, filters and 
 * interpolates to match given time intervals. Then sends data to listeners. 
 */
class SensorEventProcessor implements ISensorEventProcessor {

	static final int SIDE_AXIS = 0;
	static final int VERT_AXIS = 1;
	static final int FRONT_AXIS = 2;

	//private static final int SENSOR_TYPE = Sensor.TYPE_LINEAR_ACCELERATION;
	private static final int SENSOR_TYPE = Sensor.TYPE_ACCELEROMETER;
	private static final int SENSOR_RATE = SensorManager.SENSOR_DELAY_GAME;
	private static final int CALIBRATIONS_COUNT = 10;
	private static final float ALPHA = 0.8f;
	
	private SensorManager manager;

	
	private boolean pause = false;
	private long lastTime = 0;
	private float[] lastValues = new float[DeviceGesture.AXIS_COUNT];
	private AccelerometerFilter filter;

	
	/**
	 * Listeners, divided by period
	 */
	private HashMap<Long, List<IProcessedDataListener>> listeners;
	/**
	 * Periods, needed to be listen
	 */
	private List<TimePeriod> periods;

	
	
	public SensorEventProcessor(Context context) {
		listeners = new HashMap<Long, List<IProcessedDataListener>>();
		periods = new LinkedList<TimePeriod>();
		
		manager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		Sensor sensor = manager.getDefaultSensor(SENSOR_TYPE);
		filter = new AccelerometerFilter(this, CALIBRATIONS_COUNT, ALPHA);
		manager.registerListener(filter, sensor, SENSOR_RATE);
	}
	
	@Override
	public void addListener(IProcessedDataListener listener, long period) {
		List<IProcessedDataListener> listenersByPeriod = listeners.get(period);
		if (listenersByPeriod == null) { //There is no such period, adding
			List<IProcessedDataListener> newListeners = new LinkedList<IProcessedDataListener>();
			newListeners.add(listener);
			listeners.put(period, newListeners);
			periods.add(new TimePeriod(period));
			//Collections.sort(periods); //YAGNI!
		} else {
			if (listenersByPeriod.contains(listener)) {
				//Already contains this listener!
				//TODO log message
			} else {
				listenersByPeriod.add(listener);
			}
		}
	}

	@Override
	public void removeListener(IProcessedDataListener listener) {
		Set<Entry<Long, List<IProcessedDataListener>>> set = listeners.entrySet();
		for (Entry<Long, List<IProcessedDataListener>> entry : set) {
			if (entry.getValue().equals(listener)) {
				listeners.get(entry.getKey()).remove(listener);
			}
		}
		removeEmptyPeriods();
	}
	

	@Override
	public void close() {
		manager.unregisterListener(filter);
	}

	@Override
	public void pause() {
		pause = true;
	}

	@Override
	public void unpause() {
		pause = false;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		if (!pause) {
			//Do nothing
		}
	}

	@Override
	public void onSensorChanged(float[] values, long timestamp) {
		if (!pause) {
			for (TimePeriod period : periods) {
				//if time has come
				if (period.expectationTime <= timestamp) { 
					float[] result = new float[values.length];
					
					for (int i = 0; i < result.length; i++) {
						result[i] = Interpolations.flatInterpolate(lastTime, timestamp, 
								period.expectationTime, lastValues[i], values[i]);
					}
					
					for (IProcessedDataListener listener : listeners.get(period.period)) {
						listener.onDataReceived(result, timestamp);
					}
					period.expectationTime += period.period;
					//If expectation still in past (mb because of pause or first run) 
					if (period.expectationTime < timestamp) {
						period.expectationTime = timestamp + period.period;
					}
				}
			}
		}
		
		//Log.d("GestureLib","time="+(timestamp - lastTime));
		
		lastTime = timestamp;
		lastValues = values;
	}
	
	
	private void removeEmptyPeriods() {
		Set<Long> set = listeners.keySet();
		for (Long key : set) {
			if (listeners.get(key).isEmpty()) {
				listeners.remove(key);
				periods.remove(key);
			}
		}
	}
	
	class AccelerometerFilter implements SensorEventListener {

		private final ISensorEventProcessor dest;
		private final int threshold;
		private final float alpha;
		
		private int measures = 0;
		private float[] gravity = new float[3];
		private float[] linear_acceleration = new float[3];
		
		public AccelerometerFilter(ISensorEventProcessor dest, int calibrationsCount, float alpha) {
			this.dest = dest;
			this.threshold = calibrationsCount;
			this.alpha = alpha;
		}
		
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			dest.onAccuracyChanged(sensor, accuracy);
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
	          gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
	          gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
	          gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

	          linear_acceleration[0] = event.values[0] - gravity[0];
	          linear_acceleration[1] = event.values[1] - gravity[1];
	          linear_acceleration[2] = event.values[2] - gravity[2];
	          
	          measures++;
	          if (measures > threshold) {
	        	  dest.onSensorChanged(linear_acceleration, event.timestamp);
	          }
		}
	}
	
	class TimePeriod {

		long period;
		long expectationTime;

		public TimePeriod(long period) {
			this.period = period;
			this.expectationTime = 0;
		}
	}
}