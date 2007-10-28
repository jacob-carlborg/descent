package descent.internal.ui.text.java;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.BadLocationException;

import descent.core.CompletionProposal;
import descent.core.IJavaProject;
import descent.core.IType;
import descent.core.JavaModelException;
import descent.core.Signature;
import descent.internal.ui.JavaPlugin;
import descent.ui.text.java.CompletionProposalCollector;
import descent.ui.text.java.ContentAssistInvocationContext;
import descent.ui.text.java.IJavaCompletionProposal;
import descent.ui.text.java.JavaContentAssistInvocationContext;

/**
 * 
 * @since 3.2
 */
public class JavaTypeCompletionProposalComputer extends JavaCompletionProposalComputer {
	/*
	 * @see descent.internal.ui.text.java.JavaCompletionProposalComputer#createCollector(descent.ui.text.java.JavaContentAssistInvocationContext)
	 */
	protected CompletionProposalCollector createCollector(JavaContentAssistInvocationContext context) {
		CompletionProposalCollector collector= super.createCollector(context);
		collector.setIgnored(CompletionProposal.ANNOTATION_ATTRIBUTE_REF, true);
		collector.setIgnored(CompletionProposal.ANONYMOUS_CLASS_DECLARATION, true);
		collector.setIgnored(CompletionProposal.DDOC_MACRO, true);
		collector.setIgnored(CompletionProposal.FIELD_REF, true);		
		// collector.setIgnored(CompletionProposal.KEYWORD, true);
		collector.setIgnored(CompletionProposal.KEYWORD, false);
		collector.setIgnored(CompletionProposal.LABEL_REF, true);
		collector.setIgnored(CompletionProposal.LOCAL_VARIABLE_REF, true);
		collector.setIgnored(CompletionProposal.METHOD_DECLARATION, true);
		collector.setIgnored(CompletionProposal.METHOD_NAME_REFERENCE, true);
		collector.setIgnored(CompletionProposal.METHOD_REF, true);
		collector.setIgnored(CompletionProposal.PACKAGE_REF, true);
		collector.setIgnored(CompletionProposal.VERSION_REF, true);
		collector.setIgnored(CompletionProposal.POTENTIAL_METHOD_DECLARATION, true);
		collector.setIgnored(CompletionProposal.VARIABLE_DECLARATION, true);
		
		collector.setIgnored(CompletionProposal.JAVADOC_BLOCK_TAG, true);
		collector.setIgnored(CompletionProposal.JAVADOC_FIELD_REF, true);
		collector.setIgnored(CompletionProposal.JAVADOC_INLINE_TAG, true);
		collector.setIgnored(CompletionProposal.JAVADOC_METHOD_REF, true);
		collector.setIgnored(CompletionProposal.JAVADOC_PARAM_REF, true);
		collector.setIgnored(CompletionProposal.JAVADOC_TYPE_REF, true);
		collector.setIgnored(CompletionProposal.JAVADOC_VALUE_REF, true);
		
		collector.setIgnored(CompletionProposal.TYPE_REF, false);
		return collector;
	}
	
	/*
	 * @see descent.internal.ui.text.java.JavaCompletionProposalComputer#computeCompletionProposals(org.eclipse.jface.text.contentassist.TextContentAssistInvocationContext, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public List computeCompletionProposals(ContentAssistInvocationContext context, IProgressMonitor monitor) {
		/* TODO Descent see why there are two: JavaTypeCompletionProposal and JavaNoTypeCompletionProposal
		List types= super.computeCompletionProposals(context, monitor);
		if (context instanceof JavaContentAssistInvocationContext) {
			JavaContentAssistInvocationContext javaContext= (JavaContentAssistInvocationContext) context;
			try {
				if (types.size() > 0 && context.computeIdentifierPrefix().length() == 0) {
					IType expectedType= javaContext.getExpectedType();
					if (expectedType != null) {
						// empty prefix completion - insert LRU types if known
						LazyJavaTypeCompletionProposal typeProposal= (LazyJavaTypeCompletionProposal) types.get(0);
						List history= JavaPlugin.getDefault().getContentAssistHistory().getHistory(expectedType.getFullyQualifiedName()).getTypes();
						
						int relevance= typeProposal.getRelevance() - history.size() - 1;
						for (Iterator it= history.iterator(); it.hasNext();) {
							String type= (String) it.next();
							if (type.equals(((IType) typeProposal.getJavaElement()).getFullyQualifiedName()))
								continue;
							
							IJavaCompletionProposal proposal= createTypeProposal(relevance, type, javaContext);
							
							if (proposal != null)
								types.add(proposal);
							relevance++;
						}
					}
				}
			} catch (BadLocationException x) {
				// log & ignore
				JavaPlugin.log(x);
			} catch (JavaModelException x) {
				// log & ignore
				JavaPlugin.log(x);
			}
		}
		return types;
		*/
		return Collections.EMPTY_LIST;
	}

	private IJavaCompletionProposal createTypeProposal(int relevance, String fullyQualifiedType, JavaContentAssistInvocationContext context) throws JavaModelException {
		IType type= context.getCompilationUnit().getJavaProject().findType(fullyQualifiedType);
		if (type == null)
			return null;
		
		CompletionProposal proposal= CompletionProposal.create(CompletionProposal.TYPE_REF, context.getInvocationOffset());
		proposal.setCompletion(type.getElementName().toCharArray());
		proposal.setDeclarationSignature(type.getPackageFragment().getElementName().toCharArray());
		proposal.setFlags(type.getFlags());
		proposal.setRelevance(relevance);
		proposal.setReplaceRange(context.getInvocationOffset(), context.getInvocationOffset());
		proposal.setSignature(Signature.createTypeSignature(fullyQualifiedType, true).toCharArray());

		if (shouldProposeGenerics(context.getProject()))
			return new GenericJavaTypeProposal(proposal, context);
		else
			return new LazyJavaTypeCompletionProposal(proposal, context);
	}
	
	/**
	 * Returns <code>true</code> if generic proposals should be allowed,
	 * <code>false</code> if not. Note that even though code (in a library)
	 * may be referenced that uses generics, it is still possible that the
	 * current source does not allow generics.
	 *
	 * @return <code>true</code> if the generic proposals should be allowed,
	 *         <code>false</code> if not
	 */
	private final boolean shouldProposeGenerics(IJavaProject project) {
		return true;
		/*
		String sourceVersion;
		if (project != null)
			sourceVersion= project.getOption(JavaCore.COMPILER_SOURCE, true);
		else
			sourceVersion= JavaCore.getOption(JavaCore.COMPILER_SOURCE);

		return sourceVersion != null && JavaCore.VERSION_1_5.compareTo(sourceVersion) <= 0;
		*/
	}

}
