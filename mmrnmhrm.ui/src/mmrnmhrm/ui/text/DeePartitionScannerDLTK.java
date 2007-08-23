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

public class DeePartitionScannerDLTK extends RuleBasedPartitionScanner {


	/**
	 * Creates the partitioner and sets up the appropriate rules.
	 */
	public DeePartitionScannerDLTK() {
		super();
		
		IToken tkString = new Token(DeePartitions.DEE_STRING);
		IToken tkComment = new Token(DeePartitions.DEE_COMMENT);
		IToken tkDocComment = new Token(DeePartitions.DEE_DOCCOMMENT);
		
		List<IPredicateRule> rules = new ArrayList<IPredicateRule>();
		rules.add(new SingleLineRule("\"", "\"", tkString, '\\'));

		// FIXME: nesting
		rules.add(new MultiLineRule("/++", "/+", tkDocComment, (char) 0, true)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		rules.add(new MultiLineRule("/+", "/+", tkComment, (char) 0, true)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		rules.add(new MultiLineRule("/**", "*/", tkDocComment, (char) 0, true)); //$NON-NLS-1$ //$NON-NLS-2$
		rules.add(new MultiLineRule("/*", "*/", tkComment, (char) 0, true)); //$NON-NLS-1$ //$NON-NLS-2$

		rules.add(new EndOfLineRule("///", tkDocComment)); //$NON-NLS-1$ 
		rules.add(new EndOfLineRule("//", tkComment)); //$NON-NLS-1$
		
		rules.add(new SingleLineRule("\"", "\"", tkString, '\\'));

		setPredicateRules(rules.toArray(new IPredicateRule[rules.size()]));
		//setDefaultReturnToken(new Token(DeePartitions.DEE_CODE));
	}

}