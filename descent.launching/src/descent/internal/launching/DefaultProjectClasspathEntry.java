package descent.internal.launching;

import com.ibm.icu.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.ILaunchConfiguration;
import descent.core.ClasspathContainerInitializer;
import descent.core.IClasspathContainer;
import descent.core.IClasspathEntry;
import descent.core.IJavaProject;
import descent.core.JavaCore;
import descent.launching.IRuntimeClasspathEntry;
import descent.launching.IRuntimeContainerComparator;
import descent.launching.JavaRuntime;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Default user classpath entries for a Java project
 */
public class DefaultProjectClasspathEntry extends AbstractRuntimeClasspathEntry {
	
	public static final String TYPE_ID = "descent.launching.classpathentry.defaultClasspath"; //$NON-NLS-1$
	
	/**
	 * Whether only exported entries should be on the runtime classpath.
	 * By default all entries are on the runtime classpath.
	 */
	private boolean fExportedEntriesOnly = false;
	
	/**
	 * Default constructor need to instantiate extensions
	 */
	public DefaultProjectClasspathEntry() {
	}
	
	/**
	 * Constructs a new classpath entry for the given project.
	 * 
	 * @param project Java project
	 */
	public DefaultProjectClasspathEntry(IJavaProject project) {
		setJavaProject(project);
	}
	
	/* (non-Javadoc)
	 * @see descent.internal.launching.AbstractRuntimeClasspathEntry#buildMemento(org.w3c.dom.Document, org.w3c.dom.Element)
	 */
	protected void buildMemento(Document document, Element memento) throws CoreException {
		memento.setAttribute("project", getJavaProject().getElementName()); //$NON-NLS-1$
		memento.setAttribute("exportedEntriesOnly", Boolean.toString(fExportedEntriesOnly)); //$NON-NLS-1$
	}
	
	/* (non-Javadoc)
	 * @see descent.launching.IRuntimeClasspathEntry2#initializeFrom(org.w3c.dom.Element)
	 */
	public void initializeFrom(Element memento) throws CoreException {
		String name = memento.getAttribute("project"); //$NON-NLS-1$
		if (name == null) {
			abort(LaunchingMessages.DefaultProjectClasspathEntry_3, null); 
		}		
		IJavaProject project = JavaCore.create(ResourcesPlugin.getWorkspace().getRoot().getProject(name));
		setJavaProject(project);
		name = memento.getAttribute("exportedEntriesOnly"); //$NON-NLS-1$
		if (name == null) {
			fExportedEntriesOnly = false;
		} else {
			fExportedEntriesOnly = Boolean.valueOf(name).booleanValue();
		}
	}
	/* (non-Javadoc)
	 * @see descent.launching.IRuntimeClasspathEntry2#getTypeId()
	 */
	public String getTypeId() {
		return TYPE_ID;
	}
	/* (non-Javadoc)
	 * @see descent.launching.IRuntimeClasspathEntry#getType()
	 */
	public int getType() {
		return OTHER;
	}
	
	protected IProject getProject() {
		return getJavaProject().getProject();
	}
	
	/* (non-Javadoc)
	 * @see descent.launching.IRuntimeClasspathEntry#getLocation()
	 */
	public String getLocation() {
		return getProject().getLocation().toOSString();
	}
	
	/* (non-Javadoc)
	 * @see descent.launching.IRuntimeClasspathEntry#getPath()
	 */
	public IPath getPath() {
		return getProject().getFullPath();
	}
	
	/* (non-Javadoc)
	 * @see descent.launching.IRuntimeClasspathEntry#getResource()
	 */
	public IResource getResource() {
		return getProject();
	}
	
