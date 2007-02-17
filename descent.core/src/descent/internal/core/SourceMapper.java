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
package descent.internal.core;

import java.util.Map;

import org.eclipse.core.runtime.IPath;


/**
 * A SourceMapper maps source code in a ZIP file to binary types in
 * a JAR. The SourceMapper uses the fuzzy parser to identify source
 * fragments in a .java file, and attempts to match the source code
 * with children in a binary type. A SourceMapper is associated
 * with a JarPackageFragment by an AttachSourceOperation.
 *
 * @see descent.internal.core.JarPackageFragment
 * 
 * TODO binary: replace with real one
 */
public class SourceMapper {
	
	public SourceMapper() {
		
	}
	
	/**
	 * Creates a <code>SourceMapper</code> that locates source in the zip file
	 * at the given location in the specified package fragment root.
	 */
	public SourceMapper(IPath sourcePath, String rootPath, Map options) {
		
	}
	
	/**
	 * Specifies the location of the package fragment root within
	 * the zip (empty specifies the default root). <code>null</code> is
	 * not a valid root path.
	 */
	protected String rootPath = ""; //$NON-NLS-1$
	
	/**
	 * Closes this <code>SourceMapper</code>'s zip file. Once this is done, this
	 * <code>SourceMapper</code> cannot be used again.
	 */
	public void close() {
		
	}
	
}
