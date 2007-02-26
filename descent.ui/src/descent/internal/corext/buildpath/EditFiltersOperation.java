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

package descent.internal.corext.buildpath;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.jface.viewers.IStructuredSelection;

import descent.core.IJavaElement;
import descent.core.IJavaProject;
import descent.core.IPackageFragmentRoot;
import descent.core.JavaModelException;

import descent.internal.ui.wizards.NewWizardMessages;
import descent.internal.ui.wizards.buildpaths.newsourcepage.DialogPackageExplorerActionGroup;
import descent.internal.ui.wizards.buildpaths.newsourcepage.ClasspathModifierQueries.IInclusionExclusionQuery;
import descent.internal.ui.wizards.buildpaths.newsourcepage.GenerateBuildPathActionGroup.EditFilterAction;

/**
 * Operation to edit the inclusion / exclusion filters of an
 * <code>IJavaElement</code>.
 * 
 * @see descent.internal.corext.buildpath.ClasspathModifier#editFilters(IJavaElement, IJavaProject, IInclusionExclusionQuery, IProgressMonitor)
 */
public class EditFiltersOperation extends ClasspathModifierOperation {
	
	private final IClasspathInformationProvider fCPInformationProvider;
	private final IClasspathModifierListener fListener;

	/**
	 * Constructor
	 * 
	 * @param listener a <code>IClasspathModifierListener</code> that is notified about 
	 * changes on classpath entries or <code>null</code> if no such notification is 
	 * necessary.
	 * @param informationProvider a provider to offer information to the action
	 * 
	 * @see IClasspathInformationProvider
	 * @see ClasspathModifier
	 */
	public EditFiltersOperation(IClasspathModifierListener listener, IClasspathInformationProvider informationProvider) {
		super(listener, informationProvider, NewWizardMessages.NewSourceContainerWorkbookPage_ToolBar_Edit_tooltip, IClasspathInformationProvider.EDIT_FILTERS);
		fListener= listener;
		fCPInformationProvider= informationProvider; 
		
	}
	
	/**
	 * Method which runs the actions with a progress monitor.<br>
	 * 
	 * This operation requires the following query:
	 * <li>IInclusionExclusionQuery</li>
	 * 
	 * @param monitor a progress monitor, can be <code>null</code>
	 */
	public void run(IProgressMonitor monitor) throws InvocationTargetException {
		EditFilterAction action= new EditFilterAction();
		IStructuredSelection selection= fCPInformationProvider.getSelection();
		Object firstElement= selection.getFirstElement();
		action.selectionChanged(selection);
		action.run();
		List l= new ArrayList();
		l.add(firstElement);
		if (fListener != null) {
			List entries= action.getCPListElements();
			fListener.classpathEntryChanged(entries);
		}
		fCPInformationProvider.handleResult(l, null, IClasspathInformationProvider.EDIT_FILTERS);
	}
	
	/**
	 * Find out whether this operation can be executed on 
	 * the provided list of elements.
	 * 
	 * @param elements a list of elements
	 * @param types an array of types for each element, that is, 
	 * the type at position 'i' belongs to the selected element 
	 * at position 'i' 
	 * 
	 * @return <code>true</code> if the operation can be 
	 * executed on the provided list of elements, <code>
	 * false</code> otherwise.
	 * @throws JavaModelException 
	 */
	public boolean isValid(List elements, int[] types) throws JavaModelException {
		if (elements.size() != 1)
			return false;
		IJavaProject project= fInformationProvider.getJavaProject();
		Object element= elements.get(0);
		
		if (element instanceof IJavaProject) {
			if (isSourceFolder(project))
				return true;
		} else if (element instanceof IPackageFragmentRoot) {
			return ((IPackageFragmentRoot) element).getKind() == IPackageFragmentRoot.K_SOURCE;
		}
		return false;
	}
	
	/**
	 * Get a description for this operation. The description depends on 
	 * the provided type parameter, which must be a constant of 
	 * <code>DialogPackageExplorerActionGroup</code>. If the type is 
	 * <code>DialogPackageExplorerActionGroup.MULTI</code>, then the 
	 * description will be very general to describe the situation of 
	 * all the different selected objects as good as possible.
	 * 
	 * @param type the type of the selected object, must be a constant of 
	 * <code>DialogPackageExplorerActionGroup</code>.
	 * @return a string describing the operation
	 */
	public String getDescription(int type) {
		if (type == DialogPackageExplorerActionGroup.JAVA_PROJECT)
			return NewWizardMessages.PackageExplorerActionGroup_FormText_Edit; 
		if (type == DialogPackageExplorerActionGroup.PACKAGE_FRAGMENT_ROOT)
			return NewWizardMessages.PackageExplorerActionGroup_FormText_Edit; 
		if (type == DialogPackageExplorerActionGroup.MODIFIED_FRAGMENT_ROOT)
			return NewWizardMessages.PackageExplorerActionGroup_FormText_Edit; 
		return NewWizardMessages.PackageExplorerActionGroup_FormText_Default_Edit; 
	}
}
