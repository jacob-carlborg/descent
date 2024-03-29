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

/** 
 * Element info for ISourceReference elements. 
 */
/* package */ class SourceRefElementInfo extends JavaElementInfo {
	protected int fSourceRangeStart, fSourceRangeEnd;
/**
 * @see descent.internal.compiler.env.ISourceType#getDeclarationSourceEnd()
 * @see descent.internal.compiler.env.ISourceMethod#getDeclarationSourceEnd()
 * @see descent.internal.compiler.env.ISourceField#getDeclarationSourceEnd()
 */
public int getDeclarationSourceEnd() {
	return fSourceRangeEnd;
}
/**
 * @see descent.internal.compiler.env.ISourceType#getDeclarationSourceStart()
 * @see descent.internal.compiler.env.ISourceMethod#getDeclarationSourceStart()
 * @see descent.internal.compiler.env.ISourceField#getDeclarationSourceStart()
 */
public int getDeclarationSourceStart() {
	return fSourceRangeStart;
}
protected ISourceRange getSourceRange() {
	return new SourceRange(fSourceRangeStart, fSourceRangeEnd - fSourceRangeStart + 1);
}
protected void setSourceRangeEnd(int end) {
	fSourceRangeEnd = end;
}
protected void setSourceRangeStart(int start) {
	fSourceRangeStart = start;
}
}
