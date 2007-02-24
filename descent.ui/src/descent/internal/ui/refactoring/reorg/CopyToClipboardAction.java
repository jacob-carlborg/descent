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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

import org.eclipse.core.resources.IResource;

import org.eclipse.swt.SWTError;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;

import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ResourceTransfer;

import descent.core.ICompilationUnit;
import descent.core.IJavaElement;
import descent.core.IType;
import descent.core.JavaModelException;

import descent.internal.corext.refactoring.TypedSource;
import descent.internal.corext.refactoring.reorg.IReorgEnablementPolicy;
import descent.internal.corext.refactoring.reorg.JavaElementTransfer;
import descent.internal.corext.refactoring.reorg.ParentChecker;
import descent.internal.corext.refactoring.reorg.ReorgUtils;
import descent.internal.corext.refactoring.util.JavaElementUtil;
import descent.internal.corext.util.JavaModelUtil;

import descent.ui.JavaElementLabelProvider;
import descent.ui.actions.SelectionDispatchAction;

import descent.internal.ui.IJavaHelpContextIds;
import descent.internal.ui.JavaPlugin;
import descent.internal.ui.util.ExceptionHandler;


public class CopyToClipboardAction extends SelectionDispatchAction{

	private final Clipboard fClipboard;
	private SelectionDispatchAction fPasteAction;//may be null
	private boolean fAutoRepeatOnFailure= false;

	public CopyToClipboardAction(IWorkbenchSite site, Clipboard clipboard, SelectionDispatchAction pasteAction) {
		super(site);
		setText(ReorgMessages.CopyToClipboardAction_0); 
		setDescription(ReorgMessages.CopyToClipboardAction_1); 
		Assert.isNotNull(clipboard);
		fClipboard= clipboard;
		fPasteAction= pasteAction;
		ISharedImages workbenchImages= getWorkbenchSharedImages();
		setDisabledImageDescriptor(workbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_COPY_DISABLED));
		setImageDescriptor(workbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
		setHoverImageDescriptor(workbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
		update(getSelection());

		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IJavaHelpContextIds.COPY_ACTION);
	}

	public void setAutoRepeatOnFailure(boolean autorepeatOnFailure){
		fAutoRepeatOnFailure= autorepeatOnFailure;
	}
	
	private static ISharedImages getWorkbenchSharedImages() {
		return JavaPlugin.getDefault().getWorkbench().getSharedImages();
	}

