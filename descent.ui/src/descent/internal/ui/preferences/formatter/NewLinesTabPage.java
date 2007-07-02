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

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import descent.core.formatter.DefaultCodeFormatterConstants;

public class NewLinesTabPage extends ModifyDialogTabPage {
	
	/**
	 * Constant array for boolean selection 
	 */
	private static String[] FALSE_TRUE = {
		DefaultCodeFormatterConstants.FALSE,
		DefaultCodeFormatterConstants.TRUE
	};
	
	private final String PREVIEW=
	createPreviewHeader(FormatterMessages.NewLinesTabPage_preview_header) +
		"version(Tango)alias toString toUtf8; else alias toUtf8 toString;\n\n" +
		"void exampleFunction(){" +
		"if(x==1){Stdout(\"x is one.\");}else if(x==2){Stdout(\"x is two.\")}" +
		"else{Stdout(\"x is a number I haven't learned about yet.\")}\n\n" +
		"try{file.read();}catch(IOException e){Stdout(e.toString());}" +
		"finally{file.close();}\n\n" +
		"do{file.read();}while(file.canRead());\n\n" +
		"// Short syntax preview\n" +
		"if(false)Stdout(\"Happy opposite day!\");" +
		"else Stdout(\"Happy regular day!\");\n\n" +
		"try file.read();catch(IOException e)Stdout(e.toString());" +
		"finally file.close();\n\n" +
		"with(RatsOnYourShoulder)walkAroundLookingCool();\n\n" +
		"while(file.canRead())Stdout(file.read());\n\n" +
		"synchronized(collection)collection.add(item);}\n\n" +
		"void doNothing(){}\n\n" +
		"int addOne(in int n){return n + 1;}";
	
	private CompilationUnitPreview fPreview;
	
	public NewLinesTabPage(ModifyDialog modifyDialog, Map workingValues) {
		super(modifyDialog, workingValues);
	}

