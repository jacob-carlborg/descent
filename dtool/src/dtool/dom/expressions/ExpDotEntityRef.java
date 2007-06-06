package dtool.dom.expressions;

import util.tree.TreeVisitor;
import descent.internal.core.dom.DotIdExp;
import descent.internal.core.dom.IdentifierExp;
import dtool.dom.ast.ASTNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.base.Entity;
import dtool.dom.base.EntitySingle;

public class ExpDotEntityRef extends Expression {

	public ASTNode exp; //Entity or Expression
	public EntitySingle entity;

	public ExpDotEntityRef(DotIdExp elem) {
		convertNode(elem);
		Expression expTemp = Expression.convert(elem.e);
		if(expTemp instanceof ExpEntityRef) {
			this.exp = ((ExpEntityRef) expTemp).entity;
		} else {		
			this.exp = Expression.convert(elem.e);
		}
		this.entity = EntitySingle.convert(elem.id);
		// Fix some DMD missing ranges 
		if(hasNoSourceRangeInfo())
			setSourceRange(exp.startPos, entity.getEndPos()-exp.startPos);
	}
	
	/*ExpDotEntityRef convert(DotIdExp elem) {
		convertNode(elem);
		Expression exp = Expression.convert(elem.e);
		if(exp instanceof ExpEntityRef) {
			this.exp = ((ExpEntityRef) exp).entity;
		} else {		
			this.exp = Expression.convert(elem.e);
		}
		
		this.entity = EntitySingle.convert(elem.id);
	}*/
	
	public Entity convertRef(IdentifierExp exp) {
		return EntitySingle.convert(exp.id);
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, exp);
			TreeVisitor.acceptChildren(visitor, entity);
		}
		visitor.endVisit(this);	 
	}

}
