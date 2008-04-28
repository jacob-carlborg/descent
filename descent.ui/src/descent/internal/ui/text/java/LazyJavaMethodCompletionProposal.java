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
package descent.internal.ui.text.java;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.LinkedModeUI;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.link.EditorLinkedModeUI;

import descent.core.CompletionProposal;
import descent.core.ICompilationUnit;
import descent.core.IJavaProject;
import descent.core.IType;
import descent.core.JavaCore;
import descent.core.JavaModelException;
import descent.core.Signature;
import descent.core.dom.CompilationUnit;
import descent.core.dom.rewrite.ImportRewrite;
import descent.internal.corext.codemanipulation.ContextSensitiveImportRewriteContext;
import descent.internal.corext.codemanipulation.StubUtility;
import descent.internal.corext.util.QualifiedTypeNameHistory;
import descent.internal.ui.JavaPlugin;
import descent.internal.ui.javaeditor.ASTProvider;
import descent.internal.ui.javaeditor.EditorHighlightingSynchronizer;
import descent.internal.ui.javaeditor.JavaEditor;
import descent.ui.PreferenceConstants;
import descent.ui.text.java.JavaContentAssistInvocationContext;

/**
 * If passed compilation unit is not null, the replacement string will be seen as a qualified type name.
  */
public class LazyJavaMethodCompletionProposal extends LazyJavaCompletionProposal {
	/** Triggers for types. Do not modify. */
	protected static final char[] TYPE_TRIGGERS= new char[] { '.', '\t', '[', '(', '=', ';', ' ' };
	/** Triggers for types in javadoc. Do not modify. */
	protected static final char[] JDOC_TYPE_TRIGGERS= new char[] { '#', '}', ' ', '.' };

	/** The compilation unit, or <code>null</code> if none is available. */
	protected final ICompilationUnit fCompilationUnit;

	private boolean fHasParameters;
	private boolean fHasParametersComputed= false;
	private boolean fIsVariadic;
	private boolean fIsVariadicComputed= false;
	private boolean fIsSetter;
	private boolean fIsSetterComputed= false;
	private boolean fIsGetter;
	private boolean fIsGetterComputed= false;
	private boolean fWantProperty = false;
	private int[] fArgumentOffsets;
	private int[] fArgumentLengths;
	private String fQualifiedName;
	private String fSimpleName;
	private ImportRewrite fImportRewrite;
	private ContextSensitiveImportRewriteContext fImportContext;
	private int fContextInformationPosition;
	
	private IRegion fSelectedRegion; // initialized by apply()

	public LazyJavaMethodCompletionProposal(CompletionProposal proposal, JavaContentAssistInvocationContext context) {
		super(proposal, context);
		fCompilationUnit= context.getCompilationUnit();
		fQualifiedName= null;
	}
	
	public final String getQualifiedTypeName() {
		if (fQualifiedName == null) {
			if (fProposal.isAlias()) {
				fQualifiedName = new String(fProposal.getName());
			} else {
				fQualifiedName= String.valueOf(fProposal.getCompletion());
			}
		}
		return fQualifiedName;
	}
	
	protected final String getSimpleTypeName() {
		if (fSimpleName == null) {
			fSimpleName= new String(fProposal.getName());
		}
		return fSimpleName;
	}

