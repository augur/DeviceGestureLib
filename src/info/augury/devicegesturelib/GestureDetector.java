package info.augury.devicegesturelib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;

/**
 * Class, that takes tasks to detect gestures, specified by {@link DeviceGestureModel}. 
 * Also gives some control of {@link SensorEventProcessor}
 */
class GestureDetector implements IGestureDetector {
	

	private ISensorEventProcessor processor;
	private HashMap<DeviceGestureModel, List<IGestureComparator>> comparators;
	
	public GestureDetector(Context context) {
		processor = DeviceGestureLibrary.createSensorEventProcessor(context);
		comparators = new HashMap<DeviceGestureModel, List<IGestureComparator>>();
	}

	@Override
	public void registerGestureDetection(DeviceGestureModel gestureModel,
			IGestureDetectListener listener) {
		
		if ((gestureModel == null)||(listener == null)) 
			throw new NullPointerException();

		List<DeviceGesture> gestures = gestureModel.buildGestures();
		List<IGestureComparator> comps = new ArrayList<IGestureComparator>(gestures.size());
		
		for (DeviceGesture gesture : gestures) {
			IGestureComparator comp = DeviceGestureLibrary.createGestureComparator(gesture, listener);
			processor.addListener(comp, gesture.interval);
			comps.add(comp);
		}
		//Replace gestureModel and remove old comparators
		unregisterCompListeners(comparators.put(gestureModel, comps));
	}

	@Override
	public void unregisterGestureDetection(DeviceGestureModel gestureModel) {
		if (gestureModel == null) 
			throw new NullPointerException();
		//Remove gestureModel and its comparators
		unregisterCompListeners(comparators.remove(gestureModel));
	}
	
	@Override
	public void pause() {
		processor.pause();
	}

	@Override
	public void unpause() {
		processor.unpause();
	}
	
	@Override
	public void close() {
		processor.close();
		processor = null;
		comparators = null;
	}
	
	private void unregisterCompListeners(List<IGestureComparator> comps) {
		if (comps != null)
			for (IGestureComparator comp : comps) {
				processor.removeListener(comp);
			}
	}
}