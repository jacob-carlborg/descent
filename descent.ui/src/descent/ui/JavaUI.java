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
package descent.ui;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.util.Assert;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.ui.texteditor.IDocumentProvider;

import descent.core.IClasspathEntry;
import descent.core.ICompilationUnit;
import descent.core.IJavaElement;
import descent.core.IJavaProject;
import descent.core.IPackageFragmentRoot;
import descent.core.ISourceReference;
import descent.core.ITypeRoot;
import descent.core.JavaModelException;
import descent.core.search.IJavaSearchScope;
import descent.core.search.SearchEngine;
import descent.internal.corext.javadoc.JavaDocLocations;
import descent.internal.corext.refactoring.reorg.JavaElementTransfer;
import descent.internal.ui.JavaPlugin;
import descent.internal.ui.SharedImages;
import descent.internal.ui.dialogs.PackageSelectionDialog;
import descent.internal.ui.javaeditor.EditorUtility;
import descent.internal.ui.util.BusyIndicatorRunnableContext;
import descent.ui.text.IColorManager;

/**
 * Central access point for the Java UI plug-in (id <code>"descent.ui"</code>).
 * This class provides static methods for:
 * <ul>
 *  <li> creating various kinds of selection dialogs to present a collection
 *       of Java elements to the user and let them make a selection.</li>
 *  <li> opening a Java editor on a compilation unit.</li> 
 * </ul>
 * <p>
 * This class provides static methods and fields only; it is not intended to be
 * instantiated or subclassed by clients.
 * </p>
 */
public final class JavaUI {
	
	private static ISharedImages fgSharedImages= null;
	
	private JavaUI() {
		// prevent instantiation of JavaUI.
	}
	
	/**
	 * The id of the Java plug-in (value <code>"descent.ui"</code>).
	 */	
	public static final String ID_PLUGIN= "descent.ui"; //$NON-NLS-1$
	
	/**
	 * The id of the Java perspective
	 * (value <code>"descent.ui.JavaPerspective"</code>).
	 */	
	public static final String ID_PERSPECTIVE= 		"descent.ui.JavaPerspective"; //$NON-NLS-1$
	
	/**
	 * The id of the Java hierarchy perspective
	 * (value <code>"descent.ui.JavaHierarchyPerspective"</code>).
	 */	
	public static final String ID_HIERARCHYPERSPECTIVE= "descent.ui.JavaHierarchyPerspective"; //$NON-NLS-1$

	/**
	 * The id of the Java action set
	 * (value <code>"descent.ui.JavaActionSet"</code>).
	 */
	public static final String ID_ACTION_SET= "descent.ui.JavaActionSet"; //$NON-NLS-1$

	/**
	 * The id of the Java Element Creation action set
	 * (value <code>"descent.ui.JavaElementCreationActionSet"</code>).
	 * 
	 * @since 2.0
	 */
	public static final String ID_ELEMENT_CREATION_ACTION_SET= "descent.ui.JavaElementCreationActionSet"; //$NON-NLS-1$
	
	/**
	 * The id of the Java Coding action set
	 * (value <code>"descent.ui.CodingActionSet"</code>).
	 * 
	 * @since 2.0
	 */
	public static final String ID_CODING_ACTION_SET= "descent.ui.CodingActionSet"; //$NON-NLS-1$

	/**
	 * The id of the Java action set for open actions
	 * (value <code>"descent.ui.A_OpenActionSet"</code>).
	 * 
	 * @since 2.0
	 */
	public static final String ID_OPEN_ACTION_SET= "descent.ui.A_OpenActionSet"; //$NON-NLS-1$

	/**
	 * The id of the Java Search action set
	 * (value <code>descent.ui.SearchActionSet"</code>).
	 * 
	 * @since 2.0
	 */
	public static final String ID_SEARCH_ACTION_SET= "descent.ui.SearchActionSet"; //$NON-NLS-1$
	
	/**
	 * The editor part id of the editor that presents Java compilation units
	 * (value <code>"descent.ui.CompilationUnitEditor"</code>).
	 */	
	public static final String ID_CU_EDITOR=			"descent.ui.CompilationUnitEditor"; //$NON-NLS-1$
	
	/**
	 * The editor part id of the editor that presents Java binary class files
	 * (value <code>"descent.ui.ClassFileEditor"</code>).
	 */
	public static final String ID_CF_EDITOR=			"descent.ui.ClassFileEditor"; //$NON-NLS-1$
	
	/**
	 * The editor part id of the code snippet editor
	 * (value <code>"descent.ui.SnippetEditor"</code>).
	 */
	public static final String ID_SNIPPET_EDITOR= 		"descent.ui.SnippetEditor"; //$NON-NLS-1$

	/**
	 * The view part id of the Packages view
	 * (value <code>"descent.ui.PackageExplorer"</code>).
	 * <p>
	 * When this id is used to access
	 * a view part with <code>IWorkbenchPage.findView</code> or 
	 * <code>showView</code>, the returned <code>IViewPart</code>
	 * can be safely cast to an <code>IPackagesViewPart</code>.
	 * </p>
	 *
	 * @see IPackagesViewPart
	 * @see org.eclipse.ui.IWorkbenchPage#findView(java.lang.String)
	 * @see org.eclipse.ui.IWorkbenchPage#showView(java.lang.String)
	 */ 
	public static final String ID_PACKAGES= 			"descent.ui.PackageExplorer"; //$NON-NLS-1$
	
