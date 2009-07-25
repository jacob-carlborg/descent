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
package descent.core.search;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import descent.core.ICompilationUnit;
import descent.core.IJavaElement;
import descent.core.IType;
import descent.core.JavaModelException;
import descent.core.WorkingCopyOwner;
import descent.internal.core.search.BasicSearchEngine;
import descent.internal.core.search.TypeNameRequestorWrapper;

/**
 * A {@link SearchEngine} searches for Java elements following a search pattern.
 * The search can be limited to a search scope.
 * <p>
 * Various search patterns can be created using the factory methods 
 * {@link SearchPattern#createPattern(String, int, int, int)}, {@link SearchPattern#createPattern(IJavaElement, int)},
 * {@link SearchPattern#createOrPattern(SearchPattern, SearchPattern)}.
 * </p>
 * <p>For example, one can search for references to a method in the hierarchy of a type, 
 * or one can search for the declarations of types starting with "Abstract" in a project.
 * </p>
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 */
public class SearchEngine {
		
	// Search engine now uses basic engine functionalities
	private BasicSearchEngine basicEngine;

	/**
	 * Creates a new search engine.
	 */
	public SearchEngine() {
		this.basicEngine = new BasicSearchEngine();
	}
	
	/**
	 * Creates a new search engine with a list of working copies that will take precedence over 
	 * their original compilation units in the subsequent search operations.
	 * <p>
	 * Note that passing an empty working copy will be as if the original compilation
	 * unit had been deleted.</p>
	 * <p>
	 * Since 3.0 the given working copies take precedence over primary working copies (if any).
	 * 
	 * @param workingCopies the working copies that take precedence over their original compilation units
	 * @since 3.0
	 */
	public SearchEngine(ICompilationUnit[] workingCopies) {
		this.basicEngine = new BasicSearchEngine(workingCopies);
	}
	
	/**
	 * Creates a new search engine with the given working copy owner.
	 * The working copies owned by this owner will take precedence over 
	 * the primary compilation units in the subsequent search operations.
	 * 
	 * @param workingCopyOwner the owner of the working copies that take precedence over their original compilation units
	 * @since 3.0
	 */
	public SearchEngine(WorkingCopyOwner workingCopyOwner) {
		this.basicEngine = new BasicSearchEngine(workingCopyOwner);
	}

	/**
	 * Returns a Java search scope limited to the hierarchy of the given type.
	 * The Java elements resulting from a search with this scope will
	 * be types in this hierarchy, or members of the types in this hierarchy.
	 *
	 * @param type the focus of the hierarchy scope
	 * @return a new hierarchy scope
	 * @exception JavaModelException if the hierarchy could not be computed on the given type
	 */
	public static IJavaSearchScope createHierarchyScope(IType type) throws JavaModelException {
		return BasicSearchEngine.createHierarchyScope(type);
	}
	
	/**
	 * Returns a Java search scope limited to the hierarchy of the given type.
	 * When the hierarchy is computed, the types defined in the working copies owned
	 * by the given owner take precedence over the original compilation units.
	 * The Java elements resulting from a search with this scope will
	 * be types in this hierarchy, or members of the types in this hierarchy.
	 *
	 * @param type the focus of the hierarchy scope
	 * @param owner the owner of working copies that take precedence over original compilation units
	 * @return a new hierarchy scope
	 * @exception JavaModelException if the hierarchy could not be computed on the given type
	 * @since 3.0
	 */
	public static IJavaSearchScope createHierarchyScope(IType type, WorkingCopyOwner owner) throws JavaModelException {
		return BasicSearchEngine.createHierarchyScope(type, owner);
	}

	/**
	 * Returns a Java search scope limited to the given Java elements.
	 * The Java elements resulting from a search with this scope will
	 * be children of the given elements.
	 * <p>
	 * If an element is an IJavaProject, then the project's source folders, 
	 * its jars (external and internal) and its referenced projects (with their source 
	 * folders and jars, recursively) will be included.
	 * If an element is an IPackageFragmentRoot, then only the package fragments of 
	 * this package fragment root will be included.
	 * If an element is an IPackageFragment, then only the compilation unit and class 
	 * files of this package fragment will be included. Subpackages will NOT be 
	 * included.</p>
	 * <p>
	 * In other words, this is equivalent to using SearchEngine.createJavaSearchScope(elements, true).</p>
	 *
	 * @param elements the Java elements the scope is limited to
	 * @return a new Java search scope
	 * @since 2.0
	 */
	public static IJavaSearchScope createJavaSearchScope(IJavaElement[] elements) {
		return BasicSearchEngine.createJavaSearchScope(elements);
	}

