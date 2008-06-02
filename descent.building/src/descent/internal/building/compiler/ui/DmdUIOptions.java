package descent.internal.building.compiler.ui;

import descent.building.compiler.BooleanOption;
import descent.building.compiler.CompilerOption;

import static descent.building.IDescentBuilderConstants.*;
import static descent.internal.building.compiler.IDmdCompilerConstants.*;

public final class DmdUIOptions extends CompilerUIOptions
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
                "-release",
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
                "-unittest",
                "",
                "Adds code so that unittest {} blocks in your code are run before " +
                "the program launches and enables version(unitttest) {} blocks."
        );
    
    
    public static final CompilerOption OPTION_INSTRUMENT_FOR_COVERAGE =
    new BooleanOption
    (
            ATTR_INSTRUMENT_FOR_COVERAGE,
            false,
            "Instrument for coverage analysis",
            GROUP_FEATURES,
            "-cov",
            "",
            "Adds code to the generated objects so that they will generate " +
            "a file containing code coverage information after the program " +
            "has been run. This is useful for seeing if unit tests execute " +
            "all paths in your code."
    );
    
    public static final CompilerOption OPTION_INSTRUMENT_FOR_PROFILE =
        new BooleanOption
        (
                ATTR_INSTRUMENT_FOR_PROFILE,
                false,
                "Instrument for profiling",
                GROUP_FEATURES,
                "-profile",
                "",
                "Adds code to the generated objects so they can be profiled. " +
                "This helps find bottlenecks that could be slowing down your " +
                "application."
        );
    
    public static final CompilerOption OPTION_ALLOW_DEPRECATED =
        new BooleanOption
        (
                ATTR_ALLOW_DEPRECATED,
                true,
                "Allow deprecated code",
                GROUP_WARNINGS,
                "-d",
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
                "-w",
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
                "-O",
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
                "-inline",
                "",
                "Allows inlining of short functions for increased code efficiency. " +
                "This may cause issues with some debuggers."
         );
    
    public static final CompilerOption OPTION_NOFLOAT =
        new BooleanOption
        (
                ATTR_NOFLOAT,
                false,
                "Don't generate __fltused reference",
                GROUP_GENERATED_CODE,
                "-nofloat",
                "",
                "Prevents the emission of the __fltused reference in object files, " +
                "even if floating point code is present. This is useful in library " +
                "files."
        );
}
