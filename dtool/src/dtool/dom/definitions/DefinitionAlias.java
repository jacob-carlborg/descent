package dtool.dom.definitions;


import util.tree.TreeVisitor;
import descent.internal.core.dom.AliasDeclaration;
import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.references.Entity;
import dtool.refmodel.IScope;

/**
 * A definition of an alias.
 */
public class DefinitionAlias extends Definition {
	
	public Entity target;
	
	public DefinitionAlias(AliasDeclaration elem) {
		convertDsymbol(elem);
		target = (Entity) DescentASTConverter.convertElem(elem.type);
	}
	
	public EArcheType getArcheType() {
		return EArcheType.Alias; // XXX: use efective archetype?
	}

	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, target);
			TreeVisitor.acceptChild(visitor, defname);
		}
		visitor.endVisit(this);
	}

	@Override
	public IScope getMembersScope() {
		return target.getTargetScope();
	}

}