	/* (non-Javadoc)
	 * @see descent.launching.IRuntimeClasspathEntry2#getRuntimeClasspathEntries(org.eclipse.debug.core.ILaunchConfiguration)
	 */
	public IRuntimeClasspathEntry[] getRuntimeClasspathEntries(ILaunchConfiguration configuration) throws CoreException {
		IClasspathEntry entry = JavaCore.newProjectEntry(getJavaProject().getProject().getFullPath());
		List classpathEntries = new ArrayList(5);
		List expanding = new ArrayList(5);
		expandProject(entry, classpathEntries, expanding);
		IRuntimeClasspathEntry[] runtimeEntries = new IRuntimeClasspathEntry[classpathEntries.size()];
		for (int i = 0; i < runtimeEntries.length; i++) {
			Object e = classpathEntries.get(i);
			if (e instanceof IClasspathEntry) {
				IClasspathEntry cpe = (IClasspathEntry)e;
				runtimeEntries[i] = new RuntimeClasspathEntry(cpe);
			} else {
				runtimeEntries[i] = (IRuntimeClasspathEntry)e;				
			}
		}
		// remove bootpath entries - this is a default user classpath
		List ordered = new ArrayList(runtimeEntries.length);
		for (int i = 0; i < runtimeEntries.length; i++) {
			if (runtimeEntries[i].getClasspathProperty() == IRuntimeClasspathEntry.USER_CLASSES) {
				ordered.add(runtimeEntries[i]);
			} 
		}
		return (IRuntimeClasspathEntry[]) ordered.toArray(new IRuntimeClasspathEntry[ordered.size()]);		
	}
	