	/**
	 * Returns a Java search scope limited to the given Java elements.
	 * The Java elements resulting from a search with this scope will
	 * be children of the given elements.
	 * 
	 * If an element is an IJavaProject, then the project's source folders, 
	 * its jars (external and internal) and - if specified - its referenced projects 
	 * (with their source folders and jars, recursively) will be included.
	 * If an element is an IPackageFragmentRoot, then only the package fragments of 
	 * this package fragment root will be included.
	 * If an element is an IPackageFragment, then only the compilation unit and class 
	 * files of this package fragment will be included. Subpackages will NOT be 
	 * included.
	 *
	 * @param elements the Java elements the scope is limited to
	 * @param includeReferencedProjects a flag indicating if referenced projects must be 
	 * 									 recursively included
	 * @return a new Java search scope
	 * @since 2.0
	 */
	public static IJavaSearchScope createJavaSearchScope(IJavaElement[] elements, boolean includeReferencedProjects) {
		return BasicSearchEngine.createJavaSearchScope(elements, includeReferencedProjects);
	}

	/**
	 * Returns a Java search scope limited to the given Java elements.
	 * The Java elements resulting from a search with this scope will
	 * be children of the given elements.
	 * 
	 * If an element is an IJavaProject, then it includes:
	 * - its source folders if IJavaSearchScope.SOURCES is specified, 
	 * - its application libraries (internal and external jars, class folders that are on the raw classpath, 
	 *   or the ones that are coming from a classpath path variable,
	 *   or the ones that are coming from a classpath container with the K_APPLICATION kind)
	 *   if IJavaSearchScope.APPLICATION_LIBRARIES is specified
	 * - its system libraries (internal and external jars, class folders that are coming from an 
	 *   IClasspathContainer with the K_SYSTEM kind) 
	 *   if IJavaSearchScope.APPLICATION_LIBRARIES is specified
	 * - its referenced projects (with their source folders and jars, recursively) 
	 *   if IJavaSearchScope.REFERENCED_PROJECTS is specified.
	 * If an element is an IPackageFragmentRoot, then only the package fragments of 
	 * this package fragment root will be included.
	 * If an element is an IPackageFragment, then only the compilation unit and class 
	 * files of this package fragment will be included. Subpackages will NOT be 
	 * included.
	 *
	 * @param elements the Java elements the scope is limited to
	 * @param includeMask the bit-wise OR of all include types of interest
	 * @return a new Java search scope
	 * @see IJavaSearchScope#SOURCES
	 * @see IJavaSearchScope#APPLICATION_LIBRARIES
	 * @see IJavaSearchScope#SYSTEM_LIBRARIES
	 * @see IJavaSearchScope#REFERENCED_PROJECTS
	 * @since 3.0
	 */
	public static IJavaSearchScope createJavaSearchScope(IJavaElement[] elements, int includeMask) {
		return BasicSearchEngine.createJavaSearchScope(elements, includeMask);
	}

	/**
	 * Returns a Java search scope with the workspace as the only limit.
	 *
	 * @return a new workspace scope
	 */
	public static IJavaSearchScope createWorkspaceScope() {
		return BasicSearchEngine.createWorkspaceScope();
	}
	/**
	 * Returns a new default Java search participant.
	 * 
	 * @return a new default Java search participant
	 * @since 3.0
	 */
	public static SearchParticipant getDefaultSearchParticipant() {
		return BasicSearchEngine.getDefaultSearchParticipant();
	}
	
	/**
	 * Searches for matches of a given search pattern. Search patterns can be created using helper
	 * methods (from a String pattern or a Java element) and encapsulate the description of what is
	 * being searched (for example, search method declarations in a case sensitive way).
	 *
	 * @param pattern the pattern to search
	 * @param participants the particpants in the search
	 * @param scope the search scope
	 * @param requestor the requestor to report the matches to
	 * @param monitor the progress monitor used to report progress
	 * @exception CoreException if the search failed. Reasons include:
	 *	<ul>
	 *		<li>the classpath is incorrectly set</li>
	 *	</ul>
	 *@since 3.0
	 */
	public void search(SearchPattern pattern, SearchParticipant[] participants, IJavaSearchScope scope, SearchRequestor requestor, IProgressMonitor monitor) throws CoreException {
		this.basicEngine.search(pattern, participants, scope, requestor, monitor);
	}

