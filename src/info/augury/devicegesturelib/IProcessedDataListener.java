package info.augury.devicegesturelib;

/**
 * Interface to receieve filtered and interval-adjusted acceloremeter data. 
 */
interface IProcessedDataListener {
	
	void onDataReceived(float[] data, long timestamp);

}
