package mmrnmhrm.text;

import mmrnmhrm.ui.preferences.ColorConstants;
import mmrnmhrm.ui.preferences.ColorManager;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.PatternRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.swt.graphics.Color;

public class DeeCodeScanner extends RuleBasedScanner
{
	
    public DeeCodeScanner(ColorManager colorManager)
    {
        Color color = colorManager.getColor(ColorConstants.XXXBLUE);
        TextAttribute textAttribute = new TextAttribute(color);
        IToken textDefault = new Token(textAttribute);
    
        IRule[] rules = new IRule[1];
    
		// FIXME: eclipse bug??
		//rules[0] = new MultiLineRule("", "DUMMY FIXME", deeDefault,(char) 0, true);
		rules[0] = new PatternRule("m", "##DUMMY FIXME", textDefault, (char)0, false, true);

        setRules(rules);

        Color color2 = colorManager.getColor(ColorConstants.XXXRED);
        TextAttribute textAttribute2 = new TextAttribute(color2);
        Token token = new Token(textAttribute2);
        setDefaultReturnToken(token);
    }
}
