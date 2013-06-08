package info.augury.devicegesturelib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.util.Log;

/**
 * Comparator of flattened (coerced to [-1 ; 1]) accelerometer data arrays.
 * Model and current data arrays must be same length. Elements are compared 
 * by calculating (1 - absolute interval between them) 
 * Relatively cheap and effective.
 */
class FlattenedAxisComparator implements IAxisComparator {

	private float[] model;
	
	public FlattenedAxisComparator(Axis modelAxis) {
		model = flattenData(modelAxis.values);
	}
	
	@Override
	public float compare(List<Float> data) {
		if (model.length != data.size()) {
			Log.e(DeviceGestureLibrary.DEBUG_TAG, 
					"Axis compare failed - invalid sized data passed!");
			return 0;
		}
		
		float[] flatten = flattenData(data);
		float result = 0;
		for (int i = 0; i < model.length; i++) {
			result += Math.max(0, (1 - Math.abs(model[i] - flatten[i])));
		}
		result /= model.length;
		
		return result;
	}
	
	private static float[] flattenData(float[] data) {
		Float[] objData = new Float[data.length];
		for (int i = 0; i < data.length; i++) {
			objData[i] = data[i];
		}
		List<Float> list = new ArrayList<Float>(Arrays.asList(objData));
		return flattenData(list);
	}
	
	private static float[] flattenData(List<Float> data) {
		float[] result = new float[data.size()];
		int i = 0;

		//=== First we find max\min===
		float max = data.get(0);
		float min = max;
		for (Float f : data) {
			if (f > max) { max = f;} else
				if (f < min) { min = f;}
		}
		//============================
		
		for (Float f : data) {
			if (f > 0) {
				result[i] = Interpolations.flatInterpolate(0, max, f, 0, 1); 
			} else {
				result[i] = Interpolations.flatInterpolate(min, 0, f, -1, 0);
			}
			i++;
		}
		
		return result;
	}

}