package dtool.dom.declarations;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.core.dom.DebugSymbol;
import descent.internal.core.dom.VersionSymbol;
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
	public int kind;
	
	public DeclarationConditionalDefinition(DebugSymbol elem) {
		setSourceRange(elem);
		this.identifier = new Symbol(elem.ident);
		kind = Type.DEBUG;
	}
	
	public DeclarationConditionalDefinition(VersionSymbol elem) {
		setSourceRange(elem);
		this.identifier = new Symbol(elem.ident);
		kind = Type.VERSION;
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, identifier);
		}
		visitor.endVisit(this);
	}

}
