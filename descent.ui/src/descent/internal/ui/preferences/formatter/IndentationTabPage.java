/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     istvan@benedek-home.de
 *       - 103706 [formatter] indent empty lines
 *******************************************************************************/
package descent.internal.ui.preferences.formatter;

import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.core.runtime.Assert;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import descent.core.JavaCore;
import descent.core.formatter.DefaultCodeFormatterConstants;


public class IndentationTabPage extends ModifyDialogTabPage {
	
	/**
	 * Constant array for boolean selection 
	 */
	private static String[] FALSE_TRUE = {
		DefaultCodeFormatterConstants.FALSE,
		DefaultCodeFormatterConstants.TRUE
	};
	
	private final String PREVIEW=
	createPreviewHeader(FormatterMessages.IndentationTabPage_preview_header) +
	"class ExampleType{int x;}\n\n" +
	"enum ExampleEnum{ONE,TWO,THREE}\n\n" +
	"template ExampleTemplate(T){alias T foo;}\n\n" +
	"void simpleFunction(){doSomething();}\n\n" +
	"double divide(in double dividend, in double divisor)" +
	"in{assert(divisor != 0);}" +
	"out(val){assert(val == dividend / divisor);}" +
	"body{return dividend / divisor;}\n\n" +
	"void switchPreview(int a){switch(a){case 1:doSomething();" +
	"break;case 2:{writefln(\"2 is a bad number.\");" +
	"throw new Exception();}default:doSomethingElse();break;}}";
	private CompilationUnitPreview fPreview;
	private String fOldTabChar= null;
	
	public IndentationTabPage(ModifyDialog modifyDialog, Map workingValues) {
		super(modifyDialog, workingValues);
	}

	protected void doCreatePreferences(Composite composite, int numColumns) {

		final Group generalGroup= createGroup(numColumns, composite, FormatterMessages.IndentationTabPage_general_group_title); 
		
		final String[] tabPolicyValues = new String[] {
			DefaultCodeFormatterConstants.TAB,
			DefaultCodeFormatterConstants.SPACE,
			DefaultCodeFormatterConstants.MIXED
		};
		final String[] tabPolicyLabels = new String[] {
			FormatterMessages.IndentationTabPage_style_tab, 
			FormatterMessages.IndentationTabPage_style_space, 
			FormatterMessages.IndentationTabPage_style_mixed
		};
		final ComboPreference tabPolicy= createComboPref(generalGroup, numColumns, FormatterMessages.IndentationTabPage_tab_char, DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR, tabPolicyValues, tabPolicyLabels);
		final NumberPreference indentSize= createNumberPref(generalGroup, numColumns, FormatterMessages.IndentationTabPage_indentation_size, DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE, 0, 32); 
		final NumberPreference tabSize= createNumberPref(generalGroup, numColumns, FormatterMessages.IndentationTabPage_tab_size, DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE, 0, 32);
		
		String tabchar= (String) fWorkingValues.get(DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR);
		updateTabPreferences(tabchar, tabSize, indentSize);
		tabPolicy.addObserver(new Observer() {
			public void update(Observable o, Object arg) {
				updateTabPreferences((String) arg, tabSize, indentSize);
			}
		});
		tabSize.addObserver(new Observer() {
			public void update(Observable o, Object arg) {
				indentSize.updateWidget();
			}
		});
		
		final Group classGroup = createGroup(numColumns, composite, 
				FormatterMessages.IndentationTabPage_indent_group_title); 
		
		
		createCheckboxPref(classGroup, numColumns, 
				FormatterMessages.IndentationTabPage_indent_body_declarations_compare_to_type_header,
				DefaultCodeFormatterConstants.FORMATTER_INDENT_BODY_DECLARATIONS_COMPARE_TO_TYPE_HEADER, FALSE_TRUE); 
		createCheckboxPref(classGroup, numColumns,
				FormatterMessages.IndentationTabPage_indent_body_declarations_compare_to_template_header,
				DefaultCodeFormatterConstants.FORMATTER_INDENT_BODY_DECLARATIONS_COMPARE_TO_TEMPLATE_HEADER, FALSE_TRUE);
		createCheckboxPref(classGroup, numColumns,
				FormatterMessages.IndentationTabPage_indent_body_declarations_compare_to_modifier_header,
				DefaultCodeFormatterConstants.FORMATTER_INDENT_BODY_DECLARATIONS_COMPARE_TO_MODIFIER_HEADER, FALSE_TRUE); 
		createCheckboxPref(classGroup, numColumns,
				FormatterMessages.IndentationTabPage_indent_statements_compare_to_function_header,
				DefaultCodeFormatterConstants.FORMATTER_INDENT_STATEMENTS_COMPARE_TO_FUNCTION_HEADER, FALSE_TRUE);
		createCheckboxPref(classGroup, numColumns,
				FormatterMessages.IndentationTabPage_indent_in_out_body_compare_to_function_header,
				DefaultCodeFormatterConstants.FORMATTER_INDENT_IN_OUT_BODY_COMPARE_TO_FUNCTION_HEADER, FALSE_TRUE); 
		createCheckboxPref(classGroup, numColumns,
				FormatterMessages.IndentationTabPage_indent_statements_compare_to_function_in_header,
				DefaultCodeFormatterConstants.FORMATTER_INDENT_STATEMENTS_COMPARE_TO_FUNCTION_IN_HEADER,FALSE_TRUE); 
		createCheckboxPref(classGroup, numColumns,
				FormatterMessages.IndentationTabPage_indent_statements_compare_to_function_out_header,
				DefaultCodeFormatterConstants.FORMATTER_INDENT_STATEMENTS_COMPARE_TO_FUNCTION_OUT_HEADER, FALSE_TRUE); 
		createCheckboxPref(classGroup, numColumns,
				FormatterMessages.IndentationTabPage_indent_statements_compare_to_function_body_header,
				DefaultCodeFormatterConstants.FORMATTER_INDENT_STATEMENTS_COMPARE_TO_FUNCTION_BODY_HEADER, FALSE_TRUE); 
		createCheckboxPref(classGroup, numColumns,
				FormatterMessages.IndentationTabPage_indent_enum_members_compare_to_enum_header,
				DefaultCodeFormatterConstants.FORMATTER_INDENT_ENUM_MEMBERS_COMPARE_TO_ENUM_HEADER, FALSE_TRUE); 
		createCheckboxPref(classGroup, numColumns,
	        		FormatterMessages.IndentationTabPage_indent_cases_compare_to_switch,
	        		DefaultCodeFormatterConstants.FORMATTER_INDENT_CASES_COMPARE_TO_SWITCH, FALSE_TRUE); 
		CheckboxPreference case_preference = createCheckboxPref(classGroup, numColumns,
	        		FormatterMessages.IndentationTabPage_indent_statements_compare_to_case,
	        		DefaultCodeFormatterConstants.FORMATTER_INDENT_STATEMENTS_COMPARE_TO_CASE, FALSE_TRUE); 
		CheckboxPreference break_preference = createCheckboxPref(classGroup, numColumns,
	        		FormatterMessages.IndentationTabPage_indent_break_compare_to_switch,
	        		DefaultCodeFormatterConstants.FORMATTER_INDENT_BREAK_COMPARE_TO_SWITCH, FALSE_TRUE); 
		createCheckboxPref(classGroup, numColumns,
        		FormatterMessages.IndentationTabPage_indent_empty_lines,
        		DefaultCodeFormatterConstants.FORMATTER_INDENT_EMPTY_LINES, FALSE_TRUE);
		
		case_preference.addObserver(new Observer()
			{
				public void update(Observable o, Object arg)
				{
					/* TODO deactivate break_preference if case_preference is
					       off, activate it if case_preference is on (no
					       indenting breaks w/o indenting cases. */
				}
			});
	}
	
