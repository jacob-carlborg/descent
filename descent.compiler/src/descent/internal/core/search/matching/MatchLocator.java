package descent.internal.core.search.matching;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubProgressMonitor;

import descent.core.IJavaElement;
import descent.core.IJavaProject;
import descent.core.IType;
import descent.core.JavaModelException;
import descent.core.search.IJavaSearchScope;
import descent.core.search.SearchDocument;
import descent.core.search.SearchParticipant;
import descent.core.search.SearchPattern;
import descent.core.search.SearchRequestor;
import descent.internal.compiler.env.INameEnvironment;
import descent.internal.compiler.impl.CompilerOptions;
import descent.internal.compiler.util.SimpleLookupTable;
import descent.internal.core.CompilationUnit;
import descent.internal.core.JarPackageFragmentRoot;
import descent.internal.core.JavaModelManager;
import descent.internal.core.JavaProject;
import descent.internal.core.NameLookup;
import descent.internal.core.Openable;
import descent.internal.core.SearchableEnvironment;
import descent.internal.core.index.Index;
import descent.internal.core.search.BasicSearchEngine;
import descent.internal.core.search.IndexQueryRequestor;
import descent.internal.core.search.IndexSelector;
import descent.internal.core.search.JavaSearchDocument;
import descent.internal.core.search.JavaSearchParticipant;
import descent.internal.core.util.HandleFactory;
import descent.internal.core.util.Util;

// TODO JDT search: stub
public class MatchLocator {
	
	public static final int MAX_AT_ONCE;
	static {
		long maxMemory = Runtime.getRuntime().maxMemory();		
		int ratio = (int) Math.round(((double) maxMemory) / (64 * 0x100000));
		switch (ratio) {
			case 0:
			case 1:
				MAX_AT_ONCE = 100;
				break;
			case 2:
				MAX_AT_ONCE = 200;
				break;
			case 3:
				MAX_AT_ONCE = 300;
				break;
			default:
				MAX_AT_ONCE = 400;
				break;
		}
	}
	
//	 permanent state
	public SearchPattern pattern;
	public PatternLocator patternLocator;
	public int matchContainer;
	public SearchRequestor requestor;
	public IJavaSearchScope scope;
	public IProgressMonitor progressMonitor;
	
	public descent.core.ICompilationUnit[] workingCopies;
	public HandleFactory handleFactory;
	
	// cache of all super type names if scope is hierarchy scope
	public char[][][] allSuperTypeNames;
	
	public INameEnvironment nameEnvironment;
	public NameLookup nameLookup;
	
	/*
	 * Time spent in the IJavaSearchResultCollector
	 */
	public long resultCollectorTime = 0;

//	 Progress information
	int progressStep;
	int progressWorked;
	
	// Binding resolution and cache
	// CompilationUnitScope unitScope;
	SimpleLookupTable bindings;
	
//	 management of PossibleMatch to be processed
	public int numberOfMatches; // (numberOfMatches - 1) is the last unit in matchesToProcess
	public PossibleMatch[] matchesToProcess;
	public PossibleMatch currentPossibleMatch;
	
	public CompilerOptions options;

	public MatchLocator(SearchPattern pattern, SearchRequestor requestor, IJavaSearchScope scope, SubProgressMonitor progressMonitor) {
		this.pattern = pattern;
		this.patternLocator = PatternLocator.patternLocator(this.pattern);
		this.matchContainer = this.patternLocator.matchContainer();
		this.requestor = requestor;
		this.scope = scope;
		this.progressMonitor = progressMonitor;
	}

	public static IJavaElement projectOrJarFocus(InternalSearchPattern pattern) {
		return pattern == null || pattern.focus == null ? null : getProjectOrJar(pattern.focus);
	}

	public static boolean isPolymorphicSearch(InternalSearchPattern pattern) {
		return pattern.isPolymorphicSearch();
	}
	
