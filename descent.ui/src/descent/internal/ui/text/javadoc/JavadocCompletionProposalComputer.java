package descent.internal.ui.text.javadoc;

import descent.core.CompletionProposal;

import descent.ui.text.java.CompletionProposalCollector;
import descent.ui.text.java.JavaContentAssistInvocationContext;

import descent.internal.ui.text.java.JavaCompletionProposalComputer;

/**
 * 
 * @since 3.2
 */
public class JavadocCompletionProposalComputer extends JavaCompletionProposalComputer {
	/*
	 * @see descent.internal.ui.text.java.JavaCompletionProposalComputer#createCollector(descent.ui.text.java.JavaContentAssistInvocationContext)
	 */
	protected CompletionProposalCollector createCollector(JavaContentAssistInvocationContext context) {
		CompletionProposalCollector collector= super.createCollector(context);
		collector.setIgnored(CompletionProposal.ANNOTATION_ATTRIBUTE_REF, true);
		collector.setIgnored(CompletionProposal.ANONYMOUS_CLASS_DECLARATION, true);
		collector.setIgnored(CompletionProposal.DDOC_MACRO, false);
		collector.setIgnored(CompletionProposal.FIELD_REF, false);
		collector.setIgnored(CompletionProposal.KEYWORD, false);
		collector.setIgnored(CompletionProposal.LABEL_REF, true);
		collector.setIgnored(CompletionProposal.LOCAL_VARIABLE_REF, true);
		collector.setIgnored(CompletionProposal.METHOD_DECLARATION, true);
		collector.setIgnored(CompletionProposal.METHOD_NAME_REFERENCE, true);
		collector.setIgnored(CompletionProposal.METHOD_REF, false);
		collector.setIgnored(CompletionProposal.OP_CALL, false);
		collector.setIgnored(CompletionProposal.FUNCTION_CALL, false);
		collector.setIgnored(CompletionProposal.PACKAGE_REF, true);
		collector.setIgnored(CompletionProposal.POTENTIAL_METHOD_DECLARATION, true);
		collector.setIgnored(CompletionProposal.VARIABLE_DECLARATION, true);
		collector.setIgnored(CompletionProposal.JAVADOC_TYPE_REF, false);
		collector.setIgnored(CompletionProposal.JAVADOC_FIELD_REF, false);
		collector.setIgnored(CompletionProposal.JAVADOC_METHOD_REF, false);
		collector.setIgnored(CompletionProposal.JAVADOC_PARAM_REF, false);
		collector.setIgnored(CompletionProposal.JAVADOC_VALUE_REF, false);
		collector.setIgnored(CompletionProposal.TYPE_REF, false);
		collector.setIgnored(CompletionProposal.TEMPLATE_REF, false);
		collector.setIgnored(CompletionProposal.TEMPLATED_AGGREGATE_REF, false);
		collector.setIgnored(CompletionProposal.TEMPLATED_FUNCTION_REF, false);
		return collector;
	}
}
