package descent.internal.ui.text.java;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationExtension;

import descent.core.ICompilationUnit;
import descent.core.JavaModelException;

import descent.ui.PreferenceConstants;
import descent.ui.text.java.CompletionProposalCollector;
import descent.ui.text.java.IJavaCompletionProposalComputer;
import descent.ui.text.java.JavaContentAssistInvocationContext;
import descent.ui.text.java.ContentAssistInvocationContext;

import descent.internal.ui.text.JavaCodeReader;

/**
 * Computes Java completion proposals and context infos.
 * 
 * @since 3.2
 */
public class JavaCompletionProposalComputer implements IJavaCompletionProposalComputer {

	private static final class ContextInformationWrapper implements IContextInformation, IContextInformationExtension {

		private final IContextInformation fContextInformation;
		private int fPosition;

		public ContextInformationWrapper(IContextInformation contextInformation) {
			fContextInformation= contextInformation;
		}

		/*
		 * @see IContextInformation#getContextDisplayString()
		 */
		public String getContextDisplayString() {
			return fContextInformation.getContextDisplayString();
		}

			/*
		 * @see IContextInformation#getImage()
		 */
		public Image getImage() {
			return fContextInformation.getImage();
		}

		/*
		 * @see IContextInformation#getInformationDisplayString()
		 */
		public String getInformationDisplayString() {
			return fContextInformation.getInformationDisplayString();
		}

		/*
		 * @see IContextInformationExtension#getContextInformationPosition()
		 */
		public int getContextInformationPosition() {
			return fPosition;
		}

		public void setContextInformationPosition(int position) {
			fPosition= position;
		}

		/*
		 * @see org.eclipse.jface.text.contentassist.IContextInformation#equals(java.lang.Object)
		 */
		public boolean equals(Object object) {
			if (object instanceof ContextInformationWrapper)
				return fContextInformation.equals(((ContextInformationWrapper) object).fContextInformation);
			else
				return fContextInformation.equals(object);
		}
	}
	
	private String fErrorMessage;
	
	public JavaCompletionProposalComputer() {
	}

	private boolean looksLikeMethod(JavaCodeReader reader) throws IOException {
		int curr= reader.read();
		while (curr != JavaCodeReader.EOF && Character.isWhitespace((char) curr))
			curr= reader.read();

		if (curr == JavaCodeReader.EOF)
			return false;

		return Character.isJavaIdentifierPart((char) curr) || Character.isJavaIdentifierStart((char) curr);
	}

	private int guessContextInformationPosition(ContentAssistInvocationContext context) {
		int contextPosition= context.getInvocationOffset();

		IDocument document= context.getDocument();

		try {

			JavaCodeReader reader= new JavaCodeReader();
			reader.configureBackwardReader(document, contextPosition, true, true);

			int nestingLevel= 0;

			int curr= reader.read();
			while (curr != JavaCodeReader.EOF) {

				if (')' == (char) curr)
					++ nestingLevel;

				else if ('(' == (char) curr) {
					-- nestingLevel;

					if (nestingLevel < 0) {
						int start= reader.getOffset();
						if (looksLikeMethod(reader))
							return start + 1;
					}
				}

				curr= reader.read();
			}
		} catch (IOException e) {
		}

		return contextPosition;
	}

	private List addContextInformations(JavaContentAssistInvocationContext context, int offset, IProgressMonitor monitor) {
		List proposals= internalComputeCompletionProposals(offset, context, monitor);
		List result= new ArrayList(proposals.size());

		for (Iterator it= proposals.iterator(); it.hasNext();) {
			ICompletionProposal proposal= (ICompletionProposal) it.next();
			IContextInformation contextInformation= proposal.getContextInformation();
			if (contextInformation != null) {
				ContextInformationWrapper wrapper= new ContextInformationWrapper(contextInformation);
				wrapper.setContextInformationPosition(offset);
				result.add(wrapper);
			}
		}
		return result;
	}

