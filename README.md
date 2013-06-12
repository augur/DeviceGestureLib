DeviceGestureLib
================

Android Library for device-gestures detection.

1) Distribution
-------------------------
The source code is distributed under GNU LGPL ver. 3 (https://www.gnu.org/licenses/lgpl.html)

2) Concept
-------------------------
Library analyses accelerometer data and compares current measures with "model" device-gestures. If requirements of similarity met, it generates detection event.
 
3) Representation
-------------------------
Library source code is represented as Eclipse "Android Library Project". There are 2 ways to use it: attach as a Library to your main Android project (Properties -> Android), or use as external jar.
All library classes are bundled into info.augury.devicegesturelib package. Classes you require to utilize library are public.

4) Usage
-------------------------
At first, you need to create model of gesture. It's better to create model with real accelerometer data, other than put synthetic constants.

Class to receive accelerometer data record:

	class DataReciever implements IGestureRecordReceiver {

		public void onResults(float[] side, float[] front, float[] vert) {
			... //result processing
		}

	}

Action to record data:

	DataReceiver receiver;
	...
	long interval = 50 * 1000000; //Interval between measures in nanoseconds (50ms)
	int count = 10; //Number of measures  
	
	DeviceGestureLibrary.recordGesture(GetApplicationContext(), interval, count, receiver); 

Now we can create up to 3 Axis, which are components of gesture model. Example for front axis:

	float[] frontAxisRecord; 
	...
	float requiredProximity = 0.75f; //Threshold of detection
	CompareMode mode = CompareMode.Flattened; //Mode of axis data comparison
	
	Axis frontAxis = new Axis(frontAxisRecord, requiredProximity, mode);
	
When everything ready to create gesture model:

	Axis frontAxis;
	Axis sideAxis;
	Axis vertAxis;
	...
	int id = 100; //Gesture identification number
	long interval = 50 * 1000000; //Interval between measures in nanoseconds (50ms)
	long cooldown = 1000 * 1000000; //Idleness interval after detection event in nanoseconds (1000ms)
	long deviation = 200 * 1000000; //Possible deviation of total duration in nanoseconds (200ms)
	
	DeviceGestureModel model = new DeviceGestureModel(id, frontAxis, sideAxis, vertAxis, interval, cooldown, deviation);
	
Then we may create detector object:

	IGestureDetector detector = DeviceGestureLibrary.createGestureDetector(getApplicationContext());
	 
For detection events we need listener:

	class DetectListener implements IGestureDetectListener {
	
		public void onGestureDetected(int gestureID, long timestamp) {
		... //event processing
		}
	
	}
	
Finally, activate gesture detection:

	DeviceGestureModel model;
	IGestureDetector detector;
	DetectListener listener;
	...
	detector.registureGestureDetection(model, listener);


5) Example
-------------------------
Demo application on Google Play: https://play.google.com/store/apps/details?id=info.augury.gesturelibdemo

App usage video on Youtube: https://www.youtube.com/watch?v=8S83_gMkRnY

6) Hints
-------------------------
* Library can effectively detects very complex gestures, but only on precisely tuned models.
* If your gesture model do not use specific axis, it's highly recommended to set it null.
* If your gesture model have frequent direction shifts, ensure interval between measures is small enough.
* In rare cases, Straight compare mode is preferable rather than default Flattened mode.
* Complex gestures often require lower than average(0.7f) proximity threshold (at least on specific axis).
* Vice versa, common\short gestures require higher than average proximity threshold, to cut off false detection events.
* Often, there are sequence of detection events generated on single gesture - use model's cooldown parameter.
  
