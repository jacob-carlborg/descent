package descent.internal.building.compiler;

import static descent.internal.building.compiler.ui.GdcUIOptions.*;

import java.util.ArrayList;
import java.util.List;

import descent.building.compiler.CompilerOption;

public final class GdcCompilerInterface extends DmdfeCompilerInterface
{
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
        
        // Compatibility
        options.add(OPTION_ALL_SOURCES);
        
        // Features
        options.add(OPTION_ADD_DEBUG_INFO);
        options.add(OPTION_DISABLE_ASSERTS);
        options.add(OPTION_ADD_UNITTESTS);
        
        // Generated code
        options.add(OPTION_OPTIMIZE_CODE);
        options.add(OPTION_INLINE_CODE);
        options.add(OPTION_EMIT_TEMPLATES);
        
        // Warnings
        options.add(OPTION_ALLOW_DEPRECATED);
        options.add(OPTION_SHOW_WARNINGS);
        
        uiOptions = options.toArray(new CompilerOption[options.size()]);
    }
}
