package descent.internal.debug.ui.console;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.ui.console.IConsole;
import org.eclipse.debug.ui.console.IConsoleLineTracker;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.console.IHyperlink;
import org.eclipse.ui.console.IPatternMatchListener;
import org.eclipse.ui.console.PatternMatchEvent;
import org.eclipse.ui.console.TextConsole;
import org.eclipse.ui.texteditor.ITextEditor;

import descent.core.IJavaProject;
import descent.core.JavaCore;
import descent.core.compiler.IProblem;
import descent.internal.ui.javaeditor.EditorUtility;

public class DmdfeConsoleLineTracker implements IConsoleLineTracker {
	private static final class DmdfeErrorMatchListener implements IPatternMatchListener {
		private static final String ERROR_REGEX = "(\\w:)?.*?:";
		private static final String LINE_REGEX = "^(.*?)\\((\\d+)\\):";
		private static final String NO_LINE_REGEX = "^(.*?\\.di?):";

		private static Pattern linePattern = Pattern.compile(LINE_REGEX);
		private static Pattern noLinePattern = Pattern.compile(NO_LINE_REGEX);

		private TextConsole console;
		private IDocument doc;
		private ResourceSearch resourceSearch;
		private IJavaProject targetProject;

		public void connect(TextConsole console) {
			this.console = console;
			this.doc = console.getDocument();
		}

		public void disconnect() {
		}

		public String getLineQualifier() {
			return null;
		} // PERHAPS

		public int getCompilerFlags() {
			return 0;
		}

		public String getPattern() {
			return ERROR_REGEX;
		}

		public DmdfeErrorMatchListener(IJavaProject targetProject) {
			this.resourceSearch = new ResourceSearch(targetProject);
		}

		public void matchFound(PatternMatchEvent event) {
			try {
				int matchOfs = event.getOffset();
				int matchLength = event.getLength();
				String text = doc.get(matchOfs, matchLength);

				String file;
				int line;
				Matcher split = linePattern.matcher(text);
				if (split.matches() && split.groupCount() == 2) {
					file = split.group(1);
					line = Integer.parseInt(split.group(2));
				} else {
					split = noLinePattern.matcher(text);
					if (!split.matches() || split.groupCount() != 1)
						return;
					file = split.group(1);
					line = 0;
				}

				console.addHyperlink(new DLocationHyperlink(resourceSearch, file, line), matchOfs, matchLength - 1);
			} catch (Exception e) {
				// TODO
			}
		}
	}

	private static final class DLocationHyperlink implements IHyperlink {
		private final String filename;

		private final int line;

		private boolean doneSearch = false;

		private Object cached = null;

		private ResourceSearch resourceSearch;

		public DLocationHyperlink(ResourceSearch resourceSearch, String filename, int line) {
			this.resourceSearch = resourceSearch;
			this.filename = filename;
			this.line = line;
		}

		public void linkEntered() {
		}

		public void linkExited() {
		}

		public void linkActivated() {
			try {
				if (!doneSearch) {
					cached = resourceSearch.search(filename);
					doneSearch = true;
				}
				if (null == cached)
					return;
				ITextEditor editor = (ITextEditor) EditorUtility.openInEditor(cached);
				if (line > 0) {
					if (null == editor)
						return;
					IDocument document = editor.getDocumentProvider().getDocument(editor.getEditorInput());
					if (null == document)
						return;
					editor.selectAndReveal(document.getLineOffset(line - 1), document.getLineLength(line - 1));
				}
			} catch (Exception e) {
				// TODO
			}
		}
	}

	public void init(IConsole console) {
		// Get the working directory configured for the launch and see
		// if any project starts with that path. Then, set the target project to that one
		// so to start the search of the targeted link from there.
		IJavaProject targetProject = null;
		IStringVariableManager varManager = VariablesPlugin.getDefault().getStringVariableManager();
		IProcess process = console.getProcess();
		ILaunch launch = process.getLaunch();
		ILaunchConfiguration launchConfiguration = launch.getLaunchConfiguration();
		try {
			String workingDirectoryExpression = launchConfiguration.getAttribute("org.eclipse.ui.externaltools.ATTR_WORKING_DIRECTORY", (String)null);
			if (workingDirectoryExpression != null) {
				String workingDirectory = varManager.performStringSubstitution(workingDirectoryExpression);
				for(IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
					if (project.isOpen() && workingDirectory.startsWith(project.getLocation().toOSString())) {
						targetProject = JavaCore.create(project);
						break;
					}
				}
			}
		} catch (CoreException e) {
			
		}
		
		console.addPatternMatchListener(new DmdfeErrorMatchListener(targetProject));
		
		
	}

	public void dispose() {
	}

	public void lineAppended(IRegion line) {
	}
}
