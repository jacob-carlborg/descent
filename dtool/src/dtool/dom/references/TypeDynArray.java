/**
 * 
 */
package dtool.dom.references;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.TypeDArray;
import descent.internal.compiler.parser.ast.ASTNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.definitions.DefUnit;
import dtool.dom.definitions.NativeDefUnit;
import dtool.refmodel.DefUnitSearch;
import dtool.refmodel.IScope;
import dtool.refmodel.IScopeNode;

public class TypeDynArray extends CommonRefNative {
	public Reference elemtype;

	public TypeDynArray(TypeDArray elem) {
		setSourceRange(elem);
		this.elemtype = Reference.convertType(elem.next);
	}

	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, elemtype);
		}
		visitor.endVisit(this);
	}

	public String toStringAsElement() {
		return elemtype + "[]";
	}

	public Collection<DefUnit> findTargetDefUnits(boolean findFirstOnly) {
		return DefUnitSearch.wrapResult(IntrinsicDynArray.instance);
	}
	
	
	public static class IntrinsicDynArray extends NativeDefUnit {
		public IntrinsicDynArray() {
			super("<dynamic-array>");
		}
		
		public static final IntrinsicDynArray instance = new IntrinsicDynArray();

		@Override
		public IScopeNode getMembersScope() {
			// TODO Auto-generated method stub
			return null;
		}

		public List<IScope> getSuperScopes() {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public String toStringAsElement() {
			return "T[]";
		}

		public Iterator<? extends ASTNode> getMembersIterator() {
			// TODO Auto-generated method stub
			return null;
		}
	}
}