	/*
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposalComputer#computeContextInformation(org.eclipse.jface.text.contentassist.TextContentAssistInvocationContext, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public List computeContextInformation(ContentAssistInvocationContext context, IProgressMonitor monitor) {
		if (context instanceof JavaContentAssistInvocationContext) {
			JavaContentAssistInvocationContext javaContext= (JavaContentAssistInvocationContext) context;
			
			int contextInformationPosition= guessContextInformationPosition(javaContext);
			List result= addContextInformations(javaContext, contextInformationPosition, monitor);
			return result;
		}
		return Collections.EMPTY_LIST;
	}

	/*
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposalComputer#computeCompletionProposals(org.eclipse.jface.text.contentassist.TextContentAssistInvocationContext, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public List computeCompletionProposals(ContentAssistInvocationContext context, IProgressMonitor monitor) {
		if (context instanceof JavaContentAssistInvocationContext) {
			JavaContentAssistInvocationContext javaContext= (JavaContentAssistInvocationContext) context;
			return internalComputeCompletionProposals(context.getInvocationOffset(), javaContext, monitor);
		}
		return Collections.EMPTY_LIST;
	}

	private List internalComputeCompletionProposals(int offset, JavaContentAssistInvocationContext context, IProgressMonitor monitor) {
		ICompilationUnit unit= context.getCompilationUnit();
		if (unit == null)
			return Collections.EMPTY_LIST;
		
		ITextViewer viewer= context.getViewer();
		
		CompletionProposalCollector collector= createCollector(context);
		collector.setInvocationContext(context);

		try {
			Point selection= viewer.getSelectedRange();
			if (selection.y > 0)
				collector.setReplacementLength(selection.y);
			
				unit.codeComplete(offset, collector);
		} catch (JavaModelException x) {
			Shell shell= viewer.getTextWidget().getShell();
			if (x.isDoesNotExist() && !unit.getJavaProject().isOnClasspath(unit))
				MessageDialog.openInformation(shell, JavaTextMessages.CompletionProcessor_error_notOnBuildPath_title, JavaTextMessages.CompletionProcessor_error_notOnBuildPath_message);
			else
				ErrorDialog.openError(shell, JavaTextMessages.CompletionProcessor_error_accessing_title, JavaTextMessages.CompletionProcessor_error_accessing_message, x.getStatus());
		}

		ICompletionProposal[] javaProposals= collector.getJavaCompletionProposals();
		int contextInformationOffset= guessContextInformationPosition(context);
		if (contextInformationOffset != offset) {
			for (int i= 0; i < javaProposals.length; i++) {
				if (javaProposals[i] instanceof JavaMethodCompletionProposal) {
					JavaMethodCompletionProposal jmcp= (JavaMethodCompletionProposal) javaProposals[i];
					jmcp.setContextInformationPosition(contextInformationOffset);
				} else if (javaProposals[i] instanceof LazyJavaMethodCompletionProposal) {
					LazyJavaMethodCompletionProposal jmcp= (LazyJavaMethodCompletionProposal) javaProposals[i];
					jmcp.setContextInformationPosition(contextInformationOffset);
				} 
			}
		}
		
		List proposals= new ArrayList(Arrays.asList(javaProposals));
		if (proposals.size() == 0) {
			String error= collector.getErrorMessage();
			if (error.length() > 0)
				fErrorMessage= error;
		}
		return proposals;
	}

	/**
	 * Creates the collector used to get proposals from core.
	 */
	protected CompletionProposalCollector createCollector(JavaContentAssistInvocationContext context) {
		if (PreferenceConstants.getPreferenceStore().getBoolean(PreferenceConstants.CODEASSIST_FILL_ARGUMENT_NAMES))
			return new ExperimentalResultCollector(context);
		else
			return new CompletionProposalCollector(context.getCompilationUnit());
	}

	/*
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposalComputer#getErrorMessage()
	 */
	public String getErrorMessage() {
		return fErrorMessage;
	}

	/*
	 * @see descent.ui.text.java.IJavaCompletionProposalComputer#sessionStarted()
	 */
	public void sessionStarted() {
	}

	/*
	 * @see descent.ui.text.java.IJavaCompletionProposalComputer#sessionEnded()
	 */
	public void sessionEnded() {
		fErrorMessage= null;
	}
}
