package descent.core.builder;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DmdCompilerInterface implements ICompilerInterface
{
	protected static final boolean DEBUG = true;
	
	//--------------------------------------------------------------------------
	// Compile Command
	protected static class DmdCompileCommand extends AbstractCompileCommand
	{
		DmdCompileCommand()
		{
			setDefaults();
		}
		
		/* (non-Javadoc)
		 * @see descent.core.builder.IExecutableCommand#getCommand()
		 */
		public final String getCommand()
		{
			StringBuffer buf = new StringBuffer();

			// Add the compler executable
			buf.append(executableFile.getPath());
			buf.append(" ");
			
			// Set options
			if(compileOnly) 
			{
				buf.append("-c ");
			}
			if(null != outputDirectory)
			{
				buf.append("-od");
				buf.append(outputDirectory.getPath());
				buf.append(" ");
			}
			if(null != outputFilename)
			{
				buf.append("-of");
				buf.append(outputFilename.getPath());
				buf.append(" ");
			}
			for(File path : importPaths)
			{
				buf.append("-I");
				buf.append(path.getPath());
				buf.append(" ");
			}
			for(File path : importExpPaths)
			{
				buf.append("-J");
				buf.append(path.getPath());
				buf.append(" ");
			}
			if(allowDeprecated)
			{
				buf.append("-d ");
			}
			if(showWarnings)
			{
				buf.append("-w ");
			}
			if(addDebugInfo)
			{
				buf.append("-g ");
			}
			if(!addAssertsAndContracts)
			{
				buf.append("-release ");
			}
			if(addUnittests)
			{
				buf.append("-unittest ");
			}
			if(insertDebugCode)
			{
				buf.append("-debug ");
			}
			if(null != debugLevel)
			{
				buf.append("-debug=");
				buf.append(debugLevel.toString());
				buf.append(" ");
			}
			for(String ident : debugIdents)
			{
				buf.append("-debug=");
				buf.append(ident);
				buf.append(" ");
			}
			if(null != versionLevel)
			{
				buf.append("-version=");
				buf.append(versionLevel.toString());
				buf.append(" ");
			}
			for(String ident : versionIdents)
			{
				buf.append("-version=");
				buf.append(ident);
				buf.append(" ");
			}
			if(inlineFunctions)
			{
				buf.append("-inline ");
			}
			if(optimizeCode)
			{
				buf.append("-o ");
			}
			if(instrumentForCoverage)
			{
				buf.append("-cov ");
			}
			if(instrumentForProfile)
			{
				buf.append("-profile ");
			}
			if(verbose)
			{
				buf.append("-v ");
			}
			if(quiet)
			{
				buf.append("-quiet ");
			}
			
			// Add the files to compile
			for(File path : files)
			{
				buf.append(path.getPath());
				buf.append(" ");
			}
			
			return buf.toString().trim();
		}
	}
	
	//--------------------------------------------------------------------------
	// Link Command
	protected static class DmdLinkCommand extends AbstractLinkCommand
	{
		DmdLinkCommand()
		{
			setDefaults();
		}
		
		/* (non-Javadoc)
		 * @see descent.core.builder.IExecutableCommand#getCommand()
		 */
		public String getCommand()
		{
			StringBuffer buf = new StringBuffer();

			// Add the compler executable
			buf.append(executableFile.getPath());
			buf.append(" ");
			
			// Add the output file
			if(null != outputFilename)
			{
				buf.append("-of");
				buf.append(outputFilename.getPath());
				buf.append(" ");
			}
			
			// Add the files to link
			for(File path : files)
			{
				buf.append(path.getPath());
				buf.append(" ");
			}
			
			return buf.toString().trim();
		}
	}
	
	//--------------------------------------------------------------------------
	// Response interpreter
	protected static class DmdResponseInterpreter implements IResponseInterpreter
	{
		private SimpleBuildResponse resp = new SimpleBuildResponse();
		
		private static final Pattern ERROR_WITH_FILENAME = Pattern.compile(
				"([^\\(\\:]*)" +          // Filename
				"(?:\\((\\d*)\\))?" +     // Line number
				"\\:\\s(.*)$"             // Message
			);
		
		/* (non-Javadoc)
		 * @see descent.core.builder.IResponseInterpreter#interpret(java.lang.String)
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
				resp.addError(new SimpleBuildError(message, file, lineNum));
				return;
			}
		}
		
		/* (non-Javadoc)
		 * @see descent.core.builder.IResponseInterpreter#interpretError(java.lang.String)
		 */
		public void interpretError(String line)
		{
			// Keep all the interpretation in one method
			interpret(line);
		}

		/* (non-Javadoc)
		 * @see descent.core.builder.ICompileResponseInterpreter#getCompileResponse()
		 */
		public IBuildResponse getResponse()
		{
			resp.succesful = resp.errors.isEmpty();
			
			return resp;
		}
	}
	
	//--------------------------------------------------------------------------
	// Interface implementation
	
	/* (non-Javadoc)
	 * @see descent.core.builder.ICompilerInterface#createCompileCommand()
	 */
	public ICompileCommand createCompileCommand()
	{
		return new DmdCompileCommand();
	}

	/* (non-Javadoc)
	 * @see descent.core.builder.ICompilerInterface#createLinkCommand()
	 */
	public ILinkCommand createLinkCommand()
	{
		return new DmdLinkCommand();
	}
	
	/* (non-Javadoc)
	 * @see descent.core.builder.ICompilerInterface#createCompileResponseInterpreter()
	 */
	public IResponseInterpreter createCompileResponseInterpreter()
	{
		return new DmdResponseInterpreter();
	}

	/* (non-Javadoc)
	 * @see descent.core.builder.ICompilerInterface#createLinkResponseInterpreter()
	 */
	public IResponseInterpreter createLinkResponseInterpreter()
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
