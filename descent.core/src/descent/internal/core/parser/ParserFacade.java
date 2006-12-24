package descent.internal.core.parser;

import java.util.List;

import descent.core.dom.CompilationUnit;
import descent.core.dom.Declaration;
import descent.core.dom.Expression;
import descent.core.dom.IParser;
import descent.core.dom.Initializer;
import descent.core.dom.Parser;
import descent.core.dom.Statement;

public class ParserFacade implements IParser {
	
	public CompilationUnit parseCompilationUnit(String source) {
		Parser parser = new Parser(source);
		List<Declaration> declDefs = parser.parseModule();
		
		if (declDefs != null) {
			parser.mod.declarations().addAll(declDefs);
		}
		parser.mod.setSourceRange(0, source.length());
		
		/* TODO
		if (parser.mod.md != null) {
			parser.mod.ident = new Identifier(parser.mod.md.getName().getFullyQualifiedName(), TOK.TOKstring);
		}
		*/
		
		parser.mod.problems = parser.problems;
		
		return parser.mod;
	}
	
	public Expression parseExpression(String source) {
		Parser parser = new Parser(source);
		return parser.parseExpression();
	}
	
	public Statement parseStatement(String source) {
		Parser parser = new Parser(source);
		return parser.parseStatement(0);
	}
	
	public Initializer parseInitializer(String source) {
		Parser parser = new Parser(source);
		return parser.parseInitializer();
	}

}