	/** 
	 * The view part id of the type hierarchy part
	 * (value <code>"descent.ui.TypeHierarchy"</code>).
	 * <p>
	 * When this id is used to access
	 * a view part with <code>IWorkbenchPage.findView</code> or 
	 * <code>showView</code>, the returned <code>IViewPart</code>
	 * can be safely cast to an <code>ITypeHierarchyViewPart</code>.
	 * </p>
	 *
	 * @see ITypeHierarchyViewPart
	 * @see org.eclipse.ui.IWorkbenchPage#findView(java.lang.String)
	 * @see org.eclipse.ui.IWorkbenchPage#showView(java.lang.String)
	 */ 
	public static final String ID_TYPE_HIERARCHY= 		"descent.ui.TypeHierarchy"; //$NON-NLS-1$

	/** 
	 * The view part id of the source (declaration) view
	 * (value <code>"descent.ui.SourceView"</code>).
	 *
	 * @see org.eclipse.ui.IWorkbenchPage#findView(java.lang.String)
	 * @see org.eclipse.ui.IWorkbenchPage#showView(java.lang.String)
	 * @since 3.0
	 */ 
	public static final String ID_SOURCE_VIEW=	"descent.ui.SourceView"; //$NON-NLS-1$
	
	/** 
	 * The view part id of the compile-time view
	 * (value <code>"descent.ui.CompileTimeView"</code>).
	 *
	 * @see org.eclipse.ui.IWorkbenchPage#findView(java.lang.String)
	 * @see org.eclipse.ui.IWorkbenchPage#showView(java.lang.String)
	 * @since 3.0
	 */ 
	public static final String ID_COMPILE_TIME_VIEW=	"descent.ui.CompileTimeView"; //$NON-NLS-1$
	
	/** 
	 * The view part id of the Javadoc view
	 * (value <code>"descent.ui.JavadocView"</code>).
	 *
	 * @see org.eclipse.ui.IWorkbenchPage#findView(java.lang.String)
	 * @see org.eclipse.ui.IWorkbenchPage#showView(java.lang.String)
	 * @since 3.0
	 */ 
	public static final String ID_JAVADOC_VIEW=	"descent.ui.JavadocView"; //$NON-NLS-1$
	
	/**
	 * The id of the Java Browsing Perspective
	 * (value <code>"descent.ui.JavaBrowsingPerspective"</code>).
	 * 
	 * @since 2.0
	 */
	public static String ID_BROWSING_PERSPECTIVE= "descent.ui.JavaBrowsingPerspective"; //$NON-NLS-1$

	/**
	 * The view part id of the Java Browsing Projects view
	 * (value <code>"descent.ui.ProjectsView"</code>).
	 * 
	 * @since 2.0
	 */
	public static String ID_PROJECTS_VIEW= "descent.ui.ProjectsView"; //$NON-NLS-1$

	/**
	 * The view part id of the Java Browsing Packages view
	 * (value <code>"descent.ui.PackagesView"</code>).
	 * 
	 * @since 2.0
	 */
	public static String ID_PACKAGES_VIEW= "descent.ui.PackagesView"; //$NON-NLS-1$

	/**
	 * The view part id of the Java Browsing Types view
	 * (value <code>"descent.ui.TypesView"</code>).
	 * 
	 * @since 2.0
	 */
	public static String ID_TYPES_VIEW= "descent.ui.TypesView"; //$NON-NLS-1$

	/**
	 * The view part id of the Java Browsing Members view
	 * (value <code>"descent.ui.MembersView"</code>).
	 * 
	 * @since 2.0
	 */
	public static String ID_MEMBERS_VIEW= "descent.ui.MembersView"; //$NON-NLS-1$

	/**
	 * The class org.eclipse.debug.core.model.IProcess allows attaching
	 * String properties to processes. The Java UI contributes a property
	 * page for IProcess that will show the contents of the property
	 * with this key.
	 * The intent of this property is to show the command line a process
	 * was launched with.
	 * @deprecated
	 */
	public final static String ATTR_CMDLINE= "descent.ui.launcher.cmdLine"; //$NON-NLS-1$

	/**
	 * Returns the shared images for the Java UI.
	 *
	 * @return the shared images manager
	 */
	public static ISharedImages getSharedImages() {
		if (fgSharedImages == null)
			fgSharedImages= new SharedImages();
			
		return fgSharedImages;
	}
	 
