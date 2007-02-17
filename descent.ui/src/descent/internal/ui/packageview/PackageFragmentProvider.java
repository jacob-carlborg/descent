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
package descent.internal.ui.packageview;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

import descent.core.ElementChangedEvent;
import descent.core.IJavaElement;
import descent.core.IJavaElementDelta;
import descent.core.IJavaProject;
import descent.core.IPackageFragment;
import descent.core.IPackageFragmentRoot;
import descent.core.JavaCore;
import descent.core.JavaModelException;

import descent.ui.PreferenceConstants;

import descent.internal.ui.JavaPlugin;

/**
 * Content provider which provides package fragments for hierarchical
 * Package Explorer layout.
 * 
 * @since 2.1
 */
public class PackageFragmentProvider implements IPropertyChangeListener {

	private TreeViewer fViewer;
	private boolean fFoldPackages;
	
	public PackageFragmentProvider() {
		fFoldPackages= arePackagesFoldedInHierarchicalLayout();
		JavaPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(this);
	}
	
	/*
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(Object)
	 */
	public Object[] getChildren(Object parentElement) {
		try {
			if (parentElement instanceof IFolder) {
				IResource[] resources= ((IFolder) parentElement).members();
				return filter(getFolders(resources)).toArray();
			} else if (parentElement instanceof IJavaElement) {
				IJavaElement iJavaElement= (IJavaElement) parentElement;
				int type= iJavaElement.getElementType();
	
				switch (type) {
					case IJavaElement.JAVA_PROJECT: {
						IJavaProject project= (IJavaProject) iJavaElement;
						
						IPackageFragmentRoot root= project.findPackageFragmentRoot(project.getPath());
						if (root != null) {
							List children= getTopLevelChildren(root);
							return filter(children).toArray();
						} 
						break;
					}
					case IJavaElement.PACKAGE_FRAGMENT_ROOT: {
						IPackageFragmentRoot root= (IPackageFragmentRoot) parentElement;
						if (root.exists()) {
							return filter(getTopLevelChildren(root)).toArray();
						}
						break;
					}
					case IJavaElement.PACKAGE_FRAGMENT: {
						IPackageFragment packageFragment = (IPackageFragment) parentElement;
						if (!packageFragment.isDefaultPackage()) {
							IPackageFragmentRoot root= (IPackageFragmentRoot) packageFragment.getParent();
							List children = getPackageChildren(root, packageFragment);
							return filter(children).toArray();
						}
						break;
					}
					default :
						// do nothing
				}
			}
		} catch (CoreException e) {
			JavaPlugin.log(e);
		}
		return new Object[0];
	}
	
	private List filter(List children) throws JavaModelException {
		if (fFoldPackages) {
			int size= children.size();
			for (int i = 0; i < size; i++) {
				Object curr= children.get(i);
				if (curr instanceof IPackageFragment) {
					IPackageFragment fragment = (IPackageFragment) curr;
					if (!fragment.isDefaultPackage() && isEmpty(fragment)) {
						IPackageFragment collapsed= getCollapsed(fragment);
						if (collapsed != null) {
							children.set(i, collapsed); // replace with collapsed
						}
					}
				}
			}
		}
		return children;
	}
	
	private IPackageFragment getCollapsed(IPackageFragment pack) throws JavaModelException {
		IJavaElement[] children= ((IPackageFragmentRoot) pack.getParent()).getChildren();
		IPackageFragment child= getSinglePackageChild(pack, children);
		while (child != null && isEmpty(child)) {
			IPackageFragment collapsed= getSinglePackageChild(child, children);
			if (collapsed == null) {
				return child;
			}
			child= collapsed;
		}
		return child;
	}
		
	private boolean isEmpty(IPackageFragment fragment) throws JavaModelException {
		return !fragment.containsJavaResources() && fragment.getNonJavaResources().length == 0;
	}
	
