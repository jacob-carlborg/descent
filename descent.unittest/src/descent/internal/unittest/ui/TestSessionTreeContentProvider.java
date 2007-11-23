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

package descent.internal.unittest.ui;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import descent.internal.unittest.model.TestElement;
//import descent.internal.unittest.model.TestRoot;
//import descent.internal.unittest.model.TestSuiteElement;


public class TestSessionTreeContentProvider implements ITreeContentProvider {

	private final Object[] NO_CHILDREN= new Object[0];
	
	public void dispose() {
	}

	public Object[] getChildren(Object parentElement) {
		/* TODO if (parentElement instanceof TestSuiteElement)
			return ((TestSuiteElement) parentElement).getChildren();
		else */
			return NO_CHILDREN;
	}

	public Object[] getElements(Object inputElement) {
		//TODO return ((TestRoot) inputElement).getChildren();
		return NO_CHILDREN;
	}

	public Object getParent(Object element) {
		// TODO return ((TestElement) element).getParent();
		return null;
	}

	public boolean hasChildren(Object element) {
		/* TODO if (element instanceof TestSuiteElement)
			return ((TestSuiteElement) element).getChildren().length != 0;
		else */
			return false;
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}
}
