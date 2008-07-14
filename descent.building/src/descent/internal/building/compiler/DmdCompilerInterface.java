package descent.internal.building.compiler;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import descent.building.compiler.IBuildManager;
import descent.building.compiler.ICompileManager;
import descent.building.compiler.IResponseInterpreter;
import descent.building.compiler.ui.CompilerOption;

import static descent.internal.building.compiler.ui.DmdUIOptions.*;

public final class DmdCompilerInterface extends DmdfeCompilerInterface
{	
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

    @Override
    public ICompileManager getCompileManager(IBuildManager buildManager)
    {
        return new DmdCompileManager();
    }
}