	/**
	 * Creates a selection dialog that lists all packages of the given Java project.
	 * The caller is responsible for opening the dialog with <code>Window.open</code>,
	 * and subsequently extracting the selected package (of type
	 * <code>IPackageFragment</code>) via <code>SelectionDialog.getResult</code>.
	 * 
	 * @param parent the parent shell of the dialog to be created
	 * @param project the Java project
	 * @param style flags defining the style of the dialog; the valid flags are:
	 *   <code>IJavaElementSearchConstants.CONSIDER_BINARIES</code>, indicating that 
	 *   packages from binary package fragment roots should be included in addition
	 *   to those from source package fragment roots;
	 *   <code>IJavaElementSearchConstants.CONSIDER_REQUIRED_PROJECTS</code>, indicating that
	 *   packages from required projects should be included as well.
	 * @param filter the initial pattern to filter the set of packages. For example "com" shows 
	 * all packages starting with "com". The meta character '?' representing any character and 
	 * '*' representing any string are supported. Clients can pass an empty string if no filtering 
	 * is required.
	 * @return a new selection dialog
	 * @exception JavaModelException if the selection dialog could not be opened
	 * 
	 * @since 2.0
	 */
	public static SelectionDialog createPackageDialog(Shell parent, IJavaProject project, int style, String filter) throws JavaModelException {
		Assert.isTrue((style | IJavaElementSearchConstants.CONSIDER_BINARIES | IJavaElementSearchConstants.CONSIDER_REQUIRED_PROJECTS) ==
			(IJavaElementSearchConstants.CONSIDER_BINARIES | IJavaElementSearchConstants.CONSIDER_REQUIRED_PROJECTS));

		IPackageFragmentRoot[] roots= null;
		if ((style & IJavaElementSearchConstants.CONSIDER_REQUIRED_PROJECTS) != 0) {
		    roots= project.getAllPackageFragmentRoots();
		} else {	
			roots= project.getPackageFragmentRoots();	
		}
		
		List consideredRoots= null;
		if ((style & IJavaElementSearchConstants.CONSIDER_BINARIES) != 0) {
			consideredRoots= Arrays.asList(roots);
		} else {
			consideredRoots= new ArrayList(roots.length);
			for (int i= 0; i < roots.length; i++) {
				IPackageFragmentRoot root= roots[i];
				if (root.getKind() != IPackageFragmentRoot.K_BINARY)
					consideredRoots.add(root);
					
			}
		}
		
		IJavaSearchScope searchScope= SearchEngine.createJavaSearchScope((IJavaElement[])consideredRoots.toArray(new IJavaElement[consideredRoots.size()]));
		BusyIndicatorRunnableContext context= new BusyIndicatorRunnableContext();
		if (style == 0 || style == IJavaElementSearchConstants.CONSIDER_REQUIRED_PROJECTS) {
			return createPackageDialog(parent, context, searchScope, false, true, filter);
		} else {
			return createPackageDialog(parent, context, searchScope, false, false, filter);
		}
	}
	
	/**
	 * Creates a selection dialog that lists all packages of the given Java search scope.
	 * The caller is responsible for opening the dialog with <code>Window.open</code>,
	 * and subsequently extracting the selected package (of type
	 * <code>IPackageFragment</code>) via <code>SelectionDialog.getResult</code>.
	 * 
	 * @param parent the parent shell of the dialog to be created
	 * @param context the runnable context to run the search in
	 * @param scope the scope defining the available packages.
	 * @param multipleSelection true if multiple selection is allowed
	 * @param removeDuplicates true if only one package is shown per package name
	 * @param filter the initial pattern to filter the set of packages. For example "com" shows 
	 * all packages starting with "com". The meta character '?' representing any character and 
	 * '*' representing any string are supported. Clients can pass an empty string if no filtering 
	 * is required.
	 * @return a new selection dialog
	 * 
	 * @since 3.2
	 */
	public static SelectionDialog createPackageDialog(Shell parent, IRunnableContext context, IJavaSearchScope scope, 
			boolean multipleSelection, boolean removeDuplicates, String filter) {
		
		int flag= removeDuplicates ? PackageSelectionDialog.F_REMOVE_DUPLICATES : 0;
		PackageSelectionDialog dialog= new PackageSelectionDialog(parent, context, flag, scope);
		dialog.setFilter(filter);
		dialog.setIgnoreCase(false);
		dialog.setMultipleSelection(multipleSelection);
		return dialog;
	}
	
	/**
	 * Creates a selection dialog that lists all packages of the given Java project.
	 * The caller is responsible for opening the dialog with <code>Window.open</code>,
	 * and subsequently extracting the selected package (of type
	 * <code>IPackageFragment</code>) via <code>SelectionDialog.getResult</code>.
	 * 
	 * @param parent the parent shell of the dialog to be created
	 * @param project the Java project
	 * @param style flags defining the style of the dialog; the valid flags are:
	 *   <code>IJavaElementSearchConstants.CONSIDER_BINARIES</code>, indicating that 
	 *   packages from binary package fragment roots should be included in addition
	 *   to those from source package fragment roots;
	 *   <code>IJavaElementSearchConstants.CONSIDER_REQUIRED_PROJECTS</code>, indicating that
	 *   packages from required projects should be included as well.
	 * @return a new selection dialog
	 * @exception JavaModelException if the selection dialog could not be opened
	 */
	public static SelectionDialog createPackageDialog(Shell parent, IJavaProject project, int style) throws JavaModelException {
		return createPackageDialog(parent, project, style, ""); //$NON-NLS-1$
	}
	
