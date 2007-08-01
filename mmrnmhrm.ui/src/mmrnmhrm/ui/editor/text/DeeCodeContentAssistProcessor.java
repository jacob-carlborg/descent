package mmrnmhrm.ui.editor.text;

import java.util.ArrayList;

import mmrnmhrm.core.model.CompilationUnit;
import mmrnmhrm.ui.DeePlugin;
import mmrnmhrm.ui.views.DeeElementImageProvider;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.contentassist.ContextInformation;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationPresenter;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

import dtool.dom.ast.ASTElementFinder;
import dtool.dom.ast.ASTNode;
import dtool.dom.definitions.DefUnit;
import dtool.dom.references.EntIdentifier;
import dtool.dom.references.Entity;
import dtool.refmodel.EntityResolver;
import dtool.refmodel.EntitySearch;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.NodeUtil;
import dtool.refmodel.PartialEntitySearh;

public class DeeCodeContentAssistProcessor implements IContentAssistProcessor {

	private static final ICompletionProposal[] RESULTS_EMPTY_ARRAY = new ICompletionProposal[0];

	/**
	 * Simple content assist tip closer. The tip is valid in a range
	 * of 5 characters around its popup location.
	 */
	protected static class Validator implements IContextInformationValidator, IContextInformationPresenter {

		protected int fInstallOffset;

		public boolean isContextInformationValid(int offset) {
			return Math.abs(fInstallOffset - offset) == 0;
		}

		public void install(IContextInformation info, ITextViewer viewer, int offset) {
			fInstallOffset= offset;
		}
		
		public boolean updatePresentation(int documentPosition, TextPresentation presentation) {
			StyleRange range = new StyleRange(0,2,null, null, SWT.BOLD);
			presentation.replaceStyleRange(range);
			return false;
		}
	}

	private ITextEditor textEditor;
	private IContextInformationValidator fValidator= new Validator();
	private String errorMsg;

	
	public DeeCodeContentAssistProcessor(ITextEditor textEditor) {
		this.textEditor = textEditor;
	}

	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer,
			final int offset) {
		
		IEditorInput editorInput = textEditor.getEditorInput();
		CompilationUnit cunit;
		cunit = DeePlugin.getCompilationUnitOperation(editorInput);
		cunit.reconcile();
		
		final ArrayList<ICompletionProposal> results;
		results = new ArrayList<ICompletionProposal>();

		ASTNode node = ASTElementFinder.findElement(cunit.getModule(), offset);
		
		
		IScopeNode searchScope;
		final String searchPrefix;
		final int prefixLen;
		final int rplOffset = node.getStartPos();
		final int rplLen /*= offset - node.getStartPos()*/;

		if(node instanceof Entity)  {
			errorMsg = null;
			Entity ref = (Entity) node;
			searchScope = getRootScope(ref);
			if(node instanceof EntIdentifier) {
				EntIdentifier refIdent = (EntIdentifier) node;
				prefixLen = offset - refIdent.getOffset();
				rplLen = refIdent.getLength() - prefixLen;
				searchPrefix = refIdent.name.substring(0, prefixLen);
			} else {
				// TODO
				searchPrefix = "";
				rplLen = 0;
				prefixLen = 0;
			}
	
		} else if(node instanceof IScopeNode) {
			errorMsg = null;
			searchScope = (IScopeNode) node;
			searchPrefix = "";
			rplLen = 0;
			prefixLen = 0;
		} else {
			errorMsg = "No Completion Available";
			return null;
		}
		
		EntitySearch search = new PartialEntitySearh(searchPrefix, searchScope, false) {
			@Override
			public void addResult(DefUnit defunit) {
				results.add(new DeeCompletionProposal(
						defunit.getName().substring(prefixLen), 
						offset, 
						rplLen, 
						defunit.getName().length() - prefixLen,
						DeeElementImageProvider.getNodeImage(defunit),
						defunit.toString(),
						null
						)); 
			}
		};
		EntityResolver.findDefUnitInExtendedScope(searchScope, search);
		
		return results.toArray(RESULTS_EMPTY_ARRAY);
	}


	private IScopeNode getRootScope(Entity ref) {
		return NodeUtil.getOuterScope(ref);
	}

	public IContextInformation[] computeContextInformation(ITextViewer viewer,
			int offset) {
		IContextInformation[] result= new IContextInformation[5];
		for (int i= 0; i < result.length; i++)
			result[i]= new ContextInformation(
				"CompletionProcessor.ContextInfo.display.pattern",  //$NON-NLS-1$
				"CompletionProcessor.ContextInfo.value.pattern"); //$NON-NLS-1$
		return result; 
	}

	public char[] getCompletionProposalAutoActivationCharacters() {
		return new char[] { '.'};
	}

	public char[] getContextInformationAutoActivationCharacters() {
		return new char[] { '('};
	}

	public IContextInformationValidator getContextInformationValidator() {
		return fValidator;
	}

	public String getErrorMessage() {
		return errorMsg;
	}

}
