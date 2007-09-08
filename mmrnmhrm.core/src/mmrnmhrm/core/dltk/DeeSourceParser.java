package mmrnmhrm.core.dltk;

import mmrnmhrm.core.DeeCorePreferences;
import mmrnmhrm.core.LangCore;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.ast.declarations.ISourceParser;
import org.eclipse.dltk.compiler.problem.IProblemReporter;

import descent.core.compiler.IProblem;
import descent.core.dom.AST;
import descent.internal.compiler.parser.Module;
import descent.internal.compiler.parser.Parser;
import dtool.descentadapter.DescentASTConverter;

import static melnorme.miscutil.Assert.assertTrue;

public class DeeSourceParser implements ISourceParser {
	
	private static final class DescentProblemAdapter implements
			descent.internal.compiler.parser.ast.IProblemReporter {

		private IProblemReporter reporter;

		public DescentProblemAdapter(IProblemReporter reporter) {
			this.reporter = reporter;
		}

		@Override
		public IMarker reportProblem(IProblem problem) {
			try {
				return reporter.reportProblem(new DLTKDescentProblemWrapper(problem));
			} catch (CoreException e) {
				LangCore.log(e);
				return null;
			}
		}

		public static descent.internal.compiler.parser.ast.IProblemReporter create(
				IProblemReporter reporter) {
			if(reporter == null)
				return null;
			return new DescentProblemAdapter(reporter);
		}
	}

	private static final DeeSourceParser instance = new DeeSourceParser();
	
	public static ISourceParser getInstance() {
		return instance;
	}
	
	/** Used by reconciler. */
	@Override
	public DeeModuleDeclaration parse(char[] fileName, char[] source,
			IProblemReporter reporter) {
		return parseModule(source, reporter, fileName);
	}

	protected static DeeModuleDeclaration parseModule(char[] source,
			IProblemReporter reporter, char[] fileName) {
		Parser parser = new Parser(AST.D2, source);
		parser.setProblemReporter(DescentProblemAdapter.create(reporter));
		Module dmdModule = null;
		try {
			dmdModule = parser.parseModuleObj();
		} catch (RuntimeException e) {
			LangCore.log(e);
			throw e;
		}
		assertTrue(dmdModule.length == source.length);
		DeeModuleDeclaration moduleDec = new DeeModuleDeclaration(dmdModule);
		if(dmdModule.hasSyntaxErrors()
				&& !DeeCorePreferences.getBoolean(DeeCorePreferences.ADAPT_MALFORMED_DMD_AST)) {
			// DontLet's try to convert a malformed AST
			return moduleDec;
		}
		dtool.dom.definitions.Module neoModule = DescentASTConverter.convertModule(dmdModule);
		moduleDec.setNeoModule(neoModule);
		return moduleDec;
	}

}