	/**
	 * Creates a selection dialog that lists all packages under the given package 
	 * fragment root.
	 * The caller is responsible for opening the dialog with <code>Window.open</code>,
	 * and subsequently extracting the selected package (of type
	 * <code>IPackageFragment</code>) via <code>SelectionDialog.getResult</code>.
	 * 
	 * @param parent the parent shell of the dialog to be created
	 * @param root the package fragment root
	 * @param filter the initial pattern to filter the set of packages. For example "com" shows 
	 * all packages starting with "com". The meta character '?' representing any character and 
	 * '*' representing any string are supported. Clients can pass an empty string if no filtering 
	 * is required.
	 * @return a new selection dialog
	 * @exception JavaModelException if the selection dialog could not be opened
	 * 
	 * @since 2.0
	 */
	public static SelectionDialog createPackageDialog(Shell parent, IPackageFragmentRoot root, String filter) throws JavaModelException {
		IJavaSearchScope scope= SearchEngine.createJavaSearchScope(new IJavaElement[] {root});
		BusyIndicatorRunnableContext context= new BusyIndicatorRunnableContext();
		return createPackageDialog(parent, context, scope, false, true, filter);
	}

	/**
	 * Creates a selection dialog that lists all packages under the given package 
	 * fragment root.
	 * The caller is responsible for opening the dialog with <code>Window.open</code>,
	 * and subsequently extracting the selected package (of type
	 * <code>IPackageFragment</code>) via <code>SelectionDialog.getResult</code>.
	 * 
	 * @param parent the parent shell of the dialog to be created
	 * @param root the package fragment root
	 * @return a new selection dialog
	 * @exception JavaModelException if the selection dialog could not be opened
	 */
	public static SelectionDialog createPackageDialog(Shell parent, IPackageFragmentRoot root) throws JavaModelException {
		return createPackageDialog(parent, root, ""); //$NON-NLS-1$
	}

	/**
	 * Creates a selection dialog that lists all types in the given project.
	 * The caller is responsible for opening the dialog with <code>Window.open</code>,
	 * and subsequently extracting the selected type(s) (of type
	 * <code>IType</code>) via <code>SelectionDialog.getResult</code>.
	 * 
	 * @param parent the parent shell of the dialog to be created
	 * @param context the runnable context used to show progress when the dialog
	 *   is being populated
	 * @param project the Java project
	 * @param style flags defining the style of the dialog; the only valid values are
	 *   <code>IJavaElementSearchConstants.CONSIDER_CLASSES</code>,
	 *   <code>IJavaElementSearchConstants.CONSIDER_INTERFACES</code>, 
	 *   <code>IJavaElementSearchConstants.CONSIDER_ANNOTATION_TYPES</code>,
	 *   <code>IJavaElementSearchConstants.CONSIDER_ENUMS</code>,
	 *   <code>IJavaElementSearchConstants.CONSIDER_ALL_TYPES</code>,
	 *   <code>IJavaElementSearchConstants.CONSIDER_CLASSES_AND_INTERFACES</code>
	 *   <code>IJavaElementSearchConstants.CONSIDER_CLASSES_AND_ENUMS</code>. Please note that
	 *   the bitwise OR combination of the elementary constants is not supported.
	 * @param multipleSelection <code>true</code> if multiple selection is allowed
	 * 
	 * @return a new selection dialog
	 * 
	 * @exception JavaModelException if the selection dialog could not be opened
	 */
	public static SelectionDialog createTypeDialog(Shell parent, IRunnableContext context, IProject project, int style, boolean multipleSelection) throws JavaModelException {
		/* TODO JDT UI dialogs
		IJavaSearchScope scope= SearchEngine.createJavaSearchScope(new IJavaProject[] { JavaCore.create(project) });
		return createTypeDialog(parent, context, scope, style, multipleSelection);
		*/
		return null;
	}
	
	/**
	 * Creates a selection dialog that lists all types in the given scope.
	 * The caller is responsible for opening the dialog with <code>Window.open</code>,
	 * and subsequently extracting the selected type(s) (of type
	 * <code>IType</code>) via <code>SelectionDialog.getResult</code>.
	 * 
	 * @param parent the parent shell of the dialog to be created
	 * @param context the runnable context used to show progress when the dialog
	 *   is being populated
	 * @param scope the scope that limits which types are included
	 * @param style flags defining the style of the dialog; the only valid values are
	 *   <code>IJavaElementSearchConstants.CONSIDER_CLASSES</code>,
	 *   <code>IJavaElementSearchConstants.CONSIDER_INTERFACES</code>, 
	 *   <code>IJavaElementSearchConstants.CONSIDER_ANNOTATION_TYPES</code>,
	 *   <code>IJavaElementSearchConstants.CONSIDER_ENUMS</code>,
	 *   <code>IJavaElementSearchConstants.CONSIDER_ALL_TYPES</code>,
	 *   <code>IJavaElementSearchConstants.CONSIDER_CLASSES_AND_INTERFACES</code>
	 *   <code>IJavaElementSearchConstants.CONSIDER_CLASSES_AND_ENUMS</code>. Please note that
	 *   the bitwise OR combination of the elementary constants is not supported.
	 * @param multipleSelection <code>true</code> if multiple selection is allowed
	 * 
	 * @return a new selection dialog
	 * 
	 * @exception JavaModelException if the selection dialog could not be opened
	 */
	public static SelectionDialog createTypeDialog(Shell parent, IRunnableContext context, IJavaSearchScope scope, int style, boolean multipleSelection) throws JavaModelException {
		return createTypeDialog(parent, context, scope, style, multipleSelection, "");//$NON-NLS-1$
	}
		
