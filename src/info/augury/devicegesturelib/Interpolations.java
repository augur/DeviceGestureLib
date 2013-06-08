package info.augury.devicegesturelib;

/**
 * Bunch of interpolation methods
 */
class Interpolations {

	/**
	 * @return y in the same place between y1 and y2, as x between x1 and x2
	 */
	public static float flatInterpolate(float x1, float x2, float x, float y1, float y2) {
		float xDif = x2 - x1;
		float yDif = y2 - y1;
		float xCoef = 0;
		if (xDif != 0) {
			xCoef = (x - x1) / xDif;
		}
		return y1 + yDif * xCoef;
	}

	public static float flatInterpolate(long x1, long x2, long x, float y1, float y2) {
		long xDif = x2 - x1;
		float yDif = y2 - y1;
		float xCoef = 0;
		if (xDif != 0) {
			xCoef = (x - x1) / xDif;
		}
		return y1 + yDif * xCoef;
	}
	
	public static float flatInterpolate(int x1, int x2, int x, float y1, float y2) {
		int xDif = x2 - x1;
		float yDif = y2 - y1;
		float xCoef = 0;
		if (xDif != 0) {
			xCoef = (x - x1) / xDif;
		}
		return y1 + yDif * xCoef;
	}
	
}