	public static IJavaElement getProjectOrJar(IJavaElement element) {
		while (!(element instanceof IJavaProject) && !(element instanceof JarPackageFragmentRoot)) {
			element = element.getParent();
		}
		return element;
	}

	/**
	 * Locate the matches in the given files and report them using the search requestor. 
	 */
	public void locateMatches(SearchDocument[] searchDocuments) throws CoreException {
		int docsLength = searchDocuments.length;
		if (BasicSearchEngine.VERBOSE) {
			System.out.println("Locating matches in documents ["); //$NON-NLS-1$
			for (int i = 0; i < docsLength; i++)
				System.out.println("\t" + searchDocuments[i]); //$NON-NLS-1$
			System.out.println("]"); //$NON-NLS-1$
		}

		// init infos for progress increasing
		int n = docsLength<1000 ? Math.min(Math.max(docsLength/200+1, 2),4) : 5 *(docsLength/1000);
		this.progressStep = docsLength < n ? 1 : docsLength / n; // step should not be 0
		this.progressWorked = 0;

		// extract working copies
		ArrayList copies = new ArrayList();
		for (int i = 0; i < docsLength; i++) {
			SearchDocument document = searchDocuments[i];
			if (document instanceof WorkingCopyDocument) {
				copies.add(((WorkingCopyDocument)document).workingCopy);
			}
		}
		int copiesLength = copies.size();
		this.workingCopies = new descent.core.ICompilationUnit[copiesLength];
		copies.toArray(this.workingCopies);

		JavaModelManager manager = JavaModelManager.getJavaModelManager();
		this.bindings = new SimpleLookupTable();
		try {
			// optimize access to zip files during search operation
			manager.cacheZipFiles();

			// initialize handle factory (used as a cache of handles so as to optimize space)
			if (this.handleFactory == null)
				this.handleFactory = new HandleFactory();

			if (this.progressMonitor != null) {
				this.progressMonitor.beginTask("", searchDocuments.length); //$NON-NLS-1$
			}

			// initialize pattern for polymorphic search (ie. method reference pattern)
			this.patternLocator.initializePolymorphicSearch(this);

			JavaProject previousJavaProject = null;
			PossibleMatchSet matchSet = new PossibleMatchSet();
			Util.sort(searchDocuments, new Util.Comparer() {
				public int compare(Object a, Object b) {
					return ((SearchDocument)a).getPath().compareTo(((SearchDocument)b).getPath());
				}
			}); 
			int displayed = 0; // progress worked displayed
			String previousPath = null;
			for (int i = 0; i < docsLength; i++) {
				if (this.progressMonitor != null && this.progressMonitor.isCanceled()) {
					throw new OperationCanceledException();
				}

				// skip duplicate paths
				SearchDocument searchDocument = searchDocuments[i];
				searchDocuments[i] = null; // free current document
				String pathString = searchDocument.getPath();
				if (i > 0 && pathString.equals(previousPath)) {
					if (this.progressMonitor != null) {
						this.progressWorked++;
						if ((this.progressWorked%this.progressStep)==0) this.progressMonitor.worked(this.progressStep);
					}
					displayed++;
					continue;
				}
				previousPath = pathString;

				Openable openable;
				descent.core.ICompilationUnit workingCopy = null;
				if (searchDocument instanceof WorkingCopyDocument) {
					workingCopy = ((WorkingCopyDocument)searchDocument).workingCopy;
					openable = (Openable) workingCopy;
				} else {
					openable = this.handleFactory.createOpenable(pathString, this.scope);
				}
				if (openable == null) {
					if (this.progressMonitor != null) {
						this.progressWorked++;
						if ((this.progressWorked%this.progressStep)==0) this.progressMonitor.worked(this.progressStep);
					}
					displayed++;
					continue; // match is outside classpath
				}

				// create new parser and lookup environment if this is a new project
				IResource resource = null;
				JavaProject javaProject = (JavaProject) openable.getJavaProject();
				resource = workingCopy != null ? workingCopy.getResource() : openable.getResource();
				if (resource == null)
					resource = javaProject.getProject(); // case of a file in an external jar
				if (!javaProject.equals(previousJavaProject)) {
					// locate matches in previous project
					if (previousJavaProject != null) {
						try {
							locateMatches(previousJavaProject, matchSet, i-displayed);
							displayed = i;
						} catch (JavaModelException e) {
							// problem with classpath in this project -> skip it
						}
						matchSet.reset();
					}
					previousJavaProject = javaProject;
				}
				matchSet.add(new PossibleMatch(this, resource, openable, searchDocument, ((InternalSearchPattern) this.pattern).mustResolve));
			}

			// last project
			if (previousJavaProject != null) {
				try {
					locateMatches(previousJavaProject, matchSet, docsLength-displayed);
				} catch (JavaModelException e) {
					// problem with classpath in last project -> ignore
				}
			} 

			if (this.progressMonitor != null)
				this.progressMonitor.done();
		} finally {
			if (this.nameEnvironment != null)
				this.nameEnvironment.cleanup();
			manager.flushZipFiles();
			this.bindings = null;
		}
	}

