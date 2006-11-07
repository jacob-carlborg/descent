package descent.ui.text.scanners;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.swt.graphics.RGB;

import descent.ui.IDColorConstants;
import descent.ui.text.ColorManager;
import descent.ui.text.rules.DKeywordRule;
import descent.ui.text.rules.DOperatorRule;

public class DCodeScanner extends RuleBasedScanner {

	public DCodeScanner(ColorManager manager) {
		List<IRule> rules = new ArrayList<IRule>();
		
		rules.add(new DKeywordRule(newToken(manager, IDColorConstants.KEYWORD),
				newToken(manager, IDColorConstants.DEFAULT)));
		rules.add(new DOperatorRule(newToken(manager, IDColorConstants.OPERATOR)));
		// rules.add(new DNumberRule(newToken(manager, IDColorConstants.NUMBER)));
		// rules.add(new DEscapeSequenceRule(newToken(manager, IDColorConstants.STRING)));
		
		// Add generic whitespace rule.
		rules.add(new WhitespaceRule(new DWhitespaceDetector()));
		
		setRules(rules.toArray(new IRule[rules.size()]));
	}
	
	private static IToken newToken(ColorManager manager, RGB color) {
		return new Token(
				new TextAttribute(
					manager.getColor(color)));
	}
	
}
