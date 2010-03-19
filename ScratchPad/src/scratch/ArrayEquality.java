package scratch;

import static melnorme.miscutil.Assert.assertTrue;
import melnorme.miscutil.CoreUtil;

public class ArrayEquality {
	
	private static final String STR_123 = "123";
	private static final String STR_ABC = "abc";
	
	public static void main(String[] args) throws Exception {
		final Object[] arrayA = new String[]{ STR_ABC, STR_123};
		final String[] arrayB = new String[]{ STR_ABC, STR_123};
		final String[] arrayC = new String[]{ STR_ABC, "123"};
		
		assertTrue((arrayA == arrayA) == true);
		assertTrue((arrayA == arrayB) == false);
		
		assertTrue((CoreUtil.areEqual(arrayA, arrayA)) == true);
		assertTrue((CoreUtil.areArrayEqual(arrayA, arrayA)) == true);
		assertTrue((CoreUtil.areArrayDeepEqual(arrayA, arrayA)) == true);
		
		assertTrue(CoreUtil.areEqual(arrayA, arrayB) == false); // strange, but that's the way it is
		assertTrue(CoreUtil.areArrayEqual(arrayA, arrayB) == true);
		assertTrue(CoreUtil.areArrayDeepEqual(arrayA, arrayB) == true);

		assertTrue(CoreUtil.areArrayEqual(arrayA, arrayC) == true);
		assertTrue(CoreUtil.areArrayDeepEqual(arrayA, arrayC) == true);

	}
	
}