	public void locatePackageDeclarations(JavaSearchParticipant participant) {
		// TODO Auto-generated method stub
		
	}

	public static SearchPattern createAndPattern(SearchPattern leftPattern, SearchPattern rightPattern) {
		return null;
	}

	public static void setFocus(InternalSearchPattern pattern, IJavaElement focus) {
		pattern.focus = focus;
	}

	/**
	 * Query a given index for matching entries. Assumes the sender has opened the index and will close when finished.
	 */
	public static void findIndexMatches(InternalSearchPattern pattern, Index index, IndexQueryRequestor requestor, SearchParticipant participant, IJavaSearchScope scope, IProgressMonitor monitor) throws IOException {
		pattern.findIndexMatches(index, requestor, participant, scope, monitor);
	}

	public static SearchDocument[] addWorkingCopies(InternalSearchPattern pattern, SearchDocument[] indexMatches, descent.core.ICompilationUnit[] copies, SearchParticipant participant) {
		// working copies take precedence over corresponding compilation units
		HashMap workingCopyDocuments = workingCopiesThatCanSeeFocus(copies, pattern.focus, pattern.isPolymorphicSearch(), participant);
		SearchDocument[] matches = null;
		int length = indexMatches.length;
		for (int i = 0; i < length; i++) {
			SearchDocument searchDocument = indexMatches[i];
			if (searchDocument.getParticipant() == participant) {
				SearchDocument workingCopyDocument = (SearchDocument) workingCopyDocuments.remove(searchDocument.getPath());
				if (workingCopyDocument != null) {
					if (matches == null) {
						System.arraycopy(indexMatches, 0, matches = new SearchDocument[length], 0, length);
					}
					matches[i] = workingCopyDocument;
				}
			}
		}
		if (matches == null) { // no working copy
			matches = indexMatches;
		}
		int remainingWorkingCopiesSize = workingCopyDocuments.size();
		if (remainingWorkingCopiesSize != 0) {
			System.arraycopy(matches, 0, matches = new SearchDocument[length+remainingWorkingCopiesSize], 0, length);
			Iterator iterator = workingCopyDocuments.values().iterator();
			int index = length;
			while (iterator.hasNext()) {
				matches[index++] = (SearchDocument) iterator.next();
			}
		}
		return matches;
	}
	
	/*
	 * Returns the working copies that can see the given focus.
	 */
	private static HashMap workingCopiesThatCanSeeFocus(descent.core.ICompilationUnit[] copies, IJavaElement focus, boolean isPolymorphicSearch, SearchParticipant participant) {
		if (copies == null) return new HashMap();
		if (focus != null) {
			while (!(focus instanceof IJavaProject) && !(focus instanceof JarPackageFragmentRoot)) {
				focus = focus.getParent();
			}
		}
		HashMap result = new HashMap();
		for (int i=0, length = copies.length; i<length; i++) {
			descent.core.ICompilationUnit workingCopy = copies[i];
			IPath projectOrJar = MatchLocator.getProjectOrJar(workingCopy).getPath();
			if (focus == null || IndexSelector.canSeeFocus(focus, isPolymorphicSearch, projectOrJar)) {
				result.put(
					workingCopy.getPath().toString(),
					new WorkingCopyDocument(workingCopy, participant)
				);
			}
		}
		return result;
	}
	
