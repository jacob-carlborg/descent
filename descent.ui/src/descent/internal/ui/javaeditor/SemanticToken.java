/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package descent.internal.ui.javaeditor;

import descent.core.dom.CompilationUnit;
import descent.core.dom.Expression;
import descent.core.dom.IBinding;
import descent.core.dom.SimpleName;

/**
 * Semantic token
 */
public final class SemanticToken {

	/** AST node */
	private SimpleName fNode;
	private Expression fLiteral;

	/** Binding */
	private IBinding fBinding;
	/** Is the binding resolved? */
	private boolean fIsBindingResolved= false;

	/** AST root */
	private CompilationUnit fRoot;
	private boolean fIsRootResolved= false;

	/**
	 * @return Returns the binding, can be <code>null</code>.
	 */
	public IBinding getBinding() {
		if (!fIsBindingResolved) {
			fIsBindingResolved= true;
			if (fNode != null)
				//fBinding= toAlias(fNode.resolveBinding());
				fBinding= fNode.resolveBinding();
		}
		
		return fBinding;
	}
	
//	private IBinding toAlias(IBinding binding) {
//		if (binding instanceof IVariableBinding) {
//			IVariableBinding varBinding = (IVariableBinding) binding;
//			if (varBinding.isAlias() || varBinding.isTypedef()) {
//				return toAlias(varBinding.getType());
//			}
//		}
//		
//		return binding;
//	}

	/**
	 * @return the AST node (a {@link SimpleName})
	 */
	public SimpleName getNode() {
		return fNode;
	}
	
	/**
	 * @return the AST node (a <code>Boolean-, Character- or NumberLiteral</code>)
	 */
	public Expression getLiteral() {
		return fLiteral;
	}

	/**
	 * @return the AST root
	 */
	public CompilationUnit getRoot() {
		if (!fIsRootResolved) {
			fIsRootResolved= true;
			fRoot= (CompilationUnit) (fNode != null ? fNode : fLiteral).getRoot();
		}

		return fRoot;
	}

	/**
	 * Update this token with the given AST node.
	 * <p>
	 * NOTE: Allowed to be used by {@link SemanticHighlightingReconciler} only.
	 * </p>
	 *
	 * @param node the AST simple name
	 */
	void update(SimpleName node) {
		clear();
		fNode= node;
	}

	/**
	 * Update this token with the given AST node.
	 * <p>
	 * NOTE: Allowed to be used by {@link SemanticHighlightingReconciler} only.
	 * </p>
	 *
	 * @param literal the AST literal
	 */
	void update(Expression literal) {
		clear();
		fLiteral= literal;
	}

	/**
	 * Clears this token.
	 * <p>
	 * NOTE: Allowed to be used by {@link SemanticHighlightingReconciler} only.
	 * </p>
	 */
	void clear() {
		fNode= null;
		fLiteral= null;
		fBinding= null;
		fIsBindingResolved= false;
		fRoot= null;
		fIsRootResolved= false;
	}
}
