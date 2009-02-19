package melnorme.miscutil;


public class NumUtils {

	/** Caps given number betwen given min and max, inclusive. */
	public static int capBetween(int min, int number, int max) {
		return Math.min(max, Math.max(min, number));
	}

	/** @return the number closests to zero, between a and b */
	public static int nearestToZero(int a, int b) {
		if(Math.abs(a) < Math.abs(b))
			return a;
		else 
			return b;
	}

}
