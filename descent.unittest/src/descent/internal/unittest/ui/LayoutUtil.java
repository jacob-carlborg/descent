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
package descent.internal.unittest.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.Assert;

import descent.internal.ui.util.PixelConverter;

public class LayoutUtil {
	
	/*
	 * Sets the span of a control. Assumes that MGridData is used.
	 */
	public static void setHorizontalSpan(Control control, int span) {
		Object ld= control.getLayoutData();
		if (ld instanceof GridData) {
			((GridData)ld).horizontalSpan= span;
		} else if (span != 1) {
			GridData gd= new GridData();
			gd.horizontalSpan= span;
			control.setLayoutData(gd);
		}
	}	

	/*
	 * Sets the width hint of a control. Assumes that MGridData is used.
	 */
	public static void setWidthHint(Control control, int widthHint) {
		Object ld= control.getLayoutData();
		if (ld instanceof GridData) {
			((GridData)ld).widthHint= widthHint;
		}
	}
	
	/*
	 * Sets the horizontal indent of a control. Assumes that MGridData is used.
	 */
	public static void setHorizontalIndent(Control control, int horizontalIndent) {
		Object ld= control.getLayoutData();
		if (ld instanceof GridData) {
			((GridData)ld).horizontalIndent= horizontalIndent;
		}
	}

	/*
	 * Creates a spacer control with the given span.
	 * The composite is assumed to have <code>MGridLayout</code> as
	 * layout.
	 * @param parent The parent composite
	 */			
	public static Control createEmptySpace(Composite parent, int span) {
		Label label= new Label(parent, SWT.LEFT);
		GridData gd= new GridData();
		gd.horizontalAlignment= GridData.BEGINNING;
		gd.grabExcessHorizontalSpace= false;
		gd.horizontalSpan= span;
		gd.horizontalIndent= 0;
		gd.widthHint= 0;
		gd.heightHint= 0;
		label.setLayoutData(gd);
		return label;
	}
	

	/**
	 * Returns a width hint for a button control.
	 * @param button	the button for which to set the dimension hint
	 * @return the width hint
	 */
	public static int getButtonWidthHint(Button button) {
		button.setFont(JFaceResources.getDialogFont());
		PixelConverter converter= new PixelConverter(button);
		int widthHint= converter.convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
		return Math.max(widthHint, button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
	}

	/**
	 * Sets width and height hint for the button control.
	 * <b>Note:</b> This is a NOP if the button's layout data is not
	 * an instance of <code>GridData</code>.
	 * 
	 * @param button	the button for which to set the dimension hint
	 */		
	public static void setButtonDimensionHint(Button button) {
		Assert.isNotNull(button);
		Object gd= button.getLayoutData();
		if (gd instanceof GridData) {
			((GridData)gd).widthHint= getButtonWidthHint(button);	
			((GridData)gd).horizontalAlignment = GridData.FILL;	 
		}
	}
	
}
