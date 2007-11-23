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

 
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.core.resources.ResourcesPlugin;

import descent.core.IJavaElement;
import descent.core.IJavaProject;
import descent.core.IType;
import descent.core.JavaCore;
import descent.core.JavaModelException;
import descent.core.search.IJavaSearchConstants;
import descent.core.search.IJavaSearchScope;
import descent.core.search.SearchEngine;
import descent.core.search.SearchMatch;
import descent.core.search.SearchParticipant;
import descent.core.search.SearchPattern;
import descent.core.search.SearchRequestor;

import org.eclipse.swt.widgets.Shell;

import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.window.Window;

import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import descent.ui.JavaElementLabelProvider;

import descent.internal.unittest.Messages;

/**
 * A dialog to select a test method.
 */
public class TestMethodSelectionDialog extends ElementListSelectionDialog {

	private IJavaElement fElement;

	public static class TestReferenceCollector extends SearchRequestor {
		Set fResult= new HashSet(200);
		
		public void acceptSearchMatch(SearchMatch match) throws CoreException {
			IJavaElement enclosingElement= (IJavaElement) match.getElement();
			if (enclosingElement.getElementName().startsWith("test")) //$NON-NLS-1$
				fResult.add(enclosingElement);
		}
	
		public Object[] getResult() {
			return fResult.toArray();
		}
	}

	public TestMethodSelectionDialog(Shell shell, IJavaElement element) {
		super(shell, new JavaElementLabelProvider(JavaElementLabelProvider.SHOW_PARAMETERS | JavaElementLabelProvider.SHOW_POST_QUALIFIED));
		fElement= element;
	}
	
	/*
	 * @see Windows#configureShell
	 */
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(newShell, IJUnitHelpContextIds.TEST_SELECTION_DIALOG);
	}

	/*
	 * @see Window#open()
	 */
	public int open() {
		Object[] elements;
		IType testType= findTestType();
		
		if (testType == null) 
			return CANCEL;
		
		try {
			elements= searchTestMethods(fElement, testType);
		} catch (InterruptedException e) {
			return CANCEL;
		} catch (InvocationTargetException e) {
			MessageDialog.openError(getParentShell(), JUnitMessages.TestMethodSelectionDialog_error_title, e.getTargetException().getMessage()); 
			return CANCEL;
		}
		
		if (elements.length == 0) {
			String msg= Messages.format(JUnitMessages.TestMethodSelectionDialog_notfound_message, fElement.getElementName()); 
			MessageDialog.openInformation(getParentShell(), JUnitMessages.TestMethodSelectionDialog_no_tests_title, msg); 
			return CANCEL;
		}
		setElements(elements);
		return super.open();
	}
	
	private IType findTestType() {
		String qualifiedName= DescentUnittestPlugin.TEST_INTERFACE_NAME;
		IJavaProject[] projects;
		Set result= new HashSet();
		try {
			projects= JavaCore.create(ResourcesPlugin.getWorkspace().getRoot()).getJavaProjects();
			for (int i= 0; i < projects.length; i++) {
				IJavaProject project= projects[i];
				IType type= project.findType(qualifiedName);
				if (type != null) 
					result.add(type);
			}
		} catch (JavaModelException e) {
			ErrorDialog.openError(getParentShell(), JUnitMessages.TestMethodSelectionDialog_error_notfound_title, JUnitMessages.TestMethodSelectionDialog_error_notfound_message, e.getStatus()); 
			return null;
		}
		if (result.size() == 0) {
			String msg= Messages.format(JUnitMessages.TestMethodSelectionDialog_test_not_found, DescentUnittestPlugin.TEST_INTERFACE_NAME); 
			MessageDialog.openError(getParentShell(), JUnitMessages.TestMethodSelectionDialog_select_dialog_title, msg); 
			return null;
		}
		if (result.size() == 1)
			return (IType)result.toArray()[0];
		
		return selectTestType(result);
	}
	
	private IType selectTestType(Set result) {
		ILabelProvider labelProvider= new JavaElementLabelProvider(JavaElementLabelProvider.SHOW_PARAMETERS | JavaElementLabelProvider.SHOW_ROOT);
		ElementListSelectionDialog dialog= new ElementListSelectionDialog(getParentShell(), labelProvider);
		dialog.setTitle(JUnitMessages.TestMethodSelectionDialog_dialog_title);  
		String msg= Messages.format(JUnitMessages.TestMethodSelectionDialog_testproject, "unittest.framework.Test"); //$NON-NLS-1$
		dialog.setMessage(msg);
		IJavaProject[] projects= new IJavaProject[result.size()];
		IType[] testTypes= (IType[]) result.toArray(new IType[result.size()]);
		for (int i= 0; i < projects.length; i++) 
			projects[i]= testTypes[i].getJavaProject();
		dialog.setElements(projects);
		if (dialog.open() == Window.CANCEL)	
			return null;
		IJavaProject project= (IJavaProject) dialog.getFirstResult();
		for (int i= 0; i < testTypes.length; i++) {
			if (testTypes[i].getJavaProject().equals(project))
				return testTypes[i];
		}
		return null;	
	}
	
	public Object[] searchTestMethods(final IJavaElement element, final IType testType) throws InvocationTargetException, InterruptedException  {
		final TestReferenceCollector[] col= new TestReferenceCollector[1];
		
		IRunnableWithProgress runnable= new IRunnableWithProgress() {
			public void run(IProgressMonitor pm) throws InvocationTargetException {
				try {
					col[0]= doSearchTestMethods(element, testType, pm);
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				}
			}
		};
		PlatformUI.getWorkbench().getProgressService().busyCursorWhile(runnable);
		return col[0].getResult();
	}

	private TestReferenceCollector doSearchTestMethods(IJavaElement element, IType testType, IProgressMonitor pm) throws CoreException{
		int matchRule= SearchPattern.R_EXACT_MATCH | SearchPattern.R_CASE_SENSITIVE | SearchPattern.R_ERASURE_MATCH;
		SearchPattern pattern= SearchPattern.createPattern(element, IJavaSearchConstants.REFERENCES, matchRule);
		SearchParticipant[] participants= new SearchParticipant[] {SearchEngine.getDefaultSearchParticipant()};
		/* TODO IJavaSearchScope scope= SearchEngine.createHierarchyScope(testType); */
		TestReferenceCollector requestor= new TestReferenceCollector();
		new SearchEngine().search(pattern, participants, null /* TODO scope */, requestor, pm);
		return requestor;
	}
}
