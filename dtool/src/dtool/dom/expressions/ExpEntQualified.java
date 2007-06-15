package dtool.dom.expressions;

import util.Assert;
import util.tree.TreeVisitor;
import descent.internal.core.dom.DotIdExp;
import descent.internal.core.dom.IdentifierExp;
import dtool.dom.ast.ASTNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.base.EntModuleRoot;
import dtool.dom.base.Entity;
import dtool.dom.base.EntitySingle;
import dtool.dom.definitions.DefUnit;
import dtool.model.EntityResolver;
import dtool.model.IEntQualified;
import dtool.model.IScope;
import dtool.model.IDefUnitReference;

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
		Assert.isTrue(this.rootexp instanceof IDefUnitReference);
		this.ent = EntitySingle.convert(elem.id);

		// fix some range discrepancies
		if(this.rootexp instanceof EntModuleRoot && !this.hasNoSourceRangeInfo()) {
			// range error here
			this.rootexp.startPos = this.startPos;
			this.rootexp.setEndPos(this.ent.getEndPos());
		}
		// Fix some DMD missing ranges 
		if(hasNoSourceRangeInfo()) {
			try {
				setSourceRange(rootexp.startPos, ent.getEndPos()-rootexp.startPos);
			} catch (RuntimeException re) {
				throw new UnsupportedOperationException(re);
			}
		}		
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
		IScope scope = ((IDefUnitReference) rootexp).getTargetScope();
		return EntityResolver.getDefUnitFromScope(scope, ent.name);
	}
	
	@Override
	public IScope getTargetScope() {
		return getTargetDefUnit().getMembersScope();
	}

	public IDefUnitReference getRoot() {
		return (IDefUnitReference) rootexp;
	}

	public EntitySingle getSubEnt() {
		return ent;
	}
}
