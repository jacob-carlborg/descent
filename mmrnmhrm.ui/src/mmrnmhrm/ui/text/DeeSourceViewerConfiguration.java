package mmrnmhrm.ui.text;

import java.util.Map;

import mmrnmhrm.ui.DeePlugin;
import mmrnmhrm.ui.editor.text.DeeCodeContentAssistProcessor;
import mmrnmhrm.ui.editor.text.DeeDocTextHover;
import mmrnmhrm.ui.editor.text.DeeHyperlinkDetector;
import mmrnmhrm.ui.internal.text.RubyAutoEditStrategy;
import mmrnmhrm.ui.text.color.IDeeColorConstants;

import org.eclipse.dltk.internal.ui.editor.EditorUtility;
import org.eclipse.dltk.internal.ui.editor.ModelElementHyperlinkDetector;
import org.eclipse.dltk.internal.ui.editor.ScriptSourceViewer;
import org.eclipse.dltk.internal.ui.typehierarchy.HierarchyInformationControl;
import org.eclipse.dltk.ui.text.AbstractScriptScanner;
import org.eclipse.dltk.ui.text.IColorManager;
import org.eclipse.dltk.ui.text.ScriptOutlineInformationControl;
import org.eclipse.dltk.ui.text.ScriptPresentationReconciler;
import org.eclipse.dltk.ui.text.ScriptSourceViewerConfiguration;
import org.eclipse.dltk.ui.text.SingleTokenScriptScanner;
import org.eclipse.dltk.ui.text.completion.ContentAssistPreference;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.AbstractInformationControlManager;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
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

