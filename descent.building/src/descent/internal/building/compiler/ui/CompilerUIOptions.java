package descent.internal.building.compiler.ui;

import static descent.building.IDescentBuilderConstants.*;
import descent.building.compiler.ui.BooleanOption;

/* package */ interface CompilerUIOptions
{
    public static final String GROUP_FEATURES = CompilerUIMessages.CompilerUIOptions_group_features;
    public static final String GROUP_WARNINGS = CompilerUIMessages.CompilerUIOptions_group_warnings;
    public static final String GROUP_GENERATED_CODE = CompilerUIMessages.CompilerUIOptions_group_generated_code;
    public static final String GROUP_LINKER = CompilerUIMessages.CompilerUIOptions_group_linker;
    public static final String GROUP_COMPATABILITY = CompilerUIMessages.CompilerUIOptions_group_compatibility;
    
    public static final String NO_TEXT = ""; //$NON-NLS-1$
    
    /* package */ static final class AddDebugInfoOption extends BooleanOption
    {
        protected AddDebugInfoOption(String onText)
        {
            super(ATTR_ADD_DEBUG_INFO, true, CompilerUIMessages.CompilerUIOptions_debug_info_label,
                    GROUP_FEATURES, onText, NO_TEXT);
        }

        @Override
        public String getHelpText()
        {
            return CompilerUIMessages.CompilerUIOptions_debug_info_help;
        }
    }

    /* package */ static final class DisableAssertsOption extends BooleanOption
    {
        protected DisableAssertsOption(String onText)
        {
            super(ATTR_DISABLE_ASSERTS, false, CompilerUIMessages.CompilerUIOptions_release_label, GROUP_FEATURES,
                    onText, NO_TEXT);
        }

        @Override
        public String getHelpText()
        {
            return CompilerUIMessages.CompilerUIOptions_release_help;
        }
    }

    /* package */ static final class AddUnittestsOption extends BooleanOption
    {
        protected AddUnittestsOption(String onText)
        {
            super(ATTR_ADD_UNITTESTS, false, CompilerUIMessages.CompilerUIOptions_unittest_label, GROUP_FEATURES,
                    onText, NO_TEXT);
        }

        @Override
        public String getHelpText()
        {
            return CompilerUIMessages.CompilerUIOptions_unittest_help;
        }
    }

    /* package */ static final class InstrumentForCoverageOption extends BooleanOption
    {
        protected InstrumentForCoverageOption(String onText)
        {
            super(ATTR_INSTRUMENT_FOR_COVERAGE, false,
                    CompilerUIMessages.CompilerUIOptions_coverage_label, GROUP_FEATURES, onText,
                    NO_TEXT);
        }

        @Override
        public String getHelpText()
        {
            return CompilerUIMessages.CompilerUIOptions_coverage_help;
        }
    }

    /* package */ static final class InstrumentForProfileOption extends BooleanOption
    {
        protected InstrumentForProfileOption(String onText)
        {
            super(ATTR_INSTRUMENT_FOR_PROFILE, false,
                    CompilerUIMessages.CompilerUIOptions_profile_label, GROUP_FEATURES, onText, NO_TEXT);
        }

        @Override
        public String getHelpText()
        {
            return CompilerUIMessages.CompilerUIOptions_profile_help;
        }
    }
}
