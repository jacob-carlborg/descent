package mmrnmhrm.ui.text;

import java.util.Map;

import mmrnmhrm.ui.DeePlugin;
import mmrnmhrm.ui.editor.text.DeeCodeContentAssistProcessor;
import mmrnmhrm.ui.editor.text.DeeHyperlinkDetector;
import mmrnmhrm.ui.editor.text.DeeTextHover;
import mmrnmhrm.ui.text.color.DeeColorConstants;

import org.eclipse.dltk.internal.ui.editor.EditorUtility;
import org.eclipse.dltk.internal.ui.editor.ScriptSourceViewer;
import org.eclipse.dltk.internal.ui.typehierarchy.HierarchyInformationControl;
import org.eclipse.dltk.ui.text.AbstractScriptScanner;
import org.eclipse.dltk.ui.text.IColorManager;
import org.eclipse.dltk.ui.text.ScriptOutlineInformationControl;
import org.eclipse.dltk.ui.text.ScriptPresentationReconciler;
import org.eclipse.dltk.ui.text.ScriptSourceViewerConfiguration;
import org.eclipse.dltk.ui.text.SingleTokenScriptScanner;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.AbstractInformationControlManager;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.information.IInformationPresenter;
import org.eclipse.jface.text.information.IInformationProvider;
import org.eclipse.jface.text.information.InformationPresenter;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.texteditor.ITextEditor;

