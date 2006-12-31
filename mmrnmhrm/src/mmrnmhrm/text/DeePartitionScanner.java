package mmrnmhrm.text;

import org.eclipse.jface.text.rules.*;

public class DeePartitionScanner extends RuleBasedPartitionScanner {


	public DeePartitionScanner() {

		IToken deeDefault = new Token(EDeePartitions.DEE_DEFAULT);
		//IToken deeDoc = new Token(EDeePartitions.DEE_DOC);

		IPredicateRule[] rules = new IPredicateRule[1];
		// FIXME: eclipse bug??
		//rules[0] = new MultiLineRule("", "DUMMY FIXME", deeDefault,(char) 0, true);
		rules[0] = new PatternRule("m", "##DUMMY FIXME", deeDefault, (char)0, false, true);

		setPredicateRules(rules);
	}
}
