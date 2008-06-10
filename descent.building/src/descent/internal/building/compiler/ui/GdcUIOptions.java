package descent.internal.building.compiler.ui;

import descent.building.compiler.BooleanOption;
import descent.building.compiler.CompilerOption;
import descent.building.compiler.EnumOption;

import static descent.internal.building.compiler.IGdcCompilerConstants.*;

public interface GdcUIOptions extends DmdfeUIOptions
{
    public static final CompilerOption OPTION_ADD_DEBUG_INFO = new AddDebugInfoOption("-g");
    public static final CompilerOption OPTION_DISABLE_ASSERTS = new DisableAssertsOption("-frelease");
    public static final CompilerOption OPTION_ADD_UNITTESTS = new AddUnittestsOption("-funittest");
    
    public static final CompilerOption OPTION_OPTIMIZE_CODE = new OptimizeCodeOption("-O3");
    public static final CompilerOption OPTION_INLINE_CODE = new InlineCodeOption("-finline-functions");
    public static final CompilerOption OPTION_ALLOW_DEPRECATED = new AllowDeprecatedOption("-fdeprecated");
    public static final CompilerOption OPTION_SHOW_WARNINGS = new ShowWarningsOption("-Wall");
    
    public static final CompilerOption OPTION_ALL_SOURCES =
        new BooleanOption
        (
                ATTR_ALL_SOURCES,
                false,
                "All sources",
                GROUP_COMPATABILITY,
                "-fall-sources",
                ""
         )
    {
        @Override
        public String getHelpText()
        {
            return "For each source file on the command line, semantically process each " +
            "file preceding it.  Use this if compilation errors occur due to " +
            "complicated circular module references.  This will slow compilation " +
            "noticeably.";
        }
    };
    
    public static final CompilerOption OPTION_EMIT_TEMPLATES =
        new EnumOption
        (
                ATTR_ALL_SOURCES,
                EMIT_TEMPLATES_NORMAL,
                "Emit templates",
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
                    "Normal",
                    "Auto",
                    "Private",
                    "All",
                    "None"
                }
        )
    {
        @Override
        public String getHelpText()
        {
            return "<p>Controls whether or not template code is emitted:</p>" +
            "<li>Normal -- Emit templates, expecting multiple copies to be merged by the linker.</li>" +
            "<li>Auto -- For targets that support templates, the \"all\" mode is used.  Otherwise, the \"private\" mode is used.</li>" +
            "<li>Private -- Emit templates, but make them private to the translation unit.  The executable will have multiple copies of code and data.</li>" +
            "<li>All -- Emit all template instances with public visibility.  Do this even if they would not normally be emitted.</li>" +
            "<li>None -- Do not emit templates at all.</li>";
        }
    };
}
