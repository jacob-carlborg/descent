/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package descent.internal.ui.text.java;

import org.eclipse.jface.text.Assert;

import descent.core.CompletionProposal;
import descent.core.IJavaElement;
import descent.core.IJavaProject;
import descent.core.IMember;
import descent.core.JavaModelException;

/**
 * Proposal info that computes the javadoc lazily when it is queried.
 *
 * @since 3.1
 */
public abstract class MemberProposalInfo extends ProposalInfo {
	/* configuration */
	protected final IJavaProject fJavaProject;
	protected final CompletionProposal fProposal;

	/* cache filled lazily */
	private boolean fJavaElementResolved= false;

	/**
	 * Creates a new proposal info.
	 *
	 * @param project the java project to reference when resolving types
	 * @param proposal the proposal to generate information for
	 */
	public MemberProposalInfo(IJavaProject project, CompletionProposal proposal) {
		Assert.isNotNull(project);
		Assert.isNotNull(proposal);
		fJavaProject= project;
		fProposal= proposal;
	}

	/**
	 * Returns the java element that this computer corresponds to, possibly <code>null</code>.
	 * 
	 * @return the java element that this computer corresponds to, possibly <code>null</code>
	 * @throws JavaModelException
	 */
	public IJavaElement getJavaElement() throws JavaModelException {
		if (!fJavaElementResolved) {
			fJavaElementResolved= true;
			fElement= resolveMember();
		}
		return fElement;
	}

	/**
	 * Resolves the member described by the receiver and returns it if found.
	 * Returns <code>null</code> if no corresponding member can be found.
	 *
	 * @return the resolved member or <code>null</code> if none is found
	 * @throws JavaModelException if accessing the java model fails
	 */
	protected IMember resolveMember() throws JavaModelException {
//		char[] signature = fProposal.getSignature();
//		if (signature == null) {
//			return null;
//		}
//		
//		IJavaElement result = fJavaProject.findBySignature(new String(signature));
		IJavaElement result = fProposal.getJavaElement();
		if (result != null && result instanceof IMember) {
			return (IMember) result;
		} else {
			return null;
		}
	}


}
