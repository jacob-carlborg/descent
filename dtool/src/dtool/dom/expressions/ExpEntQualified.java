package dtool.dom.expressions;

import util.Assert;
import util.tree.TreeVisitor;
import descent.internal.core.dom.DotIdExp;
import descent.internal.core.dom.IdentifierExp;
import dtool.dom.ast.ASTNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.base.Entity;
import dtool.dom.base.EntitySingle;
import dtool.dom.definitions.DefUnit;
import dtool.model.EntityResolver;
import dtool.model.IEntQualified;
import dtool.model.IScope;
import dtool.model.IScopeBinding;

public class ExpEntQualified extends Expression implements IEntQualified {

	public ASTNode rootexp; //Entity or Expression
	public EntitySingle ent;

	public ExpEntQualified(DotIdExp elem) {
		convertNode(elem);
		Expression expTemp = Expression.convert(elem.e);
		if(expTemp instanceof ExpEntitySingle) {
			this.rootexp = ((ExpEntitySingle) expTemp).entity;
		} else {
			this.rootexp = Expression.convert(elem.e);
		}
		Assert.isTrue(this.rootexp instanceof IScopeBinding);
		this.ent = EntitySingle.convert(elem.id);
		// Fix some DMD missing ranges 
		if(hasNoSourceRangeInfo())
			setSourceRange(rootexp.startPos, ent.getEndPos()-rootexp.startPos);
		
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
			TreeVisitor.acceptChildren(visitor, rootexp);
			TreeVisitor.acceptChildren(visitor, ent);
		}
		visitor.endVisit(this);	 
	}

	
	public DefUnit getTargetDefUnit() {
		if(getParent() instanceof ExpEntQualified) {
			return ((ExpEntQualified) getParent()).getTargetDefUnit();
		}
		
		IScope scope = ((IScopeBinding) rootexp).getTargetScope();
		return EntityResolver.getDefUnitFromScope(scope, ent.name);
	}

	public IScopeBinding getRoot() {
		return (IScopeBinding) rootexp;
	}

	public EntitySingle getSubEnt() {
		return ent;
	}
}
