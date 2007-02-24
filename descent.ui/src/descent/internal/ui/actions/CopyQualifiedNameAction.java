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
package descent.internal.ui.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.part.ResourceTransfer;
import org.eclipse.ui.texteditor.IUpdate;

import descent.core.IClassFile;
import descent.core.ICompilationUnit;
import descent.core.IImportDeclaration;
import descent.core.IJavaElement;
import descent.core.IMember;
import descent.core.IPackageDeclaration;
import descent.core.IPackageFragment;
import descent.core.JavaModelException;
import descent.internal.ui.JavaPlugin;
import descent.internal.ui.JavaPluginImages;
import descent.internal.ui.javaeditor.CompilationUnitEditor;
import descent.ui.JavaElementLabels;
import descent.ui.JavaUI;
import descent.ui.actions.SelectionDispatchAction;

public class CopyQualifiedNameAction extends SelectionDispatchAction {
	
	private static final long LABEL_FLAGS= new Long(JavaElementLabels.F_FULLY_QUALIFIED | JavaElementLabels.M_FULLY_QUALIFIED | JavaElementLabels.I_FULLY_QUALIFIED | JavaElementLabels.T_FULLY_QUALIFIED | JavaElementLabels.M_PARAMETER_TYPES | JavaElementLabels.USE_RESOLVED | JavaElementLabels.T_TYPE_PARAMETERS | JavaElementLabels.CU_QUALIFIED | JavaElementLabels.CF_QUALIFIED).longValue();

    //TODO: Make API
	public static final String JAVA_EDITOR_ACTION_DEFINITIONS_ID= "descent.ui.edit.text.java.copy.qualified.name"; //$NON-NLS-1$

	//TODO: Make API
	public static final String ACTION_HANDLER_ID= "descent.ui.actions.CopyQualifiedName"; //$NON-NLS-1$

    /**
     * System clipboard
     */
    private final Clipboard fClipboard;
	private CompilationUnitEditor fEditor;
	private final IAction fPasteAction;

    public CopyQualifiedNameAction(CompilationUnitEditor editor, Clipboard clipboard, IAction pastAction) {
    	this(editor.getSite(), clipboard, pastAction);
		fEditor= editor;
	}

