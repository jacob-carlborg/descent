package descent.internal.building.compiler.ui;

import descent.building.compiler.BooleanOption;
import descent.building.compiler.CompilerOption;
import descent.building.compiler.EnumOption;

import static descent.building.IDescentBuilderConstants.*;
import static descent.internal.building.compiler.IGdcCompilerConstants.*;

public class GdcUIOptions extends CompilerUIOptions
{
    public static final CompilerOption OPTION_ADD_DEBUG_INFO = 
        new BooleanOption
        (
                ATTR_ADD_DEBUG_INFO,
                true,
                "Add debugging symbols",
                GROUP_FEATURES,
                "-g",
                "",
                "Adds debugging symbols. These make the generated objects " +
                "slightly larger but is needed to use a debugger with the " +
                "program."
        );
    
    
    public static final CompilerOption OPTION_DISABLE_ASSERTS =
        new BooleanOption
        (
                ATTR_DISABLE_ASSERTS,
                false,
                "Release mode",
                GROUP_FEATURES,
                "-frelease",
                "",
                "Turns off assert() statements in the code, in {} and out {} " +
                "blocks on functions, and checking for array bounds errors. This " +
                "makes the code run faster, so is a good choice for releasing the " +
                "application, but are often useful for development."
        );
    
    public static final CompilerOption OPTION_ADD_UNITTESTS =
        new BooleanOption
        (
                ATTR_ADD_UNITTESTS,
                false,
                "Add unit tests",
                GROUP_FEATURES,
                "-funittest",
                "",
                "Adds code so that unittest {} blocks in your code are run before " +
                "the program launches and enables version(unitttest) {} blocks."
        );
    
    public static final CompilerOption OPTION_ALLOW_DEPRECATED =
        new BooleanOption
        (
                ATTR_ALLOW_DEPRECATED,
                true,
                "Allow deprecated code",
                GROUP_WARNINGS,
                "-fdeprecated",
                "",
                "Allows code marked with the \"deprecated\" tags to be included " +
                "in your program."
        );
    
    public static final CompilerOption OPTION_SHOW_WARNINGS =
        new BooleanOption
        (
                ATTR_SHOW_WARNINGS,
                false,
                "Show warnings",
                GROUP_WARNINGS,
                "-Wall",
                "",
                "Adds warnings for potentially unsafe or error-prone code. In " +
                "DMD, if warnings are encountered, the program will not be " +
                "compiled."
        );
    
    public static final CompilerOption OPTION_OPTIMIZE_CODE =
        new BooleanOption
        (
                ATTR_OPTIMIZE_CODE,
                false,
                "Optimize code",
                GROUP_GENERATED_CODE,
                "-O3",
                "",
                "Optimizes the generated code for best efficiency."
        );
    
    public static final CompilerOption OPTION_INLINE_CODE =
        new BooleanOption
        (
                ATTR_INLINE_CODE,
                false,
                "Inline functions",
                GROUP_GENERATED_CODE,
                "-finline-functions",
                "",
                "Allows inlining of short functions for increased code efficiency. " +
                "This may cause issues with some debuggers."
         );
    
    public static final CompilerOption OPTION_ALL_SOURCES =
        new BooleanOption
        (
                ATTR_ALL_SOURCES,
                false,
                "All sources",
                GROUP_COMPATABILITY,
                "-fall-sources",
                "",
                "For each source file on the command line, semantically process each " +
                "file preceding it.  Use this if compilation errors occur due to " +
                "complicated circular module references.  This will slow compilation " +
                "noticeably."
         );
    
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
                },
                "<p>Controls whether or not template code is emitted:</p>" +
                "<li>Normal -- Emit templates, expecting multiple copies to be merged by the linker.</li>" +
                "<li>Auto -- For targets that support templates, the \"all\" mode is used.  Otherwise, the \"private\" mode is used.</li>" +
                "<li>Private -- Emit templates, but make them private to the translation unit.  The executable will have multiple copies of code and data.</li>" +
                "<li>All -- Emit all template instances with public visibility.  Do this even if they would not normally be emitted.</li>" +
                "<li>None -- Do not emit templates at all.</li>"
        );
}
