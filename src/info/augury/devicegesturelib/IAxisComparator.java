package info.augury.devicegesturelib;

import java.util.List;

/**
 * Axis comparison class contract. 
 */
interface IAxisComparator {

	/**
	 * @param data accelerometer data to compare
	 * @return proximity of input and model data, (0..1)
	 */
	float compare(List<Float> data);	
}