	/**
	 * Creates a selection dialog that lists all types in the given scope.
	 * The caller is responsible for opening the dialog with <code>Window.open</code>,
	 * and subsequently extracting the selected type(s) (of type
	 * <code>IType</code>) via <code>SelectionDialog.getResult</code>.
	 * 
	 * @param parent the parent shell of the dialog to be created
	 * @param context the runnable context used to show progress when the dialog
	 *   is being populated
	 * @param scope the scope that limits which types are included
	 * @param style flags defining the style of the dialog; the only valid values are
	 *   <code>IJavaElementSearchConstants.CONSIDER_CLASSES</code>,
	 *   <code>IJavaElementSearchConstants.CONSIDER_INTERFACES</code>, 
	 *   <code>IJavaElementSearchConstants.CONSIDER_ANNOTATION_TYPES</code>,
	 *   <code>IJavaElementSearchConstants.CONSIDER_ENUMS</code>,
	 *   <code>IJavaElementSearchConstants.CONSIDER_ALL_TYPES</code>,
	 *   <code>IJavaElementSearchConstants.CONSIDER_CLASSES_AND_INTERFACES</code>
	 *   <code>IJavaElementSearchConstants.CONSIDER_CLASSES_AND_ENUMS</code>. Please note that
	 *   the bitwise OR combination of the elementary constants is not supported.
	 * @param multipleSelection <code>true</code> if multiple selection is allowed
	 * @param filter the initial pattern to filter the set of types. For example "Abstract" shows 
	 *  all types starting with "abstract". The meta character '?' representing any character and 
	 *  '*' representing any string are supported. Clients can pass an empty string if no filtering 
	 *  is required.
	 *  
	 * @return a new selection dialog
	 * 
	 * @exception JavaModelException if the selection dialog could not be opened
	 * 
	 * @since 2.0
	 */
	public static SelectionDialog createTypeDialog(Shell parent, IRunnableContext context, IJavaSearchScope scope, int style, boolean multipleSelection, String filter) throws JavaModelException {
		/* TODO JDT UI dialogs
		return createTypeDialog(parent, context, scope, style, multipleSelection, filter, null);
		*/
		return null;
	}
	
	/**
	 * Creates a selection dialog that lists all types in the given scope.
	 * The caller is responsible for opening the dialog with <code>Window.open</code>,
	 * and subsequently extracting the selected type(s) (of type
	 * <code>IType</code>) via <code>SelectionDialog.getResult</code>.
	 * 
	 * @param parent the parent shell of the dialog to be created
	 * @param context the runnable context used to show progress when the dialog
	 *   is being populated
	 * @param scope the scope that limits which types are included
	 * @param style flags defining the style of the dialog; the only valid values are
	 *   <code>IJavaElementSearchConstants.CONSIDER_CLASSES</code>,
	 *   <code>IJavaElementSearchConstants.CONSIDER_INTERFACES</code>, 
	 *   <code>IJavaElementSearchConstants.CONSIDER_ANNOTATION_TYPES</code>,
	 *   <code>IJavaElementSearchConstants.CONSIDER_ENUMS</code>,
	 *   <code>IJavaElementSearchConstants.CONSIDER_ALL_TYPES</code>,
	 *   <code>IJavaElementSearchConstants.CONSIDER_CLASSES_AND_INTERFACES</code>
	 *   <code>IJavaElementSearchConstants.CONSIDER_CLASSES_AND_ENUMS</code>. Please note that
	 *   the bitwise OR combination of the elementary constants is not supported.
	 * @param multipleSelection <code>true</code> if multiple selection is allowed
	 * @param filter the initial pattern to filter the set of types. For example "Abstract" shows 
	 *  all types starting with "abstract". The meta character '?' representing any character and 
	 *  '*' representing any string are supported. Clients can pass an empty string if no filtering
	 *  is required.
	 * @param extension a user interface extension to the type selection dialog or <code>null</code>
	 *  if no extension is desired
	 *  
	 * @return a new selection dialog
	 * 
	 * @exception JavaModelException if the selection dialog could not be opened
	 * 
	 * @since 3.2
	 */
	/* TODO JDT UI dialogs
	public static SelectionDialog createTypeDialog(Shell parent, IRunnableContext context, IJavaSearchScope scope, int style, 
			boolean multipleSelection, String filter, TypeSelectionExtension extension) throws JavaModelException {
		int elementKinds= 0;
		if (style == IJavaElementSearchConstants.CONSIDER_ALL_TYPES) {
			elementKinds= IJavaSearchConstants.TYPE;
		} else if (style == IJavaElementSearchConstants.CONSIDER_INTERFACES) {
			elementKinds= IJavaSearchConstants.INTERFACE;
		} else if (style == IJavaElementSearchConstants.CONSIDER_CLASSES) {
			elementKinds= IJavaSearchConstants.CLASS;
		} else if (style == IJavaElementSearchConstants.CONSIDER_ANNOTATION_TYPES) {
			elementKinds= IJavaSearchConstants.ANNOTATION_TYPE;
		} else if (style == IJavaElementSearchConstants.CONSIDER_ENUMS) {
			elementKinds= IJavaSearchConstants.ENUM;
		} else if (style == IJavaElementSearchConstants.CONSIDER_CLASSES_AND_INTERFACES) {
			elementKinds= IJavaSearchConstants.CLASS_AND_INTERFACE;
		} else if (style == IJavaElementSearchConstants.CONSIDER_CLASSES_AND_ENUMS) {
			elementKinds= IJavaSearchConstants.CLASS_AND_ENUM;
		} else if (style == DEPRECATED_CONSIDER_TYPES) {
			elementKinds= IJavaSearchConstants.CLASS_AND_INTERFACE;
		} else {	
			throw new IllegalArgumentException("Invalid style constant."); //$NON-NLS-1$
		}
		TypeSelectionDialog2 dialog= new TypeSelectionDialog2(parent, multipleSelection, 
			context, scope, elementKinds, extension);
		dialog.setMessage(JavaUIMessages.JavaUI_defaultDialogMessage); 
		dialog.setFilter(filter);
		return dialog;
	}
	*/

