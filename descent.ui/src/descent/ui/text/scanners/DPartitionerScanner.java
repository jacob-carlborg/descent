package descent.ui.text.scanners;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;

import descent.ui.text.rules.DEscapeRule;
import descent.ui.text.rules.DStringRule;
import descent.ui.text.rules.NestedCommentRule;

public class DPartitionerScanner extends RuleBasedPartitionScanner {
	
	public final static String PLUS_DOC_COMMENT = "__d_plus_doc_comment";
	public final static String MULTI_DOC_COMMENT = "__d_multi_doc_comment";
	public final static String SINGLE_DOC_COMMENT = "__d_single_doc_comment";
	public final static String PLUS_COMMENT = "__d_plus_comment";
	public final static String MULTI_COMMENT = "__d_multi_comment";	
	public final static String SINGLE_COMMENT = "__d_single_comment";
	public final static String WYSIWYG_STRING = "__d_wysiwyg_string";
	public final static String HEX_STRING = "__d_hex_string";
	public final static String STRING = "__d_string";
	public final static String CHAR = "__d_char";
	
	public final static String[] LEGAL_CONTENT = new String[] {
		PLUS_DOC_COMMENT,
		MULTI_DOC_COMMENT,
		SINGLE_DOC_COMMENT,
		PLUS_COMMENT,
		MULTI_COMMENT,
		SINGLE_COMMENT,
		WYSIWYG_STRING,
		HEX_STRING,
		STRING,
		CHAR,
		IDocument.DEFAULT_CONTENT_TYPE
	};
	
	public DPartitionerScanner() {
		final IToken plusDocComment = new Token(PLUS_DOC_COMMENT);
		final IToken multiDocComment = new Token(MULTI_DOC_COMMENT);
		final IToken singleDocComment = new Token(SINGLE_DOC_COMMENT);
		final IToken plusComment = new Token(PLUS_COMMENT);
		final IToken multiComment = new Token(MULTI_COMMENT);
		final IToken singleComment = new Token(SINGLE_COMMENT);
		final IToken wysiwygString = new Token(WYSIWYG_STRING);
		final IToken hexString = new Token(WYSIWYG_STRING);
		final IToken string = new Token(STRING);
		final IToken cchar = new Token(CHAR);
		
		List<IPredicateRule> rules = new ArrayList<IPredicateRule>();

		rules.add(new NestedCommentRule("/++", "/+", "+/", plusDocComment, (char) 0, true));
		rules.add(new NestedCommentRule("/+", "/+", "+/", plusComment, (char) 0, true));
		rules.add(new MultiLineRule("/**", "*/", multiDocComment, (char) 0, true));
		rules.add(new MultiLineRule("/*", "*/", multiComment, (char) 0, true));

		rules.add(new EndOfLineRule("///", singleDocComment));
		rules.add(new EndOfLineRule("//", singleComment));
		
		rules.add(new DStringRule("x\"", "\"", hexString, (char) 0));
		
		rules.add(new DStringRule("r\"", "\"", wysiwygString, (char) 0));
		rules.add(new DStringRule("`", "`", wysiwygString, (char) 0));
		
		rules.add(new DStringRule("\"", "\"", string, '\\'));
		
		rules.add(new DEscapeRule(string));
		
		rules.add(new SingleLineRule("'", "'", cchar, '\\', true));
		
		setPredicateRules(rules.toArray(new IPredicateRule[rules.size()]));
	}

}
