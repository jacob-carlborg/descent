package dtool.dom.ast;

import util.tree.TreeDepthRecon;
import dtool.dom.base.Entity;

/**
 * Simple class for printing the AST in indented tree form.
 * DMD AST nodes are printed with a "#" prefix.
 */
public class ASTPrinter extends ASTNeoUpTreeVisitor {
	
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
	
	private static ASTPrinter singletonPrinter = new ASTPrinter();
	
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

	
	public ASTPrinter setOptions(
			boolean printRangeInfo, 
			boolean collapseLeafs, 
			boolean recurseUnconverted, 
			boolean visitChildren, 
			boolean visitQualifiedNameChildren) {
		this.printRangeInfo = printRangeInfo;
		this.collapseLeafs = collapseLeafs;
		this.recurseUnconverted = recurseUnconverted;
		this.visitChildren = visitChildren;
		this.visitQualifiedNameChildren = visitQualifiedNameChildren;
		return this;
	}

	/** Gets a String represesention according to this printer */
	public String getStringRep(ASTNode elem) {
		elem.accept(this);
		return strbuffer.toString();
	}

	/** Gets a String representation of the whole elem tree. */
	public static String toStringAST(ASTNode elem) {
		ASTPrinter astPrinter = new ASTPrinter();
		return astPrinter.getStringRep(elem);
	}	

	/** Gets a String representation of the whole elem tree, but don't recurse 
	 * unconverted AST nodes. */
	public static String toStringNeoAST(ASTNode elem) {
		ASTPrinter astPrinter = new ASTPrinter();
		astPrinter.recurseUnconverted = false;
		return astPrinter.getStringRep(elem);
	}	
	
	/** Gets a String representation of the single elem only. */
	public static String toStringElement(ASTNode elem) {
		// use singleton for optimization purposes
		singletonPrinter.visitChildren = false;
		singletonPrinter.strbuffer = new StringBuffer();
		return singletonPrinter.getStringRep(elem);
	}
	
	/** Gets a String representation of elem only, with extra info. */
	private String toStringElementExtra(ASTNode elem) {
		String name = elem.toStringClassName();
		if(printRangeInfo) 
			name += " [" + elem.startPos+"+"+elem.length+"]";
		return name;
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
		print(util.StringUtil.newFilledString(indent, "  "));
	}

	private void printGenericElement(ASTNode element, String str) {
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
		printGenericElement(elem, "#"+toStringElementExtra(elem) + "");
		return visitChildren && recurseUnconverted;
	}

	public boolean visit(descent.internal.core.dom.Identifier elem) {
		printGenericElement(elem, "#"+toStringElementExtra(elem) 
				+ " " + elem.string);
		return visitChildren && recurseUnconverted;
	}
	
	public boolean visit(descent.internal.core.dom.QualifiedName elem) {
		printGenericElement(elem, "#"+toStringElementExtra(elem) 
				+ " " + elem.toString());
		return visitChildren && recurseUnconverted;
	}	
	
	public boolean visit(descent.internal.core.dom.Dsymbol elem) {
		printGenericElement(elem, "#"+toStringElementExtra(elem) 
				+ " " + ((elem.ident != null) ? elem.ident.string : "<null>"));
		return visitChildren && recurseUnconverted;
	}	
	
	public boolean visit(descent.internal.core.dom.Type elem) {
		printGenericElement(elem, "#"+toStringElementExtra(elem) 
				+ " TYPE: " + elem.toString());
		return visitChildren && recurseUnconverted;
	}	
	
	public boolean visit(descent.internal.core.dom.TypeQualified elem) {
		printGenericElement(elem, "#"+toStringElementExtra(elem) 
				+ " TYPE: " + elem.toString());
		return visitChildren && recurseUnconverted;
	}
	
	/* ---------------- Neo ------------------ */
	
	public boolean visit(ASTNeoNode elem) {
		printGenericElement(elem, toStringElementExtra(elem) +" "+ elem);
		return visitChildren;
	}
	
	public boolean visit(Entity.QualifiedEnt elem) {
		printGenericElement(elem, toStringElementExtra(elem) 
				+ " ID: " + elem.toString());
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

