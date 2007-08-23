package mmrnmhrm.core.dltk;

import org.eclipse.dltk.ast.declarations.ISourceParser;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.compiler.problem.IProblemReporter;

import descent.core.dom.AST;
import descent.internal.compiler.parser.Module;
import descent.internal.compiler.parser.Parser;
import dtool.descentadapter.DescentASTConverter;

public class DeeSourceParser implements ISourceParser {
	
	private static final DeeSourceParser instance = new DeeSourceParser();
	
	public static ISourceParser getInstance() {
		return instance;
	}
	
	/** Used by reconciler. */
	@Override
	public ModuleDeclaration parse(char[] fileName, char[] source,
			IProblemReporter reporter) {
		return parseModule(source, reporter, fileName);
	}

	@SuppressWarnings("unchecked")
	protected static ModuleDeclaration parseModule(char[] source,
			IProblemReporter reporter, char[] fileName) {
		Parser parser = new Parser(AST.D2, source);
		Module dmdModule = parser.parseModuleObj();
		ModuleDeclaration moduleDec = new ModuleDeclaration(source.length);
		if(dmdModule.problems.size() != 0) {
			//Let's try to parse anyway
		}
		dtool.dom.definitions.Module neoModule = DescentASTConverter.convertModule(dmdModule);

		moduleDec.getStatements().add(neoModule);
		return moduleDec;
	}



}
