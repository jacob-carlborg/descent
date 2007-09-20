package dtool.ast.references;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.TypeAArray;
import descent.internal.compiler.parser.ast.ASTNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.NativeDefUnit;
import dtool.refmodel.DefUnitSearch;
import dtool.refmodel.IScope;
import dtool.refmodel.IScopeNode;

public class TypeMapArray extends CommonRefNative {
	public Reference keytype;
	public Reference valuetype;

	public TypeMapArray(TypeAArray elem) {
		setSourceRange(elem);
		this.valuetype = ReferenceConverter.convertType(elem.next);
		this.keytype = ReferenceConverter.convertType(elem.index);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, keytype);
			TreeVisitor.acceptChildren(visitor, valuetype);
		}
		visitor.endVisit(this);
	}

	@Override
	public Collection<DefUnit> findTargetDefUnits(boolean findFirstOnly) {
		return DefUnitSearch.wrapResult(IntrinsicMapArray.instance);
	}
	
	@Override
	public String toStringAsElement() {
		return valuetype.toStringAsElement() + "["+keytype.toStringAsElement()+"]";
	}
	
	public static class IntrinsicMapArray extends NativeDefUnit {
		public IntrinsicMapArray() {
			super("<map-array>");
		}
		
		public static final IntrinsicMapArray instance = new IntrinsicMapArray();


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