	/**
	 * Creates a selection dialog that lists all types in the given scope containing 
	 * a standard <code>main</code> method.
	 * The caller is responsible for opening the dialog with <code>Window.open</code>,
	 * and subsequently extracting the selected type(s) (of type
	 * <code>IType</code>) via <code>SelectionDialog.getResult</code>.
	 * 
	 * @param parent the parent shell of the dialog to be created
	 * @param context the runnable context used to show progress when the dialog
	 *   is being populated
	 * @param scope the scope that limits which types are included
	 * @param style flags defining the style of the dialog; the only valid values are
	 *   <code>IJavaElementSearchConstants.CONSIDER_BINARIES</code>,
	 *   <code>CONSIDER_EXTERNAL_JARS</code>, or their bitwise OR, or <code>0</code>
	 * @param multipleSelection <code>true</code> if multiple selection is allowed
	 * @param filter the initial pattern to filter the set of types containing a main method. For 
	 * example "App" shows all types starting with "app". The meta character '?' representing 
	 * any character and '*' representing any string are supported. Clients can pass an empty 
	 * string if no filtering is required.
	 * @return a new selection dialog
	 * 
	 * @since 2.0
	 */
	public static SelectionDialog createMainTypeDialog(Shell parent, IRunnableContext context, IJavaSearchScope scope, int style, boolean multipleSelection, String filter) {
		/* TODO JDT UI dialogs
		if (multipleSelection) {
			MultiMainTypeSelectionDialog dialog= new MultiMainTypeSelectionDialog(parent, context, scope, style);
			dialog.setFilter(filter);
			return dialog;
		} else {
			MainTypeSelectionDialog dialog= new MainTypeSelectionDialog(parent, context, scope, style);
			dialog.setFilter(filter);
			return dialog;
		}		
		*/
		return null;
	}

	/**
	 * Creates a selection dialog that lists all types in the given scope containing 
	 * a standard <code>main</code> method.
	 * The caller is responsible for opening the dialog with <code>Window.open</code>,
	 * and subsequently extracting the selected type(s) (of type
	 * <code>IType</code>) via <code>SelectionDialog.getResult</code>.
	 * 
	 * @param parent the parent shell of the dialog to be created
	 * @param context the runnable context used to show progress when the dialog
	 *   is being populated
	 * @param scope the scope that limits which types are included
	 * @param style flags defining the style of the dialog; the only valid values are
	 *   <code>IJavaElementSearchConstants.CONSIDER_BINARIES</code>,
	 *   <code>CONSIDER_EXTERNAL_JARS</code>, or their bitwise OR, or <code>0</code>
	 * @param multipleSelection <code>true</code> if multiple selection is allowed
	 * @return a new selection dialog
	 */
	public static SelectionDialog createMainTypeDialog(Shell parent, IRunnableContext context, IJavaSearchScope scope, int style, boolean multipleSelection) {
		return createMainTypeDialog(parent, context, scope, style, multipleSelection, "");//$NON-NLS-1$
	}
	
	/**
	 * Opens a Java editor on the given Java element. The element can be a compilation unit 
	 * or class file. If there already is an open Java editor for the given element, it is returned.
	 *
	 * @param element the input element; either a compilation unit 
	 *   (<code>ICompilationUnit</code>) or a class file (</code>IClassFile</code>)
	 * @return the editor, or </code>null</code> if wrong element type or opening failed
	 * @exception PartInitException if the editor could not be initialized
	 * @exception JavaModelException if this element does not exist or if an
	 *		exception occurs while accessing its underlying resource
	 */
	public static IEditorPart openInEditor(IJavaElement element) throws JavaModelException, PartInitException {
		return EditorUtility.openInEditor(element);
	}