	/*
	 * @see descent.internal.ui.text.java.LazyJavaCompletionProposal#computeReplacementString()
	 */
	protected String computeReplacementString() {
		String replacement= computeReplacementString0();
		if (replacement.endsWith("()")) { //$NON-NLS-1$
			replacement = replacement.substring(0, replacement.length() - 2);
		}
		
		if (!hasParameters() || !hasArgumentList()) {
			setCursorPosition(replacement.length() + (isVariadic() ? 1 : 2));
			if (replacement.length() > 0) {
				return replacement + "()"; //$NON-NLS-1$
			} else {
				return replacement;
			}
		}
		
		char[][] parameterNames= fProposal.findParameterNames(null);
		if (parameterNames == null) {
			System.out.println(1);
			return replacement + "()"; //$NON-NLS-1$
		}
		
		int count= parameterNames.length;
		fArgumentOffsets= new int[count];
		fArgumentLengths= new int[count];
		
		StringBuffer buffer= new StringBuffer(replacement);
		
		FormatterPrefs prefs= getFormatterPrefs();
		
		if (isSetter()) {
			if (prefs.beforeAssignmentOperator)
				buffer.append(SPACE);
			buffer.append(ASSIGN);
			if (prefs.afterAssignmentOperator)
				buffer.append(SPACE);
			
			setCursorPosition(buffer.length());
			
			if (fArgumentLengths.length > 0) {
				fArgumentOffsets[0]= buffer.length();
				buffer.append(parameterNames[0]);
				fArgumentLengths[0]= parameterNames[0].length;
			}
		} else if (isGetter()) {
			setCursorPosition(buffer.length());
		} else {
			
			if (prefs.beforeOpeningParen)
				buffer.append(SPACE);
			buffer.append(LPAREN);
			
			setCursorPosition(buffer.length());
			
			if (prefs.afterOpeningParen)
				buffer.append(SPACE);
			
			for (int i= 0; i != count; i++) {
				if (i != 0) {
					if (prefs.beforeComma)
						buffer.append(SPACE);
					buffer.append(COMMA);
					if (prefs.afterComma)
						buffer.append(SPACE);
				}
				
				fArgumentOffsets[i]= buffer.length();
				buffer.append(parameterNames[i]);
				fArgumentLengths[i]= parameterNames[i].length;
			}
			
			if (prefs.beforeClosingParen)
				buffer.append(SPACE);
	
			buffer.append(RPAREN);
		}

		return buffer.toString();
	}
	
	protected String computeReplacementString0() {
		String replacement= super.computeReplacementString();

		/* No import rewriting ever from within the import section. */
		if (isImportCompletion())
	        return replacement;
		
		/* Always use the simple name for non-formal javadoc references to types. */
		// TODO fix
//		 if (fProposal.getKind() == CompletionProposal.TYPE_REF &&  fInvocationContext.getCoreContext().isInJavadocText()) {
//			 return getSimpleTypeName();
		
		String qualifiedTypeName= getQualifiedTypeName();
 		if (qualifiedTypeName.indexOf('.') == -1)
 			// default package - no imports needed 
 			return qualifiedTypeName;

 		/*
		 * If the user types in the qualification, don't force import rewriting on him - insert the
		 * qualified name.
		 */
 		IDocument document= fInvocationContext.getDocument();
		if (document != null) {
			String prefix= getPrefix(document, getReplacementOffset() + getReplacementLength());
			int dotIndex= prefix.lastIndexOf('.');
			// match up to the last dot in order to make higher level matching still work (camel case...)
			if (dotIndex != -1 && qualifiedTypeName.toLowerCase().startsWith(prefix.substring(0, dotIndex + 1).toLowerCase()))
				return qualifiedTypeName;
		}
		
		/*
		 * The replacement does not contain a qualification (e.g. an inner type qualified by its
		 * parent) - use the replacement directly.
		 */
		if (replacement.indexOf('.') == -1) {
			if (isInJavadoc())
				return getSimpleTypeName(); // don't use the braces added for javadoc link proposals
			return replacement;
		}
		
		/* Add imports if the preference is on. */
		fImportRewrite= createImportRewrite();
		if (fImportRewrite != null) {
			//return fImportRewrite.addImport(qualifiedTypeName, fImportContext);
			fImportRewrite.addImport(qualifiedTypeName, fImportContext);
			return getSimpleTypeName();
		}
		
		// fall back for the case we don't have an import rewrite (see allowAddingImports)
		
		/* No imports for implicit imports. */
		// TODO JDT signature
//		if (fCompilationUnit != null && JavaModelUtil.isImplicitImport(Signature.getQualifier(qualifiedTypeName), fCompilationUnit)) {
//			return Signature.getSimpleName(qualifiedTypeName);
//		}
		
		/* Default: use the fully qualified type name. */
		return qualifiedTypeName;
	}

