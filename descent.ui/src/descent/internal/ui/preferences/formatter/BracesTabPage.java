/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package descent.internal.ui.preferences.formatter;
 
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import descent.core.formatter.DefaultCodeFormatterConstants;

public class BracesTabPage extends ModifyDialogTabPage {
	
	private final String PREVIEW=
	createPreviewHeader(FormatterMessages.BracesTabPage_preview_header) + 
	"class ExampleType{\n\n" +
	"private{int num;}}\n\n" +
	"enum ExampleEnum{ONE,TWO,THREE}\n\n" +
	"version(Tango){alias toUtf8 toString;}" +
	"object exampleFunction(){\n\n" +
	"if((2+2)==5){endWorld();}\n\n" +
	"while(Pluto !is Planet){protest();}\n\n" +
	"switch(x){\n\n" +
	"case NoBrace:break;case WithBrace:{break;}}\n\n" +
	"try{doSomething();}catch(Exception e){notifyUser();}finally{cleanUp();}\n\n" +
	"synchronized(x){doSomething(x);}\n\n" +
	"scope(exit){closeFile();}\n\n" +
	"with(tango.io.console){Stdout(\"Hello, formatted world!\");}\n\n" +
	"pragma(ExamplePragmaBlock){foo.bar.baz();}\n\n" +
	"functionThatTakesADelegate({beInAFunctionLiteral();});\n\n" +
	"return new class{void beUseless();};}";
	
	private CompilationUnitPreview fPreview;
	
	
	private final String [] fBracePositions= {
	    DefaultCodeFormatterConstants.END_OF_LINE,
	    DefaultCodeFormatterConstants.NEXT_LINE,
	    DefaultCodeFormatterConstants.NEXT_LINE_SHIFTED
	};
	
	private final String [] fBracePositionNames= {
	    FormatterMessages.BracesTabPage_position_same_line, 
	    FormatterMessages.BracesTabPage_position_next_line, 
	    FormatterMessages.BracesTabPage_position_next_line_indented
	};
	
	/**
	 * Create a new BracesTabPage.
	 * @param modifyDialog
	 * @param workingValues
	 */
	public BracesTabPage(ModifyDialog modifyDialog, Map workingValues) {
		super(modifyDialog, workingValues);
	}
	
	protected void doCreatePreferences(Composite composite, int numColumns) {
		final Group group = createGroup(numColumns, composite, FormatterMessages.BracesTabPage_group_brace_positions_title); 
		final ComboPreference[] prefs = new ComboPreference[17];
		prefs[0] = createBracesCombo(group, numColumns, 
				FormatterMessages.BracesTabPage_brace_position_for_function_declaration, 
				DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_FUNCTION_DECLARATION);
		prefs[1] = createBracesCombo(group, numColumns, 
				FormatterMessages.BracesTabPage_brace_position_for_type_declaration, 
				DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_TYPE_DECLARATION);
		prefs[2] = createBracesCombo(group, numColumns, 
				FormatterMessages.BracesTabPage_brace_position_for_enum_declaration, 
				DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_ENUM_DECLARATION);
		prefs[3] = createBracesCombo(group, numColumns, 
				FormatterMessages.BracesTabPage_brace_position_for_template_declarations, 
				DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_TEMPLATE_DECLARATIONS);
		prefs[4] = createBracesCombo(group, numColumns, 
				FormatterMessages.BracesTabPage_brace_position_for_conditional_declaration, 
				DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_CONDITIONAL_DECLARATION);
		prefs[5] = createBracesCombo(group, numColumns, 
				FormatterMessages.BracesTabPage_brace_position_for_conditional_statement, 
				DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_CONDITIONAL_STATEMENT);
		prefs[6] = createBracesCombo(group, numColumns, 
				FormatterMessages.BracesTabPage_brace_position_for_loop_statement, 
				DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_LOOP_STATEMENT);
		prefs[7] = createBracesCombo(group, numColumns, 
				FormatterMessages.BracesTabPage_brace_position_for_function_literal, 
				DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_FUNCTION_LITERAL);
		prefs[8] = createBracesCombo(group, numColumns, 
				FormatterMessages.BracesTabPage_brace_position_for_anonymous_type, 
				DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_ANONYMOUS_TYPE);
		prefs[9] = createBracesCombo(group, numColumns, 
				FormatterMessages.BracesTabPage_brace_position_for_switch_statement, 
				DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_SWITCH_STATEMENT);
		prefs[10] = createBracesCombo(group, numColumns, 
				FormatterMessages.BracesTabPage_brace_position_for_switch_case, 
				DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_SWITCH_CASE);
		prefs[11] = createBracesCombo(group, numColumns, 
				FormatterMessages.BracesTabPage_brace_position_for_try_catch_finally, 
				DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_TRY_CATCH_FINALLY);
		prefs[12] = createBracesCombo(group, numColumns, 
				FormatterMessages.BracesTabPage_brace_position_for_modifiers, 
				DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_MODIFIERS);
		prefs[13] = createBracesCombo(group, numColumns, 
				FormatterMessages.BracesTabPage_brace_position_for_synchronized_statements, 
				DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_SYNCHRONIZED_STATEMENTS);
		prefs[14] = createBracesCombo(group, numColumns, 
				FormatterMessages.BracesTabPage_brace_position_for_with_statements, 
				DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_WITH_STATEMENTS);
		prefs[15] = createBracesCombo(group, numColumns, 
				FormatterMessages.BracesTabPage_brace_position_for_scope_statements, 
				DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_SCOPE_STATEMENTS);
		prefs[16] = createBracesCombo(group, numColumns, 
				FormatterMessages.BracesTabPage_brace_position_for_other_blocks, 
				DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_OTHER_BLOCKS);
		
		final SetAllGroup setAll = createSetAllGroup(numColumns, composite, FormatterMessages.BracesTabPage_group_set_all_to);
		createSetAllOption(setAll, FormatterMessages.BracesTabPage_position_same_line, FormatterMessages.BracesTabPage_position_same_line);
		createSetAllOption(setAll, FormatterMessages.BracesTabPage_position_next_line, FormatterMessages.BracesTabPage_position_next_line);
		createSetAllOption(setAll, FormatterMessages.BracesTabPage_position_next_line_indented, FormatterMessages.BracesTabPage_position_next_line_indented);
		setAll.addPreferences(prefs);
	}
	
	protected void initializePage() {
	    fPreview.setPreviewText(PREVIEW);
	}
	
	protected JavaPreview doCreateJavaPreview(Composite parent) {
	    fPreview= new CompilationUnitPreview(fWorkingValues, parent);
	    return fPreview;
	}
	
	private ComboPreference createBracesCombo(Composite composite, int numColumns, String message, String key) {
		return createComboPref(composite, numColumns, message, key, fBracePositions, fBracePositionNames);
	}

    protected void doUpdatePreview() {
        fPreview.update();
    }

}
