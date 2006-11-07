package descent.ui.text;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.MonoReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.graphics.RGB;

import descent.ui.IDColorConstants;
import descent.ui.text.scanners.DCodeScanner;
import descent.ui.text.scanners.DPartitionerScanner;

public class DConfiguration extends SourceViewerConfiguration {
	
	private DCodeScanner codeScanner;
	private DEditor editor;

	public DConfiguration(DEditor editor) {
		this.editor = editor;
	}
	
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return DPartitionerScanner.LEGAL_CONTENT;
	}

	protected DCodeScanner getCodeScanner() {
		if (codeScanner == null) {
			codeScanner = new DCodeScanner(editor.getColorManager());
			codeScanner.setDefaultReturnToken(
				new Token(
					new TextAttribute(
						editor.getColorManager().getColor(IDColorConstants.CODE))));
		}
		return codeScanner;
	}

	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();

		addDefaultDamageRepairer(reconciler, getCodeScanner(), IDocument.DEFAULT_CONTENT_TYPE);
		addNonRuleBasedDamagerRepairer(reconciler, IDColorConstants.PLUS_DOC_COMMENT, DPartitionerScanner.PLUS_DOC_COMMENT);
		addNonRuleBasedDamagerRepairer(reconciler, IDColorConstants.MULTI_DOC_COMMENT, DPartitionerScanner.MULTI_DOC_COMMENT);
		addNonRuleBasedDamagerRepairer(reconciler, IDColorConstants.SINGLE_DOC_COMMENT, DPartitionerScanner.SINGLE_DOC_COMMENT);
		addNonRuleBasedDamagerRepairer(reconciler, IDColorConstants.PLUS_COMMENT, DPartitionerScanner.PLUS_COMMENT);
		addNonRuleBasedDamagerRepairer(reconciler, IDColorConstants.MULTI_COMMENT, DPartitionerScanner.MULTI_COMMENT);
		addNonRuleBasedDamagerRepairer(reconciler, IDColorConstants.SINGLE_COMMENT, DPartitionerScanner.SINGLE_COMMENT);
		addNonRuleBasedDamagerRepairer(reconciler, IDColorConstants.WYSIWYG_STRING, DPartitionerScanner.WYSIWYG_STRING);
		addNonRuleBasedDamagerRepairer(reconciler, IDColorConstants.HEX_STRING, DPartitionerScanner.HEX_STRING);
		addNonRuleBasedDamagerRepairer(reconciler, IDColorConstants.STRING, DPartitionerScanner.STRING);
		addNonRuleBasedDamagerRepairer(reconciler, IDColorConstants.CHAR, DPartitionerScanner.CHAR);

		return reconciler;
	}
	
	@Override
	public IReconciler getReconciler(ISourceViewer sourceViewer) {
		if (editor != null) {
			return new MonoReconciler(editor.getReconcilingStrategy(), false);
		} else {
			return null;
		}
	}
	
	@Override
	public IAnnotationHover getAnnotationHover(ISourceViewer sourceViewer) {
		return new DAnnotationHover();
	}
	
	private void addDefaultDamageRepairer(PresentationReconciler reconciler, ITokenScanner scanner, String contentType) {
		DefaultDamagerRepairer dr =
			new DefaultDamagerRepairer(scanner);
		reconciler.setDamager(dr, contentType);
		reconciler.setRepairer(dr, contentType);
	}
	
	private void addNonRuleBasedDamagerRepairer(PresentationReconciler reconciler, RGB color, String contentType) {
		NonRuleBasedDamagerRepairer ndr =
			new NonRuleBasedDamagerRepairer(
				new TextAttribute(
					editor.getColorManager().getColor(color)));
		reconciler.setDamager(ndr, contentType);
		reconciler.setRepairer(ndr, contentType);
	}

}