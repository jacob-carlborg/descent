/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package descent.internal.core;

import java.util.HashMap;

import org.eclipse.core.runtime.IProgressMonitor;
import descent.core.*;
import descent.core.compiler.CharOperation;

public class TypeParameter extends SourceRefElement implements ITypeParameter {

	static final ITypeParameter[] NO_TYPE_PARAMETERS = new ITypeParameter[0];
	
	protected String name;
	
	public TypeParameter(JavaElement parent, String name) {
		super(parent);
		this.name = name;
	}

	public boolean equals(Object o) {
		if (!(o instanceof TypeParameter)) return false;
		return super.equals(o);
	}

	/*
	 * @see JavaElement#generateInfos
	 */
	protected void generateInfos(Object info, HashMap newElements, IProgressMonitor pm) throws JavaModelException {
		Openable openableParent = (Openable)getOpenableParent();
		if (openableParent == null) return;
		
		JavaElementInfo openableParentInfo = (JavaElementInfo) JavaModelManager.getJavaModelManager().getInfo(openableParent);
		if (openableParentInfo == null) {
			openableParent.generateInfos(openableParent.createElementInfo(), newElements, pm);
		}
		
		/* TODO JDT binary
		if (openableParent.getElementType() == IJavaElement.CLASS_FILE) {
			ClassFileInfo classFileInfo = (ClassFileInfo) (openableParentInfo == null ? newElements.get(openableParent) : openableParentInfo);
			if (classFileInfo == null) return;
			classFileInfo.getBinaryChildren(newElements); // forces the initialization
		}
		*/
	}	
	
	public String[] getBounds() throws JavaModelException {
		TypeParameterElementInfo info = (TypeParameterElementInfo) getElementInfo();
		return CharOperation.toStrings(info.bounds);
	}
	
	public String getSignature() throws JavaModelException {
		TypeParameterElementInfo info = (TypeParameterElementInfo) getElementInfo();
		if (info.signature != null) {
			return new String(info.signature);
		} else {
			return CharOperation.NO_STRINGS[0];
		}
	}

	public IMember getDeclaringMember() {
			return (IMember) getParent();
	}

	public String getElementName() {
		return this.name;
	}

	public int getElementType() {
		return TYPE_PARAMETER;
	}

	protected char getHandleMementoDelimiter() {
		return JavaElement.JEM_TYPE_PARAMETER;
	}
	
	public ISourceRange getNameRange() throws JavaModelException {
		/* TODO JDT binary
		SourceMapper mapper= getSourceMapper();
		if (mapper != null) {
			// ensure the class file's buffer is open so that source ranges are computed
			ClassFile classFile = (ClassFile)getClassFile();
			if (classFile != null) {
				classFile.getBuffer();
				return mapper.getNameRange(this);
			}
		}
		*/
		TypeParameterElementInfo info = (TypeParameterElementInfo) getElementInfo();
		return new SourceRange(info.nameStart, info.nameEnd - info.nameStart + 1);
	}

	/*
	 * @see ISourceReference
	 */
	public ISourceRange getSourceRange() throws JavaModelException {
		/* TODO JDT binary
		SourceMapper mapper= getSourceMapper();
		if (mapper != null) {
			// ensure the class file's buffer is open so that source ranges are computed
			ClassFile classFile = (ClassFile)getClassFile();
			if (classFile != null) {
				classFile.getBuffer();
				return mapper.getSourceRange(this);
			}
		}
		*/
		return super.getSourceRange();
	}

	public IClassFile getClassFile() {
		return ((JavaElement)getParent()).getClassFile();
	}

	protected void toStringName(StringBuffer buffer) {
		buffer.append('<');
		buffer.append(getElementName());
		buffer.append('>');
	}
	@Override
	protected void appendElementSignature(StringBuilder sb) {
		// TODO Descent signature
	}
}
