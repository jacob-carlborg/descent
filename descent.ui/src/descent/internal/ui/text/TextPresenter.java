package descent.internal.ui.text;

import java.io.Reader;
import java.io.StringReader;

import org.eclipse.jface.text.TextPresentation;

public class TextPresenter extends HTMLTextPresenter {
	
	@Override
	protected Reader createReader(String hoverInfo, TextPresentation presentation) {
		return new StringReader(hoverInfo);
	}
	
}


