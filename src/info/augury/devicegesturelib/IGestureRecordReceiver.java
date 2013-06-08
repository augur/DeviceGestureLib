package info.augury.devicegesturelib;

/**
 * Interface to receieve accelerometer data record results.
 */
public interface IGestureRecordReceiver {
	
	/**
	 * Once record is finished, this method get called.
	 * @param side side-axis data.
	 * @param front front-axis data.
	 * @param vert vert-axis data.
	 */
	public void onResults(float[] side, float[] front, float[] vert);
	
}
