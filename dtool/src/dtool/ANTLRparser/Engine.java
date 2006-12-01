package dtool.ANTLRparser;
import java.util.ArrayList;
import java.util.List;

import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTree;

import dtool.parser.DParserLexer;




public class Engine {

static Token token;
static List<Token> refactortokens;



	public static void testRefactor() {
		token = (Token) Model.tokens.getTokens().get(3);
		System.out.println(">token: " + token.toString());

		
		CommonTree tree = (CommonTree)Model.root.getTree();
		System.out.println("=tree2=");
		
		refactortokens = new ArrayList<Token>();
		
		buildRefactorings(tree);

		rewriteTokenStream();
	}
	


	public static void buildRefactorings(CommonTree tree) {
		System.out.print(tree.toString());
		System.out.println(" " + tree.getLine() + ":" +  tree.getCharPositionInLine()); 

		if(tree.getToken() == token) {
			System.out.print("THIS!");
		}

		if(tree.getToken().getType() == DParserLexer.PLUS ) {
			refactortokens.add(tree.getToken());
		}
		
		for ( int i = 0; i < tree.getChildCount(); i++ ) {
			buildRefactorings((CommonTree)tree.getChild(i));
		}
		
/*		// Sort by file location
		Tree[] treear = new Tree[tree.getChildCount()];
		for ( int i = 0; i < tree.getChildCount(); i++ ) {
			treear[i] = tree.getChild(i);
		}

		java.util.Arrays.sort(treear, new Comparator<Tree>() {
			public int compare(Tree o1, Tree o2) {
				if ( o1.getLine() != o2.getLine())
					return o1.getLine() - o2.getLine();
				else
					return o1.getCharPositionInLine() - o2.getCharPositionInLine();
			}
		});
*/
	}
	
	public static void rewriteTokenStream() {
		int i;
		
		
		for (Object tokenobj : Model.tokens.getTokens()) {
			Token token = (Token) tokenobj;
			
			if(token.getText().equals("2"))
				System.out.print("TWO");
			else if (token.getText().equalsIgnoreCase("2"))
				System.out.print("two");
			else 
				System.out.print(token.getText());
		}
		
		System.out.println();
	}

}
