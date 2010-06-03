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

import descent.core.ISourceRange;
import descent.internal.compiler.parser.HashtableOfCharArrayAndObject;

public class CompilationUnitElementInfo extends OpenableElementInfo {

	/**
	 * The length of this compilation unit's source code <code>String</code>
	 */
	protected int sourceLength;

	/** 
	 * Timestamp of original resource at the time this element
	 * was opened or last updated.
	 */
	protected long timestamp;
	protected boolean hasTopLevelCompileTimeDifficulties;
	protected HashtableOfCharArrayAndObject topLevelIdentifiers;
	protected int lastImportLocation;
	
//	/*
//	 * The positions of annotations for each element in this compilation unit.
//	 * A map from IJavaElement to long[]
//	 */
//	public HashMap annotationPositions;
//	
//public void addAnnotationPositions(IJavaElement handle, long[] positions) {
//	if (positions == null) return;
//	if (this.annotationPositions == null)
//		this.annotationPositions = new HashMap();
//	this.annotationPositions.put(handle, positions);
//}
/**
 * Returns the length of the source string.
 */
public int getSourceLength() {
	return this.sourceLength;
}
protected ISourceRange getSourceRange() {
	return new SourceRange(0, this.sourceLength);
}
public void setHasTopLevelCompileTimeDifficulties(
		boolean hasTopLevelDebugOrVersionAssignment) {
	this.hasTopLevelCompileTimeDifficulties = hasTopLevelDebugOrVersionAssignment;
}
public boolean hasTopLevelCompileTimeDifficulties() {
	return hasTopLevelCompileTimeDifficulties;
}
public void setTopLevelIdentifiers(HashtableOfCharArrayAndObject topLevelIdentifiers) {
	this.topLevelIdentifiers = topLevelIdentifiers;
}
public HashtableOfCharArrayAndObject getTopLevelIdentifiers() {
	return topLevelIdentifiers;
}
public void setLastImportLocation(int lastImportLocation) {
	this.lastImportLocation = lastImportLocation;
}
public int getLastImportLocation() {
	return lastImportLocation;
}
/**
 * Sets the length of the source string.
 */
public void setSourceLength(int newSourceLength) {
	this.sourceLength = newSourceLength;
}
}
