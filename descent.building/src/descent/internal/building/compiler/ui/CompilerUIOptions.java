package descent.internal.building.compiler.ui;

import static descent.building.IDescentBuilderConstants.*;
import descent.building.compiler.BooleanOption;

/* package */ interface CompilerUIOptions
{
    public static final String GROUP_FEATURES = "Features";
    public static final String GROUP_WARNINGS = "Warnings";
    public static final String GROUP_GENERATED_CODE = "Generated code";
    public static final String GROUP_LINKER = "Linker";
    public static final String GROUP_COMPATABILITY = "Compatability";
    
    /* package */ static final class AddDebugInfoOption extends BooleanOption
    {
        protected AddDebugInfoOption(String onText)
        {
            super(ATTR_ADD_DEBUG_INFO, true, "Add debugging symbols",
                    GROUP_FEATURES, onText, "");
        }

        @Override
        public String getHelpText()
        {
            return "Adds debugging symbols. These make the generated objects "
                    + "slightly larger but is needed to use a debugger with the "
                    + "program.";
        }
    }

    /* package */ static final class DisableAssertsOption extends BooleanOption
    {
        protected DisableAssertsOption(String onText)
        {
            super(ATTR_DISABLE_ASSERTS, false, "Release mode", GROUP_FEATURES,
                    onText, "");
        }

        @Override
        public String getHelpText()
        {
            return "Turns off assert() statements in the code, in {} and out {} "
                    + "blocks on functions, and checking for array bounds errors. This "
                    + "makes the code run faster, so is a good choice for releasing the "
                    + "application, but are often useful for development.";
        }
    }

    /* package */ static final class AddUnittestsOption extends BooleanOption
    {
        protected AddUnittestsOption(String onText)
        {
            super(ATTR_ADD_UNITTESTS, false, "Add unit tests", GROUP_FEATURES,
                    onText, "");
        }

        @Override
        public String getHelpText()
        {
            return "Adds code so that unittest {} blocks in your code are run before "
                    + "the program launches and enables version(unitttest) {} blocks.";
        }
    }

    /* package */ static final class InstrumentForCoverageOption extends BooleanOption
    {
        protected InstrumentForCoverageOption(String onText)
        {
            super(ATTR_INSTRUMENT_FOR_COVERAGE, false,
                    "Instrument for coverage analysis", GROUP_FEATURES, onText,
                    "");
        }

        @Override
        public String getHelpText()
        {
            return "Adds code to the generated objects so that they will generate "
                    + "a file containing code coverage information after the program "
                    + "has been run. This is useful for seeing if unit tests execute "
                    + "all paths in your code.";
        }
    }

    /* package */ static final class InstrumentForProfileOption extends BooleanOption
    {
        protected InstrumentForProfileOption(String onText)
        {
            super(ATTR_INSTRUMENT_FOR_PROFILE, false,
                    "Instrument for profiling", GROUP_FEATURES, onText, "");
        }

        @Override
        public String getHelpText()
        {
            return "Adds code to the generated objects so they can be profiled. "
                    + "This helps find bottlenecks that could be slowing down your "
                    + "application.";
        }
    }
}