	protected final boolean isImportCompletion() {
		char[] completion= fProposal.getCompletion();
		if (completion.length == 0)
			return false;
		
		char last= completion[completion.length - 1];
		/*
		 * Proposals end in a semicolon when completing types in normal imports or when completing
		 * static members, in a period when completing types in static imports.
		 */
		return last == ';' || last == '.';
	}

	private ImportRewrite createImportRewrite() {
		if (fCompilationUnit != null && allowAddingImports()) {
			try {
				CompilationUnit cu= getASTRoot(fCompilationUnit);
				if (cu == null) {
					ImportRewrite rewrite= StubUtility.createImportRewrite(fCompilationUnit, true);
					fImportContext= null;
					return rewrite;
				} else {
					ImportRewrite rewrite= StubUtility.createImportRewrite(cu, true);
					fImportContext= new ContextSensitiveImportRewriteContext(cu, fInvocationContext.getInvocationOffset(), rewrite);
					return rewrite;
				}
			} catch (CoreException x) {
				JavaPlugin.log(x);
			}
		}
		return null;
	}

	private CompilationUnit getASTRoot(ICompilationUnit compilationUnit) {
		return JavaPlugin.getDefault().getASTProvider().getAST(compilationUnit, ASTProvider.WAIT_NO, new NullProgressMonitor());
	}

	/*
	 * @see descent.internal.ui.text.java.LazyJavaCompletionProposal#apply(org.eclipse.jface.text.IDocument, char, int)
	 */
	public void apply(IDocument document, char trigger, int offset) {
		try {
			this.fWantProperty = trigger == '=' || trigger == ';';
			
//			boolean insertClosingParenthesis= trigger == '(' && autocloseBrackets();
//			if (insertClosingParenthesis) {
//				updateReplacementWithParentheses();
//				trigger= '\0';
//			}
			
			super.apply(document, trigger, offset);
			int baseOffset= getReplacementOffset();
			String replacement= getReplacementString();

			int offsetAdded = 0;
			
			if (fImportRewrite != null && fImportRewrite.hasRecordedChanges()) {
				int oldLen= document.getLength();
				fImportRewrite.rewriteImports(new NullProgressMonitor()).apply(document, TextEdit.UPDATE_REGIONS);
				
				offsetAdded = document.getLength() - oldLen;
				setReplacementOffset(getReplacementOffset() + offsetAdded);
				setCursorPosition(getCursorPosition() + offsetAdded);
			}
			
			if (fArgumentOffsets != null && getTextViewer() != null && !isGetter()) {
				try {
					LinkedModeModel model= new LinkedModeModel();
					for (int i= 0; i != fArgumentOffsets.length; i++) {
						LinkedPositionGroup group= new LinkedPositionGroup();
						group.addPosition(new LinkedPosition(document, baseOffset + fArgumentOffsets[i] + offsetAdded, fArgumentLengths[i], LinkedPositionGroup.NO_STOP));
						model.addGroup(group);
					}

					model.forceInstall();
					JavaEditor editor= getJavaEditor();
					if (editor != null) {
						model.addLinkingListener(new EditorHighlightingSynchronizer(editor));
					}

					LinkedModeUI ui= new EditorLinkedModeUI(model, getTextViewer());
					ui.setExitPosition(getTextViewer(), baseOffset + replacement.length(), 0, Integer.MAX_VALUE);
					ui.setExitPolicy(new ExitPolicy(')', document));
					ui.setDoContextInfo(true);
					ui.setCyclingMode(LinkedModeUI.CYCLE_WHEN_NO_PARENT);
					ui.enter();

					fSelectedRegion= ui.getSelectedRegion();

				} catch (BadLocationException e) {
					JavaPlugin.log(e);
					openErrorDialog(e);
				}
			} else {
				// Before the last )
				fSelectedRegion= new Region(baseOffset + replacement.length() + offsetAdded - (isVariadic() ? 1 : 0), 0);
			}
			
			//rememberSelection();
		} catch (CoreException e) {
			JavaPlugin.log(e);
		} catch (BadLocationException e) {
			JavaPlugin.log(e);
		}
	}
	
