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
		"void exampleFunction(){" +
		"if(x==1){Stdout(\"x is one.\");}else if(x==2){Stdout(\"x is two.\")}" +
		"else{Stdout(\"x is a number I haven't learned about yet.\")}\n\n" +
		"try{file.read();}catch(IOException e){Stdout(e.toString());}" +
		"finally{file.close();}\n\n" +
		"do{file.read();}while(file.canRead());\n\n" +
		"switch(a){case 1:doSomething();break;case 2:throw new" +
		"Exception();default:doSomethingElse();break;}\n\n" +
		"endOfFunction:return;}";
	
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
		createCheckboxPref(controlStatementsGroup, numColumns, 
				FormatterMessages.NewLinesTabPage_insert_new_line_after_case_or_default_statement,
				DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_AFTER_CASE_OR_DEFAULT_STATEMENT, FALSE_TRUE); 
		createCheckboxPref(controlStatementsGroup, numColumns, 
				FormatterMessages.NewLinesTabPage_insert_new_line_after_label,
				DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_AFTER_LABEL, FALSE_TRUE); 
        
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