	/**
	 * Searches for all top-level types and member types in the given scope.
	 * The search can be selecting specific types (given a package or a type name
	 * prefix and match modes). 
	 * 
	 * @param packageName the full name of the package of the searched types, or a prefix for this
	 *						package, or a wild-carded string for this package.
	 * @param typeName the dot-separated qualified name of the searched type (the qualification include
	 *					the enclosing types if the searched type is a member type), or a prefix
	 *					for this type, or a wild-carded string for this type.
	 * @param matchRule one of
	 * <ul>
	 *		<li>{@link SearchPattern#R_EXACT_MATCH} if the package name and type name are the full names
	 *			of the searched types.</li>
	 *		<li>{@link SearchPattern#R_PREFIX_MATCH} if the package name and type name are prefixes of the names
	 *			of the searched types.</li>
	 *		<li>{@link SearchPattern#R_PATTERN_MATCH} if the package name and type name contain wild-cards.</li>
	 *		<li>{@link SearchPattern#R_CAMELCASE_MATCH} if type name are camel case of the names of the searched types.</li>
	 * </ul>
	 * combined with {@link SearchPattern#R_CASE_SENSITIVE},
	 *   e.g. {@link SearchPattern#R_EXACT_MATCH} | {@link SearchPattern#R_CASE_SENSITIVE} if an exact and case sensitive match is requested, 
	 *   or {@link SearchPattern#R_PREFIX_MATCH} if a prefix non case sensitive match is requested.
	 * @param searchFor determines the nature of the searched elements
	 *	<ul>
	 * 	<li>{@link IJavaSearchConstants#CLASS}: only look for classes</li>
	 *		<li>{@link IJavaSearchConstants#INTERFACE}: only look for interfaces</li>
	 * 	<li>{@link IJavaSearchConstants#ENUM}: only look for enumeration</li>
	 *		<li>{@link IJavaSearchConstants#ANNOTATION_TYPE}: only look for annotation type</li>
	 * 	<li>{@link IJavaSearchConstants#CLASS_AND_ENUM}: only look for classes and enumerations</li>
	 *		<li>{@link IJavaSearchConstants#CLASS_AND_INTERFACE}: only look for classes and interfaces</li>
	 * 	<li>{@link IJavaSearchConstants#TYPE}: look for all types (ie. classes, interfaces, enum and annotation types)</li>
	 *	</ul>
	 * @param scope the scope to search in
	 * @param nameRequestor the requestor that collects the results of the search
	 * @param waitingPolicy one of
	 * <ul>
	 *		<li>{@link IJavaSearchConstants#FORCE_IMMEDIATE_SEARCH} if the search should start immediately</li>
	 *		<li>{@link IJavaSearchConstants#CANCEL_IF_NOT_READY_TO_SEARCH} if the search should be cancelled if the
	 *			underlying indexer has not finished indexing the workspace</li>
	 *		<li>{@link IJavaSearchConstants#WAIT_UNTIL_READY_TO_SEARCH} if the search should wait for the
	 *			underlying indexer to finish indexing the workspace</li>
	 * </ul>
	 * @param progressMonitor the progress monitor to report progress to, or <code>null</code> if no progress
	 *							monitor is provided
	 * @exception JavaModelException if the search failed. Reasons include:
	 *	<ul>
	 *		<li>the classpath is incorrectly set</li>
	 *	</ul>
	 * @since 3.1
	 */
	public void searchAllTypeNames(
		final char[] packageName, 
		final char[] typeName,
		final int matchRule, 
		int searchFor, 
		IJavaSearchScope scope, 
		final TypeNameRequestor nameRequestor,
		int waitingPolicy,
		IProgressMonitor progressMonitor)  throws JavaModelException {
		
		TypeNameRequestorWrapper requestorWrapper = new TypeNameRequestorWrapper(nameRequestor);
		this.basicEngine.searchAllTypeNames(packageName, typeName, matchRule, searchFor, scope, requestorWrapper, waitingPolicy, progressMonitor);
	}

