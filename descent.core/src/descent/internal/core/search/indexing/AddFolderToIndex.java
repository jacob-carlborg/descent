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
package descent.internal.core.search.indexing;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

import descent.core.JavaCore;
import descent.internal.compiler.SourceElementParser;
import descent.internal.core.index.Index;
import descent.internal.core.search.processing.JobManager;
import descent.internal.core.util.Util;

class AddFolderToIndex extends IndexRequest {
	IPath folderPath;
	IProject project;
	char[][] inclusionPatterns;
	char[][] exclusionPatterns;

	public AddFolderToIndex(IPath folderPath, IProject project, char[][] inclusionPatterns, char[][] exclusionPatterns, IndexManager manager) {
		super(project.getFullPath(), manager);
		this.folderPath = folderPath;
		this.project = project;
		this.inclusionPatterns = inclusionPatterns;
		this.exclusionPatterns = exclusionPatterns;
	}
	public boolean execute(IProgressMonitor progressMonitor) {

		if (this.isCancelled || progressMonitor != null && progressMonitor.isCanceled()) return true;
		if (!project.isAccessible()) return true; // nothing to do
		IResource folder = this.project.getParent().findMember(this.folderPath);
		if (folder == null || folder.getType() == IResource.FILE) return true; // nothing to do, source folder was removed

		/* ensure no concurrent write access to index */
		Index index = this.manager.getIndex(this.containerPath, true, /*reuse index file*/ true /*create if none*/);
		if (index == null) return true;
		ReadWriteMonitor monitor = index.monitor;
		if (monitor == null) return true; // index got deleted since acquired

		try {
			monitor.enterRead(); // ask permission to read

			final IPath container = this.containerPath;
			final IndexManager indexManager = this.manager;
			final SourceElementParser parser = indexManager.getSourceElementParser(JavaCore.create(this.project), null/*requestor will be set by indexer*/);
			if (this.exclusionPatterns == null && this.inclusionPatterns == null) {
				folder.accept(
					new IResourceProxyVisitor() {
						public boolean visit(IResourceProxy proxy) /* throws CoreException */{
							if (proxy.getType() == IResource.FILE) {
								if (descent.internal.core.util.Util.isJavaLikeFileName(proxy.getName()))
									indexManager.addSource((IFile) proxy.requestResource(), container, parser);
								return false;
							}
							return true;
						}
					},
					IResource.NONE
				);
			} else {
				folder.accept(
					new IResourceProxyVisitor() {
						public boolean visit(IResourceProxy proxy) /* throws CoreException */{
							switch(proxy.getType()) {
								case IResource.FILE :
									if (descent.internal.core.util.Util.isJavaLikeFileName(proxy.getName())) {
										IResource resource = proxy.requestResource();
										if (!Util.isExcluded(resource, inclusionPatterns, exclusionPatterns))
											indexManager.addSource((IFile)resource, container, parser);
									}
									return false;
								case IResource.FOLDER :
									if (exclusionPatterns != null && inclusionPatterns == null) {
										// if there are inclusion patterns then we must walk the children
										if (Util.isExcluded(proxy.requestFullPath(), inclusionPatterns, exclusionPatterns, true)) 
										    return false;
									}
							}
							return true;
						}
					},
					IResource.NONE
				);
			}
		} catch (CoreException e) {
			if (JobManager.VERBOSE) {
				Util.verbose("-> failed to add " + this.folderPath + " to index because of the following exception:", System.err); //$NON-NLS-1$ //$NON-NLS-2$
				e.printStackTrace();
			}
			return false;
		} finally {
			monitor.exitRead(); // free read lock
		}
		return true;
	}
	public String toString() {
		return "adding " + this.folderPath + " to index " + this.containerPath; //$NON-NLS-1$ //$NON-NLS-2$
	}
}
