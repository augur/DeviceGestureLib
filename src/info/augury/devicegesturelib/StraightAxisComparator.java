package info.augury.devicegesturelib;

import java.util.List;

import android.util.Log;

/**
 * Comparator of accelerometer data arrays. Currently it uses cheap 
 * {@link StraightAxisComparator#NaiveRelativness(float, float)} 
 * This comparator precision is uncertain. 
 */
class StraightAxisComparator implements IAxisComparator {

	private float[] model;
	
	public StraightAxisComparator(Axis modelAxis) {
		model = modelAxis.values;
	}
	
	@Override
	public float compare(List<Float> data) {
		if (model.length != data.size()) {
			Log.e(DeviceGestureLibrary.DEBUG_TAG, 
					"Axis compare failed - invalid sized data passed!");
			return 0;
		}
		
		float result = 0;
		
		int i = 0;
		for (Float f : data) {
			result += NaiveRelativness(model[i], f); 
			i++;
		}
		
		result /= model.length;
		
		return result;
	}
	
	
	private static float NaiveRelativness(float model, float real) {
		if (Math.signum(model) == Math.signum(real)) {
			model = Math.abs(model);
			real = Math.abs(real);

			float min = Math.min(real, model);
			float max = Math.max(real, model);
			return min / max;
		} else {
			return 0;
		}
	}

}
