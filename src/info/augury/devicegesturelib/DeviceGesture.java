package info.augury.devicegesturelib;

import java.io.Serializable;

/**
 * Immutable data structure, which contains info about specific gesture.
 */
class DeviceGesture implements Serializable{
	
	/**
	 * Automatically generated serial ID
	 */
	private static final long serialVersionUID = -4744852350534502035L;

	static final int AXIS_COUNT = 3;

	public final int id;
	
	public final Axis front;
	public final Axis side;
	public final Axis vert;
	
	public final int pointsCount;
	public final long interval;

	private final CooldownCount cdCount;
	
	/**
	 * Constructor for base model creation.
	 */
	DeviceGesture(int id, Axis front, Axis side, Axis vert, int pointsCount, long interval, CooldownCount counter) {
		this.id = id;
		
		this.front = front;
		this.side = side;
		this.vert = vert;
		
		this.pointsCount = pointsCount;
		this.interval = interval;
		
		cdCount = counter;
	}

	CooldownCount getCDC() {
		return cdCount;
	}
}