	private static IPackageFragment getSinglePackageChild(IPackageFragment fragment, IJavaElement[] children) {
		String prefix= fragment.getElementName() + '.';
		int prefixLen= prefix.length();
		IPackageFragment found= null;
		for (int i= 0; i < children.length; i++) {
			IJavaElement element= children[i];
			String name= element.getElementName();
			if (name.startsWith(prefix) && name.length() > prefixLen && name.indexOf('.', prefixLen) == -1) {
				if (found == null) {
					found= (IPackageFragment) element;
				} else {
					return null;
				}
			}
		}
		return found;
	}
	
	
	private static List getPackageChildren(IPackageFragmentRoot parent, IPackageFragment fragment) throws JavaModelException {
		IJavaElement[] children= parent.getChildren();
		ArrayList list= new ArrayList(children.length);
		String prefix= fragment.getElementName() + '.';
		int prefixLen= prefix.length();
		for (int i= 0; i < children.length; i++) {
			IJavaElement element= children[i];
			if (element instanceof IPackageFragment) { // see bug 134256
				String name= element.getElementName();
				if (name.startsWith(prefix) && name.length() > prefixLen && name.indexOf('.', prefixLen) == -1) {
					list.add(element);
				}
			}
		}
		return list;
	}
	
	private static List getTopLevelChildren(IPackageFragmentRoot root) throws JavaModelException {
		IJavaElement[] elements= root.getChildren();
		ArrayList topLevelElements= new ArrayList(elements.length);
		for (int i= 0; i < elements.length; i++) {
			IJavaElement iJavaElement= elements[i];
			//if the name of the PackageFragment is the top level package it will contain no "." separators
			if (iJavaElement instanceof IPackageFragment && iJavaElement.getElementName().indexOf('.')==-1) {
				topLevelElements.add(iJavaElement);
			}
		}	
		return topLevelElements;
	}

	private List getFolders(IResource[] resources) throws JavaModelException {
		List list= new ArrayList(resources.length);
		for (int i= 0; i < resources.length; i++) {
			IResource resource= resources[i];
			if (resource instanceof IFolder) {
				IFolder folder= (IFolder) resource;
				IJavaElement element= JavaCore.create(folder);
				if (element instanceof IPackageFragment) {
					list.add(element);	
				} 
			}	
		}
		return list;
	}


	/*
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(Object)
	 */
	public Object getParent(Object element) {

		if (element instanceof IPackageFragment) {
			IPackageFragment frag = (IPackageFragment) element;
			//@Changed: a fix, before: if(frag.exists() && isEmpty(frag))
		
			return filterParent(getActualParent(frag));
		}
		return null;
	}

	private Object getActualParent(IPackageFragment fragment) {
		try {

			if (fragment.exists()) {
				IJavaElement parent = fragment.getParent();

				if ((parent instanceof IPackageFragmentRoot) && parent.exists()) {
					IPackageFragmentRoot root = (IPackageFragmentRoot) parent;
					if (root.isArchive()) {
						return findNextLevelParentByElementName(fragment);
					} else {

						IResource resource = fragment.getUnderlyingResource();
						if ((resource != null) && (resource instanceof IFolder)) {
							IFolder folder = (IFolder) resource;
							IResource res = folder.getParent();

							IJavaElement el = JavaCore.create(res);
							if (el != null) {
								return el;
							} else {
								return res;
							}
						}
					}
					return parent;
				}
			}

		} catch (JavaModelException e) {
			JavaPlugin.log(e);
		}
		return null;
	}
	
	private Object filterParent(Object parent) {
		if (fFoldPackages && (parent!=null)) {
			try {
				if (parent instanceof IPackageFragment) {
					IPackageFragment fragment = (IPackageFragment) parent;
					if (isEmpty(fragment) && hasSingleChild(fragment)) {
						return filterParent(getActualParent(fragment));
					}
				}
			} catch (JavaModelException e) {
				JavaPlugin.log(e);
			}
		}
		return parent;
	}

	private boolean hasSingleChild(IPackageFragment fragment) {
		return getChildren(fragment).length==1;
	}


	private Object findNextLevelParentByElementName(IPackageFragment child) {
		String name= child.getElementName();
		
		int index= name.lastIndexOf('.');
		if (index != -1) {
			String realParentName= name.substring(0, index);
			IPackageFragment element= ((IPackageFragmentRoot) child.getParent()).getPackageFragment(realParentName);
			if (element.exists()) {
				return element;
			}
		}
		return child.getParent();
	}


