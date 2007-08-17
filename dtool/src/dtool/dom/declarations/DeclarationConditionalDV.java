package dtool.dom.declarations;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.domX.ASTNode;
import descent.internal.compiler.parser.DVCondition;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.definitions.Symbol;

public class DeclarationConditionalDV extends DeclarationConditional {

	public final Symbol ident;

	public DeclarationConditionalDV(ASTNode elem,
			DVCondition condition, NodeList thendecls, NodeList elsedecls) {
		convertNode(elem);
		if(condition.id != null) {
			this.ident = new Symbol(condition.id.string);
			this.ident.setSourceRange(condition);
		} else
			ident = null;
		this.thendecls = thendecls; 
		this.elsedecls = elsedecls;
	}
	
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, ident);
			TreeVisitor.acceptChildren(visitor, thendecls.nodes);
			TreeVisitor.acceptChildren(visitor, elsedecls.nodes);
		}
		visitor.endVisit(this);
	}

}
