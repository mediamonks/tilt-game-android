package temple.core.utils;

/**
 * Created by Jonathan on 26-2-2015.
 */
public class MathUtils {
	public static double degreeToRadians(double degree) {
		return degree / 180 * Math.PI;
	}

	public static double radiansToDegree(double radians) {
		return radians * 180 / Math.PI;
	}
}