	/** 
	 * Reveals the source range of the given source reference element in the
	 * given editor. No checking is done if the editor displays a compilation unit or
	 * class file that contains the given source reference. The editor simply reveals
	 * the source range denoted by the given source reference.
	 *
	 * @param part the editor displaying the compilation unit or class file
	 * @param element the source reference element defining the source range to be revealed
	 * 
	 * @deprecated use <code>revealInEditor(IEditorPart, IJavaElement)</code> instead
	 */	
	public static void revealInEditor(IEditorPart part, ISourceReference element) {
		if (element instanceof IJavaElement)
			revealInEditor(part, (IJavaElement) element);
	}
	
	/** 
	 * Reveals the given java element  in the given editor. If the element is not an instance
	 * of <code>ISourceReference</code> this method result in a NOP. If it is a source
	 * reference no checking is done if the editor displays a compilation unit or class file that 
	 * contains the source reference element. The editor simply reveals the source range 
	 * denoted by the given element.
	 * 
	 * @param part the editor displaying a compilation unit or class file
	 * @param element the element to be revealed
	 * 
	 * @since 2.0
	 */
	public static void revealInEditor(IEditorPart part, IJavaElement element) {
		EditorUtility.revealInEditor(part, element);
	}
	 
	/**
	 * Returns the working copy manager for the Java UI plug-in.
	 *
	 * @return the working copy manager for the Java UI plug-in
	 */
	public static IWorkingCopyManager getWorkingCopyManager() {
		return JavaPlugin.getDefault().getWorkingCopyManager();
	}

	/**
	 * Returns the Java element wrapped by the given editor input.
	 *
	 * @param editorInput the editor input
	 * @return the Java element wrapped by <code>editorInput</code> or <code>null</code> if none
	 * @since 3.2
	 */
	public static IJavaElement getEditorInputJavaElement(IEditorInput editorInput) {
		// Performance: check working copy manager first: this is faster
		IJavaElement je= JavaPlugin.getDefault().getWorkingCopyManager().getWorkingCopy(editorInput);
		if (je != null)
			return je;
		
		return (IJavaElement)editorInput.getAdapter(IJavaElement.class);
	}

	/**
	 * Returns the DocumentProvider used for Java compilation units.
	 *
	 * @return the DocumentProvider for Java compilation units.
	 * 
	 * @see IDocumentProvider
	 * @since 2.0
	 */
	public static IDocumentProvider getDocumentProvider() {
		return JavaPlugin.getDefault().getCompilationUnitDocumentProvider();
	}
		
	/**
	 * Sets the Javadoc location for an archive with the given path.
	 * 
	 * @param archivePath the path of the library; this can be an workspace path
	 * or an external path in case of an external library.
	 * @param url the Javadoc location to set. This location should contain index.html and
	 * a file 'package-list'. <code>null</code> clears the current documentation
	 * location.
	 * @deprecated Javadoc is now attached to the classpath entry. 
	 * Evaluate the libraries classpath entry and reconfigure the Javadoc location there.
	 * 
	 * @since 2.0
	 */
	public static void setLibraryJavadocLocation(IPath archivePath, URL url) {
		// deprecated
	}
	
	/**
	 * Sets the Javadoc locations for archives with the given paths. 
	 * 
	 * @param archivePaths the paths of the libraries. can be workspace paths
	 * or external paths in case of an external library.
	 * @param urls the Javadoc locations to set. Each location corresponds to the archive path of the same index. A location should contain index.html and
	 * a file 'package-list'. <code>null</code> is a valid location entry and clears the current documentation
	 * location. The length of the location array must be equals to the number of archive paths passed.
	 * 
	 * 	@deprecated Javadoc is now attached to the classpath entry. 
	 * Evaluate the libraries classpath entry and reconfigure the Javadoc location there.
	 * 
	 * @since 3.0
	 */
	public static void setLibraryJavadocLocations(IPath[] archivePaths, URL[] urls) {
		// deprecated
	}
	
	/**
	 * Returns the Javadoc location for an archive or <code>null</code> if no
	 * location is available.
	 * 
	 * @param archivePath the path of the library. This can be an workspace path
	 * or an external path in case of an external library.
	 * @return the Javadoc location for an archive or <code>null</code>.
	 * 
	 * @deprecated Javadoc is now attached to the classpath entry. Use {@link #getJavadocBaseLocation(IJavaElement)}
	 * with the archive's {@link IPackageFragmentRoot} or use {@link #getLibraryJavadocLocation(IClasspathEntry)}
	 * with the archive's {@link IClasspathEntry}.
	 * 
	 * @since 2.0
	 */	
	public static URL getLibraryJavadocLocation(IPath archivePath) {
		return null;
	}
	
	/**
	 * Returns the Javadoc location for library's classpath entry or <code>null</code> if no
	 * location is available. Note that only classpath entries of kind {@link IClasspathEntry#CPE_LIBRARY} and
	 * {@link IClasspathEntry#CPE_VARIABLE} support Javadoc locations.
	 * 
	 * @param entry the classpath entry to get the Javadoc location for
	 * @return the Javadoc location or<code>null</code> if no Javadoc location is available
	 * @throws IllegalArgumentException Thrown when the entry is <code>null</code> or not of kind
	 * {@link IClasspathEntry#CPE_LIBRARY} or {@link IClasspathEntry#CPE_VARIABLE}.
	 * 
	 * @since 3.1
	 */	
	public static URL getLibraryJavadocLocation(IClasspathEntry entry) {
		return JavaDocLocations.getLibraryJavadocLocation(entry);
	}
	
