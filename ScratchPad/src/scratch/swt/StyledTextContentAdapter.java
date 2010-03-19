package scratch.swt;

import org.eclipse.jface.fieldassist.IControlContentAdapter;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;


public class StyledTextContentAdapter implements IControlContentAdapter {
	

	public String getControlContents(Control control) {
		return ((StyledText) control).getText();
	}

	public int getCursorPosition(Control control) {
		return ((StyledText) control).getCaretOffset();
	}

	public Rectangle getInsertionBounds(Control control) {
		return ((StyledText) control).getCaret().getBounds();
	}

	public void insertControlContents(Control control, String contents, int cursorPosition) {
		StyledText textControl = (StyledText) control;
		String text = textControl.getText();
		cursorPosition = cursorPosition + 1;

		// Calculate the length of the insertion

		int nextSpace = text.indexOf(' ', cursorPosition);
		if(nextSpace == -1) {	
			nextSpace = text.length();

			// Add a space on the insertion

			// contents = contents + " ";
		}
		nextSpace = nextSpace == -1 ? text.length() : nextSpace;
		int length = nextSpace - cursorPosition;

		// Replace the text and move the cursor to the end of insertion

		// If the insertion of the text and the trailing space is performed as a single
		// insertion, annotation cheese can be caused under the space.  To avoid this the
		// insertion is performed as two separate transactions.
		
		textControl.replaceTextRange(cursorPosition, length, contents);
		textControl.replaceTextRange(cursorPosition + contents.length(), 0, " ");
		textControl.setCaretOffset(cursorPosition + contents.length() + 1);
	}

	public void setControlContents(Control control, String contents, int cursorPosition) {
		((StyledText) control).setText(contents);
		((StyledText) control).setCaretOffset(cursorPosition);
	}

	public void setCursorPosition(Control control, int index) {
		((StyledText) control).setCaretOffset(index);
	}

}
