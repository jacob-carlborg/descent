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

public class BracesTabPage extends ModifyDialogTabPage {
	
	private final String PREVIEW=
	createPreviewHeader(FormatterMessages.BracesTabPage_preview_header) + 
	"void exampleFunction(){doSomething();}";
	
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
		final Group group= createGroup(numColumns, composite, FormatterMessages.BracesTabPage_group_brace_positions_title); 
		createBracesCombo(group, numColumns, FormatterMessages.BracesTabPage_brace_position_for_function_declaration, DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_FUNCTION_DECLARATION); 
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