public class DeeSourceViewerConfiguration extends
		ScriptSourceViewerConfiguration {

	private AbstractScriptScanner fCodeScanner;
	private AbstractScriptScanner fStringScanner;
	private AbstractScriptScanner fCommentScanner;
	private AbstractScriptScanner fDocScanner;
	
	public DeeSourceViewerConfiguration(IColorManager colorManager,
			IPreferenceStore preferenceStore, ITextEditor editor,
			String partitioning) {
		super(colorManager, preferenceStore, editor, partitioning);
	}
	
	@Override
	protected void initializeScanners() {
		fCodeScanner = new DeeCodeScanner(getColorManager(), fPreferenceStore);
		fStringScanner = new SingleTokenScriptScanner(getColorManager(),
				fPreferenceStore, DeeColorConstants.DEE_STRING);
		fCommentScanner = new SingleTokenScriptScanner(getColorManager(),
				fPreferenceStore, DeeColorConstants.DEE_COMMENT);
		fDocScanner = new SingleTokenScriptScanner(getColorManager(),
				fPreferenceStore, DeeColorConstants.DEE_DOCCOMMENT);
	}

	
	@Override
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return DeePartitions.LEGAL_CONTENT_TYPES;
	}
	
	@Override
	public boolean affectsTextPresentation(PropertyChangeEvent event) {
		return fCodeScanner.affectsBehavior(event)
			|| fStringScanner.affectsBehavior(event)
			|| fCommentScanner.affectsBehavior(event)
			|| fDocScanner.affectsBehavior(event);
	}
	
	@Override
	public void handlePropertyChangeEvent(PropertyChangeEvent event) {
		if (fCodeScanner.affectsBehavior(event))
			fCodeScanner.adaptToPreferenceChange(event);
		if (fStringScanner.affectsBehavior(event))
			fStringScanner.adaptToPreferenceChange(event);
		if (fCommentScanner.affectsBehavior(event))
			fCommentScanner.adaptToPreferenceChange(event);
		if (fDocScanner.affectsBehavior(event))
			fDocScanner.adaptToPreferenceChange(event);
	}
	
	public IPresentationReconciler getPresentationReconciler(
			ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new ScriptPresentationReconciler();
		reconciler.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));

		DefaultDamagerRepairer dr;

		dr = new DefaultDamagerRepairer(fCodeScanner);
		reconciler.setDamager(dr, DeePartitions.DEE_CODE);
		reconciler.setRepairer(dr, DeePartitions.DEE_CODE);

		dr = new DefaultDamagerRepairer(fStringScanner);
		reconciler.setDamager(dr, DeePartitions.DEE_STRING);
		reconciler.setRepairer(dr, DeePartitions.DEE_STRING);

		dr = new DefaultDamagerRepairer(fCommentScanner);
		reconciler.setDamager(dr, DeePartitions.DEE_COMMENT);
		reconciler.setRepairer(dr, DeePartitions.DEE_COMMENT);
		
		dr = new DefaultDamagerRepairer(fDocScanner);
		reconciler.setDamager(dr, DeePartitions.DEE_DOCCOMMENT);
		reconciler.setRepairer(dr, DeePartitions.DEE_DOCCOMMENT);

		return reconciler;
	}
	
	@Override
	public ITextHover getTextHover(ISourceViewer sourceViewer,
			String contentType, int stateMask) {
		return new DeeTextHover(sourceViewer, getEditor());
	}
	
	@Override @SuppressWarnings("unchecked")
	protected Map getHyperlinkDetectorTargets(ISourceViewer sourceViewer) {
		Map targets= super.getHyperlinkDetectorTargets(sourceViewer);
		targets.put(DeeHyperlinkDetector.DEE_EDITOR_TARGET, getEditor()); 
		return targets;
	}
	
	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
		if (getEditor() == null)
			return null;
		
		ContentAssistant assistant = new ContentAssistant();
		assistant.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));

		assistant.setRestoreCompletionProposalSize(getSettings("completion_proposal_size")); //$NON-NLS-1$

		IContentAssistProcessor deeContentAssistProcessor 
			= new DeeCodeContentAssistProcessor(assistant, getEditor());
		//= new RubyCompletionProcessor(getEditor(), assistant, IDocument.DEFAULT_CONTENT_TYPE);
		assistant.setContentAssistProcessor(deeContentAssistProcessor, DeePartitions.DEE_CODE);


		DeeContentAssistPreference.getDefault().configure(assistant,
				fPreferenceStore);

		assistant.setContextInformationPopupOrientation(IContentAssistant.CONTEXT_INFO_ABOVE);
		assistant.setInformationControlCreator(getInformationControlCreator(sourceViewer));

		// assistant.setStatusLineVisible(true);

		return assistant;
	}
	
	protected IInformationControlCreator getOutlinePresenterControlCreator(
			ISourceViewer sourceViewer, final String commandId) {
		return new IInformationControlCreator() {
			public IInformationControl createInformationControl(Shell parent) {
				int shellStyle = SWT.RESIZE;
				int treeStyle = SWT.V_SCROLL | SWT.H_SCROLL;
				return new ScriptOutlineInformationControl(parent, shellStyle, treeStyle, commandId) {
					@Override
					protected IPreferenceStore getPreferenceStore() {
						return DeePlugin.getDefault().getPreferenceStore();
					}
				};
			}
		};
	}
	
	protected void initializeQuickOutlineContexts(InformationPresenter presenter,
			IInformationProvider provider) {
		presenter.setInformationProvider(provider, DeePartitions.DEE_CODE);
		presenter.setInformationProvider(provider, DeePartitions.DEE_DOCCOMMENT);
		presenter.setInformationProvider(provider, DeePartitions.DEE_STRING);
	}

	private IInformationControlCreator getHierarchyPresenterControlCreator(ISourceViewer sourceViewer) {
		return new IInformationControlCreator() {
			public IInformationControl createInformationControl(Shell parent) {
				int shellStyle = SWT.RESIZE;
				int treeStyle = SWT.V_SCROLL | SWT.H_SCROLL;
				return new HierarchyInformationControl(parent, shellStyle, treeStyle) {
					@Override
					protected IPreferenceStore getPreferenceStore() {
						return DeePlugin.getDefault().getPreferenceStore();
					}
				};
			}
		};
	}
	

	
	@SuppressWarnings("restriction")
	public IInformationPresenter getHierarchyPresenter(
			ScriptSourceViewer sourceViewer, boolean doCodeResolve) {
		// Do not create hierarchy presenter if there's no CU.
		if (getEditor() != null
				&& getEditor().getEditorInput() != null
				&& EditorUtility.getEditorInputModelElement(getEditor(), true) == null)
			return null;

		InformationPresenter presenter = new InformationPresenter(
				getHierarchyPresenterControlCreator(sourceViewer));
		presenter.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));
		presenter.setAnchor(AbstractInformationControlManager.ANCHOR_GLOBAL);
		IInformationProvider provider = new org.eclipse.dltk.internal.ui.text. 
				ScriptElementProvider(getEditor(), doCodeResolve);
		presenter.setInformationProvider(provider, DeePartitions.DEE_CODE);
		
		presenter.setSizeConstraints(50, 20, true, false);
		return presenter;
	}

	
	public IInformationControlCreator getInformationControlCreator(ISourceViewer sourceViewer) {
		return new IInformationControlCreator() {
			@SuppressWarnings("restriction")
			public IInformationControl createInformationControl(Shell parent) {
				return new DefaultInformationControl(parent, SWT.NONE, 
						new org.eclipse.jface.internal.text.html.
						HTMLTextPresenter(true));
			}
		};
	}

}
