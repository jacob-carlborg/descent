package descent.tests.format;

import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.TextEdit;

import descent.core.ToolFactory;
import descent.core.formatter.CodeFormatter;

public abstract class AbstractFormatter_Test extends TestCase {
	
	protected void assertFormat(String expected, String original) throws Exception {
		assertFormat(expected, original, null);
	}
	
	protected void assertFormat(String expected, String original, Map options) throws Exception {
		Document document = new Document(original);
		
		CodeFormatter formatter =  ToolFactory.createCodeFormatter(null);
		TextEdit edit = formatter.format(CodeFormatter.K_COMPILATION_UNIT, original, 0, original.length(), 0, null);
		edit.apply(document);
		
		String string = document.get();
		int lastNotSpace;
		for(lastNotSpace = string.length() - 1; lastNotSpace >= 0 && Character.isWhitespace(string.charAt(lastNotSpace)); lastNotSpace--) {
		}
		string = string.substring(0, lastNotSpace + 1);
		
		assertEquals(expected, string);
	}

}
