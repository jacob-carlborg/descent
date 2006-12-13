package dtool.dom.ext;

import util.StringUtil;
import descent.core.dom.IElement;
import descent.core.domX.ASTNode;
import descent.internal.core.dom.Type;
import dtool.dom.ASTElement;
import dtool.dom.Def_Modifiers;
import dtool.dom.Definition;
import dtool.dom.EntityReference;
import dtool.dom.SingleEntityRef;
import dtool.dom.SymbolDef;

/**
 * Simple class for printing the AST in indented tree form.
 * DMD AST nodes are printed with a "#" prefix.
 */
public class ASTPrinter extends ASTNeoVisitor {
	
	public boolean collapseLeafs = true; //Print leaf nodes in same line?
	public boolean recurseUnconverted = true; //recurse UncovertedElements?
	public boolean visitChildren = true; // visit the Element's children
	public boolean visitIdentifierChildren = false; 

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
	

	private String trailString(String str, String strtrail) {
		return util.StringUtil.trailString(str, strtrail);
	}
	
	public static String trimmedElementName(Object elem) {
		return elem.getClass().getName().replaceAll("^.*\\.dom\\.", "");
	}
	
	private String nodeToString(ASTNode elem) {
		String name = elem.nodeToString();
		if(printRangeInfo) 
			name += " [" + elem.startPos+"+"+elem.length+"]";
		return name;
	}

	private static ASTPrinter singlePrinter = new ASTPrinter(false);
	public static void printSingleElement(ASTNode elem) {
		singlePrinter.visitChildren = false;
		singlePrinter.indent = 0;
		elem.accept(singlePrinter);
	}
	
	/* ---------------------------------- */
	
	private void printIndent() {
		System.out.print(util.StringUtil.newFilledString(indent, "  "));
	}

	private void printGenericElement(ASTNode element, String str) {
		int maxdepth = collapseLeafs? TreeDepthRecon.findMaxDepth(element) : -1;

		if(collapseLeafs && maxdepth == 1 && siblingsAreLeafs)
			System.out.print("  ");
		else
			printIndent();
		
		System.out.print(str);
		
		if(collapseLeafs && maxdepth == 2) {
			siblingsAreLeafs = true;
			System.out.print("    (");
		} else if(collapseLeafs && maxdepth == 1 && siblingsAreLeafs) {
			
		} else {
			System.out.println();
		}
		indent++;
	}
	
	/* ====================================================== */

	public boolean visit(ASTNode elem) {
		printGenericElement(elem, "#"+nodeToString(elem) + "");
		return visitChildren && recurseUnconverted;
	}

	public boolean visit(descent.internal.core.dom.Identifier elem) {
		printGenericElement(elem, "#"+nodeToString(elem) 
				+ " " + elem.string);
		return visitChildren && recurseUnconverted;
	}
	
	public boolean visit(descent.internal.core.dom.QualifiedName elem) {
		printGenericElement(elem, "#"+nodeToString(elem) 
				+ " " + elem.toString());
		return visitChildren && recurseUnconverted;
	}	
	
	public boolean visit(descent.internal.core.dom.Dsymbol elem) {
		printGenericElement(elem, "#"+nodeToString(elem) 
				+ " " + ((elem.ident != null) ? elem.ident.string : "<null>"));
		return visitChildren && recurseUnconverted;
	}	
	
	public boolean visit(descent.internal.core.dom.Type elem) {
		printGenericElement(elem, "#"+nodeToString(elem) 
				+ " TYPE: " + elem.toString());
		return visitChildren && recurseUnconverted;
	}	
	
	public boolean visit(descent.internal.core.dom.TypeQualified elem) {
		printGenericElement(elem, "#"+nodeToString(elem) 
				+ " TYPE: " + elem.toString());
		return visitChildren && recurseUnconverted;
	}
	
	/* ---------------- Neo ------------------ */
	
	public boolean visit(ASTElement elem) {
		printGenericElement(elem, nodeToString(elem) + "");
		return visitChildren;
	}
	
	public boolean visit(EntityReference elem) {
		printGenericElement(elem, nodeToString(elem) 
				+ " ID: " + StringUtil.collToString(elem.ents, "."));
		return visitChildren && visitIdentifierChildren;
	}
	
	public boolean visit(SymbolDef elem) {
		printGenericElement(elem, nodeToString(elem) 
				+ " " + elem.name);
		return visitChildren;
	}
	public boolean visit(SingleEntityRef.Identifier elem) {
		printGenericElement(elem, nodeToString(elem) 
				+ " " + elem.name);
		return visitChildren;
	}

	public boolean visit(Definition elem) {
		printGenericElement(elem, nodeToString(elem) + " "
				+ trailString(elem.protection.toString(), " ")
				+ trailString(Def_Modifiers.toString(elem.modifiers), " ")
				+ "=> " + elem.name);
		return visitChildren;
	}
	
	
	/* ---------------------------------- */
	
	public void endVisit(ASTNode element) {
		
		if(collapseLeafs && TreeDepthRecon.findMaxDepth(element) == 2) {
			siblingsAreLeafs = false;
			System.out.println(" )");
		}

		indent--;
	}


}

