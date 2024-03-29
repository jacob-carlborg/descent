package descent.core.formatter;

import java.util.Map;

import descent.core.JavaCore;
import descent.internal.formatter.DefaultCodeFormatterOptions;
import descent.internal.formatter.Alignment;

/**
 * Constants used to set up the options of the code formatter.
 * <p>
 * This class is not intended to be instantiated or subclassed by clients.
 * </p>
 * 
 * @since 3.0
 */
public class DefaultCodeFormatterConstants {
	
	/**
	 * <pre>
	 * FORMATTER / The wrapping is done by indenting by one compare to the current indentation.
	 * </pre>
	 * @since 3.0
	 */
	public static final int INDENT_BY_ONE= 2;
	
	/**
	 * <pre>
	 * FORMATTER / The wrapping is done by using the current indentation.
	 * </pre>
	 * @since 3.0
	 */
	public static final int INDENT_DEFAULT= 0;
	/**
	 * <pre>
	 * FORMATTER / The wrapping is done by indenting on column under the splitting location.
	 * </pre>
	 * @since 3.0
	 */
	public static final int INDENT_ON_COLUMN = 1;
	
	/*
	 * Private constants. Not in javadoc
	 */
	private static final IllegalArgumentException WRONG_ARGUMENT = new IllegalArgumentException();
	
	public static final String PROFILE_DESCENT_DEFAULTS = "descent.ui.formatter.defaults.descent_defaults";
	public static final String PROFILE_JAVA_DEFAULTS = "descent.ui.formatter.defaults.java_defaults";
	public static final String PROFILE_C_SHARP_DEFAULTS = "decent.ui.formatter.defaults.c_sharp_defaults";
	public static final String DEFAULT_PROFILE = PROFILE_DESCENT_DEFAULTS;
	
	// Boolean mappings
	public static final String FALSE = "false";
	public static final String TRUE = "true";
	
	// Brace positions
	public static final String END_OF_LINE = "end_of_line";
	public static final String NEXT_LINE = "next_line";
	public static final String NEXT_LINE_SHIFTED = "next_line_shifted";
	
	// Indentation style
	public static final String NO_INDENTATION = "no_indentation";
	public static final String INDENT_NORMAL = "indent_normal";
	public static final String INDENT_HEADING_BACK = "indent_heading_back";
	
	// Indentation type
	public static final String MIXED = "mixed";
	public static final String SPACE = JavaCore.SPACE;
	public static final String TAB = JavaCore.TAB;
	
	// Alignment options
	/* TODO redo alignment options to JDT-style in a later release, these are 
	        just the most obvious/simple ones. */ 
	
	// Don't wrap at all (leave long lines intact)
	public static final int DO_NOT_WRAP = 
		Alignment.M_NO_ALIGNMENT;
	
	// Wrap by the continuation indentation if there's a long line
	public static final int WRAP_ONLY_WHEN_NECESSARY = 
		Alignment.M_COMPACT_SPLIT;
	
	// Wrap to the column if there's a long line
	public static final int WRAP_ON_COLUMN =
		Alignment.M_INDENT_ON_COLUMN | Alignment.M_COMPACT_SPLIT;
	
	// Place one fragment per line
	public static final int WRAP_ONE_FRAGMENT_PER_LINE = 
		Alignment.M_FORCE | Alignment.M_INDENT_ON_COLUMN | 
		Alignment.M_NEXT_PER_LINE_SPLIT;
	
