package mmrnmhrm.core.dltk;

import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.model.DeeNature;

import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.compiler.task.ITaskReporter;
import org.eclipse.dltk.compiler.task.TodoTaskAstParser;
import org.eclipse.dltk.compiler.task.TodoTaskPreferences;
import org.eclipse.dltk.core.AbstractSourceElementParser;
import org.eclipse.dltk.core.SourceParserUtil;
import org.eclipse.dltk.core.ISourceModuleInfoCache.ISourceModuleInfo;

public class DeeSourceElementParser extends AbstractSourceElementParser {
 
	public DeeSourceElementParser() {
	}

	@Override
	protected String getNatureId() {
		return DeeNature.NATURE_ID;
	}
	
	public void parseSourceModule(char[] contents, ISourceModuleInfo astCache, char[] filename) {

		ModuleDeclaration moduleDeclaration = SourceParserUtil.getModuleDeclaration(filename,
				contents, getNatureId(), getProblemReporter(), astCache);
		
		DeeModuleDeclaration deeModuleDecl = (DeeModuleDeclaration) moduleDeclaration;

		DeeSourceElementProvider provider = new DeeSourceElementProvider(getRequestor());
		provider.provide(deeModuleDecl);

		if (getProblemReporter() != null) {
			final ITaskReporter taskReporter = (ITaskReporter) getProblemReporter()
					.getAdapter(ITaskReporter.class);
			if (taskReporter != null) {
				taskReporter.clearTasks();
				parseTasks(taskReporter, contents, moduleDeclaration);
			}
		}
	}

	// TODO, make our own tasks parser.
	protected void parseTasks(ITaskReporter taskReporter, char[] content,
			ModuleDeclaration moduleDeclaration) {
		final TodoTaskPreferences preferences = new TodoTaskPreferences(DeeCore.getInstance()
				.getPluginPreferences());
		if (preferences.isEnabled()) {
			final TodoTaskAstParser taskParser = new TodoTaskAstParser(taskReporter, preferences,
					moduleDeclaration);
			if (taskParser.isValid()) {
				taskParser.parse(content);
			}
		}
	}


}
