package descent.core.builder;

import java.io.File;

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
	protected static class DmdResponseInterpreter
		implements ICompileResponseInterpreter, ILinkResponseInterpreter
	{
		public void interpret(String line)
		{
			if(DEBUG)
				System.out.println("=> " + line);
		}
		
		public void interpretError(String line)
		{
			// Keep all the interpretation in one method
			interpret(line);
		}

		public ICompileResponse getCompileResponse()
		{
			SimpleCompileResponse response = new SimpleCompileResponse();
			
			return response;
		}

		public ILinkResponse getLinkResponse()
		{
			SimpleLinkResponse response = new SimpleLinkResponse();
			
			return response;
		}
	}
	
	//--------------------------------------------------------------------------
	// Interface implementation
	
	public ICompileCommand createCompileCommand()
	{
		return new DmdCompileCommand();
	}

	public ILinkCommand createLinkCommand()
	{
		return new DmdLinkCommand();
	}
	
	public ICompileResponseInterpreter createCompileResponseInterpreter()
	{
		return new DmdResponseInterpreter();
	}

	public ILinkResponseInterpreter createLinkResponseInterpreter()
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