	public void initializePage() {
	    fPreview.setPreviewText(PREVIEW);
	}
	
    protected JavaPreview doCreateJavaPreview(Composite parent) {
        fPreview= new CompilationUnitPreview(fWorkingValues, parent);
        return fPreview;
    }

    protected void doUpdatePreview() {
        fPreview.update();
    }

	private void updateTabPreferences(String tabPolicy, NumberPreference tabPreference, NumberPreference indentPreference) {
		/*
		 * If the tab-char is SPACE (or TAB), INDENTATION_SIZE
		 * preference is not used by the core formatter. We piggy back the
		 * visual tab length setting in that preference in that case. If the
		 * user selects MIXED, we use the previous TAB_SIZE preference as the
		 * new INDENTATION_SIZE (as this is what it really is) and set the 
		 * visual tab size to the value piggy backed in the INDENTATION_SIZE
		 * preference. See also CodeFormatterUtil. 
		 */
		if (DefaultCodeFormatterConstants.MIXED.equals(tabPolicy)) {
			if (JavaCore.SPACE.equals(fOldTabChar) || JavaCore.TAB.equals(fOldTabChar))
				swapTabValues();
			tabPreference.setEnabled(true);
			tabPreference.setKey(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE);
			indentPreference.setEnabled(true);
			indentPreference.setKey(DefaultCodeFormatterConstants.FORMATTER_INDENTATION_SIZE);
		} else if (JavaCore.SPACE.equals(tabPolicy)) {
			if (DefaultCodeFormatterConstants.MIXED.equals(fOldTabChar))
				swapTabValues();
			tabPreference.setEnabled(true);
			tabPreference.setKey(DefaultCodeFormatterConstants.FORMATTER_INDENTATION_SIZE);
			indentPreference.setEnabled(true);
			indentPreference.setKey(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE);
		} else if (JavaCore.TAB.equals(tabPolicy)) {
			if (DefaultCodeFormatterConstants.MIXED.equals(fOldTabChar))
				swapTabValues();
			tabPreference.setEnabled(true);
			tabPreference.setKey(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE);
			indentPreference.setEnabled(false);
			indentPreference.setKey(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE);
		} else {
			Assert.isTrue(false);
		}
		fOldTabChar= tabPolicy;
	}

	private void swapTabValues() {
		Object tabSize= fWorkingValues.get(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE);
		Object indentSize= fWorkingValues.get(DefaultCodeFormatterConstants.FORMATTER_INDENTATION_SIZE);
		fWorkingValues.put(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE, indentSize);
		fWorkingValues.put(DefaultCodeFormatterConstants.FORMATTER_INDENTATION_SIZE, tabSize);
	}
}
