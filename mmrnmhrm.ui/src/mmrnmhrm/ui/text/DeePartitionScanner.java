package mmrnmhrm.ui.text;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;

public class DeePartitionScanner extends RuleBasedPartitionScanner {


	/**
	 * Creates the partitioner and sets up the appropriate rules.
	 */
	public DeePartitionScanner() {
		super();
		
		IToken tkString = new Token(DeePartitions.DEE_STRING);
		IToken tkSingleComment = new Token(DeePartitions.DEE_SINGLE_COMMENT);
		IToken tkSingleDocComment = new Token(DeePartitions.DEE_SINGLE_DOCCOMMENT);
		IToken tkMultiComment = new Token(DeePartitions.DEE_MULTI_COMMENT);
		IToken tkMultiDocComment = new Token(DeePartitions.DEE_MULTI_DOCCOMMENT);
		IToken tkNestedComment = new Token(DeePartitions.DEE_NESTED_COMMENT);
		IToken tkNestedDocComment = new Token(DeePartitions.DEE_NESTED_DOCCOMMENT);
		
		List<IPredicateRule> rules = new ArrayList<IPredicateRule>();
		rules.add(new SingleLineRule("\"", "\"", tkString, '\\'));

		// FIXME: nesting
		rules.add(new MultiLineRule("/++", "+/", tkNestedDocComment, (char) 0, true)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		rules.add(new MultiLineRule("/+", "+/", tkNestedComment, (char) 0, true)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		rules.add(new MultiLineRule("/**", "*/", tkMultiDocComment, (char) 0, true)); //$NON-NLS-1$ //$NON-NLS-2$
		rules.add(new MultiLineRule("/*", "*/", tkMultiComment, (char) 0, true)); //$NON-NLS-1$ //$NON-NLS-2$

		rules.add(new EndOfLineRule("///", tkSingleDocComment)); //$NON-NLS-1$ 
		rules.add(new EndOfLineRule("//", tkSingleComment)); //$NON-NLS-1$
		
		rules.add(new SingleLineRule("\"", "\"", tkString, '\\'));

		IPredicateRule[] resultRules = rules.toArray(new IPredicateRule[rules.size()]);
		setPredicateRules(resultRules);
		//setDefaultReturnToken(new Token(DeePartitions.DEE_CODE));
	}

}