	public static class WorkingCopyDocument extends JavaSearchDocument {
		public descent.core.ICompilationUnit workingCopy;
		WorkingCopyDocument(descent.core.ICompilationUnit workingCopy, SearchParticipant participant) {
			super(workingCopy.getPath().toString(), participant);
			this.charContents = ((CompilationUnit)workingCopy).getContents();
			this.workingCopy = workingCopy;
		}
		public String toString() {
			return "WorkingCopyDocument for " + getPath(); //$NON-NLS-1$
		}
	}
	
	/**
	 * Locate the matches amongst the possible matches.
	 */
	protected void locateMatches(JavaProject javaProject, PossibleMatchSet matchSet, int expected) throws CoreException {
		PossibleMatch[] possibleMatches = matchSet.getPossibleMatches(javaProject.getPackageFragmentRoots());
		int length = possibleMatches.length;
		// increase progress from duplicate matches not stored in matchSet while adding...
		if (this.progressMonitor != null && expected>length) {
			this.progressWorked += expected-length;
			this.progressMonitor.worked( expected-length);
		}
		// locate matches (processed matches are limited to avoid problem while using VM default memory heap size)
		for (int index = 0; index < length;) {
			int max = Math.min(MAX_AT_ONCE, length - index);
			locateMatches(javaProject, possibleMatches, index, max);
			index += max;
		}
		this.patternLocator.clear();
	}
	
