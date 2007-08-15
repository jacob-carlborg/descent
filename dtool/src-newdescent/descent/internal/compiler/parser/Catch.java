package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.domX.IASTVisitor;

public class Catch extends ASTDmdNode {

	public Loc loc;
	public Type type;
	public IdentifierExp id;
	public Statement handler;

	public Catch(Loc loc, Type type, IdentifierExp id, Statement handler) {
		this.loc = loc;
		this.type = type;
		this.id = id;
		this.handler = handler;		
	}
	
	@Override
    public int getNodeType() {
    	return CATCH;
    }

	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, type);
			TreeVisitor.acceptChildren(visitor, id);
			TreeVisitor.acceptChildren(visitor, handler);
		}
		visitor.endVisit(this);
	}
	
}
