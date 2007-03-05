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
package descent.ui.text;

import java.util.Vector;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.AbstractInformationControlManager;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.DefaultTextDoubleClickStrategy;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextViewerExtension2;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.formatter.IContentFormatter;
import org.eclipse.jface.text.formatter.MultiPassContentFormatter;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.information.IInformationPresenter;
import org.eclipse.jface.text.information.IInformationProvider;
import org.eclipse.jface.text.information.InformationPresenter;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.quickassist.IQuickAssistAssistant;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.util.Assert;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import descent.core.IJavaElement;
import descent.core.IJavaProject;
import descent.core.JavaCore;
import descent.core.formatter.DefaultCodeFormatterConstants;
import descent.internal.ui.JavaPlugin;
import descent.internal.ui.javaeditor.ICompilationUnitDocumentProvider;
import descent.internal.ui.javaeditor.JavaElementHyperlinkDetector;
import descent.internal.ui.text.AbstractJavaScanner;
import descent.internal.ui.text.HTMLAnnotationHover;
import descent.internal.ui.text.HTMLTextPresenter;
import descent.internal.ui.text.JavaCommentScanner;
import descent.internal.ui.text.JavaCompositeReconcilingStrategy;
import descent.internal.ui.text.JavaElementProvider;
import descent.internal.ui.text.JavaOutlineInformationControl;
import descent.internal.ui.text.JavaPresentationReconciler;
import descent.internal.ui.text.JavaReconciler;
import descent.internal.ui.text.SingleTokenJavaScanner;
import descent.internal.ui.text.java.JavaAutoIndentStrategy;
import descent.internal.ui.text.java.JavaCodeScanner;
import descent.internal.ui.text.java.JavaDoubleClickSelector;
import descent.internal.ui.text.java.JavaStringAutoIndentStrategy;
import descent.internal.ui.text.java.JavaStringDoubleClickSelector;
import descent.internal.ui.text.java.JavadocDoubleClickStrategy;
import descent.internal.ui.text.java.SmartSemicolonAutoEditStrategy;
import descent.internal.ui.text.java.hover.JavaEditorTextHoverDescriptor;
import descent.internal.ui.text.java.hover.JavaEditorTextHoverProxy;
import descent.internal.ui.text.java.hover.JavaInformationProvider;
import descent.internal.ui.text.javadoc.JavaDocAutoIndentStrategy;
import descent.internal.ui.text.javadoc.JavaDocScanner;
import descent.ui.PreferenceConstants;
import descent.ui.actions.IJavaEditorActionDefinitionIds;


/**
 * Configuration for a source viewer which shows Java code.
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 */
public class JavaSourceViewerConfiguration extends TextSourceViewerConfiguration {

	/**
	 * Preference key used to look up display tab width.
	 *
	 * @since 2.0
	 * @deprecated As of 3.0, replaced by {@link AbstractDecoratedTextEditorPreferenceConstants#EDITOR_TAB_WIDTH}
	 */
	public final static String PREFERENCE_TAB_WIDTH= PreferenceConstants.EDITOR_TAB_WIDTH;

	/**
	 * Preference key for inserting spaces rather than tabs.
	 *
	 * @since 2.0
	 * @deprecated as of 3.1 use {@link descent.core.formatter.DefaultCodeFormatterConstants#FORMATTER_TAB_CHAR}
	 */
	public final static String SPACES_FOR_TABS= PreferenceConstants.EDITOR_SPACES_FOR_TABS;


	private JavaTextTools fJavaTextTools;
	private ITextEditor fTextEditor;
	/**
	 * The document partitioning.
	 * @since 3.0
	 */
	private String fDocumentPartitioning;
	/**
	 * The Java source code scanner.
	 * @since 3.0
	 */
	private AbstractJavaScanner fCodeScanner;
	/**
	 * The Java multi-line comment scanner.
	 * @since 3.0
	 */
	private AbstractJavaScanner fMultilineCommentScanner;
	/**
	 * The Java multi-line comment scanner.
	 * @since 3.0
	 */
	private AbstractJavaScanner fMultilinePlusCommentScanner;
	/**
	 * The Java multi-line comment scanner.
	 * @since 3.0
	 */
	private AbstractJavaScanner fMultilinePlusDocCommentScanner;
	/**
	 * The Java single-line comment scanner.
	 * @since 3.0
	 */
	private AbstractJavaScanner fSinglelineCommentScanner;
	/**
	 * The Java single-line comment scanner.
	 * @since 3.0
	 */
	private AbstractJavaScanner fSinglelineDocCommentScanner;
	/**
	 * The Java string scanner.
	 * @since 3.0
	 */
	private AbstractJavaScanner fStringScanner;
	/**
	 * The Javadoc scanner.
	 * @since 3.0
	 */
	private AbstractJavaScanner fJavaDocScanner;
	/**
	 * The color manager.
	 * @since 3.0
	 */
	private IColorManager fColorManager;
	/**
	 * The double click strategy.
	 * @since 3.1
	 */
	private JavaDoubleClickSelector fJavaDoubleClickSelector;