	/**
	 * Searches for all top-level types and member types in the given scope matching any of the given qualifications
	 * and type names in a case sensitive way.
	 * 
	 * @param qualifications the qualified name of the package/enclosing type of the searched types
	 * @param typeNames the simple names of the searched types
	 * @param scope the scope to search in
	 * @param nameRequestor the requestor that collects the results of the search
	 * @param waitingPolicy one of
	 * <ul>
	 *		<li>{@link IJavaSearchConstants#FORCE_IMMEDIATE_SEARCH} if the search should start immediately</li>
	 *		<li>{@link IJavaSearchConstants#CANCEL_IF_NOT_READY_TO_SEARCH} if the search should be cancelled if the
	 *			underlying indexer has not finished indexing the workspace</li>
	 *		<li>{@link IJavaSearchConstants#WAIT_UNTIL_READY_TO_SEARCH} if the search should wait for the
	 *			underlying indexer to finish indexing the workspace</li>
	 * </ul>
	 * @param progressMonitor the progress monitor to report progress to, or <code>null</code> if no progress
	 *							monitor is provided
	 * @exception JavaModelException if the search failed. Reasons include:
	 *	<ul>
	 *		<li>the classpath is incorrectly set</li>
	 *	</ul>
	 * @since 3.1
	 */
	public void searchAllTypeNames(
		final char[][] qualifications, 
		final char[][] typeNames,
		IJavaSearchScope scope, 
		final TypeNameRequestor nameRequestor,
		int waitingPolicy,
		IProgressMonitor progressMonitor)  throws JavaModelException {

		TypeNameRequestorWrapper requestorWrapper = new TypeNameRequestorWrapper(nameRequestor);
		this.basicEngine.searchAllTypeNames(
			qualifications,
			typeNames,
			SearchPattern.R_EXACT_MATCH | SearchPattern.R_CASE_SENSITIVE,
			IJavaSearchConstants.TYPE,
			scope,
			requestorWrapper,
			waitingPolicy,
			progressMonitor);
	}

	/**
	 * Searches for all declarations of the fields accessed in the given element.
	 * The element can be a compilation unit, a source type, or a source method.
	 * Reports the field declarations using the given requestor.
	 * <p>
	 * Consider the following code:
	 * <code>
	 * <pre>
	 *		class A {
	 *			int field1;
	 *		}
	 *		class B extends A {
	 *			String value;
	 *		}
	 *		class X {
	 *			void test() {
	 *				B b = new B();
	 *				System.out.println(b.value + b.field1);
	 *			};
	 *		}
	 * </pre>
	 * </code>
	 * then searching for declarations of accessed fields in method 
	 * <code>X.test()</code> would collect the fields
	 * <code>B.value</code> and <code>A.field1</code>.
	 * </p>
	 *
	 * @param enclosingElement the method, type, or compilation unit to be searched in
	 * @param requestor a callback object to which each match is reported
	 * @param monitor the progress monitor used to report progress
	 * @exception JavaModelException if the search failed. Reasons include:
	 *	<ul>
	 *		<li>the element doesn't exist</li>
	 *		<li>the classpath is incorrectly set</li>
	 *	</ul>
	 * @since 3.0
	 */	
	public void searchDeclarationsOfAccessedFields(IJavaElement enclosingElement, SearchRequestor requestor, IProgressMonitor monitor) throws JavaModelException {
		this.basicEngine.searchDeclarationsOfAccessedFields(enclosingElement, requestor, monitor);
	}
	
	/**
	 * Searches for all declarations of the types referenced in the given element.
	 * The element can be a compilation unit, a source type, or a source method.
	 * Reports the type declarations using the given requestor.
	 * <p>
	 * Consider the following code:
	 * <code>
	 * <pre>
	 *		class A {
	 *		}
	 *		class B extends A {
	 *		}
	 *		interface I {
	 *		  int VALUE = 0;
	 *		}
	 *		class X {
	 *			void test() {
	 *				B b = new B();
	 *				this.foo(b, I.VALUE);
	 *			};
	 *		}
	 * </pre>
	 * </code>
	 * then searching for declarations of referenced types in method <code>X.test()</code>
	 * would collect the class <code>B</code> and the interface <code>I</code>.
	 * </p>
	 *
	 * @param enclosingElement the method, type, or compilation unit to be searched in
	 * @param requestor a callback object to which each match is reported
	 * @param monitor the progress monitor used to report progress
	 * @exception JavaModelException if the search failed. Reasons include:
	 *	<ul>
	 *		<li>the element doesn't exist</li>
	 *		<li>the classpath is incorrectly set</li>
	 *	</ul>
	 * @since 3.0
	 */	
	public void searchDeclarationsOfReferencedTypes(IJavaElement enclosingElement, SearchRequestor requestor, IProgressMonitor monitor) throws JavaModelException {
		this.basicEngine.searchDeclarationsOfReferencedTypes(enclosingElement, requestor, monitor);
	}
	
}
