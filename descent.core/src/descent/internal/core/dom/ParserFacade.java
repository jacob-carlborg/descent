package descent.internal.core.dom;

import java.util.List;

import descent.core.dom.ICompilationUnit;
import descent.core.dom.IDElement;
import descent.core.dom.IExpression;
import descent.core.dom.IInitializer;
import descent.core.dom.IParser;
import descent.core.dom.IStatement;

public class ParserFacade implements IParser {
	
	public ICompilationUnit parseCompilationUnit(String source) {
		Parser parser = new Parser(source);
		List<IDElement> declDefs = parser.parseModule();
		
		parser.mod.members = declDefs;
		parser.mod.start = 0;
		parser.mod.length = source.length();
		
		if (parser.mod.md != null) {
			parser.mod.ident = parser.mod.md.id;
		}
		
		//parser.mod.semantic(null, parser);
		
		parser.mod.problems = parser.problems;
		
		return parser.mod;
	}
	
	public IExpression parseExpression(String source) {
		Parser parser = new Parser(source);
		return parser.parseExpression();
	}
	
	public IStatement parseStatement(String source) {
		Parser parser = new Parser(source);
		return parser.parseStatement(0);
	}
	
	public IInitializer parseInitializer(String source) {
		Parser parser = new Parser(source);
		return parser.parseInitializer();
	}

}
