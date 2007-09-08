package dtool.dom.declarations;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.DebugSymbol;
import descent.internal.compiler.parser.VersionSymbol;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.definitions.Symbol;

/**
 * Node of type:
 *	version = someident;
 *  debug = someident;   
 */
public class DeclarationConditionalDefinition extends ASTNeoNode {

	public interface Type {
		int DEBUG = 9;
		int VERSION = 10;
	}

	public Symbol identifier;
	public int conditionalKind;
	
	public DeclarationConditionalDefinition(DebugSymbol elem) {
		setSourceRange(elem);
		if(elem.ident != null)
			this.identifier = new Symbol(elem.ident);
		else 
			this.identifier = new Symbol(elem.version.value);
		conditionalKind = Type.DEBUG;
	}
	
	public DeclarationConditionalDefinition(VersionSymbol elem) {
		setSourceRange(elem);
		if(elem.ident != null)
			this.identifier = new Symbol(elem.ident);
		else 
			this.identifier = new Symbol(elem.version.value);
		conditionalKind = Type.VERSION;
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, identifier);
		}
		visitor.endVisit(this);
	}
	
	public String toStringAsElement() {
		return "["+ (conditionalKind == Type.VERSION?"debug":"version") 
			+ "="+identifier.toStringAsElement()+")]";
	}
}
