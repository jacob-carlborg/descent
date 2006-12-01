package dtool.dom.ext;

import descent.core.domX.ASTNode;
import dtool.dom.ASTElement;
import dtool.dom.Definition;
import dtool.dom.EModifiers;
import dtool.dom.Symbol;
import dtool.dom.UnconvertedElement;

/**
 * Simple class for printing the AST in indented tree form
 */
public class ASTPrinter extends ASTNeoVisitor {
	
	public boolean collapseLeafs = true;
	public boolean printUnconverted = true;
	
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
		this.printUnconverted = printUnconverted;
	}
	
	private String trailString(String str, String strtrail) {
		return util.StringUtil.trailString(str, strtrail);
	}
	
	public static String trimmedElementName(Object elem) {
		return elem.getClass().getName().replaceAll("^.*\\.dom\\.", "");
	}

	/* ---------------------------------- */
	
	private void printIndent() {
		System.out.print(util.StringUtil.newFilledString(indent, "  "));
	}

	private void printGenericElement(ASTNode element, String str) {
		int maxdepth = TreeReconPatrol.findMaxDepth(element);

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
	
	/* ---------------------------------- */

	public boolean visit(ASTNode element) {
		printGenericElement(element, "#"+element.nodeToString() + "");
		return true;
	}

	public boolean visit(descent.internal.core.dom.Identifier elem) {
		printGenericElement(elem, "#"+elem.nodeToString() 
				+ " " + elem.string);
		return true;
	}
	
	public boolean visit(descent.internal.core.dom.Dsymbol elem) {
		printGenericElement(elem, "#"+elem.nodeToString() 
				+ " " + ((elem.ident != null) ? elem.ident.string : "<null>"));
		return true;
	}	
	
	public boolean visit(descent.internal.core.dom.Type elem) {
		printGenericElement(elem, "#"+elem.nodeToString() 
				+ " TYPE: " + elem.toString());
		return true;
	}	
	
	
	public boolean visit(ASTElement element) {
		printGenericElement(element, element.nodeToString() + "");
		return true;
	}

	public boolean visit(UnconvertedElement element) {
		printGenericElement(element, "!"+element.nodeToString()
				+ " - "+ASTPrinter.trimmedElementName(element.unkelem));
		return printUnconverted;
	}
	
	public boolean visit(Symbol element) {
		printGenericElement(element, element.nodeToString() 
				+ " " + element.name);
		return true;
	}

	public boolean visit(Definition element) {
		printGenericElement(element, element.nodeToString() + " "
				+ trailString(element.protection.toString(), " ")
				+ trailString(EModifiers.toString(element.modifiers), " ")
				+ "=> " + element.name);
		return true;
	}
	
	
	/* ---------------------------------- */
	
	public void endVisit(ASTNode element) {
		int maxdepth = TreeReconPatrol.findMaxDepth(element);
		
		if(collapseLeafs && maxdepth == 2) {
			siblingsAreLeafs = false;
			System.out.println(" )");
		}

		indent--;
	}


}

