package descent.internal.ui.text.java;

import org.eclipse.jface.preference.IPreferenceStore;

import descent.core.CompletionProposal;
import descent.core.ICompilationUnit;
import descent.core.IJavaProject;
import descent.core.Signature;
import descent.internal.ui.JavaPlugin;
import descent.ui.PreferenceConstants;
import descent.ui.text.java.CompletionProposalCollector;
import descent.ui.text.java.IJavaCompletionProposal;
import descent.ui.text.java.JavaContentAssistInvocationContext;

/**
 * Bin to collect the proposal of the infrastructure on code assist in a java text.
 */
public final class ExperimentalResultCollector extends CompletionProposalCollector {

	private final boolean fIsGuessArguments;

	public ExperimentalResultCollector(JavaContentAssistInvocationContext context) {
		super(context.getCompilationUnit());
		setInvocationContext(context);
		IPreferenceStore preferenceStore= JavaPlugin.getDefault().getPreferenceStore();
		fIsGuessArguments= preferenceStore.getBoolean(PreferenceConstants.CODEASSIST_GUESS_METHOD_ARGUMENTS);
	}

	/*
	 * @see descent.internal.ui.text.java.ResultCollector#createJavaCompletionProposal(descent.core.CompletionProposal)
	 */
	protected IJavaCompletionProposal createJavaCompletionProposal(CompletionProposal proposal) {
		switch (proposal.getKind()) {
			case CompletionProposal.METHOD_REF:
				return createMethodReferenceProposal(proposal);
			case CompletionProposal.TYPE_REF:
				return createTypeProposal(proposal);
			case CompletionProposal.TEMPLATE_REF:
			case CompletionProposal.TEMPLATED_AGGREGATE_REF:
				return createTemplateProposal(proposal);
			case CompletionProposal.TEMPLATED_FUNCTION_REF:
				return createTemplatedFunctionProposal(proposal);
			default:
				return super.createJavaCompletionProposal(proposal);
		}
	}

	private IJavaCompletionProposal createMethodReferenceProposal(CompletionProposal methodProposal) {
		String completion= String.valueOf(methodProposal.getCompletion());
		
		// super class' behavior if this is not a normal completion or has no
		// parameters
		if ((completion.length() == 0) || ((completion.length() == 1) && completion.charAt(0) == ')') || Signature.getParameterCount(methodProposal.getSignature()) == 0 || getContext().isInJavadoc())
			return super.createJavaCompletionProposal(methodProposal);

		LazyJavaCompletionProposal proposal;
		ICompilationUnit compilationUnit= getCompilationUnit();
		if (compilationUnit != null && fIsGuessArguments)
			proposal= new ParameterGuessingProposal(methodProposal, getInvocationContext());
		else
			proposal= new ExperimentalMethodProposal(methodProposal, getInvocationContext());
		return proposal;
	}
	
	private IJavaCompletionProposal createTemplateProposal(CompletionProposal tempProposal) {
		String completion= String.valueOf(tempProposal.getCompletion());
		
		// super class' behavior if this is not a normal completion or has no
		// parameters
		if ((completion.length() == 0) || ((completion.length() == 1) && completion.charAt(0) == ')') || Signature.getTemplateParameterCount(tempProposal.getSignature()) == 0 || getContext().isInJavadoc())
			return super.createJavaCompletionProposal(tempProposal);

		LazyJavaCompletionProposal proposal;
		ICompilationUnit compilationUnit= getCompilationUnit();
		if (compilationUnit != null && fIsGuessArguments)
			proposal= new ParameterGuessingProposal(tempProposal, getInvocationContext());
		else
			proposal= new ExperimentalTemplateProposal(tempProposal, getInvocationContext());
		return proposal;
	}
	
	private IJavaCompletionProposal createTemplatedFunctionProposal(CompletionProposal tempProposal) {
		String completion= String.valueOf(tempProposal.getCompletion());
		
		// super class' behavior if this is not a normal completion or has no
		// parameters
		if ((completion.length() == 0) || ((completion.length() == 1) && completion.charAt(0) == ')') || 
				Signature.getTemplateParameterCount(tempProposal.getSignature()) == 0 || 
				Signature.getParameterCount(tempProposal.getSignature()) == 0 ||
				getContext().isInJavadoc())
			return super.createJavaCompletionProposal(tempProposal);

		LazyJavaCompletionProposal proposal;
		ICompilationUnit compilationUnit= getCompilationUnit();
		if (compilationUnit != null && fIsGuessArguments)
			proposal= new ParameterGuessingProposal(tempProposal, getInvocationContext());
		else
			proposal= new ExperimentalTemplatedFunctionProposal(tempProposal, getInvocationContext());
		return proposal;
	}

	/*
	 * @see descent.internal.ui.text.java.ResultCollector#createTypeCompletion(descent.core.CompletionProposal)
	 */
	private IJavaCompletionProposal createTypeProposal(CompletionProposal typeProposal) {
		final ICompilationUnit cu= getCompilationUnit();
		if (cu == null || getContext().isInJavadoc())
			return super.createJavaCompletionProposal(typeProposal);

		IJavaProject project= cu.getJavaProject();
		if (!shouldProposeGenerics(project))
			return super.createJavaCompletionProposal(typeProposal);

		char[] completion= typeProposal.getCompletion();
		// don't add parameters for import-completions nor for proposals with an empty completion (e.g. inside the type argument list)
		if (completion.length == 0 || completion[completion.length - 1] == ';' || completion[completion.length - 1] == '.')
			return super.createJavaCompletionProposal(typeProposal);

		LazyJavaCompletionProposal newProposal= new GenericJavaTypeProposal(typeProposal, getInvocationContext());
		return newProposal;
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
