package dtool.dom.references;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;

import descent.internal.core.dom.Argument;
import descent.internal.core.dom.TypeFunction;
import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.ASTNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.definitions.DefUnit;
import dtool.dom.references.TypeDynArray.IntrinsicDynArray;
import dtool.refmodel.EntitySearch;
import dtool.refmodel.IScope;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.IntrinsicDefUnit;

/**
 * A delegate type;
 * XXX: Do D delegates have linkage?
 */
public class TypeDelegate extends Entity {

	public Entity rettype;
	public List<Argument> arguments;
	public int varargs;
	//public LINK linkage;
	
	public TypeDelegate(descent.internal.core.dom.TypeDelegate elem) {
		setSourceRange(elem);
		this.rettype = (Entity) DescentASTConverter.convertElem(elem.getReturnType());
		this.varargs = ((TypeFunction) elem.next).varargs;
		//this.linkage = ((TypeFunction) elem.next).linkage;
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, rettype);
			TreeVisitor.acceptChildren(visitor, arguments);
		}
		visitor.endVisit(this);
	}

	
	public Collection<DefUnit> findTargetDefUnits(boolean findFirstOnly) {
		return EntitySearch.wrapResult(IntrinsicDynArray.instance);
	}
	
	public static class IntrinsicDelegate extends IntrinsicDefUnit {
		public static final IntrinsicDelegate instance = new IntrinsicDelegate();


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
