package mmrnmhrm.ui.editor.text;

import java.util.ArrayList;
import java.util.Iterator;

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

import descent.internal.core.dom.Parser;
import descent.internal.core.dom.ParserFacade;
import descent.internal.core.dom.TOK;
import descent.internal.core.dom.Token;
import dtool.descentadapter.DescentASTConverter;
import dtool.dom.definitions.DefUnit;
import dtool.dom.definitions.Module;
import dtool.refmodel.CommonDefUnitSearch;
import dtool.refmodel.PartialEntitySearch;
import dtool.refmodel.PartialSearchOptions;

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
		String docstr = cunit.getDocument().get();
		
		Parser parser = ParserFacade.parseCompilationUnit(docstr);
		
		Token token = null;
		for (Iterator<Token> iter = parser.tokenList.iterator(); iter.hasNext();) {
			Token newtoken = iter.next();
			if(newtoken.ptr < offset) {
				token = newtoken;
				// continue
			} else {
				break;
			}
		} // now token is the last token before offset
		
		// If completion request is *inside* a token
		if(token.ptr < offset && (token.ptr + token.len) > offset) {
			// then only allow if it's an indentifier
			if(token.value != TOK.TOKidentifier) {
				errorMsg = "Invalid Context:" + token;
				return null;
			}
		}
				
		descent.internal.core.dom.Module dmdModule;
		dmdModule = parser.mod;

		if(dmdModule.getProblems().length != 0) {
			if(token != null && token.value == TOK.TOKdot) {
				// Let's retry parsing without the dot.
				String newstr = docstr.substring(0, token.ptr) + " " 
					+ docstr.substring(token.ptr+1, docstr.length());
				
				parser = ParserFacade.parseCompilationUnit(newstr);
				dmdModule = parser.mod;
				
				if(dmdModule.getProblems().length != 0) {
					errorMsg = "Syntax Errors, cannot complete.";
					return null;
				}
			}
		}
		
		Module neoModule = DescentASTConverter.convertModule(dmdModule);
		neoModule.setCUnit(cunit);

		//cunit.reconcile();
		
		final ArrayList<ICompletionProposal> results;
		results = new ArrayList<ICompletionProposal>();

		PartialSearchOptions searchOptions = new PartialSearchOptions();
		CommonDefUnitSearch search = new PartialEntitySearch(searchOptions) {
			@Override
			public void addResult(DefUnit defunit) {
				String rplStr = defunit.getName().substring(searchOptions.prefixLen);
				results.add(new DeeCompletionProposal(
						rplStr,
						offset,
						searchOptions.rplLen, 
						rplStr.length(),
						DeeElementImageProvider.getNodeImage(defunit),
						defunit.toStringAsCodeCompletion(),
						defunit,
						null // context information
						)); 
			}
		};
		
		errorMsg = PartialEntitySearch.doCompletionSearch(offset, 
				neoModule, searchOptions, search);
		
		if(errorMsg != null)
			return null;
		
		return results.toArray(RESULTS_EMPTY_ARRAY);
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
