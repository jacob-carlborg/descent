package descent.internal.building.compiler;

import java.io.File;
import java.util.regex.Pattern;

import descent.building.compiler.AbstractCompileCommand;
import descent.building.compiler.AbstractLinkCommand;
import descent.building.compiler.BooleanOption;
import descent.building.compiler.CompilerOption;
import descent.building.compiler.ICompileCommand;
import descent.building.compiler.ICompilerInterface;
import descent.building.compiler.ILinkCommand;
import descent.building.compiler.IResponseInterpreter;
import descent.building.compiler.BuildResponse;

import static descent.building.IDescentBuilderConstants.*;
import static descent.internal.building.compiler.IDmdCompilerConstants.*;

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
		 * @see descent.launching.compiler.IExecutableCommand#getCommand()
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
            if(null != importPaths)
            {
    			for(File path : importPaths)
    			{
    				buf.append("-I");
    				buf.append(path.getPath());
    				buf.append(" ");
    			}
            }
            if(null != importExpPaths)
            {
    			for(File path : importExpPaths)
    			{
    				buf.append("-J");
    				buf.append(path.getPath());
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
            if(null != debugIdents)
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
            if(null != versionIdents)
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
			
			// Add the files to compile
			for(File path : files)
			{
				buf.append(path.getPath());
				buf.append(" ");
			}
			
            // TODO if the buffer is over a certain length, use a response file
            // instead
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
		 * @see descent.launching.compiler.IExecutableCommand#getCommand()
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
				System.out.println("OUT => " + line);
			
            /*
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
            */
		}
		
		/* (non-Javadoc)
		 * @see descent.launching.compiler.IResponseInterpreter#interpretError(java.lang.String)
		 */
		public void interpretError(String line)
		{
            if(DEBUG)
                System.out.println("ERR => " + line);
            
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
	// UI Options
	
	private static final CompilerOption[] uiOptions;
	
	static
	{
	    final String GROUP_FEATURES = "Features";
	    final String GROUP_WARNINGS = "Warnings";
	    final String GROUP_OPTIMIZATION = "Optimization";
	    
	    // TODO file options for header & doc generation
	    uiOptions = new CompilerOption[]
	    {	        
	        //------------------------------------------------------------------
	        // Features
	        
            new BooleanOption(
                    ATTR_ADD_DEBUG_INFO,
                    true,
                    "Add debugging symbols",
                    GROUP_FEATURES,
                    "-g",
                    "",
                    "Adds debugging symbols. These make the generated objects " +
                    "slightly larger but is needed to use a debugger with the " +
                    "program."),
                    
            new BooleanOption(
                    ATTR_DISABLE_ASSERTS,
                    false,
                    "Release mode (disable asserts & contracts)",
                    GROUP_FEATURES,
                    "-release",
                    "",
                    "Turns off assert() statements in the code, in {} and out {} " +
                    "blocks on functions, and checking for array bounds errors. This " +
                    "makes the code run faster, so is a good choice for releasing the " +
                    "application, but are often useful for development."),
                    
                    
            new BooleanOption(
                    ATTR_ADD_UNITTESTS,
                    false,
                    "Add unit tests",
                    GROUP_FEATURES,
                    "-unittest",
                    "",
                    "Adds code so that unittest {} blocks in your code are run before " +
                    "the program launches and enables version(unitttest) {} blocks."),
                            
            new BooleanOption(
                    ATTR_INSTRUMENT_FOR_COVERAGE,
                    false,
                    "Instrument for coverage analysis",
                    GROUP_FEATURES,
                    "-cov",
                    "",
                    "Adds code to the generated objects so that they will generate " +
                    "a file containing code coverage information after the program " +
                    "has been run. This is useful for seeing if unit tests execute " +
                    "all paths in your code."),
                                    
            new BooleanOption(
                    ATTR_INSTRUMENT_FOR_PROFILE,
                    false,
                    "Instrument for profiling",
                    GROUP_FEATURES,
                    "-profile",
                    "",
                    "Adds code to the generated objects so they can be prodiled. " +
                    "This helps find bottlenecks that could be slowing down your " +
                    "application."),
	        
	        //------------------------------------------------------------------
	        // Warnings
	        
	        new BooleanOption(
	                ATTR_ALLOW_DEPRECATED,
	                true,
	                "Allow deprecated code",
	                GROUP_WARNINGS,
	                "-d",
	                "",
	                "Allows code marked with the \"deprecated\" tags to be included " +
	                "in your program."),
	        
	        new BooleanOption(
	                ATTR_SHOW_WARNINGS,
                    false,
                    "Show warnings",
                    GROUP_WARNINGS,
                    "-w",
                    "",
                    "Adds warnings for potentially unsafe or error-prone code. In " +
                    "DMD, if warnings are encountered, the program will not be " +
                    "compiled."),
	        
              
	        //------------------------------------------------------------------
	        // Optimization
	        
            new BooleanOption(
                    ATTR_OPTIMIZE_CODE,
                    false,
                    "Optimize code",
                    GROUP_OPTIMIZATION,
                    "-O",
                    "",
                    "Optimizes the generated code for best efficiency."),
            
            new BooleanOption(
                    ATTR_INLINE_CODE,
                    false,
                    "Inline functions",
                    GROUP_OPTIMIZATION,
                    "-inline",
                    "",
                    "Allows inlining of short functions for increased code efficiency. " +
                    "This may cause issues with some debuggers."),
	        
            new BooleanOption(
                    ATTR_NOFLOAT,
                    false,
                    "Don't generate __fltused reference",
                    GROUP_OPTIMIZATION,
                    "-nofloat",
                    "",
                    "Prevents the emission of the __fltused reference in object files, " +
                    "even if floating point code is present. This is useful in library " +
                    "files."),
	    };
	}
	
	//--------------------------------------------------------------------------
	// Interface implementation
	
	/* (non-Javadoc)
	 * @see descent.launching.compiler.ICompilerInterface#createCompileCommand()
	 */
	public ICompileCommand createCompileCommand()
	{
		return new DmdCompileCommand();
	}

	/* (non-Javadoc)
	 * @see descent.launching.compiler.ICompilerInterface#createLinkCommand()
	 */
	public ILinkCommand createLinkCommand()
	{
		return new DmdLinkCommand();
	}
	
	/* (non-Javadoc)
	 * @see descent.launching.compiler.ICompilerInterface#createCompileResponseInterpreter()
	 */
	public IResponseInterpreter createCompileResponseInterpreter()
	{
		return new DmdResponseInterpreter();
	}

	/* (non-Javadoc)
	 * @see descent.launching.compiler.ICompilerInterface#createLinkResponseInterpreter()
	 */
	public IResponseInterpreter createLinkResponseInterpreter()
	{
		return new DmdResponseInterpreter();
	}

    public CompilerOption[] getOptions()
    {
        return uiOptions;
    }
}
