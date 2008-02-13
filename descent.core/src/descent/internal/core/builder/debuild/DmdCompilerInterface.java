package descent.internal.core.builder.debuild;

import descent.core.builder.AbstractCompileCommand;
import descent.core.builder.AbstractLinkCommand;
import descent.core.builder.ICompileCommand;
import descent.core.builder.ICompileResponse;
import descent.core.builder.ICompileResponseInterpreter;
import descent.core.builder.ILinkResponse;
import descent.core.builder.ILinkResponseInterpreter;
import descent.core.builder.ICompilerInterface;
import descent.core.builder.ILinkCommand;
import descent.core.builder.SimpleCompileResponse;
import descent.core.builder.SimpleLinkResponse;

public class DmdCompilerInterface implements ICompilerInterface
{
	//--------------------------------------------------------------------------
	// Compile Command
	private static class DmdCompileCommand extends AbstractCompileCommand
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
			buf.append(CompilerUtil.denormalizePath(executableFile));
			buf.append(" ");
			
			// Set options
			if(compileOnly) 
			{
				buf.append("-c ");
			}
			if(null != outputDir)
			{
				buf.append("-od");
				buf.append(CompilerUtil.denormalizePath(outputDir));
				buf.append(" ");
			}
			if(null != outputFilename)
			{
				buf.append("-of");
				buf.append(CompilerUtil.denormalizePath(outputFilename));
				buf.append(" ");
			}
			if(importPaths.size() > 0)
			{
				for(String path : importPaths)
				{
					buf.append("-I");
					buf.append(CompilerUtil.denormalizePath(path));
					buf.append(" ");
				}
			}
			if(importExpPaths.size() > 0)
			{
				for(String path : importExpPaths)
				{
					buf.append("-J");
					buf.append(CompilerUtil.denormalizePath(path));
					buf.append(" ");
				}
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
			if(debugIdents.size() > 0)
			{
				for(String ident : debugIdents)
				{
					buf.append("-debug=");
					buf.append(ident);
					buf.append(" ");
				}
			}
			if(null != versionLevel)
			{
				buf.append("-version=");
				buf.append(versionLevel.toString());
				buf.append(" ");
			}
			if(versionIdents.size() > 0)
			{
				for(String ident : versionIdents)
				{
					buf.append("-version=");
					buf.append(ident);
					buf.append(" ");
				}
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
			for(String path : files)
			{
				buf.append(CompilerUtil.denormalizePath(path));
				buf.append(" ");
			}
			
			return buf.toString().trim();
		}
	}
	
	//--------------------------------------------------------------------------
	// Link Command
	private static class DmdLinkCommand extends AbstractLinkCommand
	{
		/* (non-Javadoc)
		 * @see descent.core.builder.IExecutableCommand#getCommand()
		 */
		public String getCommand()
		{
			StringBuffer buf = new StringBuffer();

			// Add the compler executable
			buf.append(CompilerUtil.denormalizePath(executableFile));
			buf.append(" ");
			
			// Add the output file
			if(null != outputFilename)
			{
				buf.append("-of");
				buf.append(CompilerUtil.denormalizePath(outputFilename));
				buf.append(" ");
			}
			
			// Add the files to link
			for(String path : files)
			{
				buf.append(CompilerUtil.denormalizePath(path));
				buf.append(" ");
			}
			
			return buf.toString().trim();
		}
	}
	
	//--------------------------------------------------------------------------
	// Response interpreter
	private static final class DmdResponseInterpreter
		implements ICompileResponseInterpreter, ILinkResponseInterpreter
	{	
		/* (non-Javadoc)
		 * @see descent.core.builder.IResponseInterpreter#interpret(java.lang.String)
		 */
		public void interpret(String line)
		{
			System.out.println(line);
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
		public ICompileResponse getCompileResponse()
		{
			return new SimpleCompileResponse();
		}

		/* (non-Javadoc)
		 * @see descent.core.builder.ILinkResponseInterpreter#getLinkResponse()
		 */
		public ILinkResponse getLinkResponse()
		{
			return new SimpleLinkResponse();
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
