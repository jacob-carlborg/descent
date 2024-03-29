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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import descent.core.JavaCore;
import descent.core.search.SearchDocument;
import descent.internal.compiler.SourceElementParser;
import descent.internal.compiler.util.SuffixConstants;
import descent.internal.core.BasicCompilationUnit;
import descent.internal.core.JavaModelManager;

/**
 * A SourceIndexer indexes java files using a java parser. The following items are indexed:
 * Declarations of:
 * - Classes<br>
 * - Interfaces; <br>
 * - Methods;<br>
 * - Fields;<br>
 * References to:
 * - Methods (with number of arguments); <br>
 * - Fields;<br>
 * - Types;<br>
 * - Constructors.
 */
public class SourceIndexer extends AbstractIndexer implements SuffixConstants {
	
	public SourceIndexer(SearchDocument document) {
		super(document);
	}
	public void indexDocument() {
		// Create a new Parser
		SourceIndexerRequestor requestor = new SourceIndexerRequestor(this);
		String documentPath = this.document.getPath();
		IPath path = new Path(documentPath);
		SourceElementParser parser = ((InternalSearchDocument) this.document).parser;
		if (parser == null) {
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(path.segment(0));
			parser = JavaModelManager.getJavaModelManager().indexManager.getSourceElementParser(JavaCore.create(project), requestor);
		} else {
			parser.requestor = requestor;
		}
		
		// Launch the parser
		char[] source = null;
		char[] name = null;
		try {
			source = document.getCharContents();
			name = documentPath.toCharArray();
		} catch(Exception e){
			// ignore
		}
		if (source == null || name == null) return; // could not retrieve document info (e.g. resource was discarded)
		try {
			parser.parseCompilationUnit(new BasicCompilationUnit(source, null, getFqn(), new String(name)));
		} catch (Exception e) {
//			if (JobManager.VERBOSE) {
				e.printStackTrace();
//			}
		}
	}
	private String getFqn() {
		String relativePath = document.getContainerRelativePath();
		if (relativePath.endsWith(".d")) {
			relativePath = relativePath.substring(0, relativePath.length() - 2);
		}
		relativePath = relativePath.replace('/', '.');
		relativePath = relativePath.replace('/', '.');
		return relativePath;
	}
	
}
