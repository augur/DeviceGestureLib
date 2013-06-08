package info.augury.devicegesturelib;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Immutable data structure, which contains measurements of specific axis, along 
 * with parameters of comparison. However, it's not bound to specific time period.
 * 
 */
public class Axis implements Serializable {
	
	private static final long serialVersionUID = -5127671354147908997L;
	
	public final float[] values;
	public final float requiredProximity;
	public final CompareMode compareMode;
	
	/**
	 * @param values Array of measurements, must be > 1 elements
	 * @param requiredProximity Axis comparison result must be that high, to 
	 * assume axis are similar. Must be in range (0;1). See {@link IAxisComparator}   
	 * @param compareMode Mode of comparison. See {@link CompareMode}
	 */
	public Axis(float[] values, float requiredProximity, CompareMode compareMode) {
		if (values.length <= 1) 
			throw new RuntimeException("Too few measurements");
		this.values = Arrays.copyOf(values, values.length);
		
		if ((requiredProximity <= 0)||(requiredProximity >= 1))
			throw new RuntimeException("Invalid proximity");
		this.requiredProximity = requiredProximity;
		
		if (compareMode == null)
			throw new NullPointerException("compareMode is null");
		this.compareMode = compareMode;
	}
}