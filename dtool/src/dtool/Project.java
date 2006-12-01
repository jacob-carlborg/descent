package dtool;

import util.ExceptionAdapter;

public class Project {

	static CompilationUnit testcu;
	
	
	public static void newTestProject() {
		try {
			testcu = new CompilationUnit("testinput/test.d");
			System.out.println(">> read: " + testcu.file + " ");
			//System.out.println(testcu.source);
			
		} catch (Exception e) {
			throw new ExceptionAdapter(e);
		}
	}

}
