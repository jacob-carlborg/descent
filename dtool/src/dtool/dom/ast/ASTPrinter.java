package dtool.dom.ast;

import melnorme.miscutil.tree.TreeDepthRecon;
import descent.internal.compiler.parser.ast.ASTNode;
import descent.internal.compiler.parser.ast.IASTNode;
import dtool.dom.references.RefQualified;

/**
 * Simple class for printing the AST in indented tree form.
 * DMD AST nodes are printed with a "#" prefix.
 */
public class ASTPrinter extends ASTNeoUpTreeVisitor {

	/** => #toStringNodeExtra(node, true) */
	public static String toStringNodeExtra(IASTNode node) {
		return toStringNodeExtra(node, true);
	}

	/** Gets an extended String representation of given node. */
	public static String toStringNodeExtra(IASTNode node, boolean printRangeInfo) {
		return node.toStringAsNode(printRangeInfo) + " " + node.toString();
	}
	
	/** #toStringAST(elem, true) */
	public static String toStringAST(ASTNode elem) {
		return toStringAST(elem, true);
	}	

	/** Gets a String representation of the whole AST tree. */
	public static String toStringAST(ASTNode elem, boolean recurseUnconverted) {
		ASTPrinter astPrinter = new ASTPrinter();
		astPrinter.recurseUnconverted = recurseUnconverted;
		return astPrinter.getStringRep(elem);
	}

//	private static ASTPrinter singletonPrinter = new ASTPrinter();
	
	/** Gets a String representation of given node. */
/*	public static String toStringElement(ASTNode node) {
		// use singleton for optimization purposes
		singletonPrinter.visitChildren = false;
		singletonPrinter.strbuffer = new StringBuffer();
		return singletonPrinter.getStringRep(node);
	}
*/
	
	// print source range
	public boolean printRangeInfo = true;
	//Print leaf nodes in same line?
	public boolean collapseLeafs = false; 
	//recurse UncovertedElements?	
	public boolean recurseUnconverted = true; 
	// visit the Element's children
	public boolean visitChildren = true;
	// visit children of node type QualifiedName
	public boolean visitQualifiedNameChildren = false;

	private int indent;
	private boolean allSiblingsAreLeafs;
	
	
	// A string buffer to where the string representation is written 
	protected StringBuffer strbuffer;
	
	private ASTPrinter() {
		this.indent = 0;
		this.strbuffer = new StringBuffer();
	}
	/*private ASTPrinter(boolean collapseLeafs) {
		this();
		this.collapseLeafs = collapseLeafs;
	}*/

	
	/** Gets a String represesention according to this printer */
	public String getStringRep(ASTNode elem) {
		elem.accept(this);
		return strbuffer.toString();
	}

	
	/** Gets a String representation of elem only, with extra info. */
	private String toStringElementExtra(IASTNode elem) {
		return toStringNodeExtra(elem, printRangeInfo);
	}
	
	/* ---------------------------------- */
	protected void print(String str) {
		strbuffer.append(str);
	}
	protected void println(String str) {
		strbuffer.append(str);
		strbuffer.append("\n");
	}	

	
	private void printIndent() {
		print(melnorme.miscutil.StringUtil.newFilledString(indent, "  "));
	}

	private void printGenericElement(IASTNode element, String str) {
		int maxdepth = collapseLeafs? TreeDepthRecon.findMaxDepth(element) : -1;

		if(collapseLeafs && maxdepth == 1 && allSiblingsAreLeafs)
			print("  ");
		else
			printIndent();
		
		print(str);
		
		if(collapseLeafs && maxdepth == 2) {
			allSiblingsAreLeafs = true;
			print("    (");
		} else if(collapseLeafs && maxdepth == 1 && allSiblingsAreLeafs) {
			
		} else {
			println("");
		}
		indent++;
	}
	
	/* ====================================================== */

	public boolean visit(ASTNode elem) {
		printGenericElement(elem, toStringElementExtra(elem));
		return visitChildren && recurseUnconverted;
	}

	
	/* ---------------- Neo ------------------ */
	
	public boolean visit(ASTNeoNode elem) {
		printGenericElement(elem, toStringElementExtra(elem) );
		return visitChildren;
	}
	
	public boolean visit(RefQualified elem) {
		printGenericElement(elem, toStringElementExtra(elem));
		return visitChildren && visitQualifiedNameChildren;
	}
	
	
	/* ---------------------------------- */
	
	public void endVisit(ASTNode element) {
		
		if(collapseLeafs && TreeDepthRecon.findMaxDepth(element) == 2) {
			allSiblingsAreLeafs = false;
			println(" )");
		}

		indent--;
	}

}

