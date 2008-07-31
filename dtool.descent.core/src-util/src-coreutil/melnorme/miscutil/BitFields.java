package melnorme.miscutil;

public final class BitFields {
	/**
	 * Counts the active flags in the given bitfield
	 */
	public static int countActiveFlags(int bitfield, int[] flags) {
		int count = 0;
		for (int i = 0; i < flags.length; i++) {
			if((bitfield & flags[i]) != 0)
				count++;
		}
		return count;
	}

}