	protected boolean needsLinkedMode() {
		return hasArgumentList() && hasParameters() && !isSetter() && !isGetter();
	}

	private void updateReplacementWithParentheses() {
		StringBuffer replacement= new StringBuffer(getReplacementString());
		FormatterPrefs prefs= getFormatterPrefs();

		if (prefs.beforeOpeningParen)
			replacement.append(SPACE);
		replacement.append(LPAREN);


		if (prefs.afterOpeningParen)
			replacement.append(SPACE);

		setCursorPosition(replacement.length());
		
		if (prefs.afterOpeningParen)
			replacement.append(SPACE);
		
		replacement.append(RPAREN);
		
		setReplacementString(replacement.toString());
	}

	/**
	 * Remembers the selection in the content assist history.
	 * 
	 * @throws JavaModelException if anything goes wrong
	 * @since 3.2
	 */
	protected final void rememberSelection() throws JavaModelException {
		IType lhs= fInvocationContext.getExpectedType();
		IType rhs= (IType) getJavaElement();
		if (lhs != null && rhs != null)
			JavaPlugin.getDefault().getContentAssistHistory().remember(lhs, rhs);
		
		QualifiedTypeNameHistory.remember(getQualifiedTypeName());
	}

	/**
	 * Returns <code>true</code> if imports may be added. The return value depends on the context
	 * and preferences only and does not take into account the contents of the compilation unit or
	 * the kind of proposal. Even if <code>true</code> is returned, there may be cases where no
	 * imports are added for the proposal. For example:
	 * <ul>
	 * <li>when completing within the import section</li>
	 * <li>when completing informal javadoc references (e.g. within <code>&lt;code&gt;</code>
	 * tags)</li>
	 * <li>when completing a type that conflicts with an existing import</li>
	 * <li>when completing an implicitly imported type (same package, <code>java.lang</code>
	 * types)</li>
	 * </ul>
	 * <p>
	 * The decision whether a qualified type or the simple type name should be inserted must take
	 * into account these different scenarios.
	 * </p>
	 * <p>
	 * Subclasses may extend.
	 * </p>
	 * 
	 * @return <code>true</code> if imports may be added, <code>false</code> if not
	 */
	protected boolean allowAddingImports() {
		if (isInJavadoc()) {
			// TODO fix
//			if (!fContext.isInJavadocFormalReference())
//				return false;
			if (fProposal.getKind() == CompletionProposal.TYPE_REF &&  fInvocationContext.getCoreContext().isInJavadocText())
				return false;
			
			if (!isJavadocProcessingEnabled())
				return false;
		}
		
		IPreferenceStore preferenceStore= JavaPlugin.getDefault().getPreferenceStore();
		return preferenceStore.getBoolean(PreferenceConstants.CODEASSIST_ADDIMPORT);
	}

	private boolean isJavadocProcessingEnabled() {
		IJavaProject project= fCompilationUnit.getJavaProject();
		boolean processJavadoc;
		if (project == null)
			processJavadoc= JavaCore.ENABLED.equals(JavaCore.getOption(JavaCore.COMPILER_DOC_COMMENT_SUPPORT));
		else
			processJavadoc= JavaCore.ENABLED.equals(project.getOption(JavaCore.COMPILER_DOC_COMMENT_SUPPORT, true));
		return processJavadoc;
	}

