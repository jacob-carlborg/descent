package descent.internal.core.search.indexing;

import descent.internal.compiler.ISourceElementRequestor;
import descent.internal.compiler.SourceElementParser;
import descent.internal.compiler.impl.CompilerOptions;
import descent.internal.compiler.parser.Module;
import descent.internal.compiler.parser.Parser;

// TODO JDT stub
public class IndexingParser extends SourceElementParser {

	public IndexingParser(ISourceElementRequestor requestor, CompilerOptions options) {
		super(requestor, options);
	}
	
	public Module parseCompilationUnit(descent.internal.compiler.env.ICompilationUnit unit, boolean resolveBindings) {
		char[] contents = unit.getContents();
		Parser parser = new Parser(contents, 0, contents.length, false, false, false, false, getASTlevel(), null, null, false, unit.getFileName());
		parser.nextToken();
		
		module = parser.parseModuleObj();
	
		requestor.enterCompilationUnit();
		module.accept(this);
		requestor.exitCompilationUnit(endOf(module));
		
		return module;
	}

}
