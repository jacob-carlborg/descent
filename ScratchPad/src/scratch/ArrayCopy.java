package scratch;

import static melnorme.miscutil.Assert.assertTrue;

public class ArrayCopy {

	public static void main(String[] args) {
		Foo foo = new Foo(1, 2);
		Object[] array = new Object[] { "123" , foo, "6" , "88"};
		Object[] clone = array.clone();
		
		assertTrue(array[1] == clone[1]);
		assertTrue(array.getClass().getComponentType() == clone.getClass().getComponentType());
		assertTrue(array.getClass().getComponentType() != args.getClass().getComponentType());
		assertTrue(args.clone().getClass().getComponentType() == String.class);
	}
}
