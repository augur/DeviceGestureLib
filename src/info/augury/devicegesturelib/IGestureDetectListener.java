package info.augury.devicegesturelib;

/**
 * Interface for retrieving Gesture detection events  
 */
public interface IGestureDetectListener {
	
	/**
	 * Called when gesture detected.
	 * @param gestureID Id, assigned to detected {@link DeviceGestureModel}.
	 * @param timestamp Event time stamp in nanoseconds.
	 */
	public void onGestureDetected(int gestureID, long timestamp);
	
}
