package info.augury.devicegesturelib;

import java.io.Serializable;

import android.util.Log;

/**
 * Counter of cooldown after gesture detection.
 */
class CooldownCount implements Serializable {
	
	/**
	 * Automatically generated serial ID
	 */
	private static final long serialVersionUID = -5728506138632524882L;
	
	private final long cooldown;
	private long lastInvoke = 0;
	
	public CooldownCount(long cooldown) {
		if (cooldown < 0) {
			Log.e(DeviceGestureLibrary.DEBUG_TAG, "Invalid cooldown in CooldownCount constructor = "+cooldown);
			this.cooldown = 0;
		} else {
			this.cooldown = cooldown;
		}
	}
	
	public boolean isReady(long timestamp) {
		return (timestamp > lastInvoke + cooldown); 
	}
	
	public void invokeCooldown(long timestamp) {
		lastInvoke = timestamp;
	}
	

}
