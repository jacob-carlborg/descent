package dtool.ANTLRparser;
import java.io.FileReader;

import org.antlr.runtime.ANTLRReaderStream;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTree;

import util.ExceptionAdapter;
import util.StringUtil;
import dtool.Main;
import dtool.parser.DParser;
import dtool.parser.DParserLexer;
import dtool.project.CompilationUnit;


public class Model {
	
	public static CommonTokenStream tokens;
	public static DParser.dmodule_return root;
	
	public static void createModel(CompilationUnit cu) {
		try {
			FileReader fr = new java.io.FileReader(cu.file);
			ANTLRStringStream afs = new ANTLRReaderStream(fr);
			DParserLexer lex = new DParserLexer(afs);

			tokens = new CommonTokenStream(lex);
			System.out.println("=== Tokens ===");
			System.out.print( tokens.toString());
			System.out.println("----");
			for ( Object tokenobj : tokens.getTokens() ) {
				Token token = (Token) tokenobj;
				System.out.println( token);
			}
			
			DParser parser = new DParser(tokens);
			System.out.println("Parsing...");
			root = parser.dmodule();
			System.out.println("Parsed.");
			System.out.flush();
		} catch (Exception e) {
			throw new ExceptionAdapter(e);
		}
	}
	
	public static void printModel() {

		CommonTree tree = (CommonTree)Model.root.getTree();
		System.out.println("= Tree Model =");
		System.out.println("=> toStringTree:");
		System.out.println(tree.toStringTree());
		System.out.println("=> Tree:");
		printTreeElem(tree, 0);

	}
	
	public static void printTreeElem(CommonTree tree, int indent) {
		System.out.println(tree.toString() + " [ " + tree.getType() + " ] ");
		
		for ( int i = 0; i < tree.getChildCount(); i++ ) {
			System.out.print(StringUtil.newFilledString(indent,"  "));
			printTreeElem((CommonTree)tree.getChild(i),indent+1);
		}
		
	}

	public static void testDtool(String[] args) {
		System.out.println("== ANTLR Parsing... ==");
		
		createModel(Main.dproj.testcu);
		printModel();
		//Engine.testRefactor();
	}


}