	// Formatter value names
	public static final String FORMATTER_BRACE_POSITION_FOR_FUNCTION_DECLARATION = JavaCore.PLUGIN_ID + ".formatter.brace_position_for_function_declaration";
	public static final String FORMATTER_BRACE_POSITION_FOR_POSTBLIT_DECLARATION = JavaCore.PLUGIN_ID + ".formatter.brace_position_for_postblit_declaration";
	public static final String FORMATTER_BRACE_POSITION_FOR_TYPE_DECLARATION = JavaCore.PLUGIN_ID + ".formatter.brace_position_for_type_declaration";
	public static final String FORMATTER_BRACE_POSITION_FOR_ENUM_DECLARATION = JavaCore.PLUGIN_ID + ".formatter.brace_position_for_enum_declaration";
	public static final String FORMATTER_BRACE_POSITION_FOR_TEMPLATE_DECLARATION = JavaCore.PLUGIN_ID + ".formatter.brace_position_for_template_declaration";
	public static final String FORMATTER_BRACE_POSITION_FOR_CONDITIONAL_DECLARATION = JavaCore.PLUGIN_ID + ".formatter.brace_position_for_conditional_declaration";
	public static final String FORMATTER_BRACE_POSITION_FOR_ALIGN_DECLARATION = JavaCore.PLUGIN_ID + ".formatter.brace_position_for_align_declaration";
	public static final String FORMATTER_BRACE_POSITION_FOR_CONDITIONAL_STATEMENT = JavaCore.PLUGIN_ID + ".formatter.brace_position_for_conditional_statement";
	public static final String FORMATTER_BRACE_POSITION_FOR_LOOP_STATEMENT = JavaCore.PLUGIN_ID + ".formatter.brace_position_for_loop_statement";
	public static final String FORMATTER_BRACE_POSITION_FOR_FUNCTION_LITERAL = JavaCore.PLUGIN_ID + ".formatter.brace_position_for_function_literal";
	public static final String FORMATTER_BRACE_POSITION_FOR_ANONYMOUS_TYPE = JavaCore.PLUGIN_ID + ".formatter.brace_position_for_anonymous_type";
	public static final String FORMATTER_BRACE_POSITION_FOR_SWITCH_STATEMENT = JavaCore.PLUGIN_ID + ".formatter.brace_position_for_switch_statement";
	public static final String FORMATTER_BRACE_POSITION_FOR_SWITCH_CASE = JavaCore.PLUGIN_ID + ".formatter.brace_position_for_switch_case";
	public static final String FORMATTER_BRACE_POSITION_FOR_TRY_CATCH_FINALLY = JavaCore.PLUGIN_ID + ".formatter.brace_position_for_try_catch_finally";
	public static final String FORMATTER_BRACE_POSITION_FOR_MODIFIERS = JavaCore.PLUGIN_ID + ".formatter.brace_position_for_modifiers";
	public static final String FORMATTER_BRACE_POSITION_FOR_SYNCHRONIZED_STATEMENT = JavaCore.PLUGIN_ID + ".formatter.brace_position_for_synchronized_statement";
	public static final String FORMATTER_BRACE_POSITION_FOR_WITH_STATEMENT = JavaCore.PLUGIN_ID + ".formatter.brace_position_for_with_statement";
	public static final String FORMATTER_BRACE_POSITION_FOR_SCOPE_STATEMENT = JavaCore.PLUGIN_ID + ".formatter.brace_position_for_scope_statement";
	public static final String FORMATTER_BRACE_POSITION_FOR_PRAGMAS = JavaCore.PLUGIN_ID + ".formatter.brace_position_for_pragmas";
	public static final String FORMATTER_BRACE_POSITION_FOR_UNITTEST = JavaCore.PLUGIN_ID + ".formatter.brace_position_for_unittest";
	public static final String FORMATTER_BRACE_POSITION_FOR_CLASS_INVARIANT = JavaCore.PLUGIN_ID + ".formatter.brace_position_for_class_invariant";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_FUNCTION_DECLARATION_PARAMETERS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_opening_paren_in_function_declaration_parameters";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_FUNCTION_TEMPLATE_ARGS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_opening_paren_in_function_template_args";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_FUNCTION_INVOCATION = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_opening_paren_in_function_invocation";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_CATCH = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_opening_paren_in_catch";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_FOR_LOOPS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_opening_paren_in_for_loops";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_WHILE_LOOPS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_opening_paren_in_while_loops";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_FOREACH_LOOPS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_opening_paren_in_foreach_loops";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_OUT_DECLARATION = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_opening_paren_in_out_declaration";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_SYNCHRONIZED_STATEMENT = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_opening_paren_in_synchronized_statement";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_SWITCH_STATEMENTS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_opening_paren_in_switch_statements";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_ALIGN_DECLARATIONS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_opening_paren_in_align_declarations";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_CLASS_TEMPLATE_PARAMS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_opening_paren_in_class_template_params";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_ASSERT_STATEMENTS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_opening_paren_in_assert_statements";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_VERSION_DEBUG = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_opening_paren_in_version_debug";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_MIXINS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_opening_paren_in_mixins";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_PRAGMAS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_opening_paren_in_pragmas";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_SCOPE_STATEMENTS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_opening_paren_in_scope_statements";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_WITH_STATEMENTS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_opening_paren_in_with_statements";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_TYPEOF_STATEMENTS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_opening_paren_in_typeof_statements";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_TYPEID_STATEMENTS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_opening_paren_in_typeid_statements";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_DELEGATE = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_opening_paren_in_delegate";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_C_STYLE_FP = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_opening_paren_in_c_style_fp";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_NEW_ARGUMENTS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_opening_paren_in_new_arguments";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_EXTERN_DECLARATIONS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_opening_paren_in_extern_declarations";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_FILE_IMPORTS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_opening_paren_in_file_imports";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_IF_STATEMENTS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_opening_paren_in_if_statements";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_IS_EXPRESSIONS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_opening_paren_in_is_expressions";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_CASTS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_opening_paren_in_casts";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_TEMPLATE_DECLARATIONS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_opening_paren_in_template_declarations";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_PARENTHESIZED_EXPRESSIONS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_opening_paren_in_parenthesized_expressions";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_CLASS_INVARIANTS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_opening_paren_in_class_invariants";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_TRAITS_EXPRESSION = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_opening_paren_in_traits_expression";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_MODIFIED_TYPE = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_opening_paren_in_modified_type";
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_FUNCTION_DECLARATION_PARAMETERS = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_opening_paren_in_function_declaration_parameters";
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_FUNCTION_TEMPLATE_ARGS = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_opening_paren_in_function_template_args";
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_FUNCTION_INVOCATION = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_opening_paren_in_function_invocation";
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_CATCH = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_opening_paren_in_catch";
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_FOR_LOOPS = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_opening_paren_in_for_loops";
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_WHILE_LOOPS = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_opening_paren_in_while_loops";
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_FOREACH_LOOPS = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_opening_paren_in_foreach_loops";
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_OUT_DECLARATION = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_opening_paren_in_out_declaration";
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_SYNCHRONIZED_STATEMENT = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_opening_paren_in_synchronized_statement";
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_SWITCH_STATEMENTS = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_opening_paren_in_switch_statements";
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_ALIGN_DECLARATIONS = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_opening_paren_in_align_declarations";
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_CLASS_TEMPLATE_PARAMS = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_opening_paren_in_class_template_params";
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_ASSERT_STATEMENTS = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_opening_paren_in_assert_statements";
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_VERSION_DEBUG = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_opening_paren_in_version_debug";
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_MIXINS = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_opening_paren_in_mixins";
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_PRAGMAS = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_opening_paren_in_pragmas";
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_SCOPE_STATEMENTS = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_opening_paren_in_scope_statements";
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_WITH_STATEMENTS = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_opening_paren_in_with_statements";
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_TYPEOF_STATEMENTS = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_opening_paren_in_typeof_statements";
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_TYPEID_STATEMENTS = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_opening_paren_in_typeid_statements";
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_DELEGATE = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_opening_paren_in_delegate";
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_C_STYLE_FP = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_opening_paren_in_c_style_fp";
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_NEW_ARGUMENTS = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_opening_paren_in_new_arguments";
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_EXTERN_DECLARATIONS = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_opening_paren_in_extern_declarations";
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_FILE_IMPORTS = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_opening_paren_in_file_imports";
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_IF_STATEMENTS = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_opening_paren_in_if_statements";
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_IS_EXPRESSIONS = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_opening_paren_in_is_expressions";
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_CASTS = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_opening_paren_in_casts";
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_TEMPLATE_DECLARATIONS = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_opening_paren_in_template_declarations";
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_PARENTHESIZED_EXPRESSIONS = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_opening_paren_in_parenthesized_expressions";
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_TEMPLATE_INVOCATION = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_opening_paren_in_template_invocation";
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_TRAITS_EXPRESSION = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_opening_paren_in_traits_expression";
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_MODIFIED_TYPE = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_opening_paren_in_modified_type";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_FUNCTION_DECLARATION_PARAMETERS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_closing_paren_in_function_declaration_parameters";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_FUNCTION_TEMPLATE_ARGS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_closing_paren_in_function_template_args";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_FUNCTION_INVOCATION = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_closing_paren_in_function_invocation";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_CATCH = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_closing_paren_in_catch";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_FOR_LOOPS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_closing_paren_in_for_loops";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_WHILE_LOOPS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_closing_paren_in_while_loops";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_FOREACH_LOOPS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_closing_paren_in_foreach_loops";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_OUT_DECLARATION = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_closing_paren_in_out_declaration";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_SYNCHRONIZED_STATEMENT = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_closing_paren_in_synchronized_statement";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_SWITCH_STATEMENTS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_closing_paren_in_switch_statements";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_ALIGN_DECLARATIONS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_closing_paren_in_align_declarations";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_CLASS_TEMPLATE_PARAMS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_closing_paren_in_class_template_params";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_ASSERT_STATEMENTS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_closing_paren_in_assert_statements";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_VERSION_DEBUG = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_closing_paren_in_version_debug";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_MIXINS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_closing_paren_in_mixins";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_PRAGMAS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_closing_paren_in_pragmas";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_SCOPE_STATEMENTS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_closing_paren_in_scope_statements";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_WITH_STATEMENTS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_closing_paren_in_with_statements";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_TYPEOF_STATEMENTS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_closing_paren_in_typeof_statements";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_TYPEID_STATEMENTS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_closing_paren_in_typeid_statements";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_DELEGATE = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_closing_paren_in_delegate";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_C_STYLE_FP = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_closing_paren_in_c_style_fp";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_NEW_ARGUMENTS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_closing_paren_in_new_arguments";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_EXTERN_DECLARATIONS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_closing_paren_in_extern_declarations";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_FILE_IMPORTS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_closing_paren_in_file_imports";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_IF_STATEMENTS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_closing_paren_in_if_statements";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_IS_EXPRESSIONS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_closing_paren_in_is_expressions";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_CASTS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_closing_paren_in_casts";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_TEMPLATE_DECLARATIONS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_closing_paren_in_template_declarations";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_PARENTHESIZED_EXPRESSIONS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_closing_paren_in_parenthesized_expressions";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_TEMPLATE_INVOCATION = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_closing_paren_in_template_invocation";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_TRAITS_EXPRESSION = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_closing_paren_in_traits_expression";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_MODIFIED_TYPE = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_closing_paren_in_modified_type";
	public static final String FORMATTER_INSERT_SPACE_BETWEEN_EMPTY_PARENS_IN_FUNCTION_DECLARATION_PARAMETERS = JavaCore.PLUGIN_ID + ".formatter.insert_space_between_empty_parens_in_function_declaration_parameters";
	public static final String FORMATTER_INSERT_SPACE_BETWEEN_EMPTY_PARENS_IN_FUNCTION_TEMPLATE_ARGS = JavaCore.PLUGIN_ID + ".formatter.insert_space_between_empty_parens_in_function_template_args";
	public static final String FORMATTER_INSERT_SPACE_BETWEEN_EMPTY_PARENS_IN_FUNCTION_INVOCATION = JavaCore.PLUGIN_ID + ".formatter.insert_space_between_empty_parens_in_function_invocation";
	public static final String FORMATTER_INSERT_SPACE_BETWEEN_EMPTY_PARENS_IN_OUT_DECLARATION = JavaCore.PLUGIN_ID + ".formatter.insert_space_between_empty_parens_in_out_declaration";
	public static final String FORMATTER_INSERT_SPACE_BETWEEN_EMPTY_PARENS_IN_CLASS_TEMPLATE_PARAMS = JavaCore.PLUGIN_ID + ".formatter.insert_space_between_empty_parens_in_class_template_params";
	public static final String FORMATTER_INSERT_SPACE_BETWEEN_EMPTY_PARENS_IN_DELEGATE = JavaCore.PLUGIN_ID + ".formatter.insert_space_between_empty_parens_in_delegate";
	public static final String FORMATTER_INSERT_SPACE_BETWEEN_EMPTY_PARENS_IN_NEW_ARGUMENTS = JavaCore.PLUGIN_ID + ".formatter.insert_space_between_empty_parens_in_new_arguments";
	public static final String FORMATTER_INSERT_SPACE_BETWEEN_EMPTY_PARENS_IN_EXTERN_DECLARATIONS = JavaCore.PLUGIN_ID + ".formatter.insert_space_between_empty_parens_in_extern_declarations";
	public static final String FORMATTER_INSERT_SPACE_BETWEEN_EMPTY_PARENS_IN_TEMPLATE_DECLARATIONS = JavaCore.PLUGIN_ID + ".formatter.insert_space_between_empty_parens_in_template_declarations";
	public static final String FORMATTER_INSERT_SPACE_BETWEEN_EMPTY_PARENS_IN_TEMPLATE_INVOCATION = JavaCore.PLUGIN_ID + ".formatter.insert_space_between_empty_parens_in_template_invocation";
	public static final String FORMATTER_INSERT_SPACE_BETWEEN_EMPTY_PARENS_IN_CLASS_INVARIANTS = JavaCore.PLUGIN_ID + ".formatter.insert_space_between_empty_parens_in_class_invariants";
	public static final String FORMATTER_INSERT_SPACE_BETWEEN_NAME_AND_ARGS_IN_C_STYLE_FP = JavaCore.PLUGIN_ID + ".formatter.insert_space_between_name_and_args_in_c_style_fp";
	public static final String FORMATTER_INSERT_SPACE_BETWEEN_TEMPLATE_AND_ARG_PARENS_IN_FUNCTION_DECLARATION = JavaCore.PLUGIN_ID + ".formatter.insert_space_between_template_and_arg_parens_in_function_declaration";
	public static final String FORMATTER_INSERT_SPACE_BETWEEN_TEMPLATE_ARGS_AND_FUNCTION_ARGS = JavaCore.PLUGIN_ID + ".formatter.insert_space_between_template_args_and_function_args";
	public static final String FORMATTER_INSERT_SPACE_BETWEEN_SUCCESIVE_OPCALLS = JavaCore.PLUGIN_ID + ".formatter.insert_space_between_succesive_opcalls";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_BASE_CLASS_LISTS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_comma_in_base_class_lists";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_ARRAY_LITERAL = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_comma_in_array_literal";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_FUNCTION_INVOCATION_ARGUMENTS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_comma_in_function_invocation_arguments";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_FUNCTION_DECLARATION_PARAMETERS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_comma_in_function_declaration_parameters";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_FUNCTION_TEMPLATE_PARAMETERS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_comma_in_function_template_parameters";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_AGGREGATE_TEMPLATE_PARAMETERS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_comma_in_aggregate_template_parameters";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_FOREACH_STATEMENT = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_comma_in_foreach_statement";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_MULTIPLE_IMPORTS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_comma_in_multiple_imports";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_SELECTIVE_IMPORTS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_comma_in_selective_imports";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_PRAGMAS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_comma_in_pragmas";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_STRUCT_INITIALIZER = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_comma_in_struct_initializer";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_TEMPLATE_DECLARATION = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_comma_in_template_declaration";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_TEMPLATE_INVOCATION = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_comma_in_template_invocation";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_MULTIPLE_FIELD_DECLARATIONS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_comma_in_multiple_field_declarations";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_ARRAY_ACCESS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_comma_in_array_access";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_DELEGATES = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_comma_in_delegates";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_NEW_ARGUMENTS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_comma_in_new_arguments";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_TRAILING_COMMA_IN_ARRAY_INITIALIZER = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_trailing_comma_in_array_initializer";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_ASSERT_STATEMENTS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_comma_in_assert_statements";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_ENUM_MEMBER_LISTS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_comma_in_enum_member_lists";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_TRAITS_EXPRESSION = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_comma_in_traits_expression";
	public static final String FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_BASE_CLASS_LISTS = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_comma_in_base_class_lists";
	public static final String FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_ARRAY_LITERAL = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_comma_in_array_literal";
	public static final String FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_FUNCTION_INVOCATION_ARGUMENTS = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_comma_in_function_invocation_arguments";
	public static final String FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_FUNCTION_DECLARATION_PARAMETERS = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_comma_in_function_declaration_parameters";
	public static final String FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_FUNCTION_TEMPLATE_PARAMETERS = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_comma_in_function_template_parameters";
	public static final String FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_AGGREGATE_TEMPLATE_PARAMETERS = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_comma_in_aggregate_template_parameters";
	public static final String FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_FOREACH_STATEMENT = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_comma_in_foreach_statement";
	public static final String FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_MULTIPLE_IMPORTS = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_comma_in_multiple_imports";
	public static final String FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_SELECTIVE_IMPORTS = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_comma_in_selective_imports";
	public static final String FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_PRAGMAS = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_comma_in_pragmas";
	public static final String FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_STRUCT_INITIALIZER = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_comma_in_struct_initializer";
	public static final String FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_TEMPLATE_DECLARATION = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_comma_in_template_declaration";
	public static final String FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_TEMPLATE_INVOCATION = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_comma_in_template_invocation";
	public static final String FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_MULTIPLE_FIELD_DECLARATIONS = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_comma_in_multiple_field_declarations";
	public static final String FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_ARRAY_ACCESS = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_comma_in_array_access";
	public static final String FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_DELEGATES = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_comma_in_delegates";
	public static final String FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_NEW_ARGUMENTS = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_comma_in_new_arguments";
	public static final String FORMATTER_INSERT_SPACE_AFTER_TRAILING_COMMA_IN_ARRAY_INITIALIZER = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_trailing_comma_in_array_initializer";
	public static final String FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_ASSERT_STATEMENTS = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_comma_in_assert_statements";
	public static final String FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_TRAITS_EXPRESSION = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_comma_in_traits_expression";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_ASSIGNMENT_OPERATOR = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_assignment_operator";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_PREFIX_OPERATOR = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_prefix_operator";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_INFIX_OPERATOR = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_infix_operator";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_POSTFIX_OPERATOR = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_postfix_operator";
	public static final String FORMATTER_INSERT_SPACE_AFTER_ASSIGNMENT_OPERATOR = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_assignment_operator";
	public static final String FORMATTER_INSERT_SPACE_AFTER_PREFIX_OPERATOR = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_prefix_operator";
	public static final String FORMATTER_INSERT_SPACE_AFTER_INFIX_OPERATOR = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_infix_operator";
	public static final String FORMATTER_INSERT_SPACE_AFTER_POSTFIX_OPERATOR = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_postfix_operator";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_EXCLAMATION_POINT_IN_TEMPLATE_INVOCATION = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_exclamation_point_in_template_invocation";
	public static final String FORMATTER_INSERT_SPACE_AFTER_EXCLAMATION_POINT_IN_TEMPLATE_INVOCATION = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_exclamation_point_in_template_invocation";
	public static final String FORMATTER_INSERT_SPACE_AFTER_CLOSING_PAREN_IN_CASTS = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_closing_paren_in_casts";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_DOT_IN_QUALIFIED_NAMES = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_dot_in_qualified_names";
	public static final String FORMATTER_INSERT_SPACE_AFTER_DOT_IN_QUALIFIED_NAMES = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_dot_in_qualified_names";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_SLICE_OPERATOR = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_slice_operator";
	public static final String FORMATTER_INSERT_SPACE_AFTER_SLICE_OPERATOR = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_slice_operator";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_SLICE_OPERATOR_IN_FOREACH_RANGE_STATEMENT = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_slice_operator_in_foreach_range_statement";
	public static final String FORMATTER_INSERT_SPACE_AFTER_SLICE_OPERATOR_IN_FOREACH_RANGE_STATEMENT = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_slice_operator_in_foreach_range_statement";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_ELIPSIS_IN_FUNCTION_VARARGS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_elipsis_in_function_varargs";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_ELIPSIS_IN_TUPLES = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_elipsis_in_tuples";
	public static final String FORMATTER_INSERT_SPACE_AFTER_ELIPSIS_IN_FUNCTION_VARARGS = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_elipsis_in_function_varargs";
	public static final String FORMATTER_INSERT_SPACE_AFTER_ELIPSIS_IN_TUPLES = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_elipsis_in_tuples";
	public static final String FORMATTER_INSERT_SPACE_BETWEEN_EMPTY_BRACKETS_IN_SLICE = JavaCore.PLUGIN_ID + ".formatter.insert_space_between_empty_brackets_in_slice";
	public static final String FORMATTER_INSERT_SPACE_BETWEEN_EMPTY_BRACKETS_IN_DYNAMIC_ARRAY_TYPE = JavaCore.PLUGIN_ID + ".formatter.insert_space_between_empty_brackets_in_dynamic_array_type";
	public static final String FORMATTER_INSERT_SPACE_BETWEEN_ADJACENT_BRACKETS_IN_MULTIDIMENSIONAL_ARRAYS = JavaCore.PLUGIN_ID + ".formatter.insert_space_between_adjacent_brackets_in_multidimensional_arrays";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACKET_IN_ARRAY_ACCESS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_opening_bracket_in_array_access";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACKET_IN_SLICES = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_opening_bracket_in_slices";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACKET_IN_ARRAY_CONSTRUCTORS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_opening_bracket_in_array_constructors";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACKET_IN_DYNAMIC_ARRAYS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_opening_bracket_in_dynamic_arrays";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACKET_IN_ASSOCIATIVE_ARRAYS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_opening_bracket_in_associative_arrays";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_OPENING_BRACKET_IN_STATIC_ARRAYS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_opening_bracket_in_static_arrays";
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_BRACKET_IN_ARRAY_ACCESS = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_opening_bracket_in_array_access";
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_BRACKET_IN_SLICES = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_opening_bracket_in_slices";
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_BRACKET_IN_ARRAY_LITERALS = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_opening_bracket_in_array_literals";
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_BRACKET_IN_ARRAY_CONSTRUCTORS = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_opening_bracket_in_array_constructors";
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_BRACKET_IN_ASSOCIATIVE_ARRAYS = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_opening_bracket_in_associative_arrays";
	public static final String FORMATTER_INSERT_SPACE_AFTER_OPENING_BRACKET_IN_STATIC_ARRAYS = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_opening_bracket_in_static_arrays";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_BRACKET_IN_ARRAY_ACCESS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_closing_bracket_in_array_access";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_BRACKET_IN_SLICES = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_closing_bracket_in_slices";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_BRACKET_IN_ARRAY_LITERALS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_closing_bracket_in_array_literals";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_BRACKET_IN_ARRAY_CONSTRUCTORS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_closing_bracket_in_array_constructors";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_BRACKET_IN_ASSOCIATIVE_ARRAYS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_closing_bracket_in_associative_arrays";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_CLOSING_BRACKET_IN_STATIC_ARRAYS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_closing_bracket_in_static_arrays";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COLON_IN_BASE_CLASS_LISTS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_colon_in_base_class_lists";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COLON_IN_TEMPLATE_SPECIFIC_TYPE = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_colon_in_template_specific_type";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COLON_IN_ARRAY_INITIALIZER = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_colon_in_array_initializer";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COLON_IN_STRUCT_INITIALIZER = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_colon_in_struct_initializer";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COLON_IN_CASE_DEFAULT_STATEMENT = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_colon_in_case_default_statement";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COLON_IN_SELECTIVE_IMPORTS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_colon_in_selective_imports";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COLON_IN_CONDITIONAL_EXPRESSIONS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_colon_in_conditional_expressions";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COLON_IN_STATEMENT_LABELS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_colon_in_statement_labels";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_COLON_IN_MODIFIERS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_colon_in_modifiers";
	public static final String FORMATTER_INSERT_SPACE_AFTER_COLON_IN_BASE_CLASS_LISTS = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_colon_in_base_class_lists";
	public static final String FORMATTER_INSERT_SPACE_AFTER_COLON_IN_TEMPLATE_SPECIFIC_TYPE = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_colon_in_template_specific_type";
	public static final String FORMATTER_INSERT_SPACE_AFTER_COLON_IN_ARRAY_INITIALIZER = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_colon_in_array_initializer";
	public static final String FORMATTER_INSERT_SPACE_AFTER_COLON_IN_STRUCT_INITIALIZER = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_colon_in_struct_initializer";
	public static final String FORMATTER_INSERT_SPACE_AFTER_COLON_IN_CASE_DEFAULT_STATEMENT = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_colon_in_case_default_statement";
	public static final String FORMATTER_INSERT_SPACE_AFTER_COLON_IN_SELECTIVE_IMPORTS = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_colon_in_selective_imports";
	public static final String FORMATTER_INSERT_SPACE_AFTER_COLON_IN_CONDITIONAL_EXPRESSIONS = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_colon_in_conditional_expressions";
	public static final String FORMATTER_INSERT_SPACE_AFTER_COLON_IN_STATEMENT_LABELS = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_colon_in_statement_labels";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_SEMICOLON = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_semicolon";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_SEMICOLON_IN_FOR_STATEMENT = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_semicolon_in_for_statement";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_SEMICOLON_IN_FOREACH_STATEMENT = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_semicolon_in_foreach_statement";
	public static final String FORMATTER_INSERT_SPACE_AFTER_SEMICOLON_IN_FOREACH_STATEMENT = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_semicolon_in_foreach_statement";
	public static final String FORMATTER_INSERT_SPACE_AFTER_SEMICOLON_IN_FOR_STATEMENT = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_semicolon_in_for_statement";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_QUESTION_MARK_IN_CONDITIONAL_EXPRESSIONS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_question_mark_in_conditional_expressions";
	public static final String FORMATTER_INSERT_SPACE_AFTER_QUESTION_MARK_IN_CONDITIONAL_EXPRESSIONS = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_question_mark_in_conditional_expressions";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_EQUALS_IN_RENAMED_IMPORTS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_equals_in_renamed_imports";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_EQUALS_IN_DEFAULT_FUNCTION_ARGUMENTS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_equals_in_default_function_arguments";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_EQUALS_IN_DEFAULT_TEMPLATE_ARGUMENTS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_equals_in_default_template_arguments";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_EQUALS_IN_VERSION_DEBUG_ASSIGNMENT = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_equals_in_version_debug_assignment";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_EQUALS_IN_ENUM_CONSTANTS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_equals_in_enum_constants";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_EQUALS_IN_VARIABLE_INITS = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_equals_in_variable_inits";
	public static final String FORMATTER_INSERT_SPACE_AFTER_EQUALS_IN_RENAMED_IMPORTS = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_equals_in_renamed_imports";
	public static final String FORMATTER_INSERT_SPACE_AFTER_EQUALS_IN_DEFAULT_FUNCTION_ARGUMENTS = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_equals_in_default_function_arguments";
	public static final String FORMATTER_INSERT_SPACE_AFTER_EQUALS_IN_DEFAULT_TEMPLATE_ARGUMENTS = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_equals_in_default_template_arguments";
	public static final String FORMATTER_INSERT_SPACE_AFTER_EQUALS_IN_VERSION_DEBUG_ASSIGNMENT = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_equals_in_version_debug_assignment";
	public static final String FORMATTER_INSERT_SPACE_AFTER_EQUALS_IN_ENUM_CONSTANTS = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_equals_in_enum_constants";
	public static final String FORMATTER_INSERT_SPACE_AFTER_EQUALS_IN_VARIABLE_INITS = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_equals_in_variable_inits";
	public static final String FORMATTER_INSERT_SPACE_AFTER_STAR_IN_C_STYLE_FP = JavaCore.PLUGIN_ID + ".formatter.insert_space_after_star_in_c_style_fp";
	public static final String FORMATTER_INSERT_SPACE_BEFORE_ASTERISK_FOR_POINTER_TYPES = JavaCore.PLUGIN_ID + ".formatter.insert_space_before_asterisk_for_pointer_types";
	public static final String FORMATTER_BLANK_LINES_BEFORE_MODULE = JavaCore.PLUGIN_ID + ".formatter.blank_lines_before_module";
	public static final String FORMATTER_BLANK_LINES_AFTER_MODULE = JavaCore.PLUGIN_ID + ".formatter.blank_lines_after_module";
	public static final String FORMATTER_NUMBER_OF_EMPTY_LINES_TO_PRESERVE = JavaCore.PLUGIN_ID + ".formatter.number_of_empty_lines_to_preserve";
	public static final String FORMATTER_INSERT_NEW_LINE_BEFORE_ELSE_STATEMENT = JavaCore.PLUGIN_ID + ".formatter.insert_new_line_before_else_statement";
	public static final String FORMATTER_INSERT_NEW_LINE_BEFORE_ELSE_DECLARATION = JavaCore.PLUGIN_ID + ".formatter.insert_new_line_before_else_declaration";
	public static final String FORMATTER_INSERT_NEW_LINE_BEFORE_CATCH = JavaCore.PLUGIN_ID + ".formatter.insert_new_line_before_catch";
	public static final String FORMATTER_INSERT_NEW_LINE_BEFORE_FINALLY = JavaCore.PLUGIN_ID + ".formatter.insert_new_line_before_finally";
	public static final String FORMATTER_INSERT_NEW_LINE_BEFORE_WHILE_IN_DO_STATEMENT = JavaCore.PLUGIN_ID + ".formatter.insert_new_line_before_while_in_do_statement";
	public static final String FORMATTER_INSERT_NEW_LINE_AFTER_CASE_OR_DEFAULT_STATEMENT = JavaCore.PLUGIN_ID + ".formatter.insert_new_line_after_case_or_default_statement";
	public static final String FORMATTER_INSERT_NEW_LINE_AFTER_LABEL = JavaCore.PLUGIN_ID + ".formatter.insert_new_line_after_label";
	public static final String FORMATTER_KEEP_ELSE_CONDITIONAL_ON_ONE_LINE = JavaCore.PLUGIN_ID + ".formatter.keep_else_conditional_on_one_line";
	public static final String FORMATTER_KEEP_ELSE_VERSION_DEBUG_ON_ONE_LINE = JavaCore.PLUGIN_ID + ".formatter.keep_else_version_debug_on_one_line";
	public static final String FORMATTER_INSERT_NEW_LINE_AT_END_OF_FILE_IF_MISSING = JavaCore.PLUGIN_ID + ".formatter.insert_new_line_at_end_of_file_if_missing";
	public static final String FORMATTER_KEEP_SIMPLE_THEN_DECLARATION_ON_SAME_LINE = JavaCore.PLUGIN_ID + ".formatter.keep_simple_then_declaration_on_same_line";
	public static final String FORMATTER_KEEP_SIMPLE_ELSE_DECLARATION_ON_SAME_LINE = JavaCore.PLUGIN_ID + ".formatter.keep_simple_else_declaration_on_same_line";
	public static final String FORMATTER_KEEP_SIMPLE_THEN_STATEMENT_ON_SAME_LINE = JavaCore.PLUGIN_ID + ".formatter.keep_simple_then_statement_on_same_line";
	public static final String FORMATTER_KEEP_SIMPLE_ELSE_STATEMENT_ON_SAME_LINE = JavaCore.PLUGIN_ID + ".formatter.keep_simple_else_statement_on_same_line";
	public static final String FORMATTER_KEEP_SIMPLE_TRY_STATEMENT_ON_SAME_LINE = JavaCore.PLUGIN_ID + ".formatter.keep_simple_try_statement_on_same_line";
	public static final String FORMATTER_KEEP_SIMPLE_CATCH_STATEMENT_ON_SAME_LINE = JavaCore.PLUGIN_ID + ".formatter.keep_simple_catch_statement_on_same_line";
	public static final String FORMATTER_KEEP_SIMPLE_FINALLY_STATEMENT_ON_SAME_LINE = JavaCore.PLUGIN_ID + ".formatter.keep_simple_finally_statement_on_same_line";
	public static final String FORMATTER_KEEP_SIMPLE_LOOP_STATEMENT_ON_SAME_LINE = JavaCore.PLUGIN_ID + ".formatter.keep_simple_loop_statement_on_same_line";
	public static final String FORMATTER_KEEP_SIMPLE_SCOPE_STATEMENT_ON_SAME_LINE = JavaCore.PLUGIN_ID + ".formatter.keep_simple_scope_statement_on_same_line";
	public static final String FORMATTER_KEEP_SIMPLE_SYNCHRONIZED_STATEMENT_ON_SAME_LINE = JavaCore.PLUGIN_ID + ".formatter.keep_simple_synchronized_statement_on_same_line";
	public static final String FORMATTER_KEEP_SIMPLE_WITH_STATEMENT_ON_SAME_LINE = JavaCore.PLUGIN_ID + ".formatter.keep_simple_with_statement_on_same_line";
	public static final String FORMATTER_KEEP_FUNCTIONS_WITH_NO_STATEMENT_IN_ONE_LINE = JavaCore.PLUGIN_ID + ".formatter.keep_functions_with_no_statement_in_one_line";
	public static final String FORMATTER_KEEP_FUNCTIONS_WITH_ONE_STATEMENT_IN_ONE_LINE = JavaCore.PLUGIN_ID + ".formatter.keep_functions_with_one_statement_in_one_line";
	public static final String FORMATTER_INDENTATION_SIZE = JavaCore.PLUGIN_ID + ".formatter.indentation_size";
	public static final String FORMATTER_CONTINUATION_INDENTATION = JavaCore.PLUGIN_ID + ".formatter.continuation_indentation";
	public static final String FORMATTER_INDENTATION_STYLE_COMPARE_TO_MODIFIER_HEADER = JavaCore.PLUGIN_ID + ".formatter.indentation_style_compare_to_modifier_header";
	public static final String FORMATTER_INDENT_EMPTY_LINES = JavaCore.PLUGIN_ID + ".formatter.indent_empty_lines";
	public static final String FORMATTER_INDENT_BODY_DECLARATIONS_COMPARE_TO_TYPE_HEADER = JavaCore.PLUGIN_ID + ".formatter.indent_body_declarations_compare_to_type_header";
	public static final String FORMATTER_INDENT_BODY_DECLARATIONS_COMPARE_TO_TEMPLATE_HEADER = JavaCore.PLUGIN_ID + ".formatter.indent_body_declarations_compare_to_template_header";
	public static final String FORMATTER_INDENT_BODY_DECLARATIONS_COMPARE_TO_ALIGN_HEADER = JavaCore.PLUGIN_ID + ".formatter.indent_body_declarations_compare_to_align_header";
	public static final String FORMATTER_INDENT_STATEMENTS_COMPARE_TO_FUNCTION_HEADER = JavaCore.PLUGIN_ID + ".formatter.indent_statements_compare_to_function_header";
	public static final String FORMATTER_INDENT_IN_OUT_BODY_COMPARE_TO_FUNCTION_HEADER = JavaCore.PLUGIN_ID + ".formatter.indent_in_out_body_compare_to_function_header";
	public static final String FORMATTER_INDENT_STATEMENTS_COMPARE_TO_FUNCTION_IN_HEADER = JavaCore.PLUGIN_ID + ".formatter.indent_statements_compare_to_function_in_header";
	public static final String FORMATTER_INDENT_STATEMENTS_COMPARE_TO_FUNCTION_OUT_HEADER = JavaCore.PLUGIN_ID + ".formatter.indent_statements_compare_to_function_out_header";
	public static final String FORMATTER_INDENT_STATEMENTS_COMPARE_TO_FUNCTION_BODY_HEADER = JavaCore.PLUGIN_ID + ".formatter.indent_statements_compare_to_function_body_header";
	public static final String FORMATTER_INDENT_ENUM_MEMBERS_COMPARE_TO_ENUM_HEADER = JavaCore.PLUGIN_ID + ".formatter.indent_enum_members_compare_to_enum_header";
	public static final String FORMATTER_INDENT_CASES_COMPARE_TO_SWITCH = JavaCore.PLUGIN_ID + ".formatter.indent_cases_compare_to_switch";
	public static final String FORMATTER_INDENT_BREAK_COMPARE_TO_SWITCH = JavaCore.PLUGIN_ID + ".formatter.indent_break_compare_to_switch";
	public static final String FORMATTER_INDENT_STATEMENTS_COMPARE_TO_CASE = JavaCore.PLUGIN_ID + ".formatter.indent_statements_compare_to_case";
	public static final String FORMATTER_INDENT_BODY_DECLARATIONS_COMPARE_TO_PRAGMA_HEADER = JavaCore.PLUGIN_ID + ".formatter.indent_body_declarations_compare_to_pragma_header";
	public static final String FORMATTER_ALIGNMENT_FOR_MULTIPLE_VARIABLE_DECLARATIONS = JavaCore.PLUGIN_ID + ".formatter.alignment_for_multiple_variable_declarations";
	public static final String FORMATTER_ALIGNMENT_FOR_FUNCTION_DECLARATION_PARAMETERS = JavaCore.PLUGIN_ID + ".formatter.alignment_for_function_declaration_parameters";
	public static final String FORMATTER_ALIGNMENT_FOR_TEMPLATE_DECLARATION_PARAMETERS = JavaCore.PLUGIN_ID + ".formatter.alignment_for_template_declaration_parameters";
	public static final String FORMATTER_ALIGNMENT_FOR_FUNCTION_INVOCATION_ARGUMENTS = JavaCore.PLUGIN_ID + ".formatter.alignment_for_function_invocation_arguments";
	public static final String FORMATTER_ALIGNMENT_FOR_TEMPLATE_INVOCATION_ARGUMENTS = JavaCore.PLUGIN_ID + ".formatter.alignment_for_template_invocation_arguments";
	public static final String FORMATTER_ALIGNMENT_FOR_ARRAY_LITERALS = JavaCore.PLUGIN_ID + ".formatter.alignment_for_array_literals";
	public static final String FORMATTER_ALIGNMENT_FOR_BASE_CLASS_LISTS = JavaCore.PLUGIN_ID + ".formatter.alignment_for_base_class_lists";
	public static final String FORMATTER_ALIGNMENT_FOR_SELECTIVE_IMPORTS = JavaCore.PLUGIN_ID + ".formatter.alignment_for_selective_imports";
	public static final String FORMATTER_ALIGNMENT_FOR_STRUCT_INITIALIZER = JavaCore.PLUGIN_ID + ".formatter.alignment_for_struct_initializer";
	public static final String FORMATTER_TAB_CHAR = JavaCore.PLUGIN_ID + ".formatter.tab_char";
	public static final String FORMATTER_TAB_SIZE = JavaCore.PLUGIN_ID + ".formatter.tab_size";
	public static final String FORMATTER_USE_TABS_ONLY_FOR_LEADING_INDENTATIONS = JavaCore.PLUGIN_ID + ".formatter.use_tabs_only_for_leading_indentations";
	public static final String FORMATTER_NEVER_INDENT_BLOCK_COMMENTS_ON_FIRST_COLUMN = JavaCore.PLUGIN_ID + ".formatter.never_indent_block_comments_on_first_column";
	public static final String FORMATTER_NEVER_INDENT_LINE_COMMENTS_ON_FIRST_COLUMN = JavaCore.PLUGIN_ID + ".formatter.never_indent_line_comments_on_first_column";
	public static final String FORMATTER_LINE_SPLIT = JavaCore.PLUGIN_ID + ".formatter.line_split";
	public static final String FORMATTER_PAGE_WIDTH = JavaCore.PLUGIN_ID + ".formatter.page_width";