	/**
	 * Returns the transitive closure of classpath entries for the
	 * given project entry.
	 * 
	 * @param projectEntry project classpath entry
	 * @param expandedPath a list of entries already expanded, should be empty
	 * to begin, and contains the result
	 * @param expanding a list of projects that have been or are currently being
	 * expanded (to detect cycles)
	 * @exception CoreException if unable to expand the classpath
	 */
	private void expandProject(IClasspathEntry projectEntry, List expandedPath, List expanding) throws CoreException {
		expanding.add(projectEntry);
		// 1. Get the raw classpath
		// 2. Replace source folder entries with a project entry
		IPath projectPath = projectEntry.getPath();
		IResource res = ResourcesPlugin.getWorkspace().getRoot().findMember(projectPath.lastSegment());
		if (res == null) {
			// add project entry and return
			expandedPath.add(projectEntry);
			return;
		}
		IJavaProject project = (IJavaProject)JavaCore.create(res);
		if (project == null || !project.getProject().isOpen() || !project.exists()) {
			// add project entry and return
			expandedPath.add(projectEntry);
			return;
		}
		
		IClasspathEntry[] buildPath = project.getRawClasspath();
		List unexpandedPath = new ArrayList(buildPath.length);
		boolean projectAdded = false;
		for (int i = 0; i < buildPath.length; i++) {
			IClasspathEntry classpathEntry = buildPath[i];
			if (classpathEntry.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
				if (!projectAdded) {
					projectAdded = true;
					unexpandedPath.add(projectEntry);
				}
			} else {
				// add exported entires, as configured
				if (classpathEntry.isExported()) {
					unexpandedPath.add(classpathEntry);
				} else if (!isExportedEntriesOnly() || project.equals(getJavaProject())) {
					// add non exported entries from root project or if we are including all entries
					unexpandedPath.add(classpathEntry);
				}
			}
		}
		// 3. expand each project entry (except for the root project)
		// 4. replace each container entry with a runtime entry associated with the project
		Iterator iter = unexpandedPath.iterator();
		while (iter.hasNext()) {
			IClasspathEntry entry = (IClasspathEntry)iter.next();
			if (entry == projectEntry) {
				expandedPath.add(entry);
			} else {
				switch (entry.getEntryKind()) {
					case IClasspathEntry.CPE_PROJECT:
						if (!expanding.contains(entry)) {
							expandProject(entry, expandedPath, expanding);
						}
						break;
					case IClasspathEntry.CPE_CONTAINER:
						IClasspathContainer container = JavaCore.getClasspathContainer(entry.getPath(), project);
						int property = -1;
						if (container != null) {
							switch (container.getKind()) {
								case IClasspathContainer.K_APPLICATION:
									property = IRuntimeClasspathEntry.USER_CLASSES;
									break;
								case IClasspathContainer.K_DEFAULT_SYSTEM:
									property = IRuntimeClasspathEntry.STANDARD_CLASSES;
									break;	
								case IClasspathContainer.K_SYSTEM:
									property = IRuntimeClasspathEntry.BOOTSTRAP_CLASSES;
									break;
							}
							IRuntimeClasspathEntry r = JavaRuntime.newRuntimeContainerClasspathEntry(entry.getPath(), property, project);
							// check for duplicate/redundant entries 
							boolean duplicate = false;
							ClasspathContainerInitializer initializer = JavaCore.getClasspathContainerInitializer(r.getPath().segment(0));
							for (int i = 0; i < expandedPath.size(); i++) {
								Object o = expandedPath.get(i);
								if (o instanceof IRuntimeClasspathEntry) {
									IRuntimeClasspathEntry re = (IRuntimeClasspathEntry)o;
									if (re.getType() == IRuntimeClasspathEntry.CONTAINER) {
										if (container instanceof IRuntimeContainerComparator) {
											duplicate = ((IRuntimeContainerComparator)container).isDuplicate(re.getPath());
										} else {
											ClasspathContainerInitializer initializer2 = JavaCore.getClasspathContainerInitializer(re.getPath().segment(0));
											Object id1 = null;
											Object id2 = null;
											if (initializer == null) {
												id1 = r.getPath().segment(0);
											} else {
												id1 = initializer.getComparisonID(r.getPath(), project);
											}
											if (initializer2 == null) {
												id2 = re.getPath().segment(0);
											} else {
												IJavaProject context = re.getJavaProject();
												if (context == null) {
													context = project;
												}
												id2 = initializer2.getComparisonID(re.getPath(), context);
											}
											if (id1 == null) {
												duplicate = id2 == null;
											} else {
												duplicate = id1.equals(id2);
											}
										}
										if (duplicate) {
											break;
										}
									}
								}
							}
							if (!duplicate) {
								expandedPath.add(r);
							}	
						}
						break;
					case IClasspathEntry.CPE_VARIABLE:
						if (entry.getPath().segment(0).equals(JavaRuntime.JRELIB_VARIABLE)) {
							IRuntimeClasspathEntry r = JavaRuntime.newVariableRuntimeClasspathEntry(entry.getPath());
							r.setSourceAttachmentPath(entry.getSourceAttachmentPath());
							r.setSourceAttachmentRootPath(entry.getSourceAttachmentRootPath());
							r.setClasspathProperty(IRuntimeClasspathEntry.STANDARD_CLASSES);
							if (!expandedPath.contains(r)) {
								expandedPath.add(r);
							}
							break;
						}
						// fall through if not the special JRELIB variable
					default:
						if (!expandedPath.contains(entry)) {
							expandedPath.add(entry);
						}
						break;
				}
			}
		}
		return;
	}	
	/* (non-Javadoc)
	 * @see descent.launching.IRuntimeClasspathEntry2#isComposite()
	 */
	public boolean isComposite() {
		return true;
	}
	/* (non-Javadoc)
	 * @see descent.launching.IRuntimeClasspathEntry2#getName()
	 */
	public String getName() {
		if (isExportedEntriesOnly()) {
			return MessageFormat.format(LaunchingMessages.DefaultProjectClasspathEntry_2, new String[] {getJavaProject().getElementName()});
		}
		return MessageFormat.format(LaunchingMessages.DefaultProjectClasspathEntry_4, new String[] {getJavaProject().getElementName()}); 
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj instanceof DefaultProjectClasspathEntry) {
			DefaultProjectClasspathEntry entry = (DefaultProjectClasspathEntry) obj;
			return entry.getJavaProject().equals(getJavaProject()) &&
				entry.isExportedEntriesOnly() == isExportedEntriesOnly();
		}
		return false;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return getJavaProject().hashCode();
	}
	
	/**
	 * Sets whether the runtime classpath computaion should only
	 * include exported entries in referenced projects.
	 * 
	 * @param exportedOnly
	 * @since 3.2
	 */
	public void setExportedEntriesOnly(boolean exportedOnly) {
		fExportedEntriesOnly = exportedOnly;
	}
	
	/**
	 * Returns whether the classpath computation only includes exported
	 * entries in referenced projects.
	 * 
	 * @return
	 * @since 3.2
	 */
	public boolean isExportedEntriesOnly() {
		return fExportedEntriesOnly;
	}
}
