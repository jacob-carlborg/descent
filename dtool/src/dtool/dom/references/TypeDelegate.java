package dtool.dom.references;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.core.dom.TypeFunction;
import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.ASTNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.definitions.DefUnit;
import dtool.dom.definitions.DefinitionFunction;
import dtool.dom.definitions.IFunctionParameter;
import dtool.dom.definitions.NativeDefUnit;
import dtool.refmodel.DefUnitSearch;
import dtool.refmodel.IScope;
import dtool.refmodel.IScopeNode;

/**
 * A delegate type;
 * XXX: Do D delegates have linkage?
 */
public class TypeDelegate extends CommonRefNative {

	public Reference rettype;
	public List<IFunctionParameter> params;
	public boolean varargs;
	//public LINK linkage;
	
	public TypeDelegate(descent.internal.core.dom.TypeDelegate elem) {
		setSourceRange(elem);
		this.rettype = (Reference) DescentASTConverter.convertElem(elem.getReturnType());
		this.varargs = DefinitionFunction
				.convertVarArgs(((TypeFunction) elem.next).varargs);
		this.params = DescentASTConverter.convertManyL(elem.getArguments(), this.params); 
		//this.linkage = ((TypeFunction) elem.next).linkage;
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, rettype);
			TreeVisitor.acceptChildren(visitor, params);
		}
		visitor.endVisit(this);
	}

	
	public Collection<DefUnit> findTargetDefUnits(boolean findFirstOnly) {
		return DefUnitSearch.wrapResult(IntrinsicDelegate.instance);
	}
	
	@Override
	public String toString() {
		return rettype.toString() + " delegate"  
		+ DefinitionFunction.toStringParameterSig(params, varargs);
	}
	
	public static class IntrinsicDelegate extends NativeDefUnit {
		public IntrinsicDelegate() {
			super("<delegate>");
		}
		
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
