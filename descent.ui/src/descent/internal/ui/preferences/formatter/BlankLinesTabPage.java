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


public class BlankLinesTabPage extends ModifyDialogTabPage {

	private final String PREVIEW=
	createPreviewHeader(FormatterMessages.BlankLinesTabPage_preview_header) +
	"module foo.bar;void fn(){\nint x;\n" +
	"// Between here...\n" +
	"\n\n\n\n\n\n\n\n\n\n" +
	"// ...and here are 10 blank lines\n" +
	"}";
	
	private final static int MIN_NUMBER_LINES= 0;
	private final static int MAX_NUMBER_LINES= 99;
	

	private CompilationUnitPreview fPreview;
	
	/**
	 * Create a new BlankLinesTabPage.
	 * @param modifyDialog The main configuration dialog
	 * 
	 * @param workingValues The values wherein the options are stored. 
	 */
	public BlankLinesTabPage(ModifyDialog modifyDialog, Map workingValues) {
		super(modifyDialog, workingValues);
	}

	protected void doCreatePreferences(Composite composite, int numColumns) {
				
	    Group group;
	    
		group= createGroup(numColumns, composite,
				FormatterMessages.BlankLinesTabPage_compilation_unit_group_title); 
		
		createBlankLineTextField(group, numColumns, 
				FormatterMessages.BlankLinesTabPage_blank_lines_before_module,
				DefaultCodeFormatterConstants.FORMATTER_BLANK_LINES_BEFORE_MODULE); 
		createBlankLineTextField(group, numColumns,
				FormatterMessages.BlankLinesTabPage_blank_lines_after_module,
				DefaultCodeFormatterConstants.FORMATTER_BLANK_LINES_AFTER_MODULE);

		group= createGroup(numColumns, composite, FormatterMessages.BlankLinesTabPage_blank_lines_group_title); 
		createBlankLineTextField(group, numColumns, 
			FormatterMessages.BlankLinesTabPage_number_of_empty_lines_to_preserve,
			DefaultCodeFormatterConstants.FORMATTER_NUMBER_OF_EMPTY_LINES_TO_PRESERVE); 
	}
	
	protected void initializePage() {
	    fPreview.setPreviewText(PREVIEW);
	}
	
	/*
	 * A helper method to create a number preference for blank lines.
	 */
	private void createBlankLineTextField(Composite composite, int numColumns, String message, String key) {
		createNumberPref(composite, numColumns, message, key, MIN_NUMBER_LINES, MAX_NUMBER_LINES);
	}

    /* (non-Javadoc)
     * @see descent.internal.ui.preferences.formatter.ModifyDialogTabPage#doCreateJavaPreview(org.eclipse.swt.widgets.Composite)
     */
    protected JavaPreview doCreateJavaPreview(Composite parent) {
        fPreview= new CompilationUnitPreview(fWorkingValues, parent);
        return fPreview;
    }

    /* (non-Javadoc)
     * @see descent.internal.ui.preferences.formatter.ModifyDialogTabPage#doUpdatePreview()
     */
    protected void doUpdatePreview() {
        fPreview.update();
    }
}