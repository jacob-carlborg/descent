package descent.internal.building.compiler.ui;

import descent.building.compiler.BooleanOption;
import static descent.internal.building.compiler.IDmdCompilerConstants.*;

/* package */ interface DmdfeUIOptions extends CompilerUIOptions
{
    /* package */ static final class AllowDeprecatedOption extends BooleanOption
    {
        protected AllowDeprecatedOption(String onText)
        {
            super(ATTR_ALLOW_DEPRECATED, true, "Allow deprecated code",
                    GROUP_WARNINGS, onText, "");
        }

        @Override
        public String getHelpText()
        {
            return "Allows code marked with the \"deprecated\" tags to be included "
                    + "in your program.";
        }
    }

    /* package */ static final class ShowWarningsOption extends BooleanOption
    {
        protected ShowWarningsOption(String onText)
        {
            super(ATTR_SHOW_WARNINGS, false, "Show warnings", GROUP_WARNINGS,
                    onText, "");
        }

        @Override
        public String getHelpText()
        {
            return "Adds warnings for potentially unsafe or error-prone code. In "
                    + "DMD, if warnings are encountered, the program will not be "
                    + "compiled.";
        }
    }

    /* package */ static final class OptimizeCodeOption extends BooleanOption
    {
        protected OptimizeCodeOption(String onText)
        {
            super(ATTR_OPTIMIZE_CODE, false, "Optimize code",
                    GROUP_GENERATED_CODE, onText, "");
        }

        @Override
        public String getHelpText()
        {
            return "Optimizes the generated code for best efficiency.";
        }
    }

    /* package */ static final class InlineCodeOption extends BooleanOption
    {
        protected InlineCodeOption(String onText)
        {
            super(ATTR_INLINE_CODE, false, "Inline functions",
                    GROUP_GENERATED_CODE, onText, "");
        }

        @Override
        public String getHelpText()
        {
            return "Allows inlining of short functions for increased code efficiency. "
                    + "This may cause issues with some debuggers.";
        }
    }
}
