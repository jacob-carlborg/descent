package descent.internal.launching.dmd;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import descent.launching.compiler.ICompilerInterface;
import descent.launching.compiler.IResponseInterpreter;
import descent.launching.compiler.BuildError;
import descent.launching.compiler.BuildResponse;

public class DmdCompilerInterface implements ICompilerInterface
{
	protected static final boolean DEBUG = true;
	
	//--------------------------------------------------------------------------
	// Response interpreter
	protected static class DmdResponseInterpreter implements IResponseInterpreter
	{
		private BuildResponse resp = new BuildResponse();
		
		private static final Pattern ERROR_WITH_FILENAME = Pattern.compile(
				"([^\\(\\:]*)" +          // Filename
				"(?:\\((\\d*)\\))?" +     // Line number
				"\\:\\s(.*)$"             // Message
			);
		
		/* (non-Javadoc)
		 * @see descent.launching.compiler.IResponseInterpreter#interpret(java.lang.String)
		 */
		public void interpret(String line)
		{
			// TODO finish & test
			
			if(DEBUG)
				System.out.println("=> " + line);
			
			Matcher m = ERROR_WITH_FILENAME.matcher(line);
			if(m.find())
			{
				String file = m.group(1);
				String lineStr = m.group(2);
				String message = m.group(3);
				int lineNum = null != lineStr ? Integer.parseInt(lineStr) : -1;
				resp.addError(new BuildError(message, file, lineNum));
				return;
			}
		}
		
		/* (non-Javadoc)
		 * @see descent.launching.compiler.IResponseInterpreter#interpretError(java.lang.String)
		 */
		public void interpretError(String line)
		{
			// Keep all the interpretation in one method
			interpret(line);
		}

		/* (non-Javadoc)
		 * @see descent.launching.compiler.ICompileResponseInterpreter#getCompileResponse()
		 */
		public BuildResponse getResponse()
		{	
			return resp;
		}
	}
	
	//--------------------------------------------------------------------------
	// Interface implementation
	
	public IResponseInterpreter createRebuildResponseInterpreter()
	{
		return new DmdResponseInterpreter();
	}

	//--------------------------------------------------------------------------
	// Instance management
	private static final ICompilerInterface instance = new DmdCompilerInterface();
	
	private DmdCompilerInterface()
	{
		// Can't instantiate
	}
	
	public static ICompilerInterface getInstance()
	{
		return instance;
	}
}
