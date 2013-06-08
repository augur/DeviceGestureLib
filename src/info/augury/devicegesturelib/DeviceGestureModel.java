package info.augury.devicegesturelib;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Immutable data structure, which contains full information about gesture,
 * and its detection conditions
 * Serializable - to make it storing easy. 
 */
public class DeviceGestureModel implements Serializable {
	
	private static final long serialVersionUID = 1104159247640584046L;
	
	private final int id;
	private final Axis front;
	private final Axis side;
	private final Axis vert;
	
	private final long baseInterval;
	private final int measuresCount;
	private final long cooldown;
	
	private final long baseDuration; 
	private final long minDuration;
	private final long maxDuration;
	
	/**
	 * @param id Integer value for identification purposes.
	 * @param front Front {@link Axis}. May be null.
	 * @param side Side {@link Axis}. May be null.
	 * @param vert Vertical {@link Axis}. May be null.
	 * @param interval Time interval in nanoseconds between all axis measures. Therefore gesture duration = (measuresCount - 1) * interval 
	 * @param cooldown Idleness interval in nanoseconds after gesture has detected (if 0, there might be multiple detections of single gesture)
	 * @param durationDeviation Base gesture duration = (measuresCount - 1) * interval, and it can differ ± given deviation in nanoseconds   
	 */
	public DeviceGestureModel(int id, Axis front, Axis side, Axis vert, long interval, long cooldown, long durationDeviation) {
		this.id = id;
		this.front = front;
		this.side = side;
		this.vert = vert;
		
		if (interval <= 0) 
			throw new IllegalArgumentException("Invalid time interval passed");
		this.baseInterval = interval;
		
		//Also checks axis validity
		this.measuresCount = calcPointsCount();
		
		if (cooldown < 0)
			throw new IllegalArgumentException("Negative cooldown passed");
		this.cooldown = cooldown;
		
		baseDuration = (measuresCount - 1) * interval;

		if (durationDeviation < 0)
			throw new IllegalArgumentException("Negative duration deviation passed");
		maxDuration = baseDuration + durationDeviation;
		minDuration = baseDuration - durationDeviation;
	}
	
	List<DeviceGesture> buildGestures() {
		List<DeviceGesture> result = new LinkedList<DeviceGesture>();

		CooldownCount counter = new CooldownCount(cooldown);
		
		//base gesture
		result.add(new DeviceGesture(id, front, side, vert, measuresCount, baseInterval, counter));
		
		//minus-duration deviations
		long duration = baseDuration - baseInterval;
		while (duration >= minDuration) {
			long interval = duration / (measuresCount - 1);
			result.add(new DeviceGesture(id, front, side, vert, measuresCount, interval, counter));
			duration -= baseInterval;
		}

		//plus-duration deviations
		duration = baseDuration + baseInterval;
		while (duration <= maxDuration) {
			long interval = duration / (measuresCount - 1);
			result.add(new DeviceGesture(id, front, side, vert, measuresCount, interval, counter));
			duration += baseInterval;
		}
		
		return result;
	}
	
	private int calcPointsCount() {
		int count = 0;
		boolean checkCount = true;
		
		if (front != null) 
			if (count == 0)
				count = front.values.length;
			else
				checkCount = checkCount && (count == front.values.length);

		if (side != null) 
			if (count == 0)
				count = side.values.length;
			else
				checkCount = checkCount && (count == side.values.length);

		if (vert != null) 
			if (count == 0)
				count = vert.values.length;
			else
				checkCount = checkCount && (count == vert.values.length);
		
		if (count == 0)
			throw new NullPointerException("All axis are null");
		if (!checkCount)
			throw new IllegalArgumentException("Axis count of measures differ");
		
		return count;
	}
}
