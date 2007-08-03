package dtool.dom.definitions;


import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.core.dom.AliasDeclaration;
import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.references.Entity;
import dtool.dom.statements.IStatement;
import dtool.refmodel.IScopeNode;

/**
 * A definition of an alias.
 */
public class DefinitionAlias extends Definition implements IStatement {
	
	public Entity target;
	
	public DefinitionAlias(AliasDeclaration elem) {
		convertDsymbol(elem);
		target = (Entity) DescentASTConverter.convertElem(elem.type);
	}
	
	public EArcheType getArcheType() {
		return EArcheType.Alias;
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
	public IScopeNode getMembersScope() {
		return target.getTargetScope();
	}
	
	@Override
	public String toStringAsCodeCompletion() {
		return defname + " -> " + target.toString();
	}
	
}