package info.augury.devicegesturelib;

import android.content.Context;
/**
 * Main library class, designed as bunch of static methods
 */
public final class DeviceGestureLibrary {
	
	final static String DEBUG_TAG = "DeviceGestureLibrary";
	/**
	 * Must not be created
	 */
	private DeviceGestureLibrary() {}
	
	/**
	 * Read data from accelerometer, that may be used to create {@link Axis} objects
	 * @param context Android Application Context
	 * @param period Time interval in nanoseconds between measures
	 * @param count Total number of measures
	 * @param receiver Interface to get results. See {@link IGestureRecordReceiver}
	 */
	public static void recordGesture(Context context, long period, int count, IGestureRecordReceiver receiver) {
		new GestureRecorder(context, period, count, receiver); 
	}

	/**
	 * "Simple-factory" method to get Detector object
	 * @param context Android Application Context
	 * @return new instance of {@link IGestureDetector}
	 */
	public static IGestureDetector createGestureDetector(Context context) {
		return new GestureDetector(context);
	}
	
	static ISensorEventProcessor createSensorEventProcessor(Context context) {
		return new SensorEventProcessor(context);
	}

	static IGestureComparator createGestureComparator(DeviceGesture gesture, IGestureDetectListener listener) {
		return new GestureComparator(gesture, listener);
	}
	
	static IAxisComparator createAxisComparator(Axis axis) {
		switch (axis.compareMode) {
		case Straight:
			return new StraightAxisComparator(axis);
		case Flattened:
			return new FlattenedAxisComparator(axis);
		default:
			throw new RuntimeException("Invalid axis compare mode!");
		}
	}
	

}
