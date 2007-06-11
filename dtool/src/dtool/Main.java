package dtool;




public class Main {
	
	private Main() { }
	

	public static void main(String[] args) throws Exception {
		System.out.println("======== DTool ========");

		try {

		} catch (Exception e) {
			// For annoying flushing problems in Eclipse console
			System.out.flush();
			System.err.flush();

			throw e;
		}
		System.out.println("= THE END =");
	}


}
