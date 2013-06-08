package info.augury.devicegesturelib;

import static info.augury.devicegesturelib.SensorEventProcessor.FRONT_AXIS;
import static info.augury.devicegesturelib.SensorEventProcessor.SIDE_AXIS;
import static info.augury.devicegesturelib.SensorEventProcessor.VERT_AXIS;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Class that collects data from accelerometer, compares it, using multiple {@link IAxisComparator}
 * and makes decision about gesture detection 
 */
class GestureComparator implements IGestureComparator {
	

	private DeviceGesture gesture;
	private int properLength;
	private IGestureDetectListener listener;
	
	IAxisComparator sideComparator;
	IAxisComparator frontComparator;
	IAxisComparator vertComparator;
	
	List<List<Float>> buffers = new ArrayList<List<Float>>(DeviceGesture.AXIS_COUNT);
	
	public GestureComparator(DeviceGesture gesture, IGestureDetectListener listener) {
		this.gesture = gesture;
		this.listener = listener;

		this.properLength = gesture.pointsCount;
		if (gesture.side != null) {
			sideComparator = DeviceGestureLibrary.createAxisComparator(gesture.side);
		}
		if (gesture.front != null) {
			frontComparator = DeviceGestureLibrary.createAxisComparator(gesture.front);
		}
		if (gesture.vert != null) {
			vertComparator = DeviceGestureLibrary.createAxisComparator(gesture.vert);
		}
		
		for (int i = 0; i < DeviceGesture.AXIS_COUNT; i++) {
			buffers.add(null);
		}
		buffers.set(SIDE_AXIS, new LinkedList<Float>());
		buffers.set(FRONT_AXIS, new LinkedList<Float>());
		buffers.set(VERT_AXIS, new LinkedList<Float>());
	}


	@Override
	public void onDataReceived(float[] data, long timestamp) {
		
		//Add new elements
		for (int i = 0; i < data.length; i++) {
			buffers.get(i).add(data[i]);
		}
		
		//if buffers are oversized
		if (buffers.get(0).size() > properLength) {
			//remove first (oldest) elements
			for (int i = 0; i < data.length; i++) {
				buffers.get(i).remove(0);
			}
			
			//Now ready to compare (if not on cd)
			if (gesture.getCDC().isReady(timestamp)) {

				boolean failed = false;
				
				//Emulate loop to have ability to use break
				do {
					
					if (sideComparator != null) {
						float proximity = sideComparator.compare(buffers.get(SIDE_AXIS));
						if (proximity <= gesture.side.requiredProximity) {
							failed = true;
							break;
						}
					}
					
					if (frontComparator != null) {
						float proximity = frontComparator.compare(buffers.get(FRONT_AXIS));
						if (proximity <= gesture.front.requiredProximity) {
							failed = true;
							break;
						}
					}

					if (vertComparator != null) {
						float proximity = vertComparator.compare(buffers.get(VERT_AXIS));
						if (proximity <= gesture.vert.requiredProximity) {
							failed = true;
							break;
						}
					}
					
				} while (false);


				if (!failed) {
					gesture.getCDC().invokeCooldown(timestamp);
					listener.onGestureDetected(gesture.id, timestamp);
				}
				
			}
		}
	}
}