	/*
	 * @see descent.internal.ui.text.java.LazyJavaCompletionProposal#isValidPrefix(java.lang.String)
	 */
	protected boolean isValidPrefix(String prefix) {
		String simple = getSimpleTypeName();
		return isPrefix(prefix, simple) || isPrefix(prefix, getQualifiedTypeName()) 
			|| simple.equals("_ctor") || simple.equals("opCall"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/*
	 * @see descent.internal.ui.text.java.JavaCompletionProposal#getCompletionText()
	 */
	public CharSequence getPrefixCompletionText(IDocument document, int completionOffset) {
		String prefix= getPrefix(document, completionOffset);
		
		String completion;
		// return the qualified name if the prefix is already qualified
		if (prefix.indexOf('.') != -1)
			completion= getQualifiedTypeName();
		else
			completion= getSimpleTypeName();
		
		if (isCamelCaseMatching())
			return getCamelCaseCompound(prefix, completion);

		return completion;
	}
	
	/*
	 * @see descent.internal.ui.text.java.LazyJavaCompletionProposal#computeTriggerCharacters()
	 */
	protected char[] computeTriggerCharacters() {
		return isInJavadoc() ? JDOC_TYPE_TRIGGERS : TYPE_TRIGGERS;
	}
	
	/*
	 * @see descent.internal.ui.text.java.LazyJavaCompletionProposal#computeProposalInfo()
	 */
	protected ProposalInfo computeProposalInfo() {
		if (fCompilationUnit != null) {
			IJavaProject project= fCompilationUnit.getJavaProject();
			if (project != null)
				return new MethodProposalInfo(project, fProposal);
		}
		return super.computeProposalInfo();
	}

	/*
	 * @see descent.internal.ui.text.java.LazyJavaCompletionProposal#computeSortString()
	 */
	protected String computeSortString() {
		// try fast sort string to avoid display string creation
		return getSimpleTypeName() + Character.MIN_VALUE + getQualifiedTypeName();
	}
	
	/*
	 * @see descent.internal.ui.text.java.LazyJavaCompletionProposal#computeRelevance()
	 */
	protected int computeRelevance() {
		/*
		 * There are two histories: the RHS history remembers types used for the current expected
		 * type (left hand side), while the type history remembers recently used types in general).
		 * 
		 * The presence of an RHS ranking is a much more precise sign for relevance as it proves the
		 * subtype relationship between the proposed type and the expected type.
		 * 
		 * The "recently used" factor (of either the RHS or general history) is less important, it should
		 * not override other relevance factors such as if the type is already imported etc.
		 */
		float rhsHistoryRank= fInvocationContext.getHistoryRelevance(getQualifiedTypeName());
		float typeHistoryRank= QualifiedTypeNameHistory.getDefault().getNormalizedPosition(getQualifiedTypeName());

		int recencyBoost= Math.round((rhsHistoryRank + typeHistoryRank) * 5);
		int rhsBoost= rhsHistoryRank > 0.0f ? 50 : 0;
		int baseRelevance= super.computeRelevance();
		
		return baseRelevance +  rhsBoost + recencyBoost;
	}
	
	/**
	 * Returns <code>true</code> if the method being inserted has at least one parameter. Note
	 * that this does not say anything about whether the argument list should be inserted. This
	 * depends on the position in the document and the kind of proposal; see
	 * {@link #hasArgumentList() }.
	 * 
	 * @return <code>true</code> if the method has any parameters, <code>false</code> if it has
	 *         no parameters
	 */
	protected final boolean hasParameters() {
		if (!fHasParametersComputed) {
			fHasParametersComputed= true;
			fHasParameters= computeHasParameters();
		}
		return fHasParameters;
	}
	
	protected final boolean isVariadic() {
		if (!fIsVariadicComputed) {
			fIsVariadicComputed= true;
			fIsVariadic= computeIsVariadic();
		}
		return fIsVariadic;
	}
	
	protected final boolean isSetter() {
		if (!fIsSetterComputed) {
			fIsSetterComputed= true;
			fIsSetter = computeIsSetter();
		}
		return fIsSetter;
	}
	
	protected final boolean isGetter() {
		if (!fIsGetterComputed) {
			fIsGetterComputed= true;
			fIsGetter = computeIsGetter();
		}
		return fIsGetter;
	}
	
	private boolean computeHasParameters() throws IllegalArgumentException {
		return Signature.getParameterCount(fProposal.getTypeSignature()) > 0;
	}
	
	private boolean computeIsVariadic() throws IllegalArgumentException {
		return Signature.isVariadic(fProposal.getTypeSignature());
	}
	
	private boolean computeIsSetter() throws IllegalArgumentException {
		if (!fWantProperty) {
			return false;
		}
		
		char[] retType = Signature.getReturnType(fProposal.getTypeSignature());
		if (retType.length == 1 && retType[0] == 'v') {
			return Signature.getParameterCount(fProposal.getTypeSignature()) == 1;
		}
		return false;
	}
	
	private boolean computeIsGetter() throws IllegalArgumentException {
		if (!fWantProperty) {
			return false;
		}
		
		char[] retType = Signature.getReturnType(fProposal.getTypeSignature());
		if (retType.length != 1 || retType[0] != 'v') {
			return !hasParameters();
		}
		return false;
	}
	
	/**
	 * Returns <code>true</code> if the argument list should be inserted by the proposal,
	 * <code>false</code> if not.
	 * 
	 * @return <code>true</code> when the proposal is not in javadoc nor within an import and comprises the
	 *         parameter list
	 */
	protected boolean hasArgumentList() {
		if (CompletionProposal.METHOD_NAME_REFERENCE == fProposal.getKind())
			return false;
		IPreferenceStore preferenceStore= JavaPlugin.getDefault().getPreferenceStore();
		boolean noOverwrite= preferenceStore.getBoolean(PreferenceConstants.CODEASSIST_INSERT_COMPLETION) ^ isToggleEating();
		char[] completion= fProposal.getCompletion();
		return !isInJavadoc() && completion.length > 0 && (noOverwrite  || completion[completion.length - 1] == ')');
	}
	
	/**
	 * Returns the currently active java editor, or <code>null</code> if it
	 * cannot be determined.
	 *
	 * @return  the currently active java editor, or <code>null</code>
	 */
	private JavaEditor getJavaEditor() {
		IEditorPart part= JavaPlugin.getActivePage().getActiveEditor();
		if (part instanceof JavaEditor)
			return (JavaEditor) part;
		else
			return null;
	}
	
	private void openErrorDialog(BadLocationException e) {
		Shell shell= getTextViewer().getTextWidget().getShell();
		MessageDialog.openError(shell, JavaTextMessages.ExperimentalProposal_error_msg, e.getMessage());
	}
	
	/**
	 * Overrides the default context information position. Ignored if set to zero.
	 * 
	 * @param contextInformationPosition the replaced position.
	 */
	public void setContextInformationPosition(int contextInformationPosition) {
		fContextInformationPosition= contextInformationPosition;
	}
	
	protected IContextInformation computeContextInformation() {
		// no context information for METHOD_NAME_REF proposals (e.g. for static imports)
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=94654
		if ((fProposal.getKind() == CompletionProposal.METHOD_REF || fProposal.getKind() == CompletionProposal.OP_CALL) &&  hasParameters() && 
				(getReplacementString().endsWith(RPAREN) || getReplacementString().length() == 0
						|| isSetter())) {
			ProposalContextInformation contextInformation= new ProposalContextInformation(fProposal);
			if (fContextInformationPosition != 0 && fProposal.getCompletion().length == 0)
				contextInformation.setContextInformationPosition(fContextInformationPosition);
			return contextInformation;
		}
		return super.computeContextInformation();
	}
	
	@Override
	public Point getSelection(IDocument document) {
		if (fSelectedRegion == null)
			return new Point(getReplacementOffset(), 0);

		return new Point(fSelectedRegion.getOffset(), fSelectedRegion.getLength());
	}
	
	@Override
	public int getContextInformationPosition() {
		return getReplacementOffset();
	}
	
}
