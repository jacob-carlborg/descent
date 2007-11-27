/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package descent.internal.unittest.launcher;

 
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.ui.dialogs.TwoPaneElementSelector;

import descent.core.ICompilationUnit;
import descent.core.IType;

import descent.ui.JavaElementLabelProvider;

/**
 * A dialog to select a test class or a test suite from a list of types.
 */
// TODO this should be a module flow diagram thing, not just a list of modules
public class TestSelectionDialog extends TwoPaneElementSelector {

	private final ICompilationUnit[] modules;
	
	private static class PackageRenderer extends JavaElementLabelProvider {
		public PackageRenderer() {
			super(JavaElementLabelProvider.SHOW_PARAMETERS | JavaElementLabelProvider.SHOW_POST_QUALIFIED | JavaElementLabelProvider.SHOW_ROOT);	
		}

		public Image getImage(Object element) {
			return super.getImage(((ICompilationUnit)element).getParent());
		}
		
		public String getText(Object element) {
			return super.getText(((ICompilationUnit)element).getParent());
		}
	}
	
	public TestSelectionDialog(Shell shell, ICompilationUnit[] $modules) {
		super(shell, new JavaElementLabelProvider(JavaElementLabelProvider.SHOW_BASICS | JavaElementLabelProvider.SHOW_OVERLAY_ICONS), 
				new PackageRenderer());
		modules = $modules;
	}

	/**
	 * @see org.eclipse.jface.window.Window#configureShell(Shell)
	 */
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		//PlatformUI.getWorkbench().getHelpSystem().setHelp(newShell, new Object[] { IJavaHelpContextIds.MAINTYPE_SELECTION_DIALOG });
	}

	/*
	 * @see Window#open()
	 */
	public int open() {
		setElements(modules);
		return super.open();
	}
	
}
