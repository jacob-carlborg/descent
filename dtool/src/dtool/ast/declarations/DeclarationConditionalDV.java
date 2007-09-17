package dtool.ast.declarations;

import static melnorme.miscutil.Assert.assertTrue;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.DVCondition;
import descent.internal.compiler.parser.DebugCondition;
import descent.internal.compiler.parser.VersionCondition;
import descent.internal.compiler.parser.ast.ASTNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.definitions.Symbol;

public class DeclarationConditionalDV extends DeclarationConditional {

	public final Symbol ident;
	public final boolean isDebug;

	public DeclarationConditionalDV(ASTNode elem,
			DVCondition condition, NodeList thendecls, NodeList elsedecls) {
		convertNode(elem);
		if(condition.ident != null) {
			this.ident = new Symbol(condition.ident);
			this.ident.setSourceRange(condition);
		} else
			ident = null;
		isDebug = condition instanceof DebugCondition;
		if(!isDebug)
			assertTrue(condition instanceof VersionCondition);
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
	
	@Override
	public String toStringAsElement() {
		if(ident!= null)
			return "["+ (isDebug?"debug":"version") 
				+ "("+ident.toStringAsElement()+")]";
		else 
			return "["+ (isDebug?"debug":"version")+"()]";
	}
}