	protected void locateMatches(JavaProject javaProject, PossibleMatch[] possibleMatches, int start, int length) throws CoreException {
		initialize(javaProject, length);

		/* TODO JDT Search
		// create and resolve binding (equivalent to beginCompilation() in Compiler)
		boolean mustResolvePattern = ((InternalSearchPattern)this.pattern).mustResolve;
		boolean mustResolve = mustResolvePattern;
		//this.patternLocator.mayBeGeneric = this.options.sourceLevel >= ClassFileConstants.JDK1_5;
		boolean bindingsWereCreated = mustResolve;
		try {
			for (int i = start, maxUnits = start + length; i < maxUnits; i++) {
				PossibleMatch possibleMatch = possibleMatches[i];
				try {
					if (!parseAndBuildBindings(possibleMatch, mustResolvePattern)) continue;
					// Currently we only need to resolve over pattern flag if there's potential parameterized types
					if (this.patternLocator.mayBeGeneric) {
						// If pattern does not resolve then rely on possible match node set resolution
						// which may have been modified while locator was adding possible matches to it
						if (!mustResolvePattern && !mustResolve) {
							mustResolve = possibleMatch.nodeSet.mustResolve;
							bindingsWereCreated = mustResolve;
						}
					} else {
						// Reset matching node resolution with pattern one if there's no potential parameterized type
						// to minimize side effect on previous search behavior
						possibleMatch.nodeSet.mustResolve = mustResolvePattern;
					}
					// possible match node resolution has been merged with pattern one, so rely on it to know
					// whether we need to process compilation unit now or later
					if (!possibleMatch.nodeSet.mustResolve) {
						if (this.progressMonitor != null) {
							this.progressWorked++;
							if ((this.progressWorked%this.progressStep)==0) this.progressMonitor.worked(this.progressStep);
						}
						process(possibleMatch, bindingsWereCreated);
						if (this.numberOfMatches>0 && this.matchesToProcess[this.numberOfMatches-1] == possibleMatch) {
							// forget last possible match as it was processed
							this.numberOfMatches--;
						}
					}
				} finally {
					if (!possibleMatch.nodeSet.mustResolve)
						possibleMatch.cleanUp();
				}
			}
			if (mustResolve)
				this.lookupEnvironment.completeTypeBindings();

			// create hierarchy resolver if needed
			IType focusType = getFocusType();
			if (focusType == null) {
				this.hierarchyResolver = null;
			} else if (!createHierarchyResolver(focusType, possibleMatches)) {
				// focus type is not visible, use the super type names instead of the bindings
				if (computeSuperTypeNames(focusType) == null) return;
			}
		} catch (AbortCompilation e) {
			bindingsWereCreated = false;
		}

		if (!mustResolve) {
			return;
		}
		
		// possible match resolution
		for (int i = 0; i < this.numberOfMatches; i++) {
			if (this.progressMonitor != null && this.progressMonitor.isCanceled())
				throw new OperationCanceledException();
			PossibleMatch possibleMatch = this.matchesToProcess[i];
			this.matchesToProcess[i] = null; // release reference to processed possible match
			try {
				process(possibleMatch, bindingsWereCreated);
			} catch (AbortCompilation e) {
				// problem with class path: it could not find base classes
				// continue and try next matching openable reporting innacurate matches (since bindings will be null)
				bindingsWereCreated = false;
			} catch (JavaModelException e) {
				// problem with class path: it could not find base classes
				// continue and try next matching openable reporting innacurate matches (since bindings will be null)
				bindingsWereCreated = false;
			} finally {
				if (this.progressMonitor != null) {
					this.progressWorked++;
					if ((this.progressWorked%this.progressStep)==0) this.progressMonitor.worked(this.progressStep);
				}
				if (this.options.verbose)
					System.out.println(
						Messages.bind(Messages.compilation_done,
							new String[] {
								String.valueOf(i + 1),
								String.valueOf(this.numberOfMatches),
								new String(possibleMatch.parsedUnit.getFileName())
							}));
				// cleanup compilation unit result
				possibleMatch.cleanUp();
			}
		}
		*/
	}
	
	/**
	 * Create a new parser for the given project, as well as a lookup environment.
	 */
	public void initialize(JavaProject project, int possibleMatchSize) throws JavaModelException {
		// clean up name environment only if there are several possible match as it is reused
		// when only one possible match (bug 58581)
		if (this.nameEnvironment != null && possibleMatchSize != 1)
			this.nameEnvironment.cleanup();

		SearchableEnvironment searchableEnvironment = project.newSearchableNameEnvironment(this.workingCopies);
		
		// if only one possible match, a file name environment costs too much,
		// so use the existing searchable  environment which will populate the java model
		// only for this possible match and its required types.
		this.nameEnvironment = possibleMatchSize == 1
			? (INameEnvironment) searchableEnvironment
			: (INameEnvironment) new JavaSearchNameEnvironment(project, this.workingCopies);

		// create lookup environment
		Map map = project.getOptions(true);
		map.put(CompilerOptions.OPTION_TaskTags, ""); //$NON-NLS-1$
		this.options = new CompilerOptions(map);
		/* TODO JDT Search
		ProblemReporter problemReporter =
			new ProblemReporter(
				DefaultErrorHandlingPolicies.proceedWithAllProblems(),
				this.options,
				new DefaultProblemFactory());
		this.lookupEnvironment = new LookupEnvironment(this, this.options, problemReporter, this.nameEnvironment);

		this.parser = MatchLocatorParser.createParser(problemReporter, this);

		// remember project's name lookup
		this.nameLookup = searchableEnvironment.nameLookup;

		// initialize queue of units
		this.numberOfMatches = 0;
		this.matchesToProcess = new PossibleMatch[possibleMatchSize];
		*/
	}
	
	protected IType getFocusType() {
		//return this.scope instanceof HierarchyScope ? ((HierarchyScope) this.scope).focusType : null;
		return null;
	}

}
