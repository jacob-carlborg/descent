/*******************************************************************************
 * Copyright (c) 2003, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package descent.internal.ui.navigator;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.TreeViewer;

import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionConstants;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;

import descent.ui.actions.OpenAction;
import descent.ui.actions.OpenEditorActionGroup;

public class PackageExplorerOpenActionProvider extends CommonActionProvider {
	

	private IAction fOpenAndExpand;
	private OpenEditorActionGroup fOpenGroup;

	private boolean fInViewPart = false;

	public void fillActionBars(IActionBars actionBars) { 
		if(fInViewPart) { 
			fOpenGroup.fillActionBars(actionBars); 
			
			if(fOpenAndExpand == null && fOpenGroup.getOpenAction().isEnabled()) // TODO: is not updated!
				actionBars.setGlobalActionHandler(ICommonActionConstants.OPEN, fOpenGroup.getOpenAction());
			// Descent: commented to not expand the tree each time we open a module, I don't know why it's not working well
			else // if(fOpenAndExpand.isEnabled())
				actionBars.setGlobalActionHandler(ICommonActionConstants.OPEN, fOpenAndExpand);
		}

	}

	public void fillContextMenu(IMenuManager menu) {

		if (fInViewPart) {
			if (fOpenGroup.getOpenAction().isEnabled()) {
				fOpenGroup.fillContextMenu(menu);
			} 
		}
	}

	public void init(ICommonActionExtensionSite site) {

		ICommonViewerWorkbenchSite workbenchSite = null;
		if (site.getViewSite() instanceof ICommonViewerWorkbenchSite)
			workbenchSite = (ICommonViewerWorkbenchSite) site.getViewSite();
 
		if (workbenchSite != null) {
			if (workbenchSite.getPart() != null && workbenchSite.getPart() instanceof IViewPart) {
				IViewPart viewPart = (IViewPart) workbenchSite.getPart();

				fOpenGroup = new OpenEditorActionGroup(viewPart); 

				if (site.getStructuredViewer() instanceof TreeViewer) {
					// Changed for Descent: we don't want the tree to expand each time we open a module
//					fOpenAndExpand = new OpenAndExpand(workbenchSite.getSite(), (OpenAction) fOpenGroup.getOpenAction(), (TreeViewer) site.getStructuredViewer());
					fOpenAndExpand = (OpenAction) fOpenGroup.getOpenAction();
				}
				fInViewPart = true;
			} 
		} 
	}

	public void setContext(ActionContext context) {
		super.setContext(context);
		if (fInViewPart) {
			fOpenGroup.setContext(context); 
		}
	}

}