public class DeeSourceViewerConfiguration extends ScriptSourceViewerConfiguration {

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
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return DeePartitions.DEE_PARTITION_TYPES;
	}
	
	@Override
	protected void initializeScanners() {
		fCodeScanner = new DeeCodeScanner(getColorManager(), fPreferenceStore);
		fStringScanner = new SingleTokenScriptScanner(getColorManager(),
				fPreferenceStore, IDeeColorConstants.DEE_STRING);
		fCommentScanner = new SingleTokenScriptScanner(getColorManager(),
				fPreferenceStore, IDeeColorConstants.DEE_COMMENT);
		fDocScanner = new SingleTokenScriptScanner(getColorManager(),
				fPreferenceStore, IDeeColorConstants.DEE_DOCCOMMENT);
	}

	@Override
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
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
		reconciler.setDamager(dr, DeePartitions.DEE_SINGLE_COMMENT);
		reconciler.setRepairer(dr, DeePartitions.DEE_SINGLE_COMMENT);
		dr = new DefaultDamagerRepairer(fCommentScanner);
		reconciler.setDamager(dr, DeePartitions.DEE_MULTI_COMMENT);
		reconciler.setRepairer(dr, DeePartitions.DEE_MULTI_COMMENT);
		dr = new DefaultDamagerRepairer(fCommentScanner);
		reconciler.setDamager(dr, DeePartitions.DEE_NESTED_COMMENT);
		reconciler.setRepairer(dr, DeePartitions.DEE_NESTED_COMMENT);

		dr = new DefaultDamagerRepairer(fDocScanner);
		reconciler.setDamager(dr, DeePartitions.DEE_SINGLE_DOCCOMMENT);
		reconciler.setRepairer(dr, DeePartitions.DEE_SINGLE_DOCCOMMENT);
		dr = new DefaultDamagerRepairer(fDocScanner);
		reconciler.setDamager(dr, DeePartitions.DEE_MULTI_DOCCOMMENT);
		reconciler.setRepairer(dr, DeePartitions.DEE_MULTI_DOCCOMMENT);
		dr = new DefaultDamagerRepairer(fDocScanner);
		reconciler.setDamager(dr, DeePartitions.DEE_NESTED_DOCCOMMENT);
		reconciler.setRepairer(dr, DeePartitions.DEE_NESTED_DOCCOMMENT);
		
		return reconciler;
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
	
	
	
	@SuppressWarnings("unchecked")
	@Override 
	protected Map getHyperlinkDetectorTargets(ISourceViewer sourceViewer) {
		Map<String, ITextEditor> targets= super.getHyperlinkDetectorTargets(sourceViewer);
		targets.put(DeeHyperlinkDetector.DEE_EDITOR_TARGET, getEditor()); 
		return targets;
	}

	@Override
	public IHyperlinkDetector[] getHyperlinkDetectors(ISourceViewer sourceViewer) {
		IHyperlinkDetector[] hyperlinkDetectors = super.getHyperlinkDetectors(sourceViewer);
		for (int i = 0; i < hyperlinkDetectors.length; i++) {
			if(hyperlinkDetectors[i] instanceof ModelElementHyperlinkDetector) {
				// Remove ModelElementHyperlinkDetector cause it sucks
				// Creating a new array is not necessary I think
				hyperlinkDetectors[i] = null; 
			}
		}
		return hyperlinkDetectors;
	}
	
	
	@Override
	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
		return super.getContentAssistant(sourceViewer);
	}
	
	@Override
	protected void alterContentAssistant(ContentAssistant assistant) {
		super.alterContentAssistant(assistant);
		IContentAssistProcessor deeContentAssistProcessor = new DeeCodeContentAssistProcessor(
				assistant, getEditor());
		assistant.setContentAssistProcessor(deeContentAssistProcessor, DeePartitions.DEE_CODE);

		// assistant.setStatusLineVisible(true);
	}
	
	@Override
	protected ContentAssistPreference getContentAssistPreference() {
		return DeeContentAssistPreference.getDefault();
	}
	
	@Override
	public IAutoEditStrategy[] getAutoEditStrategies(ISourceViewer sourceViewer, String contentType) {
		// TODO improve auto edit strategy
		String partitioning = getConfiguredDocumentPartitioning(sourceViewer);
		return new IAutoEditStrategy[] { new RubyAutoEditStrategy(partitioning ) };
	}

	
	@Override
	public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType, int stateMask) {
		// TODO: Note: we are currently using own TextHover, not DLTK's. maybe can change
		return new DeeDocTextHover(getEditor());
	}
	
	@Override
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
	
	@Override
	public IInformationPresenter getInformationPresenter(ISourceViewer sourceViewer) {
		return super.getInformationPresenter(sourceViewer);
	}
	
	@Override
	protected void initializeQuickOutlineContexts(InformationPresenter presenter,
			IInformationProvider provider) {
		String[] contentTypes = DeePartitions.DEE_PARTITION_TYPES;
		for (int i= 0; i < contentTypes.length; i++)
			presenter.setInformationProvider(provider, contentTypes[i]);
	}

	private IInformationControlCreator getHierarchyPresenterControlCreator() {
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

	
	@Override
	public IInformationPresenter getHierarchyPresenter(
			ScriptSourceViewer sourceViewer, boolean doCodeResolve) {
		// Do not create hierarchy presenter if there's no CU.
		if (getEditor() != null
				&& getEditor().getEditorInput() != null
				&& EditorUtility.getEditorInputModelElement(getEditor(), true) == null)
			return null;

		InformationPresenter presenter = new InformationPresenter(
				getHierarchyPresenterControlCreator());
		presenter.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));
		presenter.setAnchor(AbstractInformationControlManager.ANCHOR_GLOBAL);
		@SuppressWarnings("restriction")
		IInformationProvider provider = new org.eclipse.dltk.internal.ui.text. 
				ScriptElementProvider(getEditor(), doCodeResolve);
		presenter.setInformationProvider(provider, DeePartitions.DEE_CODE);
		
		presenter.setSizeConstraints(50, 20, true, false);
		return presenter;
	}


	// XXX: use DTLK default method?
	@Override
	public IInformationControlCreator getInformationControlCreator(ISourceViewer sourceViewer) {
		return new IInformationControlCreator() {
			@SuppressWarnings("restriction")
			public IInformationControl createInformationControl(Shell parent) {
				return new DefaultInformationControl(parent,
						new org.eclipse.jface.internal.text.html.HTMLTextPresenter(true));
			}
		};
	}


}