	public CopyQualifiedNameAction(IWorkbenchSite site, Clipboard clipboard, IAction pastAction) {
		super(site);
		fPasteAction= pastAction;
		if (clipboard == null) {
			fClipboard= new Clipboard(getShell().getDisplay());
		} else {
			fClipboard= clipboard;
		}
		setText(ActionMessages.CopyQualifiedNameAction_ActionName);
		setToolTipText(ActionMessages.CopyQualifiedNameAction_ToolTipText);
		setDisabledImageDescriptor(JavaPluginImages.DESC_DLCL_COPY_QUALIFIED_NAME);
		setImageDescriptor(JavaPluginImages.DESC_ELCL_COPY_QUALIFIED_NAME);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void selectionChanged(IStructuredSelection selection) {
		setEnabled(canEnable(selection.toArray()));
	}

	private boolean canEnable(Object[] objects) {
		for (int i= 0; i < objects.length; i++) {
			Object element= objects[i];
			if (isValideElement(element))
				return true;
		}

		return false;
	}
	
	private boolean isValideElement(Object element) {
		if (element instanceof IMember)
			return true;
		
		if (element instanceof IClassFile)
			return true;
		
		if (element instanceof ICompilationUnit)
			return true;
		
		if (element instanceof IPackageDeclaration)
			return true;
		
		if (element instanceof IImportDeclaration)
			return true;
		
		if (element instanceof IPackageFragment)
			return true;
		
		return false;
	}
	
	/* (non-Javadoc)
     * @see org.eclipse.jface.action.Action#run()
     */
    public void run() {
    	
    	try {
			IJavaElement[] elements= getSelectedElements();
			if (elements == null) {
				MessageDialog.openInformation(getShell(), ActionMessages.CopyQualifiedNameAction_InfoDialogTitel, ActionMessages.CopyQualifiedNameAction_NoElementToQualify);
				return;
			}

			Object[] data= null;
			Transfer[] dataTypes= null;
			
			if (elements.length == 1) {
				String qualifiedName= JavaElementLabels.getElementLabel(elements[0], LABEL_FLAGS);
				IResource resource= elements[0].getCorrespondingResource();
				
				if (resource != null) {
					IPath location= resource.getLocation();
					if (location != null) {
						data= new Object[] {qualifiedName, resource, new String[] {location.toOSString()}};
						dataTypes= new Transfer[] {TextTransfer.getInstance(), ResourceTransfer.getInstance(), FileTransfer.getInstance()};
					} else {
						data= new Object[] {qualifiedName, resource};
						dataTypes= new Transfer[] {TextTransfer.getInstance(), ResourceTransfer.getInstance()};
					}
				} else {
					data= new Object[] {qualifiedName};
					dataTypes= new Transfer[] {TextTransfer.getInstance()};
				}
			} else {
				StringBuffer buf= new StringBuffer();
				buf.append(JavaElementLabels.getElementLabel(elements[0], LABEL_FLAGS));
				for (int i= 1; i < elements.length; i++) {
					IJavaElement element= elements[i];
					
					String qualifiedName= JavaElementLabels.getElementLabel(element, LABEL_FLAGS);
					buf.append('\r').append('\n').append(qualifiedName);
				}
				data= new Object[] {buf.toString()};
				dataTypes= new Transfer[] {TextTransfer.getInstance()};
			}
			
			try {
				fClipboard.setContents(data, dataTypes);
				
				// update the enablement of the paste action
				// workaround since the clipboard does not support callbacks				
				if (fPasteAction != null) {
					if (fPasteAction instanceof SelectionDispatchAction) {
						if (((SelectionDispatchAction)fPasteAction).getSelection() != null) {
							((SelectionDispatchAction)fPasteAction).update(((SelectionDispatchAction)fPasteAction).getSelection());
						}
					} else if (fPasteAction instanceof IUpdate) {
						((IUpdate)fPasteAction).update();
					}
				}
			} catch (SWTError e) {
	            if (e.code != DND.ERROR_CANNOT_SET_CLIPBOARD) {
					throw e;
				}
	            if (MessageDialog.openQuestion(getShell(), ActionMessages.CopyQualifiedNameAction_ErrorTitle, ActionMessages.CopyQualifiedNameAction_ErrorDescription)) {
	            	fClipboard.setContents(data, dataTypes);
				}
	        }
		} catch (JavaModelException e) {
			JavaPlugin.log(e);
		}
    }

    private IJavaElement[] getSelectedElements() throws JavaModelException {
    	if (fEditor != null) {
    		IJavaElement element= getSelectedElement(fEditor);
    		if (element == null)
    			return null;
    		
    		return new IJavaElement[] {element}; 
    	}
    	
    	ISelection selection= getSelection();
    	if (!(selection instanceof IStructuredSelection))
    		return null;
    	
    	List result= new ArrayList();
    	for (Iterator iter= ((IStructuredSelection)selection).iterator(); iter.hasNext();) {
			Object element= iter.next();
			if (isValideElement(element))
				result.add(element);
		}
    	if (result.isEmpty())
    		return null;
    	
		return (IJavaElement[])result.toArray(new IJavaElement[result.size()]);
	}

	private IJavaElement getSelectedElement(CompilationUnitEditor editor) {
		ISourceViewer viewer= editor.getViewer();
		if (viewer == null)
			return null;
		
		Point selectedRange= viewer.getSelectedRange();
		int length= selectedRange.y;
		int offset= selectedRange.x;
		
		ICompilationUnit cu= JavaUI.getWorkingCopyManager().getWorkingCopy(editor.getEditorInput());
		if (cu == null)
			return null;
		
		/* TODO JDT UI code completion
		AssistContext context= new AssistContext(cu, offset, length);
		ASTNode node= context.getCoveringNode();
		
		IBinding binding= null;
		if (node instanceof Name) {
			binding= ((Name)node).resolveBinding();
		} else if (node instanceof MethodInvocation) {
			binding= ((MethodInvocation)node).resolveMethodBinding();
		} else if (node instanceof MethodDeclaration) {
			binding= ((MethodDeclaration)node).resolveBinding();
		} else if (node instanceof Type) {
			binding= ((Type)node).resolveBinding();
		} else if (node instanceof AnonymousClassDeclaration) {
			binding= ((AnonymousClassDeclaration)node).resolveBinding();
		} else if (node instanceof TypeDeclaration) {
			binding= ((TypeDeclaration)node).resolveBinding();
		} else if (node instanceof CompilationUnit) {
			return ((CompilationUnit)node).getJavaElement();
		} else if (node instanceof Expression) {
			binding= ((Expression)node).resolveTypeBinding();
		} else if (node instanceof ImportDeclaration) {
			binding= ((ImportDeclaration)node).resolveBinding();
		} else if (node instanceof MemberRef) {
			binding= ((MemberRef)node).resolveBinding();
		} else if (node instanceof MemberValuePair) {
			binding= ((MemberValuePair)node).resolveMemberValuePairBinding();
		} else if (node instanceof PackageDeclaration) {
			binding= ((PackageDeclaration)node).resolveBinding();
		} else if (node instanceof TypeParameter) {
			binding= ((TypeParameter)node).resolveBinding();
		} else if (node instanceof VariableDeclaration) {
			binding= ((VariableDeclaration)node).resolveBinding();
		} 
			
		if (binding != null)
			return binding.getJavaElement();
		*/

		return null;
	}
}