	/**
	 * Creates a new Java source viewer configuration for viewers in the given editor
	 * using the given preference store, the color manager and the specified document partitioning.
	 * <p>
	 * Creates a Java source viewer configuration in the new setup without text tools. Clients are
	 * allowed to call {@link JavaSourceViewerConfiguration#handlePropertyChangeEvent(PropertyChangeEvent)}
	 * and disallowed to call {@link JavaSourceViewerConfiguration#getPreferenceStore()} on the resulting
	 * Java source viewer configuration.
	 * </p>
	 *
	 * @param colorManager the color manager
	 * @param preferenceStore the preference store, can be read-only
	 * @param editor the editor in which the configured viewer(s) will reside, or <code>null</code> if none
	 * @param partitioning the document partitioning for this configuration, or <code>null</code> for the default partitioning
	 * @since 3.0
	 */
	public JavaSourceViewerConfiguration(IColorManager colorManager, IPreferenceStore preferenceStore, ITextEditor editor, String partitioning) {
		super(preferenceStore);
		fColorManager= colorManager;
		fTextEditor= editor;
		fDocumentPartitioning= partitioning;
		initializeScanners();
	}

	/**
	 * Returns the Java source code scanner for this configuration.
	 *
	 * @return the Java source code scanner
	 */
	protected RuleBasedScanner getCodeScanner() {
		return fCodeScanner;
	}

	/**
	 * Returns the Java multi-line comment scanner for this configuration.
	 *
	 * @return the Java multi-line comment scanner
	 * @since 2.0
	 */
	protected RuleBasedScanner getMultilineCommentScanner() {
		return fMultilineCommentScanner;
	}
	
	/**
	 * Returns the Java multi-line comment scanner for this configuration.
	 *
	 * @return the Java multi-line comment scanner
	 * @since 2.0
	 */
	protected RuleBasedScanner getMultilinePlusCommentScanner() {
		return fMultilinePlusCommentScanner;
	}
	
	/**
	 * Returns the Java multi-line comment scanner for this configuration.
	 *
	 * @return the Java multi-line comment scanner
	 * @since 2.0
	 */
	protected RuleBasedScanner getMultilinePlusDocCommentScanner() {
		return fMultilinePlusDocCommentScanner;
	}

	/**
	 * Returns the Java single-line comment scanner for this configuration.
	 *
	 * @return the Java single-line comment scanner
	 * @since 2.0
	 */
	protected RuleBasedScanner getSinglelineCommentScanner() {
		return fSinglelineCommentScanner;
	}
	
	/**
	 * Returns the Java single-line comment scanner for this configuration.
	 *
	 * @return the Java single-line comment scanner
	 * @since 2.0
	 */
	protected RuleBasedScanner getSinglelineDocCommentScanner() {
		return fSinglelineDocCommentScanner;
	}


	/**
	 * Returns the Java string scanner for this configuration.
	 *
	 * @return the Java string scanner
	 * @since 2.0
	 */
	protected RuleBasedScanner getStringScanner() {
		return fStringScanner;
	}

	/**
	 * Returns the JavaDoc scanner for this configuration.
	 *
	 * @return the JavaDoc scanner
	 */
	protected RuleBasedScanner getJavaDocScanner() {
		return fJavaDocScanner;
	}

	/**
	 * Returns the color manager for this configuration.
	 *
	 * @return the color manager
	 */
	protected IColorManager getColorManager() {
		return fColorManager;
	}

	/**
	 * Returns the editor in which the configured viewer(s) will reside.
	 *
	 * @return the enclosing editor
	 */
	protected ITextEditor getEditor() {
		return fTextEditor;
	}

	/**
	 * Returns the preference store used by this configuration to initialize
	 * the individual bits and pieces.
	 * <p>
	 * Clients are not allowed to call this method if the new setup without
	 * text tools is in use.
	 * @see JavaSourceViewerConfiguration#JavaSourceViewerConfiguration(IColorManager, IPreferenceStore, ITextEditor, String)
	 * </p>
	 *
	 * @return the preference store used to initialize this configuration
	 * @since 2.0
	 * @deprecated As of 3.0
	 */
	protected IPreferenceStore getPreferenceStore() {
		Assert.isTrue(!isNewSetup());
		return fJavaTextTools.getPreferenceStore();
	}

	/**
	 * @return <code>true</code> iff the new setup without text tools is in use.
	 *
	 * @since 3.0
	 */
	private boolean isNewSetup() {
		return fJavaTextTools == null;
	}