	public static Map getBuiltInProfile(String name) {
		return DefaultCodeFormatterOptions.getBuiltInProfile(name).getMap();
	}
	
	public static Map getDefaultSettings() {
		return DefaultCodeFormatterOptions.getBuiltInProfile(DEFAULT_PROFILE).getMap();
	}
	
	/**
	 * <p>Return the indentation style of the given alignment value.
	 * The given alignment value should be created using the <code>createAlignmentValue(boolean, int, int)</code>
	 * API.
	 * </p>
	 *
	 * @param value the given alignment value
	 * @return the indentation style of the given alignment value
	 * @see #createAlignmentValue(boolean, int, int)
	 * @exception IllegalArgumentException if the given alignment value is null, or if it 
	 * doesn't have a valid format.
	 */
	public static int getIndentStyle(String value) {
		if (value == null) {
			throw WRONG_ARGUMENT;
		}
		try {
			int existingValue = Integer.parseInt(value);
			if ((existingValue & Alignment.M_INDENT_BY_ONE) != 0) {
				return INDENT_BY_ONE;
			} else if ((existingValue & Alignment.M_INDENT_ON_COLUMN) != 0) {
				return INDENT_ON_COLUMN;
			} else {
				return INDENT_DEFAULT;
			}
		} catch (NumberFormatException e) {
			throw WRONG_ARGUMENT;
		}
	}
}
