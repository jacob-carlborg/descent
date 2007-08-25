package mmrnmhrm.ui.text;

import java.util.ArrayList;
import java.util.List;

import melnorme.miscutil.log.Logg;
import mmrnmhrm.ui.text.color.DeeColorConstants;

import org.eclipse.dltk.ui.text.AbstractScriptScanner;
import org.eclipse.dltk.ui.text.IColorManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;

import descent.internal.compiler.parser.ast.TokenUtil;

public class DeeCodeScanner extends AbstractScriptScanner {

	
	public DeeCodeScanner(IColorManager manager, IPreferenceStore store) {
		super(manager, store);
		initialize();
	}
	
	private static String fgTokenProperties[] = new String[] {
		DeeColorConstants.DEE_SPECIAL,
		DeeColorConstants.DEE_STRING,
		DeeColorConstants.DEE_LITERALS,
		DeeColorConstants.DEE_OPERATORS,
		DeeColorConstants.DEE_BASICTYPES,
		DeeColorConstants.DEE_KEYWORD,
		DeeColorConstants.DEE_DOCCOMMENT,
		DeeColorConstants.DEE_COMMENT,
		DeeColorConstants.DEE_DEFAULT
	};

	
	protected String[] getTokenProperties() {
		return fgTokenProperties;
	}

	protected List createRules() {
		List<IRule> rules = new ArrayList<IRule>();
		
		IToken tkKeyword = getToken(DeeColorConstants.DEE_KEYWORD);
		IToken tkBasics = getToken(DeeColorConstants.DEE_BASICTYPES);
//		IToken tkOperators = getToken(DeeColorConstants.DEE_OPERATORS);
		IToken tkLiterals = getToken(DeeColorConstants.DEE_LITERALS);
		IToken tkString = getToken(DeeColorConstants.DEE_STRING);
		IToken tkOther = getToken(DeeColorConstants.DEE_DEFAULT);
		
		rules.add(new SingleLineRule("'", "'", tkString, '\\'));
		
		// Add generic whitespace rule.
		rules.add(new WhitespaceRule(new LangWhitespaceDetector()));
							
		// Add word rule for keywords, types, and constants.
		WordRule wordRule = new WordRule(new JavaWordDetector(), tkOther);
		for (int i = 0; i < TokenUtil.keywords.length; i++) {
			wordRule.addWord(TokenUtil.keywords[i].toString(), tkKeyword);
		}
		for (int i = 0; i < TokenUtil.basicTypes.length; i++) {
			wordRule.addWord(TokenUtil.basicTypes[i].toString(), tkBasics);
		}
		for (int i = 0; i < TokenUtil.specialNamedLiterals.length; i++) {
			wordRule.addWord(TokenUtil.specialNamedLiterals[i].toString(), tkLiterals);
		}
		rules.add(wordRule);
		
		/*WordRule wordRule2 = new WordRule(new JavaWordDetector(), tkOther);
		for (int i = 0; i < TokenUtil.literals.length; i++) {
			wordRule.addWord(TokenUtil.literals[i].toString(), tkLiterals);
		}*/
		//rules.add(wordRule2);
		
		setDefaultReturnToken(tkOther);
		return rules;
	}
	
	@Override
	public void setRange(IDocument document, int offset, int length) {
		Logg.codeScanner.println(">> DeeCodeScanner#setRange: " + offset + ","+ length);
		super.setRange(document, offset, length);
	}
	
	@Override
	public IToken nextToken() {
		IToken token = super.nextToken();
		String offsetStr = String.format("%3d", getTokenOffset());
		String lenStr = String.format("%2d", getTokenLength());
		Object data = token.getData();
		//int ix = indexOf(token);
		Logg.codeScanner.println("Token["+offsetStr +","+ lenStr +"]: " +data);
		return token;
	}
	
}
