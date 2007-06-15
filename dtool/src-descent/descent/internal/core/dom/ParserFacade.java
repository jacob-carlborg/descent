package descent.internal.core.dom;

import java.util.List;

import descent.core.dom.IDeclaration;
import descent.core.dom.IExpression;
import descent.core.dom.IInitializer;
import descent.core.dom.IDescentStatement;

public class ParserFacade {
	
	public Parser parseCompilationUnit(String source) {
		Parser parser = new Parser(source);
		List<IDeclaration> declDefs = parser.parseModule();
		
		parser.mod.members = declDefs;
		parser.mod.startPos = 0;
		parser.mod.length = source.length();
		
		if (parser.mod.md != null) {
			parser.mod.ident = parser.mod.md.ident;
		}
		
		//parser.mod.semantic(null, parser);
		
		parser.mod.problems = parser.problems;
		
		//parser.freelist
		
		return parser;
	}
	
	public IExpression parseExpression(String source) {
		Parser parser = new Parser(source);
		return parser.parseExpression();
	}
	
	public IDescentStatement parseStatement(String source) {
		Parser parser = new Parser(source);
		return parser.parseStatement(0);
	}
	
	public IInitializer parseInitializer(String source) {
		Parser parser = new Parser(source);
		return parser.parseInitializer();
	}

}
