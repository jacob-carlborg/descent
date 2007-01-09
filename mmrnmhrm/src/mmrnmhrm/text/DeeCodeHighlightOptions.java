package mmrnmhrm.text;

import mmrnmhrm.ui.preferences.ColorConstants;
import mmrnmhrm.ui.preferences.ColorManager;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

import descent.internal.core.dom.Lexer;
import descent.internal.core.dom.TOK;

public class DeeCodeHighlightOptions 
{
	private static DeeCodeHighlightOptions defaultInstance;
	
	static {
		defaultInstance = new DeeCodeHighlightOptions();
	}

	public static DeeCodeHighlightOptions getDefault() {
		return defaultInstance;
	}

	private Token attribDefault;
	private Token attribKeyword;
	private Token attribString;
	private Token attribComment;
	
    public DeeCodeHighlightOptions()
    {
    	ColorManager colorManager = ColorManager.getInstance();
    	Color color, bgcolor;
    	TextAttribute textAttribute;
    	
    	color = colorManager.getColor(new RGB(0,0,0));
        textAttribute = new TextAttribute(color);
        attribDefault = new Token(textAttribute);
    
    	color = colorManager.getColor(ColorConstants.XXXBLUE);
    	bgcolor = colorManager.getColor(ColorConstants.WHITE);
    	textAttribute = new TextAttribute(color, bgcolor, SWT.BOLD);
        attribKeyword = new Token(textAttribute);   
        
    	color = colorManager.getColor(ColorConstants.XXXGREEN);
    	//bgcolor = colorManager.getColor(ColorConstants.BLACK);
    	textAttribute = new TextAttribute(color);
        attribString = new Token(textAttribute);
        
    	color = colorManager.getColor(ColorConstants.XXXGRAY);
    	//bgcolor = colorManager.getColor(ColorConstants.BLACK);
    	textAttribute = new TextAttribute(color);
    	attribComment = new Token(textAttribute); 
    }

	public IToken getAttributes(TOK tok) {
		if( Lexer.keywords.containsValue(tok))
			return attribKeyword;
		else if(tok == TOK.TOKstring)
			return attribString;
		else if(tok == TOK.TOKcomment)
			return attribComment;
		else
			return attribDefault;
	}

}