	/**
	 * Initializes the scanners.
	 *
	 * @since 3.0
	 */
	private void initializeScanners() {
		Assert.isTrue(isNewSetup());
		fCodeScanner= new JavaCodeScanner(getColorManager(), fPreferenceStore);
		fMultilineCommentScanner= new JavaCommentScanner(getColorManager(), fPreferenceStore, IJavaColorConstants.JAVA_MULTI_LINE_COMMENT);
		fMultilinePlusCommentScanner= new JavaCommentScanner(getColorManager(), fPreferenceStore, IJavaColorConstants.JAVA_MULTI_LINE_PLUS_COMMENT);
		fMultilinePlusDocCommentScanner= new JavaCommentScanner(getColorManager(), fPreferenceStore, IJavaColorConstants.JAVA_MULTI_LINE_PLUS_DOC_COMMENT);
		fSinglelineCommentScanner= new JavaCommentScanner(getColorManager(), fPreferenceStore, IJavaColorConstants.JAVA_SINGLE_LINE_COMMENT);
		fSinglelineDocCommentScanner= new JavaCommentScanner(getColorManager(), fPreferenceStore, IJavaColorConstants.JAVA_SINGLE_LINE_DOC_COMMENT);
		fStringScanner= new SingleTokenJavaScanner(getColorManager(), fPreferenceStore, IJavaColorConstants.JAVA_STRING);
		fJavaDocScanner= new JavaDocScanner(getColorManager(), fPreferenceStore);
	}

