package descent.internal.building.compiler;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import descent.building.compiler.IResponseInterpreter;
import descent.building.compiler.ui.CompilerOption;

import static descent.internal.building.compiler.ui.DmdUIOptions.*;

public final class DmdCompilerInterface extends DmdfeCompilerInterface
{
	protected static final boolean DEBUG = true;
	
	//--------------------------------------------------------------------------
	// Response interpreter
	protected static class DmdResponseInterpreter implements IResponseInterpreter
	{
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
			// TODO interpret(line);
		}
	}
	
	//--------------------------------------------------------------------------
	// UI Options
	
	private CompilerOption[] uiOptions;
	
	public synchronized final CompilerOption[] getOptions()
    {
        if(null == uiOptions)
            initializeUIOptions();
        return uiOptions;
    }
	
	private final void initializeUIOptions()
	{
	    List<CompilerOption> options = new ArrayList<CompilerOption>();
	    
	    // Features
	    options.add(OPTION_ADD_DEBUG_INFO);
	    options.add(OPTION_DISABLE_ASSERTS);
	    options.add(OPTION_ADD_UNITTESTS);
	    options.add(OPTION_INSTRUMENT_FOR_COVERAGE);
	    options.add(OPTION_INSTRUMENT_FOR_PROFILE);
	    
	    // Generated code
	    options.add(OPTION_OPTIMIZE_CODE);
	    options.add(OPTION_INLINE_CODE);
	    options.add(OPTION_NOFLOAT);
	    
	    // Warnings
	    options.add(OPTION_ALLOW_DEPRECATED);
	    options.add(OPTION_SHOW_WARNINGS);
	    
	    uiOptions = options.toArray(new CompilerOption[options.size()]);
	}
}
