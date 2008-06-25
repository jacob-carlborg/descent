package descent.internal.building.compiler.ui;

import descent.building.compiler.ui.BooleanOption;
import descent.building.compiler.ui.CompilerOption;
import descent.building.compiler.ui.EnumOption;

import static descent.internal.building.compiler.IGdcCompilerConstants.*;

public interface GdcUIOptions extends DmdfeUIOptions
{
    public static final CompilerOption OPTION_ADD_DEBUG_INFO = new AddDebugInfoOption(CompilerUIMessages.GdcUIOptions_debug_info_switch);
    public static final CompilerOption OPTION_DISABLE_ASSERTS = new DisableAssertsOption(CompilerUIMessages.GdcUIOptions_release_switch);
    public static final CompilerOption OPTION_ADD_UNITTESTS = new AddUnittestsOption(CompilerUIMessages.GdcUIOptions_unittest_switch);
    
    public static final CompilerOption OPTION_OPTIMIZE_CODE = new OptimizeCodeOption(CompilerUIMessages.GdcUIOptions_optimize_switch);
    public static final CompilerOption OPTION_INLINE_CODE = new InlineCodeOption(CompilerUIMessages.GdcUIOptions_inline_switch);
    public static final CompilerOption OPTION_ALLOW_DEPRECATED = new AllowDeprecatedOption(CompilerUIMessages.GdcUIOptions_deprecated_switch);
    public static final CompilerOption OPTION_SHOW_WARNINGS = new ShowWarningsOption(CompilerUIMessages.GdcUIOptions_warnings_switch);
    
    public static final CompilerOption OPTION_ALL_SOURCES =
        new BooleanOption
        (
                ATTR_ALL_SOURCES,
                false,
                CompilerUIMessages.GdcUIOptions_all_sources_label,
                GROUP_COMPATABILITY,
                CompilerUIMessages.GdcUIOptions_all_sources_switch,
                NO_TEXT
         )
    {
        @Override
        public String getHelpText()
        {
            return CompilerUIMessages.GdcUIOptions_all_sources_help;
        }
    };
    
    public static final CompilerOption OPTION_EMIT_TEMPLATES =
        new EnumOption
        (
                ATTR_ALL_SOURCES,
                EMIT_TEMPLATES_NORMAL,
                CompilerUIMessages.GdcUIOptions_emit_templates_label,
                GROUP_GENERATED_CODE,
                new String[]
                {
                    EMIT_TEMPLATES_NORMAL,
                    EMIT_TEMPLATES_AUTO,
                    EMIT_TEMPLATES_PRIVATE,
                    EMIT_TEMPLATES_ALL,
                    EMIT_TEMPLATES_NONE,
                },
                new String[]
                {
                    CompilerUIMessages.GdcUIOptions_emit_templates_option_normal,
                    CompilerUIMessages.GdcUIOptions_emit_templates_option_auto,
                    CompilerUIMessages.GdcUIOptions_emit_templates_option_private,
                    CompilerUIMessages.GdcUIOptions_emit_templates_option_all,
                    CompilerUIMessages.GdcUIOptions_emit_templates_option_none
                }
        )
    {
        @Override
        public String getHelpText()
        {
            return CompilerUIMessages.GdcUIOptions_emit_templates_help;
        }
    };
}
