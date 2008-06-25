package descent.internal.building.compiler.ui;

import descent.building.compiler.ui.BooleanOption;
import static descent.internal.building.compiler.IDmdCompilerConstants.*;

/* package */ interface DmdfeUIOptions extends CompilerUIOptions
{
    /* package */ static final class AllowDeprecatedOption extends BooleanOption
    {
        protected AllowDeprecatedOption(String onText)
        {
            super(ATTR_ALLOW_DEPRECATED, true, CompilerUIMessages.DmdfeUIOptions_deprecated_label,
                    GROUP_WARNINGS, onText, NO_TEXT);
        }

        @Override
        public String getHelpText()
        {
            return CompilerUIMessages.DmdfeUIOptions_deprecated_help;
        }
    }

    /* package */ static final class ShowWarningsOption extends BooleanOption
    {
        protected ShowWarningsOption(String onText)
        {
            super(ATTR_SHOW_WARNINGS, false, CompilerUIMessages.DmdfeUIOptions_warnings_label, GROUP_WARNINGS,
                    onText, NO_TEXT);
        }

        @Override
        public String getHelpText()
        {
            return CompilerUIMessages.DmdfeUIOptions_warnings_help;
        }
    }

    /* package */ static final class OptimizeCodeOption extends BooleanOption
    {
        protected OptimizeCodeOption(String onText)
        {
            super(ATTR_OPTIMIZE_CODE, false, CompilerUIMessages.DmdfeUIOptions_optimize_label,
                    GROUP_GENERATED_CODE, onText, NO_TEXT);
        }

        @Override
        public String getHelpText()
        {
            return CompilerUIMessages.DmdfeUIOptions_optimize_help;
        }
    }

    /* package */ static final class InlineCodeOption extends BooleanOption
    {
        protected InlineCodeOption(String onText)
        {
            super(ATTR_INLINE_CODE, false, CompilerUIMessages.DmdfeUIOptions_inline_label,
                    GROUP_GENERATED_CODE, onText, NO_TEXT);
        }

        @Override
        public String getHelpText()
        {
            return CompilerUIMessages.DmdfeUIOptions_inline_help;
        }
    }
}
