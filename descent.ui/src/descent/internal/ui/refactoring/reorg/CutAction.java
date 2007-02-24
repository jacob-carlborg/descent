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
package descent.internal.ui.refactoring.reorg;

import java.util.Iterator;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.PlatformUI;

import descent.core.IJavaElement;
import descent.core.IType;
import descent.internal.corext.refactoring.reorg.ReorgUtils;
import descent.internal.ui.IJavaHelpContextIds;
import descent.internal.ui.JavaPlugin;
import descent.ui.actions.SelectionDispatchAction;

public class CutAction extends SelectionDispatchAction{

	private CopyToClipboardAction fCopyToClipboardAction;

	public CutAction(IWorkbenchSite site, Clipboard clipboard, SelectionDispatchAction pasteAction) {
		super(site);
		setText(ReorgMessages.CutAction_text); 
		fCopyToClipboardAction= new CopyToClipboardAction(site, clipboard, pasteAction);

		ISharedImages workbenchImages= JavaPlugin.getDefault().getWorkbench().getSharedImages();
		setDisabledImageDescriptor(workbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_CUT_DISABLED));
		setImageDescriptor(workbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_CUT));
		setHoverImageDescriptor(workbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_CUT));

		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IJavaHelpContextIds.CUT_ACTION);
	}

	public void selectionChanged(IStructuredSelection selection) {
		if (!selection.isEmpty()) {
			//try {
				// cannot cut top-level types. this deletes the cu and then you cannot paste because the cu is gone.
				if (!containsOnlyElementsInsideCompilationUnits(selection) || containsTopLevelTypes(selection)) {
					setEnabled(false);
					return;
				}
				fCopyToClipboardAction.selectionChanged(selection);
				setEnabled(fCopyToClipboardAction.isEnabled()
						/* TODO JDT UI refactor
						&& RefactoringAvailabilityTester.isDeleteAvailable(selection)
						*/
					);
			/*
			} catch (CoreException e) {
				// no ui here - this happens on selection changes
				// http://bugs.eclipse.org/bugs/show_bug.cgi?id=19253
				if (JavaModelUtil.isExceptionToBeLogged(e))
					JavaPlugin.log(e);
				setEnabled(false);
			}
			*/
		} else
			setEnabled(false);
	}

	private static boolean containsOnlyElementsInsideCompilationUnits(IStructuredSelection selection) {
		for (Iterator iter = selection.iterator(); iter.hasNext();) {
			Object object= iter.next();
			if (! (object instanceof IJavaElement && ReorgUtils.isInsideCompilationUnit((IJavaElement)object)))
				return false;
		}
		return true;
	}

	private static boolean containsTopLevelTypes(IStructuredSelection selection) {
		for (Iterator iter = selection.iterator(); iter.hasNext();) {
			Object each= iter.next();
			if ((each instanceof IType) && ((IType)each).getDeclaringType() == null)
				return true;
		}
		return false;
	}

	public void run(IStructuredSelection selection) {
		//try {
			selectionChanged(selection);
			if (isEnabled()) {
				fCopyToClipboardAction.run(selection);
				/* TODO JDT UI refactor
				RefactoringExecutionStarter.startCutRefactoring(selection.toArray(), getShell());
				*/
			}
		/*
		} catch (CoreException e) {
			ExceptionHandler.handle(e, RefactoringMessages.OpenRefactoringWizardAction_refactoring, RefactoringMessages.OpenRefactoringWizardAction_exception); 
		} catch (InterruptedException e) {
			//OK
		} catch (InvocationTargetException e) {
			ExceptionHandler.handle(e, RefactoringMessages.OpenRefactoringWizardAction_refactoring, RefactoringMessages.OpenRefactoringWizardAction_exception); 
		}
		*/
	}
}
