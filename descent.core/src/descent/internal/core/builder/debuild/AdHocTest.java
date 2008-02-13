package descent.internal.core.builder.debuild;

import java.io.File;

import descent.core.builder.DmdCompilerInterface;
import descent.core.builder.ICompileCommand;
import descent.core.builder.ICompileResponse;
import descent.core.builder.ICompileResponseInterpreter;
import descent.core.builder.ICompilerInterface;

/**
 * Ad-hoc tetsing environment to remove when builder is integrated into
 * Eclipse builder.
 */
public class AdHocTest
{
	// TODO remove or convert to JUnit tests where necessary
	
	private static final String WORKING_DIRECTORY = 
			"C:/Users/xycos/workspace/descent.unittest/testdata/src";
	private static final String DMD_EXECUTABLE = "dmd";
	private static final String SOURCE_FILE = "sample/module1.d";
	private static final String IMPORT_PATH = "C:/dmd/src/phobos";
	private static final String OUTPUT_FILENAME = "lolcat.obj";
	
	public static void main(String[] args)
	{
		ICompilerInterface compiler = DmdCompilerInterface.getInstance();
		
		ICompileCommand compileCmd = compiler.createCompileCommand();
		compileCmd.setExecutableFile(new File(DMD_EXECUTABLE));
		compileCmd.addFile(new File(SOURCE_FILE));
		compileCmd.addImportPath(new File(IMPORT_PATH));
		compileCmd.setOutputFilename(new File(OUTPUT_FILENAME));
		
		ICompileResponseInterpreter interpreter = 
			compiler.createCompileResponseInterpreter();
		ExecutionMonitor monitor = new ExecutionMonitor(compileCmd,
				interpreter, null, WORKING_DIRECTORY);
		monitor.run();
		
		ICompileResponse response = interpreter.getCompileResponse();
	}
}
