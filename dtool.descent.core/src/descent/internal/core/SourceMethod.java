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

import org.eclipse.core.runtime.Assert;

import descent.core.Flags;
import descent.core.IBuffer;
import descent.core.ICompilationUnit;
import descent.core.IJavaElement;
import descent.core.IMethod;
import descent.core.ISourceRange;
import descent.core.IType;
import descent.core.ITypeParameter;
import descent.core.JavaModelException;
import descent.core.Signature;
import descent.core.ToolFactory;
import descent.core.compiler.CharOperation;
import descent.core.compiler.IScanner;
import descent.core.compiler.ITerminalSymbols;
import descent.core.compiler.InvalidInputException;
import descent.core.dom.AST;
import descent.internal.core.util.Util;

/**
 * @see IMethod
 */

public class SourceMethod extends NamedMember implements IMethod {

	/**
	 * The parameter type signatures of the method - stored locally
	 * to perform equality test. <code>null</code> indicates no
	 * parameters.
	 */
	protected String[] parameterTypes;

protected SourceMethod(JavaElement parent, String name, String[] parameterTypes) {
	super(parent, name);
	Assert.isTrue(name.indexOf('.') == -1);
	if (parameterTypes == null) {
		this.parameterTypes= CharOperation.NO_STRINGS;
	} else {
		this.parameterTypes= parameterTypes;
	}
}
protected void closing(Object info) throws JavaModelException {
	super.closing(info);
	SourceMethodElementInfo elementInfo = (SourceMethodElementInfo) info;
	ITypeParameter[] typeParameters = elementInfo.typeParameters;
	for (int i = 0, length = typeParameters.length; i < length; i++) {
		((TypeParameter) typeParameters[i]).close();
	}
}
public boolean equals(Object o) {
	if (!(o instanceof SourceMethod)) return false;
	return super.equals(o) && Util.equalArraysOrNull(this.parameterTypes, ((SourceMethod)o).parameterTypes);
}
/**
 * @see IJavaElement
 */
public int getElementType() {
	return METHOD;
}
/**
 * @see IMethod
 */
public String[] getExceptionTypes() throws JavaModelException {
	/* TODO JDT exceptions
	SourceMethodElementInfo info = (SourceMethodElementInfo) getElementInfo();
	char[][] exs= info.getExceptionTypeNames();
	return CompilationUnitStructureRequestor.convertTypeNamesToSigs(exs);
	*/
	return new String[0];
}
/**
 * @see JavaElement#getHandleMemento(StringBuffer)
 */
protected void getHandleMemento(StringBuffer buff) {
	((JavaElement) getParent()).getHandleMemento(buff);
	char delimiter = getHandleMementoDelimiter();
	buff.append(delimiter);
	escapeMementoName(buff, getElementName());
	for (int i = 0; i < this.parameterTypes.length; i++) {
		buff.append(delimiter);
		escapeMementoName(buff, this.parameterTypes[i]);
	}
	if (this.occurrenceCount > 1) {
		buff.append(JEM_COUNT);
		buff.append(this.occurrenceCount);
	}
}
/**
 * @see JavaElement#getHandleMemento()
 */
protected char getHandleMementoDelimiter() {
	return JavaElement.JEM_METHOD;
}
/* (non-Javadoc)
 * @see descent.core.IMethod#getKey()
 */
public String getKey() {
	try {
		return getKey(this, false/*don't open*/);
	} catch (JavaModelException e) {
		// happen only if force open is true
		return null;
	}
}
/**
 * @see IMethod
 */
public int getNumberOfParameters() {
	return this.parameterTypes == null ? 0 : this.parameterTypes.length;
}
/**
 * @see IMethod
 */
public String[] getParameterNames() throws JavaModelException {
	SourceMethodElementInfo info = (SourceMethodElementInfo) getElementInfo();
	char[][] names= info.getArgumentNames();
	return CharOperation.toStrings(names);
}
/**
 * @see IMethod
 */
public String[] getParameterTypes() {
	return this.parameterTypes;
}
/**
 * @see IMethod
 */
public String[] getRawParameterTypes() throws JavaModelException {
	SourceMethodElementInfo info = (SourceMethodElementInfo) getElementInfo();
	return CharOperation.toStrings(info.parameterTypes);
}

public ITypeParameter getTypeParameter(String typeParameterName) {
	return new TypeParameter(this, typeParameterName);
}

public ITypeParameter[] getTypeParameters() throws JavaModelException {
	SourceMethodElementInfo info = (SourceMethodElementInfo) getElementInfo();
	return info.typeParameters;
}

/**
 * @see IMethod#getTypeParameterSignatures()
 * @since 3.0
 * @deprecated
 */
public String[] getTypeParameterSignatures() throws JavaModelException {
	ITypeParameter[] typeParameters = getTypeParameters();
	int length = typeParameters.length;
	String[] typeParameterSignatures = new String[length];
	for (int i = 0; i < length; i++) {
		TypeParameter typeParameter = (TypeParameter) typeParameters[i];
		TypeParameterElementInfo info = (TypeParameterElementInfo) typeParameter.getElementInfo();
		char[][] bounds = info.bounds;
		if (bounds == null) {
			typeParameterSignatures[i] = Signature.createTypeParameterSignature(typeParameter.getElementName(), CharOperation.NO_STRINGS);
		} else {
			int boundsLength = bounds.length;
			char[][] boundSignatures = new char[boundsLength][];
			for (int j = 0; j < boundsLength; j++) {
				boundSignatures[j] = Signature.createCharArrayTypeSignature(bounds[j], false);
			}
			typeParameterSignatures[i] = new String(Signature.createTypeParameterSignature(typeParameter.getElementName().toCharArray(), boundSignatures));
		}
	}
	return typeParameterSignatures;
}

/*
 * @see JavaElement#getPrimaryElement(boolean)
 */
public IJavaElement getPrimaryElement(boolean checkOwner) {
	if (checkOwner) {
		CompilationUnit cu = (CompilationUnit)getAncestor(COMPILATION_UNIT);
		if (cu.isPrimary()) return this;
	}
	IJavaElement primaryParent = this.parent.getPrimaryElement(false);
	return ((IType)primaryParent).getMethod(this.name, this.parameterTypes);
}
public String[] getRawParameterNames() throws JavaModelException {
	return getParameterNames();
}
/**
 * @see IMethod
 */
public String getReturnType() throws JavaModelException {
	SourceMethodElementInfo info = (SourceMethodElementInfo) getElementInfo();
	return Signature.createTypeSignature(info.getReturnTypeName(), false);
}
/**
 * @see IMethod
 */
public String getRawReturnType() throws JavaModelException {
	SourceMethodElementInfo elementInfo = (SourceMethodElementInfo) getElementInfo();
	if (elementInfo instanceof SourceMethodInfo) {
		SourceMethodInfo info = (SourceMethodInfo) elementInfo;
		return new String(info.returnType);
	} else {
		return "void";
	}
}
/**
 * @see IMethod
 */
public String getSignature() throws JavaModelException {
	SourceMethodElementInfo info = (SourceMethodElementInfo) getElementInfo();
	return Signature.createMethodSignature(this.parameterTypes, Signature.createTypeSignature(info.getReturnTypeName(), false));
}
/**
 * @see descent.internal.core.JavaElement#hashCode()
 */
public int hashCode() {
   int hash = super.hashCode();
	for (int i = 0, length = this.parameterTypes.length; i < length; i++) {
	    hash = Util.combineHashCodes(hash, this.parameterTypes[i].hashCode());
	}
	return hash;
}
public boolean isMethod() throws JavaModelException {
	int flags = getFlags();
	return !Flags.isConstructor(flags) 
		&& !Flags.isDestructor(flags)
		&& !Flags.isNew(flags)
		&& !Flags.isDelete(flags);
}
/**
 * @see IMethod
 */
public boolean isConstructor() throws JavaModelException {
	return Flags.isConstructor(getFlags());
}
public boolean isDestructor() throws JavaModelException {
	return Flags.isDestructor(getFlags());
}
public boolean isNew() throws JavaModelException {
	return Flags.isNew(getFlags());
}
public boolean isDelete() throws JavaModelException {
	return Flags.isDelete(getFlags());
}
/**
 * @see IMethod#isMainMethod()
 */
public boolean isMainMethod() throws JavaModelException {
	return this.isMainMethod(this);
}
/* (non-Javadoc)
 * @see descent.core.IMethod#isResolved()
 */
public boolean isResolved() {
	return false;
}
/**
 * @see IMethod#isSimilar(IMethod)
 */
public boolean isSimilar(IMethod method) {
	return 
		areSimilarMethods(
			this.getElementName(), this.getParameterTypes(),
			method.getElementName(), method.getParameterTypes(),
			null);
}

@Override
public ISourceRange getNameRange() throws JavaModelException {
	if (isConstructor() || isDestructor() || isNew() || isDelete()) {
		int token = 0;
		if (isConstructor()) {
			token = ITerminalSymbols.TokenNamethis;
		} else if (isDestructor()) {
			token = ITerminalSymbols.TokenNameTILDE;
		} else if (isNew()) {
			token = ITerminalSymbols.TokenNamenew;
		} else if (isDelete()) {
			token = ITerminalSymbols.TokenNamedelete;
		}
		
		ISourceRange range= this.getSourceRange();
		if (range == null) return null;
		IBuffer buf= null;
		if (this.isBinary()) {
			buf = this.getClassFile().getBuffer();
		} else {
			ICompilationUnit compilationUnit = this.getCompilationUnit();
			if (!compilationUnit.isConsistent()) {
				return null;
			}
			buf = compilationUnit.getBuffer();
		}
		final int start= range.getOffset();
		final int length= range.getLength();
		
		if (length > 0) {
			IScanner scanner= ToolFactory.createScanner(true, false, false, false, AST.D2);
			scanner.setSource(buf.getText(start, length).toCharArray());
			try {
				int nameOffset= -1;
				int nameEnd= -1;
				
				int terminal= scanner.getNextToken();
				while (terminal != ITerminalSymbols.TokenNameEOF) {
					if (terminal == token) {
						nameOffset = scanner.getCurrentTokenStartPosition();
						nameEnd = scanner.getCurrentTokenEndPosition();
						if (token == ITerminalSymbols.TokenNameTILDE) {
							nameEnd += 4; // ~this
						}
						return new SourceRange(nameOffset + start, nameEnd - nameOffset + 1);
					}
					
					terminal = scanner.getNextToken();
				}
			} catch (InvalidInputException ex) {
				// try if there is inherited Javadoc
			}
		}		
		return null;
	}
	return super.getNameRange();
}

/**
 */
public String readableName() {

	StringBuffer buffer = new StringBuffer(super.readableName());
	buffer.append('(');
	int length;
	if (this.parameterTypes != null && (length = this.parameterTypes.length) > 0) {
		for (int i = 0; i < length; i++) {
			buffer.append(Signature.toString(this.parameterTypes[i]));
			if (i < length - 1) {
				buffer.append(", "); //$NON-NLS-1$
			}
		}
	}
	buffer.append(')');
	return buffer.toString();
}
/* TODO JDT binding
public JavaElement resolved(Binding binding) {
	SourceRefElement resolvedHandle = new ResolvedSourceMethod(this.parent, this.name, this.parameterTypes, new String(binding.computeUniqueKey()));
	resolvedHandle.occurrenceCount = this.occurrenceCount;
	return resolvedHandle;
}
*/
/**
 * @private Debugging purposes
 */
// TODO JDT debug purposes
protected void toStringInfo(int tab, StringBuffer buffer, Object info, boolean showResolvedInfo) {
	buffer.append(tabString(tab));
	if (info == null) {
		toStringName(buffer);
		buffer.append(" (not open)"); //$NON-NLS-1$
	} else if (info == NO_INFO) {
		toStringName(buffer);
	} else {
		SourceMethodElementInfo methodInfo = (SourceMethodElementInfo) info;
		int flags = methodInfo.getModifiers();
		if (Flags.isStatic(flags)) {
			buffer.append("static "); //$NON-NLS-1$
		}
		/*
		if (!methodInfo.isConstructor()) {
			buffer.append(methodInfo.getReturnTypeName());
			buffer.append(' ');
		}
		*/
		toStringName(buffer, flags);
	}
}
protected void toStringName(StringBuffer buffer) {
	toStringName(buffer, 0);
}
protected void toStringName(StringBuffer buffer, int flags) {
	buffer.append(getElementName());
	buffer.append('(');
	/* TODO JDT debug -> varargs
	String[] parameters = getParameterTypes();
	int length;
	if (parameters != null && (length = parameters.length) > 0) {
		boolean isVarargs = Flags.isVarargs(flags);
		for (int i = 0; i < length; i++) {
			try {
				if (i < length - 1) {
					buffer.append(Signature.toString(parameters[i]));
					buffer.append(", "); //$NON-NLS-1$
				} else if (isVarargs) {
					// remove array from signature
					String parameter = parameters[i].substring(1);
					buffer.append(Signature.toString(parameter));
					buffer.append(" ..."); //$NON-NLS-1$
				} else {
					buffer.append(Signature.toString(parameters[i]));
				}
			} catch (IllegalArgumentException e) {
				// parameter signature is malformed
				buffer.append("*** invalid signature: "); //$NON-NLS-1$
				buffer.append(parameters[i]);
			}
		}
	}
	*/
	buffer.append(')');
	if (this.occurrenceCount > 1) {
		buffer.append("#"); //$NON-NLS-1$
		buffer.append(this.occurrenceCount);
	}
}
}