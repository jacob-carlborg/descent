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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import descent.core.IJavaProject;
import descent.core.JavaModelException;

import descent.internal.ui.wizards.NewWizardMessages;

/**
 * Operation to reset all output folders of the project's source folders
 */
public class ResetAllOutputFoldersOperation extends ClasspathModifierOperation {
    
    /**
     * Constructor
     * 
     * @param listener a <code>IClasspathModifierListener</code> that is notified about 
     * changes on classpath entries or <code>null</code> if no such notification is 
     * necessary.
     * @param informationProvider a provider to offer information to the operation
     * 
     * @see IClasspathInformationProvider
     * @see ClasspathModifier
     */
    public ResetAllOutputFoldersOperation(IClasspathModifierListener listener, IClasspathInformationProvider informationProvider) {
        super(listener, informationProvider, NewWizardMessages.NewSourceContainerWorkbookPage_ToolBar_Reset_tooltip, IClasspathInformationProvider.RESET); 
    }
    
    /**
     * Method which runs the actions with a progress monitor.<br>
     * 
     * This operation does not require any queries from the provider.
     * 
     * @param monitor a progress monitor, can be <code>null</code>
     */
    public void run(IProgressMonitor monitor) throws InvocationTargetException {
        List result= new ArrayList();
        fException= null;
        try {
            IJavaProject project= fInformationProvider.getJavaProject();
            resetOutputFolders(project, monitor);
        } catch (CoreException e) {
            fException= e;
            result= null;
        }
        super.handleResult(result, monitor);
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
        return true; // does not depend on any elements
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
        return NewWizardMessages.PackageExplorerActionGroup_FormText_Default_ResetAllOutputFolders; 
    }
}