	/*
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(Object)
	 */
	public boolean hasChildren(Object element) {
		
		if (element instanceof IPackageFragment) {
			IPackageFragment fragment= (IPackageFragment) element;
			if(fragment.isDefaultPackage())
				return false;
		}
		return getChildren(element).length > 0;
	}

	/*
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(Object)
	 */
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	/*
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
		JavaPlugin.getDefault().getPreferenceStore().removePropertyChangeListener(this);
	}

	/**
	 * Called when the view is closed and opened.
	 * 
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(Viewer, Object, Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		fViewer= (TreeViewer)viewer;
	}
	
	/*
	 * @see descent.core.IElementChangedListener#elementChanged(descent.core.ElementChangedEvent)
	 */
	public void elementChanged(ElementChangedEvent event) {
		processDelta(event.getDelta());
	}
	
	public void processDelta(IJavaElementDelta delta) {

		int kind = delta.getKind();
		final IJavaElement element = delta.getElement();

		if (element instanceof IPackageFragment) {

			if (kind == IJavaElementDelta.REMOVED) {

				postRunnable(new Runnable() {
					public void run() {
						Control ctrl = fViewer.getControl();
						if (ctrl != null && !ctrl.isDisposed()) {
							if (!fFoldPackages)
								 fViewer.remove(element);
							else
								refreshGrandParent(element);
						}
					}
				});
				return;

			} else if (kind == IJavaElementDelta.ADDED) {

				final Object parent = getParent(element);
				if (parent != null) {
					postRunnable(new Runnable() {
						public void run() {
							Control ctrl = fViewer.getControl();
							if (ctrl != null && !ctrl.isDisposed()) {
								if (!fFoldPackages)
									 fViewer.add(parent, element);
								else
									refreshGrandParent(element);
							}
						}
					});
				}
				return;
			} 
		}
	}

	// XXX: needs to be revisited - might be a performance issue
	private void refreshGrandParent(final IJavaElement element) {
		if (element instanceof IPackageFragment) {
			Object gp= getGrandParent((IPackageFragment)element);
			if (gp instanceof IJavaElement) {
				IJavaElement el = (IJavaElement) gp;
				if(el.exists())
					fViewer.refresh(gp);
			} else if (gp instanceof IFolder) {
				IFolder folder= (IFolder)gp;
				if (folder.exists())
					fViewer.refresh(folder);
			}
		}
	}

	private Object getGrandParent(IPackageFragment element) {

		Object parent= findNextLevelParentByElementName(element);
		if (parent instanceof IPackageFragmentRoot) {
			IPackageFragmentRoot root= (IPackageFragmentRoot) parent;
			if(isRootProject(root))
				return root.getJavaProject();
			else return root;
		}

		Object grandParent= getParent(parent);
		if(grandParent==null){
			return parent;
		}
		return grandParent;
	}

	private boolean isRootProject(IPackageFragmentRoot root) {
		if (IPackageFragmentRoot.DEFAULT_PACKAGEROOT_PATH.equals(root.getElementName()))
			return true;
		return false;
	}
	
	private void postRunnable(final Runnable r) {
		Control ctrl= fViewer.getControl();
		if (ctrl != null && !ctrl.isDisposed()) {

			Display currentDisplay= Display.getCurrent();
			if (currentDisplay != null && currentDisplay.equals(ctrl.getDisplay()))
				ctrl.getDisplay().syncExec(r);
			else
				ctrl.getDisplay().asyncExec(r);
		}
	}

	/*
	 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event) {
		if (arePackagesFoldedInHierarchicalLayout() != fFoldPackages){
			fFoldPackages= arePackagesFoldedInHierarchicalLayout();
			if (fViewer != null && !fViewer.getControl().isDisposed()) {
				fViewer.getControl().setRedraw(false);
				Object[] expandedObjects= fViewer.getExpandedElements();
				fViewer.refresh();	
				fViewer.setExpandedElements(expandedObjects);
				fViewer.getControl().setRedraw(true);
			}
		}
	}

	private boolean arePackagesFoldedInHierarchicalLayout(){
		return PreferenceConstants.getPreferenceStore().getBoolean(PreferenceConstants.APPEARANCE_FOLD_PACKAGES_IN_PACKAGE_EXPLORER);
	}
}
