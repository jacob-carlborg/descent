package melnorme.miscutil;

public class Misc {

	/** Combines two hash codes to make a new one. */
	public static int combineHashCodes(int hashCode1, int hashCode2) {
		return hashCode1 * 17 + hashCode2;
	}

}
