package dtool.dom.references;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.core.dom.Argument;
import descent.internal.core.dom.LINK;
import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.ASTNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.definitions.DefUnit;
import dtool.refmodel.EntitySearch;
import dtool.refmodel.IScope;
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

	public Collection<DefUnit> findTargetDefUnits(boolean findFirstOnly) {
		return EntitySearch.wrapResult(IntrinsicFunction.instance);
	}

	
	public static class IntrinsicFunction extends IntrinsicDefUnit {
		public static final IntrinsicFunction instance = new IntrinsicFunction();

		@Override
		public IScopeNode getMembersScope() {
			// TODO Auto-generated method stub
			return null;
		}

		public List<IScope> getSuperScopes() {
			// TODO Auto-generated method stub
			return null;
		}

		public Iterator<? extends ASTNode> getMembersIterator() {
			// TODO Auto-generated method stub
			return null;
		}
	}
}