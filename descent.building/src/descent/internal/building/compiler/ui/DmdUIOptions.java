package descent.internal.building.compiler.ui;

import static descent.internal.building.compiler.IDmdCompilerConstants.ATTR_NOFLOAT;
import descent.building.compiler.ui.BooleanOption;
import descent.building.compiler.ui.CompilerOption;

public interface DmdUIOptions extends DmdfeUIOptions
{
    public static final CompilerOption OPTION_ADD_DEBUG_INFO = new AddDebugInfoOption(CompilerUIMessages.DmdUIOptions_debug_info_switch);
    public static final CompilerOption OPTION_DISABLE_ASSERTS = new DisableAssertsOption(CompilerUIMessages.DmdUIOptions_release_switch);
    public static final CompilerOption OPTION_ADD_UNITTESTS = new AddUnittestsOption(CompilerUIMessages.DmdUIOptions_unitttest_switch);
    public static final CompilerOption OPTION_INSTRUMENT_FOR_COVERAGE = new InstrumentForCoverageOption(CompilerUIMessages.DmdUIOptions_coverage_switch);
    public static final CompilerOption OPTION_INSTRUMENT_FOR_PROFILE = new InstrumentForProfileOption(CompilerUIMessages.DmdUIOptions_profile_switch);
    
    public static final CompilerOption OPTION_OPTIMIZE_CODE = new OptimizeCodeOption(CompilerUIMessages.DmdUIOptions_optimize_switch);
    public static final CompilerOption OPTION_INLINE_CODE = new InlineCodeOption(CompilerUIMessages.DmdUIOptions_inline_switch);
    public static final CompilerOption OPTION_NOFLOAT = new BooleanOption
        (ATTR_NOFLOAT, false, CompilerUIMessages.DmdUIOptions_nofloat_label,
         GROUP_GENERATED_CODE, CompilerUIMessages.DmdUIOptions_nofloat_switch, NO_TEXT)
    {
        @Override
        public String getHelpText()
        {
            return CompilerUIMessages.DmdUIOptions_nofloat_help;
        }
    };
    
    public static final CompilerOption OPTION_ALLOW_DEPRECATED = new AllowDeprecatedOption(CompilerUIMessages.DmdUIOptions_deprecated_switch);
    public static final CompilerOption OPTION_SHOW_WARNINGS = new ShowWarningsOption(CompilerUIMessages.DmdUIOptions_warnings_switch);
}
