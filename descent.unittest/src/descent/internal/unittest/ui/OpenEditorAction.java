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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import descent.core.IJavaElement;
import descent.core.IJavaModel;
import descent.core.IJavaProject;
import descent.core.IType;
import descent.core.JavaModelException;

import org.eclipse.swt.widgets.Shell;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;

import org.eclipse.ui.texteditor.ITextEditor;

import descent.internal.ui.javaeditor.EditorUtility;

/**
 * Abstract Action for opening a Java editor.
 */
public abstract class OpenEditorAction extends Action {
	protected String fClassName;
	protected TestRunnerViewPart fTestRunner;
	private final boolean fActivate;
	
	protected OpenEditorAction(TestRunnerViewPart testRunner, String testClassName) {
		this(testRunner, testClassName, true);
	}

	public OpenEditorAction(TestRunnerViewPart testRunner, String className, boolean activate) {
		super(JUnitMessages.OpenEditorAction_action_label); 
		fClassName= className;
		fTestRunner= testRunner;
		fActivate= activate;
	}

	/*
	 * @see IAction#run()
	 */
	public void run() {
		ITextEditor textEditor= null;
		try {
			IJavaElement element= findElement(getLaunchedProject(), fClassName);
			if (element == null) {
				MessageDialog.openError(getShell(), 
					JUnitMessages.OpenEditorAction_error_cannotopen_title, JUnitMessages.OpenEditorAction_error_cannotopen_message); 
				return;
			} 
			textEditor= (ITextEditor)EditorUtility.openInEditor(element, fActivate);			
		} catch (CoreException e) {
			ErrorDialog.openError(getShell(), JUnitMessages.OpenEditorAction_error_dialog_title, JUnitMessages.OpenEditorAction_error_dialog_message, e.getStatus()); 
			return;
		}
		if (textEditor == null) {
			fTestRunner.registerInfoMessage(JUnitMessages.OpenEditorAction_message_cannotopen); 
			return;
		}
		reveal(textEditor);
	}
	
	protected Shell getShell() {
		return fTestRunner.getSite().getShell();
	}

	protected IJavaProject getLaunchedProject() {
		return fTestRunner.getLaunchedProject();
	}
	
	protected String getClassName() {
		return fClassName;
	}

	protected abstract IJavaElement findElement(IJavaProject project, String className) throws CoreException;
	
	protected abstract void reveal(ITextEditor editor);

	protected IType findType(IJavaProject project, String className) throws JavaModelException {
		return internalFindType(project, className, new HashSet());
	}

	private IType internalFindType(IJavaProject project, String className, Set/*<IJavaProject>*/ visitedProjects) throws JavaModelException {
		if (visitedProjects.contains(project))
			return null;
		
		IType type= project.findType(className, (IProgressMonitor) null);
		if (type != null)
			return type;
		
		//fix for bug 87492: visit required projects explicitly to also find not exported types
		visitedProjects.add(project);
		IJavaModel javaModel= project.getJavaModel();
		String[] requiredProjectNames= project.getRequiredProjectNames();
		for (int i= 0; i < requiredProjectNames.length; i++) {
			IJavaProject requiredProject= javaModel.getJavaProject(requiredProjectNames[i]);
			if (requiredProject.exists()) {
				type= internalFindType(requiredProject, className, visitedProjects);
				if (type != null)
					return type;
			}
		}
		return null;
	}
	
}