	protected void doCreatePreferences(Composite composite, int numColumns) {
		
		final Group controlStatementsGroup = createGroup(numColumns, composite, FormatterMessages.NewLinesTabPage_control_statements_title);
		createCheckboxPref(controlStatementsGroup, numColumns, 
				FormatterMessages.NewLinesTabPage_insert_new_line_before_else,
				DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_ELSE, FALSE_TRUE); 
		createCheckboxPref(controlStatementsGroup, numColumns, 
				FormatterMessages.NewLinesTabPage_insert_new_line_before_catch,
				DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_CATCH, FALSE_TRUE); 
		createCheckboxPref(controlStatementsGroup, numColumns, 
				FormatterMessages.NewLinesTabPage_insert_new_line_before_finally,
				DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_FINALLY, FALSE_TRUE); 
		createCheckboxPref(controlStatementsGroup, numColumns, 
				FormatterMessages.NewLinesTabPage_insert_new_line_before_while_in_do_statement,
				DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_WHILE_IN_DO_STATEMENT, FALSE_TRUE);
		createCheckboxPref(controlStatementsGroup, numColumns, 
				FormatterMessages.NewLinesTabPage_keep_else_conditional_on_one_line,
				DefaultCodeFormatterConstants.FORMATTER_KEEP_ELSE_CONDITIONAL_ON_ONE_LINE, FALSE_TRUE); 
		
		final Group shortSyntaxGroup = createGroup(numColumns, composite, FormatterMessages.NewLinesTabPage_short_syntax_group_title); 
		createCheckboxPref(shortSyntaxGroup, numColumns, 
				FormatterMessages.NewLinesTabPage_keep_simple_then_declaration_on_same_line,
				DefaultCodeFormatterConstants.FORMATTER_KEEP_SIMPLE_THEN_DECLARATION_ON_SAME_LINE, FALSE_TRUE); 
		createCheckboxPref(shortSyntaxGroup, numColumns,
				FormatterMessages.NewLinesTabPage_keep_simple_else_declaration_on_same_line,
				DefaultCodeFormatterConstants.FORMATTER_KEEP_SIMPLE_ELSE_DECLARATION_ON_SAME_LINE, FALSE_TRUE);
		createCheckboxPref(shortSyntaxGroup, numColumns,
				FormatterMessages.NewLinesTabPage_keep_simple_then_statement_on_same_line,
				DefaultCodeFormatterConstants.FORMATTER_KEEP_SIMPLE_THEN_STATEMENT_ON_SAME_LINE, FALSE_TRUE); 
		createCheckboxPref(shortSyntaxGroup, numColumns,
				FormatterMessages.NewLinesTabPage_keep_simple_else_statement_on_same_line,
				DefaultCodeFormatterConstants.FORMATTER_KEEP_SIMPLE_ELSE_STATEMENT_ON_SAME_LINE, FALSE_TRUE);
		createCheckboxPref(shortSyntaxGroup, numColumns,
				FormatterMessages.NewLinesTabPage_keep_simple_try_statement_on_same_line,
				DefaultCodeFormatterConstants.FORMATTER_KEEP_SIMPLE_TRY_STATEMENT_ON_SAME_LINE, FALSE_TRUE); 
		createCheckboxPref(shortSyntaxGroup, numColumns,
				FormatterMessages.NewLinesTabPage_keep_simple_catch_statement_on_same_line,
				DefaultCodeFormatterConstants.FORMATTER_KEEP_SIMPLE_CATCH_STATEMENT_ON_SAME_LINE,FALSE_TRUE); 
		createCheckboxPref(shortSyntaxGroup, numColumns,
				FormatterMessages.NewLinesTabPage_keep_simple_finally_statement_on_same_line,
				DefaultCodeFormatterConstants.FORMATTER_KEEP_SIMPLE_FINALLY_STATEMENT_ON_SAME_LINE, FALSE_TRUE); 
		createCheckboxPref(shortSyntaxGroup, numColumns,
				FormatterMessages.NewLinesTabPage_keep_simple_loop_statement_on_same_line,
				DefaultCodeFormatterConstants.FORMATTER_KEEP_SIMPLE_LOOP_STATEMENT_ON_SAME_LINE, FALSE_TRUE); 
		createCheckboxPref(shortSyntaxGroup, numColumns,
				FormatterMessages.NewLinesTabPage_keep_simple_synchronized_statement_on_same_line,
				DefaultCodeFormatterConstants.FORMATTER_KEEP_SIMPLE_SYNCHRONIZED_STATEMENT_ON_SAME_LINE, FALSE_TRUE); 
        createCheckboxPref(shortSyntaxGroup, numColumns,
        		FormatterMessages.NewLinesTabPage_keep_simple_with_statement_on_same_line,
        		DefaultCodeFormatterConstants.FORMATTER_KEEP_SIMPLE_WITH_STATEMENT_ON_SAME_LINE, FALSE_TRUE);
        createCheckboxPref(shortSyntaxGroup, numColumns,
				FormatterMessages.NewLinesTabPage_keep_functions_with_no_statement_in_one_line,
				DefaultCodeFormatterConstants.FORMATTER_KEEP_FUNCTIONS_WITH_NO_STATEMENT_IN_ONE_LINE, FALSE_TRUE); 
        createCheckboxPref(shortSyntaxGroup, numColumns,
        		FormatterMessages.NewLinesTabPage_keep_functions_with_one_statement_in_one_line,
        		DefaultCodeFormatterConstants.FORMATTER_KEEP_FUNCTIONS_WITH_ONE_STATEMENT_IN_ONE_LINE, FALSE_TRUE);
        
        final Group otherGroup = createGroup(numColumns, composite, FormatterMessages.NewLinesTabPage_control_statements_title);
        createCheckboxPref(otherGroup, numColumns, 
				FormatterMessages.NewLinesTabPage_insert_new_line_at_end_of_file_if_missing,
				DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_AT_END_OF_FILE_IF_MISSING, FALSE_TRUE); 
        
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
}
