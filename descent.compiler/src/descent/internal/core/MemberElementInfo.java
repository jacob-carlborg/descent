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

/** 
 *Element info for IMember elements. 
 */
/* package */ abstract class MemberElementInfo extends SourceRefElementInfo {
	/**
	 * The modifiers associated with this member.
	 *
	 * @see descent.internal.compiler.classfmt.ClassFileConstants
	 */
	protected long flags;

	/**
	 * The start position of this member's name in the its
	 * openable's buffer.
	 */
	protected int nameStart= -1;

	/**
	 * The last position of this member's name in the its
	 * openable's buffer.
	 */
	protected int nameEnd= -1;

	/**
	 * @see descent.internal.compiler.env.IGenericType#getModifiers()
	 * @see descent.internal.compiler.env.IGenericMethod#getModifiers()
	 * @see descent.internal.compiler.env.IGenericField#getModifiers()
	 */
	public long getModifiers() {
		return this.flags;
	}
	/**
	 * @see descent.internal.compiler.env.ISourceType#getNameSourceEnd()
	 * @see descent.internal.compiler.env.ISourceMethod#getNameSourceEnd()
	 * @see descent.internal.compiler.env.ISourceField#getNameSourceEnd()
	 */
	public int getNameSourceEnd() {
		return this.nameEnd;
	}
	/**
	 * @see descent.internal.compiler.env.ISourceType#getNameSourceStart()
	 * @see descent.internal.compiler.env.ISourceMethod#getNameSourceStart()
	 * @see descent.internal.compiler.env.ISourceField#getNameSourceStart()
	 */
	public int getNameSourceStart() {
		return this.nameStart;
	}
	protected void setFlags(long flags) {
		this.flags = flags;
	}
	/**
	 * Sets the last position of this member's name, relative
	 * to its openable's source buffer.
	 */
	protected void setNameSourceEnd(int end) {
		this.nameEnd= end;
	}
	/**
	 * Sets the start position of this member's name, relative
	 * to its openable's source buffer.
	 */
	protected void setNameSourceStart(int start) {
		this.nameStart= start;
	}
}
