package descent.internal.debug.ui.console;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.debug.ui.console.IConsole;
import org.eclipse.debug.ui.console.IConsoleLineTracker;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.console.IHyperlink;
import org.eclipse.ui.console.IPatternMatchListener;
import org.eclipse.ui.console.PatternMatchEvent;
import org.eclipse.ui.console.TextConsole;
import org.eclipse.ui.texteditor.ITextEditor;

import descent.internal.ui.javaeditor.EditorUtility;

public class DmdfeConsoleLineTracker implements IConsoleLineTracker
{
	private static final class DmdfeErrorMatchListener implements IPatternMatchListener
	{
		private static final String ERROR_REGEX = ".*?\\(\\d+\\):";
		private static final String SPLITTER_REGEX = "(.*?)\\((\\d+)\\)";
		private static Pattern splitterPattern = Pattern.compile(SPLITTER_REGEX);
		
		private TextConsole console;
		private IDocument doc;
		
		public void connect(TextConsole console) { this.console = console; this.doc = console.getDocument(); }
		public void disconnect() { }
		public String getLineQualifier() { return null; } // PERHAPS
		public int getCompilerFlags() { return 0; }
		public String getPattern() { return ERROR_REGEX; }

		public void matchFound(PatternMatchEvent event)
		{
			try
			{
				int linkOfs = event.getOffset();
				int linkLength = event.getLength() - 1;
				String text = doc.get(linkOfs, linkLength);
				Matcher split = splitterPattern.matcher(text);
				if(!split.matches() || split.groupCount() != 2)
					return;
				String file = split.group(1);
				int line = Integer.parseInt(split.group(2));
				console.addHyperlink(new DLocationHyperlink(file, line), linkOfs, linkLength);
			}
			catch(Exception e)
			{
				// TODO
			}
		}
	}
	
	private static final class DLocationHyperlink implements IHyperlink
	{
		private final String filename;
		private final int line;
		private boolean doneSearch = false;
		private IFile cached = null;
		
		public DLocationHyperlink(String filename, int line)
		{
			this.filename = filename;
			this.line = line;
		}
		
		public void linkEntered() { }
		public void linkExited() { }
		
		public void linkActivated()
		{
			try
			{
				if(!doneSearch)
				{
					cached = ResourceSearch.search(filename);
					doneSearch = true;
				}
				if(null == cached)
					return;
				ITextEditor editor = (ITextEditor) EditorUtility.openInEditor(cached);
				if(null == editor)
					return;
				IDocument document = editor.getDocumentProvider().getDocument(editor.getEditorInput());
				if(null == document)
					return;
				editor.selectAndReveal(document.getLineOffset(line - 1), document.getLineLength(line - 1));
			}
			catch (Exception e)
			{
				// TODO
			}
		}
	}
	
	public void init(IConsole console) { console.addPatternMatchListener(new DmdfeErrorMatchListener()); }
	public void dispose() { }
	public void lineAppended(IRegion line) { }
}
