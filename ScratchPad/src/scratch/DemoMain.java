package scratch;



public class DemoMain {
	

	@SuppressWarnings("unchecked")
	public static <T> T[] castize(@SuppressWarnings("unused") T obj) {
		return (T[]) new Object[0];
	}

	public static void main(String[] args) throws Exception {
		if(false)
			throw new Exception();

		
	
		Boolean nil = null;
		
		System.out.println("" + (nil == true));
		System.out.println("" + (nil == false));
		
		boolean b = foo();
		System.out.println(b);

		
		String[] strArray2 = castize("String");
		String[] strArray = (String[]) new Object[0]; // Runtime invalid
		
	}
	
	
	public static Boolean foo() {
		return true;
	}
	
}