	/**
	 * Sets the Javadoc location for a Java project. This location is used for
	 * all types located in the project's source folders.
	 * 
	 * @param project the project
	 * @param url the Javadoc location to set. This location should contain index.html and
	 * a file 'package-list'. <code>null</code> clears the current documentation
	 * location.
	 * 
	 * @since 2.1
	 */
	public static void setProjectJavadocLocation(IJavaProject project, URL url) {
		JavaDocLocations.setProjectJavadocLocation(project, url);
	}

	/**
	 * Returns the Javadoc location for a Java project or <code>null</code> if no
	 * location is available. This location is used for all types located in the project's
	 * source folders.
	 * 
	 * @param project the project
	 * @return the Javadoc location for a Java project or <code>null</code>
	 * 
	 * @since 2.1
	 */
	public static URL getProjectJavadocLocation(IJavaProject project) {
		return JavaDocLocations.getProjectJavadocLocation(project);
	}	

	/**
	 * Returns the Javadoc base URL for an element. The base location contains the
	 * index file. This location doesn't have to exist. Returns <code>null</code>
	 * if no javadoc location has been attached to the element's library or project.
	 * Example of a returned URL is <i>http://www.junit.org/junit/javadoc</i>.
	 * 
	 * @param element the element for which the documentation URL is requested.
	 * @return the base location
	 * @throws JavaModelException thrown when the element can not be accessed
	 * 
	 * @since 2.0
	 */
	public static URL getJavadocBaseLocation(IJavaElement element) throws JavaModelException {	
		return JavaDocLocations.getJavadocBaseLocation(element);
	}
	
	/**
	 * Returns the Javadoc URL for an element. Example of a returned URL is
	 * <i>http://www.junit.org/junit/javadoc/junit/extensions/TestSetup.html</i>.
	 * This returned location doesn't have to exist. Returns <code>null</code>
	 * if no javadoc location has been attached to the element's library or
	 * project.
	 * 
	 * @param element the element for which the documentation URL is requested.
	 * @param includeAnchor If set, the URL contains an anchor for member references:
	 * <i>http://www.junit.org/junit/javadoc/junit/extensions/TestSetup.html#run(junit.framework.TestResult)</i>. Note
	 * that this involves type resolving and is a more expensive call than without anchor.
	 * @return the Javadoc URL for the element
	 * @throws JavaModelException thrown when the element can not be accessed
	 * 
	 * @since 2.0
	 */
	public static URL getJavadocLocation(IJavaElement element, boolean includeAnchor) throws JavaModelException {
		return JavaDocLocations.getJavadocLocation(element, includeAnchor);
	}
	
	/**
	 * Returns the transfer instance used to copy/paste Java elements to
	 * and from the clipboard. Objects managed by this transfer instance
	 * are of type <code>IJavaElement[]</code>. So to access data from the
	 * clipboard clients should use the following code snippet:
	 * <pre>
	 *   IJavaElement[] elements=
	 *     (IJavaElement[])clipboard.getContents(JavaUI.getJavaElementClipboardTransfer());
	 * </pre>  
	 * 
	 * To put elements into the clipboard use the following snippet:
	 * 
	 * <pre>
	 *    IJavaElement[] javaElements= ...;
	 *    clipboard.setContents(
	 *     new Object[] { javaElements },
	 *     new Transfer[] { JavaUI.getJavaElementClipboardTransfer() } );
	 * </pre>
	 * 
	 * @return returns the transfer object used to copy/paste Java elements
	 *  to and from the clipboard
	 * 
	 * @since 3.0
	 */	
	public static Transfer getJavaElementClipboardTransfer() {
		return JavaElementTransfer.getInstance();
	}
	
	/**
	 * Returns the color manager the Java UI plug-in which is used to manage
	 * any Java-specific colors needed for such things like syntax highlighting.
	 *
	 * @return the color manager to be used for Java text viewers
	 * @since 3.2
	 */
	public static IColorManager getColorManager() {
		return JavaPlugin.getDefault().getJavaTextTools().getColorManager();
	}
	
	/**
	 * Returns the {@link ITypeRoot} wrapped by the given editor input.
	 *
	 * @param editorInput the editor input
	 * @return the {@link ITypeRoot} wrapped by <code>editorInput</code> or <code>null</code> if the editor input
	 * does not stand for a ITypeRoot
	 *
	 * @since 3.4
	 */
	public static ITypeRoot getEditorInputTypeRoot(IEditorInput editorInput) {
		// Performance: check working copy manager first: this is faster
		ICompilationUnit cu= JavaPlugin.getDefault().getWorkingCopyManager().getWorkingCopy(editorInput);
		if (cu != null)
			return cu;

		IJavaElement je= (IJavaElement) editorInput.getAdapter(IJavaElement.class);
		if (je instanceof ITypeRoot)
			return (ITypeRoot) je;

		return null;
	}
	
}
