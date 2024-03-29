/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package descent.internal.ui.text.java;

import descent.core.CompletionProposal;
import descent.core.IJavaProject;
import descent.core.IMember;
import descent.core.JavaModelException;

import descent.internal.corext.template.java.SignatureUtil;


/**
 * Proposal info that computes the javadoc lazily when it is queried.
 *
 * @since 3.1
 */
public final class AnonymousTypeProposalInfo extends MemberProposalInfo {

	/**
	 * Creates a new proposal info.
	 *
	 * @param project the java project to reference when resolving types
	 * @param proposal the proposal to generate information for
	 */
	public AnonymousTypeProposalInfo(IJavaProject project, CompletionProposal proposal) {
		super(project, proposal);
	}

	/**
	 * Resolves the member described by the receiver and returns it if found.
	 * Returns <code>null</code> if no corresponding member can be found.
	 *
	 * @return the resolved member or <code>null</code> if none is found
	 * @throws JavaModelException if accessing the java model fails
	 */
	protected IMember resolveMember() throws JavaModelException {
		char[] signature= fProposal.getDeclarationSignature();
		String typeName= SignatureUtil.stripSignatureToFQN(String.valueOf(signature));
		return fJavaProject.findType(typeName);
	}
}
