package scratch;


public class ArrayTyping {

	public static void main(String[] args) {
		Foo foo = new Foo(1, 2);
		Object[] array = new Object[] { "123" , foo, "6" , "88"};
		Foo[] fooArray = (Foo[]) array;
		
		System.out.println(fooArray[1]);
	}
}
