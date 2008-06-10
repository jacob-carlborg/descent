package descent.internal.building.compiler.ui;

import static descent.internal.building.compiler.IDmdCompilerConstants.ATTR_NOFLOAT;
import descent.building.compiler.BooleanOption;
import descent.building.compiler.CompilerOption;

public interface DmdUIOptions extends DmdfeUIOptions
{
    public static final CompilerOption OPTION_ADD_DEBUG_INFO = new AddDebugInfoOption("-g");
    public static final CompilerOption OPTION_DISABLE_ASSERTS = new DisableAssertsOption("-release");
    public static final CompilerOption OPTION_ADD_UNITTESTS = new AddUnittestsOption("-unittest");
    public static final CompilerOption OPTION_INSTRUMENT_FOR_COVERAGE = new InstrumentForCoverageOption("-cov");
    public static final CompilerOption OPTION_INSTRUMENT_FOR_PROFILE = new InstrumentForProfileOption("-profile");
    
    public static final CompilerOption OPTION_OPTIMIZE_CODE = new OptimizeCodeOption("-O");
    public static final CompilerOption OPTION_INLINE_CODE = new InlineCodeOption("-inline");
    public static final CompilerOption OPTION_NOFLOAT = new BooleanOption
        (ATTR_NOFLOAT, false, "Don't generate __fltused reference",
         GROUP_GENERATED_CODE, "-nofloat", "")
    {
        @Override
        public String getHelpText()
        {
            return "Prevents the emission of the __fltused reference in object files, "
                    + "even if floating point code is present. This is useful in library "
                    + "files.";
        }
    };
    
    public static final CompilerOption OPTION_ALLOW_DEPRECATED = new AllowDeprecatedOption("-d");
    public static final CompilerOption OPTION_SHOW_WARNINGS = new ShowWarningsOption("-w");
}
