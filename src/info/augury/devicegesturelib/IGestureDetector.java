package info.augury.devicegesturelib;

/**
 * Contract of main Library class, which used for gesture detection 
 */
public interface IGestureDetector {

	/**
	 * @param gestureModel Gesture Model to be detecting.
	 * @param listener Place where to send detection events. 
	 */
	public void registerGestureDetection(DeviceGestureModel gestureModel, IGestureDetectListener listener);
	/**
	 * @param gestureModel Gesture Model to stop being detecting.  
	 */
	public void unregisterGestureDetection(DeviceGestureModel gestureModel);
	/**
	 * Pause all detections.
	 */
	public void pause();
	/**
	 * Unpause all detections.
	 */
	public void unpause();
	/**
	 * When done with Library, one should finish the job, using this method.
	 */
	public void close();

}
