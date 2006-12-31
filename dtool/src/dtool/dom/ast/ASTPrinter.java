package dtool.dom.ast;

import dtool.dom.base.ASTElement;
import dtool.dom.base.ASTNode;
import dtool.dom.base.DefUnit;
import dtool.dom.base.Def_Modifiers;
import dtool.dom.base.Definition;
import dtool.dom.base.Entity;
import dtool.dom.base.EntitySingle;

/**
 * Simple class for printing the AST in indented tree form.
 * DMD AST nodes are printed with a "#" prefix.
 */
public class ASTPrinter extends ASTNeoVisitor {
	
	public boolean collapseLeafs = true; //Print leaf nodes in same line?
	public boolean recurseUnconverted = true; //recurse UncovertedElements?
	public boolean visitChildren = true; // visit the Element's children
	public boolean visitQualifiedNameChildren = false; 

	public boolean printRangeInfo = true;

	
	int indent;
	boolean siblingsAreLeafs;
	
	public ASTPrinter() {
		this.indent = 0;
	}
	public ASTPrinter(boolean collapseLeafs) {
		this();
		this.collapseLeafs = collapseLeafs;
	}
	public ASTPrinter(boolean collapseLeafs, boolean printUnconverted) {
		this(collapseLeafs);
		this.recurseUnconverted = printUnconverted;
	}
	
	private void print(String str) {
		System.out.print(str);
	}
	private void println(String str) {
		System.out.println(str);
	}	

	private String trailString(String str, String strtrail) {
		return util.StringUtil.trailString(str, strtrail);
	}
	
	public static String trimmedElementName(Object elem) {
		return elem.getClass().getName().replaceAll("^.*\\.dom\\.", "");
	}
	
	private String toStringExtra(ASTNode elem) {
		String name = elem.nodeToString();
		if(printRangeInfo) 
			name += " [" + elem.startPos+"+"+elem.length+"]";
		return name;
	}

	private static ASTPrinter singleElemPrinter;
	static {
		singleElemPrinter = new ASTPrinter(false);
		singleElemPrinter.visitChildren = false;
		singleElemPrinter.indent = 0;
	}
	public static void printSingleElement(ASTNode elem) {
		elem.accept(singleElemPrinter);
	}
	
	/* ---------------------------------- */
	
	private void printIndent() {
		print(util.StringUtil.newFilledString(indent, "  "));
	}

	private void printGenericElement(ASTNode element, String str) {
		int maxdepth = collapseLeafs? TreeDepthRecon.findMaxDepth(element) : -1;

		if(collapseLeafs && maxdepth == 1 && siblingsAreLeafs)
			print("  ");
		else
			printIndent();
		
		print(str);
		
		if(collapseLeafs && maxdepth == 2) {
			siblingsAreLeafs = true;
			print("    (");
		} else if(collapseLeafs && maxdepth == 1 && siblingsAreLeafs) {
			
		} else {
			println("");
		}
		indent++;
	}
	
	/* ====================================================== */

	public boolean visit(ASTNode elem) {
		printGenericElement(elem, "#"+toStringExtra(elem) + "");
		return visitChildren && recurseUnconverted;
	}

	public boolean visit(descent.internal.core.dom.Identifier elem) {
		printGenericElement(elem, "#"+toStringExtra(elem) 
				+ " " + elem.string);
		return visitChildren && recurseUnconverted;
	}
	
	public boolean visit(descent.internal.core.dom.QualifiedName elem) {
		printGenericElement(elem, "#"+toStringExtra(elem) 
				+ " " + elem.toString());
		return visitChildren && recurseUnconverted;
	}	
	
	public boolean visit(descent.internal.core.dom.Dsymbol elem) {
		printGenericElement(elem, "#"+toStringExtra(elem) 
				+ " " + ((elem.ident != null) ? elem.ident.string : "<null>"));
		return visitChildren && recurseUnconverted;
	}	
	
	public boolean visit(descent.internal.core.dom.Type elem) {
		printGenericElement(elem, "#"+toStringExtra(elem) 
				+ " TYPE: " + elem.toString());
		return visitChildren && recurseUnconverted;
	}	
	
	public boolean visit(descent.internal.core.dom.TypeQualified elem) {
		printGenericElement(elem, "#"+toStringExtra(elem) 
				+ " TYPE: " + elem.toString());
		return visitChildren && recurseUnconverted;
	}
	
	/* ---------------- Neo ------------------ */
	
	public boolean visit(ASTElement elem) {
		printGenericElement(elem, toStringExtra(elem) + "");
		return visitChildren;
	}
	
	public boolean visit(Entity.QualifiedEnt elem) {
		printGenericElement(elem, toStringExtra(elem) 
				+ " ID: " + elem.toString());
		return visitChildren && visitQualifiedNameChildren;
	}
	
	public boolean visit(DefUnit elem) {
		printGenericElement(elem, toStringExtra(elem) 
				+ " " + elem.name);
		return visitChildren;
	}
	public boolean visit(EntitySingle.Identifier elem) {
		printGenericElement(elem, toStringExtra(elem) 
				+ " " + elem.name);
		return visitChildren;
	}

	public boolean visit(Definition elem) {
		printGenericElement(elem, toStringExtra(elem) + " "
				+ trailString(elem.protection.toString(), " ")
				+ trailString(Def_Modifiers.toString(elem.modifiers), " ")
				+ "=> " + elem.name);
		return visitChildren;
	}
	
	
	/* ---------------------------------- */
	
	public void endVisit(ASTNode element) {
		
		if(collapseLeafs && TreeDepthRecon.findMaxDepth(element) == 2) {
			siblingsAreLeafs = false;
			println(" )");
		}

		indent--;
	}
	public static String toStringAST(ASTNode elem) {
		elem.accept(new ASTPrinter(false));
		return "TODO";
	}


}