	/* (non-Javadoc)
	 * @see descent.ui.actions.SelectionDispatchAction#selectionChanged(org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void selectionChanged(IStructuredSelection selection) {
		try {
			List elements= selection.toList();
			IResource[] resources= ReorgUtils.getResources(elements);
			IJavaElement[] javaElements= ReorgUtils.getJavaElements(elements);
			if (elements.size() != resources.length + javaElements.length)
				setEnabled(false);
			else
				setEnabled(canEnable(resources, javaElements));
		} catch (JavaModelException e) {
			//no ui here - this happens on selection changes
			// http://bugs.eclipse.org/bugs/show_bug.cgi?id=19253
			if (JavaModelUtil.isExceptionToBeLogged(e))
				JavaPlugin.log(e);
			setEnabled(false);
		}
	}

	/* (non-Javadoc)
	 * @see descent.ui.actions.SelectionDispatchAction#run(org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void run(IStructuredSelection selection) {
		try {
			List elements= selection.toList();
			IResource[] resources= ReorgUtils.getResources(elements);
			IJavaElement[] javaElements= ReorgUtils.getJavaElements(elements);
			if (elements.size() == resources.length + javaElements.length && canEnable(resources, javaElements)) 
				doRun(resources, javaElements);
		} catch (CoreException e) {
			ExceptionHandler.handle(e, getShell(), ReorgMessages.CopyToClipboardAction_2, ReorgMessages.CopyToClipboardAction_3); 
		}
	}

	private void doRun(IResource[] resources, IJavaElement[] javaElements) throws CoreException {
		new ClipboardCopier(resources, javaElements, fClipboard, getShell(), fAutoRepeatOnFailure).copyToClipboard();

		// update the enablement of the paste action
		// workaround since the clipboard does not support callbacks				
		if (fPasteAction != null && fPasteAction.getSelection() != null)
			fPasteAction.update(fPasteAction.getSelection());
	}

	private boolean canEnable(IResource[] resources, IJavaElement[] javaElements) throws JavaModelException {
		return new CopyToClipboardEnablementPolicy(resources, javaElements).canEnable();
	}
	
	//----------------------------------------------------------------------------------------//
	
	private static class ClipboardCopier{
		private final boolean fAutoRepeatOnFailure;
		private final IResource[] fResources;
		private final IJavaElement[] fJavaElements;
		private final Clipboard fClipboard;
		private final Shell fShell;
		private final ILabelProvider fLabelProvider;
		
		private ClipboardCopier(IResource[] resources, IJavaElement[] javaElements, Clipboard clipboard, Shell shell, boolean autoRepeatOnFailure){
			Assert.isNotNull(resources);
			Assert.isNotNull(javaElements);
			Assert.isNotNull(clipboard);
			Assert.isNotNull(shell);
			fResources= resources;
			fJavaElements= javaElements;
			fClipboard= clipboard;
			fShell= shell;
			fLabelProvider= createLabelProvider();
			fAutoRepeatOnFailure= autoRepeatOnFailure;
		}

		public void copyToClipboard() throws CoreException{
			//Set<String> fileNames
			Set fileNames= new HashSet(fResources.length + fJavaElements.length);
			StringBuffer namesBuf = new StringBuffer();
			processResources(fileNames, namesBuf);
			processJavaElements(fileNames, namesBuf);

			IType[] mainTypes= ReorgUtils.getMainTypes(fJavaElements);
			ICompilationUnit[] cusOfMainTypes= ReorgUtils.getCompilationUnits(mainTypes);
			IResource[] resourcesOfMainTypes= ReorgUtils.getResources(cusOfMainTypes);
			addFileNames(fileNames, resourcesOfMainTypes);
			
			IResource[] cuResources= ReorgUtils.getResources(getCompilationUnits(fJavaElements));
			addFileNames(fileNames, cuResources);

			IResource[] resourcesForClipboard= ReorgUtils.union(fResources, ReorgUtils.union(cuResources, resourcesOfMainTypes));
			IJavaElement[] javaElementsForClipboard= ReorgUtils.union(fJavaElements, cusOfMainTypes);
			
			TypedSource[] typedSources= TypedSource.createTypedSources(javaElementsForClipboard);
			String[] fileNameArray= (String[]) fileNames.toArray(new String[fileNames.size()]);
			copyToClipboard(resourcesForClipboard, fileNameArray, namesBuf.toString(), javaElementsForClipboard, typedSources, 0);
		}

		private static IJavaElement[] getCompilationUnits(IJavaElement[] javaElements) {
			List cus= ReorgUtils.getElementsOfType(javaElements, IJavaElement.COMPILATION_UNIT);
			return (ICompilationUnit[]) cus.toArray(new ICompilationUnit[cus.size()]);
		}

		private void processResources(Set fileNames, StringBuffer namesBuf) {
			for (int i= 0; i < fResources.length; i++) {
				IResource resource= fResources[i];
				addFileName(fileNames, resource);

				if (i > 0)
					namesBuf.append('\n');
				namesBuf.append(getName(resource));
			}
		}

		private void processJavaElements(Set fileNames, StringBuffer namesBuf) {
			for (int i= 0; i < fJavaElements.length; i++) {
				IJavaElement element= fJavaElements[i];
				switch (element.getElementType()) {
					case IJavaElement.JAVA_PROJECT :
					case IJavaElement.PACKAGE_FRAGMENT_ROOT :
					case IJavaElement.PACKAGE_FRAGMENT :
					case IJavaElement.COMPILATION_UNIT :
					case IJavaElement.CLASS_FILE :
						addFileName(fileNames, ReorgUtils.getResource(element));
						break;
					default :
						break;
				}

				if (fResources.length > 0 || i > 0)
					namesBuf.append('\n');
				namesBuf.append(getName(element));
			}
		}

		private static void addFileNames(Set fileName, IResource[] resources) {
			for (int i= 0; i < resources.length; i++) {
				addFileName(fileName, resources[i]);
			}
		}

		private static void addFileName(Set fileName, IResource resource){
			if (resource == null)
				return;
			IPath location = resource.getLocation();
			if (location != null) {
				fileName.add(location.toOSString());
			} else {
				// not a file system path. skip file.
			}
		}
		
		private void copyToClipboard(IResource[] resources, String[] fileNames, String names, IJavaElement[] javaElements, TypedSource[] typedSources, int repeat){
			final int repeat_max_count= 10;
			try{
				fClipboard.setContents( createDataArray(resources, javaElements, fileNames, names, typedSources),
										createDataTypeArray(resources, javaElements, fileNames, typedSources));
			} catch (SWTError e) {
				if (e.code != DND.ERROR_CANNOT_SET_CLIPBOARD || repeat >= repeat_max_count)
					throw e;
				if (fAutoRepeatOnFailure) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e1) {
						// do nothing.
					}
				}
				if (fAutoRepeatOnFailure || MessageDialog.openQuestion(fShell, ReorgMessages.CopyToClipboardAction_4, ReorgMessages.CopyToClipboardAction_5)) 
					copyToClipboard(resources, fileNames, names, javaElements, typedSources, repeat+1);
			}
		}
		
		private static Transfer[] createDataTypeArray(IResource[] resources, IJavaElement[] javaElements, String[] fileNames, TypedSource[] typedSources) {
			List result= new ArrayList(4);
			if (resources.length != 0)
				result.add(ResourceTransfer.getInstance());
			if (javaElements.length != 0)
				result.add(JavaElementTransfer.getInstance());
			if (fileNames.length != 0)
				result.add(FileTransfer.getInstance());
			if (typedSources.length != 0)
				result.add(TypedSourceTransfer.getInstance());
			result.add(TextTransfer.getInstance());			
			return (Transfer[]) result.toArray(new Transfer[result.size()]);
		}

		private static Object[] createDataArray(IResource[] resources, IJavaElement[] javaElements, String[] fileNames, String names, TypedSource[] typedSources) {
			List result= new ArrayList(4);
			if (resources.length != 0)
				result.add(resources);
			if (javaElements.length != 0)
				result.add(javaElements);
			if (fileNames.length != 0)
				result.add(fileNames);
			if (typedSources.length != 0)
				result.add(typedSources);
			result.add(names);
			return result.toArray();
		}

		private static ILabelProvider createLabelProvider(){
			return new JavaElementLabelProvider(
				JavaElementLabelProvider.SHOW_VARIABLE
				+ JavaElementLabelProvider.SHOW_PARAMETERS
				+ JavaElementLabelProvider.SHOW_TYPE
			);		
		}
		private String getName(IResource resource){
			return fLabelProvider.getText(resource);
		}
		private String getName(IJavaElement javaElement){
			return fLabelProvider.getText(javaElement);
		}
	}
	
	private static class CopyToClipboardEnablementPolicy implements IReorgEnablementPolicy{
		private final IResource[] fResources;
		private final IJavaElement[] fJavaElements;
		public CopyToClipboardEnablementPolicy(IResource[] resources, IJavaElement[] javaElements){
			Assert.isNotNull(resources);
			Assert.isNotNull(javaElements);
			fResources= resources;
			fJavaElements= javaElements;
		}

		public boolean canEnable() throws JavaModelException{
			if (fResources.length + fJavaElements.length == 0)
				return false;
			if (hasProjects() && hasNonProjects())
				return false;
			if (! canCopyAllToClipboard())
				return false;
			if (! new ParentChecker(fResources, fJavaElements).haveCommonParent())
				return false;
			return true;
		}

		private boolean canCopyAllToClipboard() throws JavaModelException {
			for (int i= 0; i < fResources.length; i++) {
				if (! canCopyToClipboard(fResources[i])) return false;
			}
			for (int i= 0; i < fJavaElements.length; i++) {
				if (! canCopyToClipboard(fJavaElements[i])) return false;
			}
			return true;
		}

		private static boolean canCopyToClipboard(IJavaElement element) throws JavaModelException {
			if (element == null || ! element.exists())
				return false;
				
			if (JavaElementUtil.isDefaultPackage(element))		
				return false;
			
			return true;
		}

		private static boolean canCopyToClipboard(IResource resource) {
			return 	resource != null && 
					resource.exists() &&
					! resource.isPhantom() &&
					resource.getType() != IResource.ROOT;
		}

		private boolean hasProjects() {
			for (int i= 0; i < fResources.length; i++) {
				if (ReorgUtils.isProject(fResources[i])) return true;
			}
			for (int i= 0; i < fJavaElements.length; i++) {
				if (ReorgUtils.isProject(fJavaElements[i])) return true;
			}
			return false;
		}

		private boolean hasNonProjects() {
			for (int i= 0; i < fResources.length; i++) {
				if (! ReorgUtils.isProject(fResources[i])) return true;
			}
			for (int i= 0; i < fJavaElements.length; i++) {
				if (! ReorgUtils.isProject(fJavaElements[i])) return true;
			}
			return false;
		}
	}
}
