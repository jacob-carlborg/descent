package dtool.dom.references;

import java.util.List;

import util.tree.TreeVisitor;
import descent.internal.core.dom.Argument;
import descent.internal.core.dom.LINK;
import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.definitions.DefUnit;
import dtool.dom.references.TypeDynArray.IntrinsicDynArray;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.IntrinsicDefUnit;

/**
 * A function pointer type
 */
public class TypeFunction extends Entity {
	
	public Entity rettype;
	public List<Argument> arguments;
	public int varargs;
	public LINK linkage;

	public TypeFunction(descent.internal.core.dom.TypeFunction elem) {
		setSourceRange(elem);
		this.rettype = (Entity) DescentASTConverter.convertElem(elem.getReturnType());
		this.varargs = elem.varargs;
		this.linkage = elem.linkage;
	}

	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, rettype);
			TreeVisitor.acceptChildren(visitor, arguments);
		}
		visitor.endVisit(this);
	}

	@Override
	public DefUnit getTargetDefUnit() {
		return IntrinsicDynArray.instance;
	}
	
	public static class IntrinsicFunction extends IntrinsicDefUnit {
		public static final IntrinsicFunction instance = new IntrinsicFunction();

		public List<DefUnit> getDefUnits() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public IScopeNode getMembersScope() {
			// TODO Auto-generated method stub
			return null;
		}

		public List<IScopeNode> getSuperScopes() {
			// TODO Auto-generated method stub
			return null;
		}
	}
}