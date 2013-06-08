package info.augury.devicegesturelib;

import android.content.Context;
import static info.augury.devicegesturelib.SensorEventProcessor.*;

/**
 * Class, that gives ability to record and retrieve accelerometer data from 
 * {@link SensorEventProcessor}. 
 */
class GestureRecorder implements IProcessedDataListener {

	private final int count;
	private final IGestureRecordReceiver receiver;
	private final ISensorEventProcessor processor;

	private int index;

	private float[] side;
	private float[] front;
	private float[] vert;
	
	public GestureRecorder(Context context, long period, int count, IGestureRecordReceiver receiver) {
		
		if ((period <= 0)||(count <= 0))
			throw new IllegalArgumentException();

		if (receiver == null)
			throw new NullPointerException();
		
		processor = DeviceGestureLibrary.createSensorEventProcessor(context);
		processor.addListener(this, period);

		this.count = count;
		this.receiver = receiver;
		
		side = new float[count];
		front = new float[count];
		vert = new float[count];
		
		index = 0;
	}
	
	@Override
	public void onDataReceived(float[] data, long timestamp) {

		side[index] = data[SIDE_AXIS];
		front[index] = data[FRONT_AXIS];
		vert[index] = data[VERT_AXIS];
		
		index++;
		if (index == count) {
			processor.removeListener(this);
			processor.close();
			receiver.onResults(side, front, vert);
		}
	}

}