	/*
	 * @see SourceViewerConfiguration#getPresentationReconciler(ISourceViewer)
	 */
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {

		PresentationReconciler reconciler= new JavaPresentationReconciler();
		reconciler.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));

		DefaultDamagerRepairer dr= new DefaultDamagerRepairer(getCodeScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		dr= new DefaultDamagerRepairer(getJavaDocScanner());
		reconciler.setDamager(dr, IJavaPartitions.JAVA_DOC);
		reconciler.setRepairer(dr, IJavaPartitions.JAVA_DOC);

		dr= new DefaultDamagerRepairer(getMultilineCommentScanner());
		reconciler.setDamager(dr, IJavaPartitions.JAVA_MULTI_LINE_COMMENT);
		reconciler.setRepairer(dr, IJavaPartitions.JAVA_MULTI_LINE_COMMENT);
		
		dr= new DefaultDamagerRepairer(getMultilinePlusCommentScanner());
		reconciler.setDamager(dr, IJavaPartitions.JAVA_MULTI_LINE_PLUS_COMMENT);
		reconciler.setRepairer(dr, IJavaPartitions.JAVA_MULTI_LINE_PLUS_COMMENT);
		
		dr= new DefaultDamagerRepairer(getMultilinePlusDocCommentScanner());
		reconciler.setDamager(dr, IJavaPartitions.JAVA_MULTI_LINE_PLUS_DOC_COMMENT);
		reconciler.setRepairer(dr, IJavaPartitions.JAVA_MULTI_LINE_PLUS_DOC_COMMENT);

		dr= new DefaultDamagerRepairer(getSinglelineCommentScanner());
		reconciler.setDamager(dr, IJavaPartitions.JAVA_SINGLE_LINE_COMMENT);
		reconciler.setRepairer(dr, IJavaPartitions.JAVA_SINGLE_LINE_COMMENT);
		
		dr= new DefaultDamagerRepairer(getSinglelineDocCommentScanner());
		reconciler.setDamager(dr, IJavaPartitions.JAVA_SINGLE_LINE_DOC_COMMENT);
		reconciler.setRepairer(dr, IJavaPartitions.JAVA_SINGLE_LINE_DOC_COMMENT);

		dr= new DefaultDamagerRepairer(getStringScanner());
		reconciler.setDamager(dr, IJavaPartitions.JAVA_STRING);
		reconciler.setRepairer(dr, IJavaPartitions.JAVA_STRING);

		dr= new DefaultDamagerRepairer(getStringScanner());
		reconciler.setDamager(dr, IJavaPartitions.JAVA_CHARACTER);
		reconciler.setRepairer(dr, IJavaPartitions.JAVA_CHARACTER);


		return reconciler;
	}

	/*
	 * @see SourceViewerConfiguration#getContentAssistant(ISourceViewer)
	 */
	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {

		if (getEditor() != null) {

			ContentAssistant assistant= new ContentAssistant();
			assistant.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));

			assistant.setRestoreCompletionProposalSize(getSettings("completion_proposal_size")); //$NON-NLS-1$

			/* TODO JDT UI code completion
			IContentAssistProcessor javaProcessor= new JavaCompletionProcessor(getEditor(), assistant, IDocument.DEFAULT_CONTENT_TYPE);
			assistant.setContentAssistProcessor(javaProcessor, IDocument.DEFAULT_CONTENT_TYPE);

			ContentAssistProcessor singleLineProcessor= new JavaCompletionProcessor(getEditor(), assistant, IJavaPartitions.JAVA_SINGLE_LINE_COMMENT);
			assistant.setContentAssistProcessor(singleLineProcessor, IJavaPartitions.JAVA_SINGLE_LINE_COMMENT);

			ContentAssistProcessor stringProcessor= new JavaCompletionProcessor(getEditor(), assistant, IJavaPartitions.JAVA_STRING);
			assistant.setContentAssistProcessor(stringProcessor, IJavaPartitions.JAVA_STRING);
			
			ContentAssistProcessor multiLineProcessor= new JavaCompletionProcessor(getEditor(), assistant, IJavaPartitions.JAVA_MULTI_LINE_COMMENT);
			assistant.setContentAssistProcessor(multiLineProcessor, IJavaPartitions.JAVA_MULTI_LINE_COMMENT);

			ContentAssistProcessor javadocProcessor= new JavadocCompletionProcessor(getEditor(), assistant);
			assistant.setContentAssistProcessor(javadocProcessor, IJavaPartitions.JAVA_DOC);

			ContentAssistPreference.configure(assistant, fPreferenceStore);
			*/

			assistant.setContextInformationPopupOrientation(IContentAssistant.CONTEXT_INFO_ABOVE);
			assistant.setInformationControlCreator(getInformationControlCreator(sourceViewer));
			
			return assistant;
		}

		return null;
	}

	/*
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getQuickAssistAssistant(org.eclipse.jface.text.source.ISourceViewer)
	 * @since 3.2
	 */
	public IQuickAssistAssistant getQuickAssistAssistant(ISourceViewer sourceViewer) {
		/* TODO JDT UI correction
		if (getEditor() != null)
			return new JavaCorrectionAssistant(getEditor());
		*/
		return null;
	}

	/*
	 * @see SourceViewerConfiguration#getReconciler(ISourceViewer)
	 */
	public IReconciler getReconciler(ISourceViewer sourceViewer) {

		final ITextEditor editor= getEditor();
		if (editor != null && editor.isEditable()) {

			JavaCompositeReconcilingStrategy strategy= new JavaCompositeReconcilingStrategy(editor, getConfiguredDocumentPartitioning(sourceViewer));
			JavaReconciler reconciler= new JavaReconciler(editor, strategy, false);
			reconciler.setIsIncrementalReconciler(false);
			reconciler.setIsAllowedToModifyDocument(false);
			reconciler.setProgressMonitor(new NullProgressMonitor());
			reconciler.setDelay(500);

			return reconciler;
		}
		return null;
	}

	/*
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getAutoEditStrategies(org.eclipse.jface.text.source.ISourceViewer, java.lang.String)
	 */
	public IAutoEditStrategy[] getAutoEditStrategies(ISourceViewer sourceViewer, String contentType) {
		String partitioning= getConfiguredDocumentPartitioning(sourceViewer);
		if (IJavaPartitions.JAVA_DOC.equals(contentType) || IJavaPartitions.JAVA_MULTI_LINE_COMMENT.equals(contentType))
			return new IAutoEditStrategy[] { new JavaDocAutoIndentStrategy(partitioning) };
		else if (IJavaPartitions.JAVA_STRING.equals(contentType))
			return new IAutoEditStrategy[] { new SmartSemicolonAutoEditStrategy(partitioning), new JavaStringAutoIndentStrategy(partitioning) };
		else if (IJavaPartitions.JAVA_CHARACTER.equals(contentType) || IDocument.DEFAULT_CONTENT_TYPE.equals(contentType))
			return new IAutoEditStrategy[] { new SmartSemicolonAutoEditStrategy(partitioning), new JavaAutoIndentStrategy(partitioning, getProject()) };
		else
			return new IAutoEditStrategy[] { new JavaAutoIndentStrategy(partitioning, getProject()) };
	}

	/*
	 * @see SourceViewerConfiguration#getDoubleClickStrategy(ISourceViewer, String)
	 */
	public ITextDoubleClickStrategy getDoubleClickStrategy(ISourceViewer sourceViewer, String contentType) {
		if (IJavaPartitions.JAVA_DOC.equals(contentType))
			return new JavadocDoubleClickStrategy();
		if (IJavaPartitions.JAVA_MULTI_LINE_COMMENT.equals(contentType) ||
				IJavaPartitions.JAVA_MULTI_LINE_PLUS_COMMENT.equals(contentType) ||
				IJavaPartitions.JAVA_MULTI_LINE_PLUS_DOC_COMMENT.equals(contentType) ||
				IJavaPartitions.JAVA_SINGLE_LINE_COMMENT.equals(contentType) ||
				IJavaPartitions.JAVA_SINGLE_LINE_DOC_COMMENT.equals(contentType)
				)
			return new DefaultTextDoubleClickStrategy();
		else if (IJavaPartitions.JAVA_STRING.equals(contentType) ||
				IJavaPartitions.JAVA_CHARACTER.equals(contentType))
			return new JavaStringDoubleClickSelector(getConfiguredDocumentPartitioning(sourceViewer));
		if (fJavaDoubleClickSelector == null) {
			fJavaDoubleClickSelector= new JavaDoubleClickSelector();
			fJavaDoubleClickSelector.setSourceVersion(fPreferenceStore.getString(JavaCore.COMPILER_SOURCE));
		}
		return fJavaDoubleClickSelector;
	}

	/*
	 * @see SourceViewerConfiguration#getDefaultPrefixes(ISourceViewer, String)
	 * @since 2.0
	 */
	public String[] getDefaultPrefixes(ISourceViewer sourceViewer, String contentType) {
		return new String[] { "//", "" }; //$NON-NLS-1$ //$NON-NLS-2$
	}

	/*
	 * @see SourceViewerConfiguration#getIndentPrefixes(ISourceViewer, String)
	 */
	public String[] getIndentPrefixes(ISourceViewer sourceViewer, String contentType) {

		Vector vector= new Vector();

		// prefix[0] is either '\t' or ' ' x tabWidth, depending on useSpaces

		IJavaProject project= getProject();
		/* TODO JDT UI format
		final int tabWidth= CodeFormatterUtil.getTabWidth(project);
		final int indentWidth= CodeFormatterUtil.getIndentWidth(project);
		*/
		final int tabWidth= 4;
		final int indentWidth= 4;
		int spaceEquivalents= Math.min(tabWidth, indentWidth);
		boolean useSpaces;
		if (project == null)
			useSpaces= JavaCore.SPACE.equals(JavaCore.getOption(DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR)) || tabWidth > indentWidth;
		else
			useSpaces= JavaCore.SPACE.equals(project.getOption(DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR, true)) || tabWidth > indentWidth;

		for (int i= 0; i <= spaceEquivalents; i++) {
		    StringBuffer prefix= new StringBuffer();

			if (useSpaces) {
			    for (int j= 0; j + i < spaceEquivalents; j++)
			    	prefix.append(' ');

				if (i != 0)
		    		prefix.append('\t');
			} else {
			    for (int j= 0; j < i; j++)
			    	prefix.append(' ');

				if (i != spaceEquivalents)
		    		prefix.append('\t');
			}

			vector.add(prefix.toString());
		}

		vector.add(""); //$NON-NLS-1$

		return (String[]) vector.toArray(new String[vector.size()]);
	}

	private IJavaProject getProject() {
		ITextEditor editor= getEditor();
		if (editor == null)
			return null;

		IJavaElement element= null;
		IEditorInput input= editor.getEditorInput();
		IDocumentProvider provider= editor.getDocumentProvider();
		if (provider instanceof ICompilationUnitDocumentProvider) {
			ICompilationUnitDocumentProvider cudp= (ICompilationUnitDocumentProvider) provider;
			element= cudp.getWorkingCopy(input);
		/* TODO JDT UI binary
		} else if (input instanceof IClassFileEditorInput) {
			IClassFileEditorInput cfei= (IClassFileEditorInput) input;
			element= cfei.getClassFile();
		*/
		}
		

		if (element == null)
			return null;

		return element.getJavaProject();
	}

	/*
	 * @see SourceViewerConfiguration#getTabWidth(ISourceViewer)
	 */
	public int getTabWidth(ISourceViewer sourceViewer) {
		/* TODO JDT UI format
		return CodeFormatterUtil.getTabWidth(getProject());
		*/
		return 4;
	}

	/*
	 * @see SourceViewerConfiguration#getAnnotationHover(ISourceViewer)
	 */
	public IAnnotationHover getAnnotationHover(ISourceViewer sourceViewer) {
		return new HTMLAnnotationHover() {
			protected boolean isIncluded(Annotation annotation) {
				return isShowInVerticalRuler(annotation);
			}
		};
	}

	/*
	 * @see SourceViewerConfiguration#getOverviewRulerAnnotationHover(ISourceViewer)
	 * @since 3.0
	 */
	public IAnnotationHover getOverviewRulerAnnotationHover(ISourceViewer sourceViewer) {
		return new HTMLAnnotationHover() {
			protected boolean isIncluded(Annotation annotation) {
				return isShowInOverviewRuler(annotation);
			}
		};
	}

	/*
	 * @see SourceViewerConfiguration#getConfiguredTextHoverStateMasks(ISourceViewer, String)
	 * @since 2.1
	 */
	public int[] getConfiguredTextHoverStateMasks(ISourceViewer sourceViewer, String contentType) {
		JavaEditorTextHoverDescriptor[] hoverDescs= JavaPlugin.getDefault().getJavaEditorTextHoverDescriptors();
		int stateMasks[]= new int[hoverDescs.length];
		int stateMasksLength= 0;
		for (int i= 0; i < hoverDescs.length; i++) {
			if (hoverDescs[i].isEnabled()) {
				int j= 0;
				int stateMask= hoverDescs[i].getStateMask();
				while (j < stateMasksLength) {
					if (stateMasks[j] == stateMask)
						break;
					j++;
				}
				if (j == stateMasksLength)
					stateMasks[stateMasksLength++]= stateMask;
			}
		}
		if (stateMasksLength == hoverDescs.length)
			return stateMasks;

		int[] shortenedStateMasks= new int[stateMasksLength];
		System.arraycopy(stateMasks, 0, shortenedStateMasks, 0, stateMasksLength);
		return shortenedStateMasks;
	}

	/*
	 * @see SourceViewerConfiguration#getTextHover(ISourceViewer, String, int)
	 * @since 2.1
	 */
	public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType, int stateMask) {
		JavaEditorTextHoverDescriptor[] hoverDescs= JavaPlugin.getDefault().getJavaEditorTextHoverDescriptors();
		int i= 0;
		while (i < hoverDescs.length) {
			if (hoverDescs[i].isEnabled() &&  hoverDescs[i].getStateMask() == stateMask)
				return new JavaEditorTextHoverProxy(hoverDescs[i], getEditor());
			i++;
		}

		return null;
	}

	/*
	 * @see SourceViewerConfiguration#getTextHover(ISourceViewer, String)
	 */
	public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType) {
		return getTextHover(sourceViewer, contentType, ITextViewerExtension2.DEFAULT_HOVER_STATE_MASK);
	}

	/*
	 * @see SourceViewerConfiguration#getConfiguredContentTypes(ISourceViewer)
	 */
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] {
			IDocument.DEFAULT_CONTENT_TYPE,
			IJavaPartitions.JAVA_DOC,
			IJavaPartitions.JAVA_MULTI_LINE_COMMENT,
			IJavaPartitions.JAVA_MULTI_LINE_PLUS_COMMENT,
			IJavaPartitions.JAVA_MULTI_LINE_PLUS_DOC_COMMENT,
			IJavaPartitions.JAVA_SINGLE_LINE_COMMENT,
			IJavaPartitions.JAVA_SINGLE_LINE_DOC_COMMENT,
			IJavaPartitions.JAVA_STRING,
			IJavaPartitions.JAVA_CHARACTER
		};
	}

	/*
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getConfiguredDocumentPartitioning(org.eclipse.jface.text.source.ISourceViewer)
	 * @since 3.0
	 */
	public String getConfiguredDocumentPartitioning(ISourceViewer sourceViewer) {
		if (fDocumentPartitioning != null)
			return fDocumentPartitioning;
		return super.getConfiguredDocumentPartitioning(sourceViewer);
	}

	/*
	 * @see SourceViewerConfiguration#getContentFormatter(ISourceViewer)
	 */
	public IContentFormatter getContentFormatter(ISourceViewer sourceViewer) {
		final MultiPassContentFormatter formatter= new MultiPassContentFormatter(getConfiguredDocumentPartitioning(sourceViewer), IDocument.DEFAULT_CONTENT_TYPE);

		/* TODO JDT UI format
		formatter.setMasterStrategy(new JavaFormattingStrategy());
		formatter.setSlaveStrategy(new CommentFormattingStrategy(), IJavaPartitions.JAVA_DOC);
		formatter.setSlaveStrategy(new CommentFormattingStrategy(), IJavaPartitions.JAVA_SINGLE_LINE_COMMENT);
		formatter.setSlaveStrategy(new CommentFormattingStrategy(), IJavaPartitions.JAVA_MULTI_LINE_COMMENT);
		*/

		return formatter;
	}

	/*
	 * @see SourceViewerConfiguration#getInformationControlCreator(ISourceViewer)
	 * @since 2.0
	 */
	public IInformationControlCreator getInformationControlCreator(ISourceViewer sourceViewer) {
		return new IInformationControlCreator() {
			public IInformationControl createInformationControl(Shell parent) {
				return new DefaultInformationControl(parent, SWT.NONE, new HTMLTextPresenter(true));
			}
		};
	}

	/**
	 * Returns the information presenter control creator. The creator is a factory creating the
	 * presenter controls for the given source viewer. This implementation always returns a creator
	 * for <code>DefaultInformationControl</code> instances.
	 *
	 * @param sourceViewer the source viewer to be configured by this configuration
	 * @return an information control creator
	 * @since 2.1
	 */
	private IInformationControlCreator getInformationPresenterControlCreator(ISourceViewer sourceViewer) {
		return new IInformationControlCreator() {
			public IInformationControl createInformationControl(Shell parent) {
				int shellStyle= SWT.RESIZE | SWT.TOOL;
				int style= SWT.V_SCROLL | SWT.H_SCROLL;
				return new DefaultInformationControl(parent, shellStyle, style, new HTMLTextPresenter(false));
			}
		};
	}

	/**
	 * Returns the outline presenter control creator. The creator is a factory creating outline
	 * presenter controls for the given source viewer. This implementation always returns a creator
	 * for <code>JavaOutlineInformationControl</code> instances.
	 *
	 * @param sourceViewer the source viewer to be configured by this configuration
	 * @param commandId the ID of the command that opens this control
	 * @return an information control creator
	 * @since 2.1
	 */
	private IInformationControlCreator getOutlinePresenterControlCreator(ISourceViewer sourceViewer, final String commandId) {
		return new IInformationControlCreator() {
			public IInformationControl createInformationControl(Shell parent) {
				int shellStyle= SWT.RESIZE;
				int treeStyle= SWT.V_SCROLL | SWT.H_SCROLL;
				return new JavaOutlineInformationControl(parent, shellStyle, treeStyle, commandId);
			}
		};
	}

	/* TODO JDT UI type hierarchy
	private IInformationControlCreator getHierarchyPresenterControlCreator(ISourceViewer sourceViewer) {
		return new IInformationControlCreator() {
			public IInformationControl createInformationControl(Shell parent) {
				int shellStyle= SWT.RESIZE;
				int treeStyle= SWT.V_SCROLL | SWT.H_SCROLL;
				return new HierarchyInformationControl(parent, shellStyle, treeStyle);
			}
		};
	}
	*/

	/*
	 * @see SourceViewerConfiguration#getInformationPresenter(ISourceViewer)
	 * @since 2.0
	 */
	public IInformationPresenter getInformationPresenter(ISourceViewer sourceViewer) {
		InformationPresenter presenter= new InformationPresenter(getInformationPresenterControlCreator(sourceViewer));
		presenter.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));
		
		// Register information provider
		IInformationProvider provider= new JavaInformationProvider(getEditor());
		String[] contentTypes= getConfiguredContentTypes(sourceViewer);
		for (int i= 0; i < contentTypes.length; i++)
			presenter.setInformationProvider(provider, contentTypes[i]);
		
		presenter.setSizeConstraints(60, 10, true, true);
		return presenter;
	}

	/**
	 * Returns the outline presenter which will determine and shown
	 * information requested for the current cursor position.
	 *
	 * @param sourceViewer the source viewer to be configured by this configuration
	 * @param doCodeResolve a boolean which specifies whether code resolve should be used to compute the Java element
	 * @return an information presenter
	 * @since 2.1
	 */
	public IInformationPresenter getOutlinePresenter(ISourceViewer sourceViewer, boolean doCodeResolve) {
		InformationPresenter presenter;
		if (doCodeResolve)
			presenter= new InformationPresenter(getOutlinePresenterControlCreator(sourceViewer, IJavaEditorActionDefinitionIds.OPEN_STRUCTURE));
		else
			presenter= new InformationPresenter(getOutlinePresenterControlCreator(sourceViewer, IJavaEditorActionDefinitionIds.SHOW_OUTLINE));
		presenter.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));
		presenter.setAnchor(AbstractInformationControlManager.ANCHOR_GLOBAL);
		IInformationProvider provider= new JavaElementProvider(getEditor(), doCodeResolve);
		presenter.setInformationProvider(provider, IDocument.DEFAULT_CONTENT_TYPE);
		presenter.setInformationProvider(provider, IJavaPartitions.JAVA_DOC);
		presenter.setInformationProvider(provider, IJavaPartitions.JAVA_MULTI_LINE_COMMENT);
		presenter.setInformationProvider(provider, IJavaPartitions.JAVA_MULTI_LINE_PLUS_COMMENT);
		presenter.setInformationProvider(provider, IJavaPartitions.JAVA_MULTI_LINE_PLUS_DOC_COMMENT);
		presenter.setInformationProvider(provider, IJavaPartitions.JAVA_SINGLE_LINE_COMMENT);
		presenter.setInformationProvider(provider, IJavaPartitions.JAVA_SINGLE_LINE_DOC_COMMENT);
		presenter.setInformationProvider(provider, IJavaPartitions.JAVA_STRING);
		presenter.setInformationProvider(provider, IJavaPartitions.JAVA_CHARACTER);
		presenter.setSizeConstraints(50, 20, true, false);
		return presenter;
	}

	/**
	 * Returns the settings for the given section.
	 *
	 * @param sectionName the section name
	 * @return the settings
	 * @since 3.0
	 */
	private IDialogSettings getSettings(String sectionName) {
		IDialogSettings settings= JavaPlugin.getDefault().getDialogSettings().getSection(sectionName);
		if (settings == null)
			settings= JavaPlugin.getDefault().getDialogSettings().addNewSection(sectionName);

		return settings;
	}

	/**
	 * Returns the hierarchy presenter which will determine and shown type hierarchy
	 * information requested for the current cursor position.
	 *
	 * @param sourceViewer the source viewer to be configured by this configuration
	 * @param doCodeResolve a boolean which specifies whether code resolve should be used to compute the Java element
	 * @return an information presenter
	 * @since 3.0
	 */
	/* TODO JDT UI type hierarchy
	public IInformationPresenter getHierarchyPresenter(ISourceViewer sourceViewer, boolean doCodeResolve) {
		
		// Do not create hierarchy presenter if there's no CU.
		if (getEditor() != null && getEditor().getEditorInput() != null && JavaUI.getEditorInputJavaElement(getEditor().getEditorInput()) == null)
			return null;
		
		InformationPresenter presenter= new InformationPresenter(getHierarchyPresenterControlCreator(sourceViewer));
		presenter.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));
		presenter.setAnchor(AbstractInformationControlManager.ANCHOR_GLOBAL);
		IInformationProvider provider= new JavaElementProvider(getEditor(), doCodeResolve);
		presenter.setInformationProvider(provider, IDocument.DEFAULT_CONTENT_TYPE);
		presenter.setInformationProvider(provider, IJavaPartitions.JAVA_DOC);
		presenter.setInformationProvider(provider, IJavaPartitions.JAVA_MULTI_LINE_COMMENT);
		presenter.setInformationProvider(provider, IJavaPartitions.JAVA_SINGLE_LINE_COMMENT);
		presenter.setInformationProvider(provider, IJavaPartitions.JAVA_STRING);
		presenter.setInformationProvider(provider, IJavaPartitions.JAVA_CHARACTER);
		presenter.setSizeConstraints(50, 20, true, false);
		return presenter;
	}
	*/

	/**
	 * Determines whether the preference change encoded by the given event
	 * changes the behavior of one of its contained components.
	 *
	 * @param event the event to be investigated
	 * @return <code>true</code> if event causes a behavioral change
	 * @since 3.0
	 */
	public boolean affectsTextPresentation(PropertyChangeEvent event) {
		return  fCodeScanner.affectsBehavior(event)
			|| fMultilineCommentScanner.affectsBehavior(event)
			|| fMultilinePlusCommentScanner.affectsBehavior(event)
			|| fMultilinePlusDocCommentScanner.affectsBehavior(event)
			|| fSinglelineCommentScanner.affectsBehavior(event)
			|| fSinglelineDocCommentScanner.affectsBehavior(event)
			|| fStringScanner.affectsBehavior(event)
			|| fJavaDocScanner.affectsBehavior(event);
	}

	/**
	 * Adapts the behavior of the contained components to the change
	 * encoded in the given event.
	 * <p>
	 * Clients are not allowed to call this method if the old setup with
	 * text tools is in use.
	 * </p>
	 *
	 * @param event the event to which to adapt
	 * @see JavaSourceViewerConfiguration#JavaSourceViewerConfiguration(IColorManager, IPreferenceStore, ITextEditor, String)
	 * @since 3.0
	 */
	public void handlePropertyChangeEvent(PropertyChangeEvent event) {
		Assert.isTrue(isNewSetup());
		if (fCodeScanner.affectsBehavior(event))
			fCodeScanner.adaptToPreferenceChange(event);
		if (fMultilineCommentScanner.affectsBehavior(event))
			fMultilineCommentScanner.adaptToPreferenceChange(event);
		if (fMultilinePlusCommentScanner.affectsBehavior(event))
			fMultilinePlusCommentScanner.adaptToPreferenceChange(event);
		if (fMultilinePlusDocCommentScanner.affectsBehavior(event))
			fMultilinePlusDocCommentScanner.adaptToPreferenceChange(event);
		if (fSinglelineCommentScanner.affectsBehavior(event))
			fSinglelineCommentScanner.adaptToPreferenceChange(event);
		if (fSinglelineDocCommentScanner.affectsBehavior(event))
			fSinglelineDocCommentScanner.adaptToPreferenceChange(event);
		if (fStringScanner.affectsBehavior(event))
			fStringScanner.adaptToPreferenceChange(event);
		if (fJavaDocScanner.affectsBehavior(event))
			fJavaDocScanner.adaptToPreferenceChange(event);
		if (fJavaDoubleClickSelector != null && JavaCore.COMPILER_SOURCE.equals(event.getProperty()))
			if (event.getNewValue() instanceof String)
				fJavaDoubleClickSelector.setSourceVersion((String) event.getNewValue());
	}

	/*
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getHyperlinkDetectors(org.eclipse.jface.text.source.ISourceViewer)
	 * @since 3.1
	 */
	public IHyperlinkDetector[] getHyperlinkDetectors(ISourceViewer sourceViewer) {
		if (!fPreferenceStore.getBoolean(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_HYPERLINKS_ENABLED))
			return null;

		IHyperlinkDetector[] inheritedDetectors= super.getHyperlinkDetectors(sourceViewer);

		if (fTextEditor == null)
			return inheritedDetectors;

		int inheritedDetectorsLength= inheritedDetectors != null ? inheritedDetectors.length : 0;
		// TODO JDT UI NLS
		IHyperlinkDetector[] detectors= new IHyperlinkDetector[inheritedDetectorsLength + 1];
		// detectors[0]= new NLSKeyHyperlinkDetector(fTextEditor);
		detectors[0]= new JavaElementHyperlinkDetector(fTextEditor);
		for (int i= 0; i < inheritedDetectorsLength; i++)
			detectors[i+1]= inheritedDetectors[i];

		return detectors;
	}
}
