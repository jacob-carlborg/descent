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
package descent.ui.text.java;

import org.eclipse.core.runtime.Assert;

import org.eclipse.jface.text.ITextViewer;

import org.eclipse.ui.IEditorPart;

import descent.core.CompletionContext;
import descent.core.ICompilationUnit;
import descent.core.IJavaElement;
import descent.core.IJavaProject;
import descent.core.IType;
import descent.core.JavaModelException;

import descent.internal.corext.template.java.SignatureUtil;

import descent.internal.ui.JavaPlugin;
import descent.internal.ui.javaeditor.EditorUtility;
import descent.internal.ui.text.java.ContentAssistHistory.RHSHistory;

/**
 * Describes the context of a content assist invocation in a Java editor.
 * <p>
 * Clients may use but not subclass this class.
 * </p>
 * 
 * @since 3.2
 */
public class JavaContentAssistInvocationContext extends ContentAssistInvocationContext {
	private final IEditorPart fEditor;
	
	private ICompilationUnit fCU= null;
	private boolean fCUComputed= false;
	
	private CompletionProposalLabelProvider fLabelProvider;
	private CompletionProposalCollector fCollector;
	private RHSHistory fRHSHistory;
	private IType fType;

	/**
	 * Creates a new context.
	 * 
	 * @param viewer the viewer used by the editor
	 * @param offset the invocation offset
	 * @param editor the editor that content assist is invoked in
	 */
	public JavaContentAssistInvocationContext(ITextViewer viewer, int offset, IEditorPart editor) {
		super(viewer, offset);
		Assert.isNotNull(editor);
		fEditor= editor;
	}
	
	/**
	 * Creates a new context.
	 * 
	 * @param unit the compilation unit in <code>document</code>
	 */
	public JavaContentAssistInvocationContext(ICompilationUnit unit) {
		super();
		fCU= unit;
		fCUComputed= true;
		fEditor= null;
	}
	
	/**
	 * Returns the compilation unit that content assist is invoked in, <code>null</code> if there
	 * is none.
	 * 
	 * @return the compilation unit that content assist is invoked in, possibly <code>null</code>
	 */
	public ICompilationUnit getCompilationUnit() {
		if (!fCUComputed) {
			fCUComputed= true;
			if (fCollector != null)
				fCU= fCollector.getCompilationUnit();
			else {
				IJavaElement je= EditorUtility.getEditorInputJavaElement(fEditor, false);
				if (je instanceof ICompilationUnit)
					fCU= (ICompilationUnit)je;
			}
		}
		return fCU;
	}
	
	/**
	 * Returns the project of the compilation unit that content assist is invoked in,
	 * <code>null</code> if none.
	 * 
	 * @return the current java project, possibly <code>null</code>
	 */
	public IJavaProject getProject() {
		ICompilationUnit unit= getCompilationUnit();
		return unit == null ? null : unit.getJavaProject();
	}
	
	/**
	 * Returns the keyword proposals that are available in this context, possibly none.
	 * 
	 * @return the available keyword proposals.
	 */
	public IJavaCompletionProposal[] getKeywordProposals() {
		if (fCollector != null)
			return fCollector.getKeywordCompletionProposals();
		return new IJavaCompletionProposal[0];
	}
	
	/**
	 * Sets the collector, which is used to access the compilation unit, the core context and the
	 * label provider.
	 * 
	 * @param collector the collector
	 */
	void setCollector(CompletionProposalCollector collector) {
		fCollector= collector;
	}
	
	/**
	 * Returns the {@link CompletionContext core completion context} if available, <code>null</code>
	 * otherwise.
	 * 
	 * @return the core completion context if available, <code>null</code> otherwise
	 */
	public CompletionContext getCoreContext() {
		if (fCollector != null)
			return fCollector.getContext();
		return null;
	}

	/**
	 * Returns an float in [0.0,&nbsp;1.0] based on whether the type has been recently used as a
	 * right hand side for the type expected in the current context. 0 signals that the
	 * <code>qualifiedTypeName</code> does not match the expected type, while 1.0 signals that
	 * <code>qualifiedTypeName</code> has most recently been used in a similar context.
	 * 
	 * @param qualifiedTypeName the type name of the type of interest
	 * @return a relevance in [0.0,&nbsp;1.0] based on previous content assist invocations
	 */
	public float getHistoryRelevance(String qualifiedTypeName) {
		return getRHSHistory().getRank(qualifiedTypeName);
	}
	
	/**
	 * Returns the content assist type history for the expected type.
	 * 
	 * @return the content assist type history for the expected type
	 */
	private RHSHistory getRHSHistory() {
		if (fRHSHistory == null) {
			CompletionContext context= getCoreContext();
			if (context != null) {
				char[][] expectedTypes= context.getExpectedTypesSignatures();
				if (expectedTypes != null && expectedTypes.length > 0) {
					String expected= SignatureUtil.stripSignatureToFQN(String.valueOf(expectedTypes[0]));
					fRHSHistory= JavaPlugin.getDefault().getContentAssistHistory().getHistory(expected);
				}
			}
			if (fRHSHistory == null)
				fRHSHistory= JavaPlugin.getDefault().getContentAssistHistory().getHistory(null);
		}
		return fRHSHistory;
	}
	
	/**
	 * Returns the expected type if any, <code>null</code> otherwise.
	 * 
	 * @return the expected type if any, <code>null</code> otherwise
	 */
	public IType getExpectedType() {
		if (fType == null && getCompilationUnit() != null) {
			CompletionContext context= getCoreContext();
			if (context != null) {
				char[][] expectedTypes= context.getExpectedTypesSignatures();
				if (expectedTypes != null && expectedTypes.length > 0) {
					IJavaProject project= getCompilationUnit().getJavaProject();
					if (project != null) {
						try {
							fType= project.findType(SignatureUtil.stripSignatureToFQN(String.valueOf(expectedTypes[0])));
						} catch (JavaModelException x) {
							JavaPlugin.log(x);
						}
					}
				}
			}
		}
		return fType;
	}
	
	/**
	 * Returns a label provider that can be used to compute proposal labels.
	 * 
	 * @return a label provider that can be used to compute proposal labels
	 */
	public CompletionProposalLabelProvider getLabelProvider() {
		if (fLabelProvider == null) {
			if (fCollector != null)
				fLabelProvider= fCollector.getLabelProvider();
			else
				fLabelProvider= new CompletionProposalLabelProvider();
		}

		return fLabelProvider;
	}
	
	/*
	 * Implementation note: There is no need to override hashCode and equals, as we only add cached
	 * values shared across one assist invocation.
	 */
}
