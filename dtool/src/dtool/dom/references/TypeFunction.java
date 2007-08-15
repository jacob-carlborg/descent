package dtool.dom.references;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.domX.ASTNode;
import descent.internal.compiler.parser.LINK;
import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.definitions.DefUnit;
import dtool.dom.definitions.DefinitionFunction;
import dtool.dom.definitions.IFunctionParameter;
import dtool.dom.definitions.NativeDefUnit;
import dtool.refmodel.DefUnitSearch;
import dtool.refmodel.IScope;
import dtool.refmodel.IScopeNode;

/**
 * A function pointer type
 */
public class TypeFunction extends CommonRefNative {
	
	public Reference rettype;
	public List<IFunctionParameter> params;
	public int varargs;
	public LINK linkage;

	public TypeFunction(descent.internal.compiler.parser.TypeFunction elem) {
		setSourceRange(elem);
		this.rettype = (Reference) DescentASTConverter.convertElem(elem.rto);
		this.params = DescentASTConverter.convertManyL(elem.parameters, this.params);
		this.varargs = DefinitionFunction.convertVarArgs(elem.varargs);
		this.linkage = elem.linkage;
	}



	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, rettype);
			TreeVisitor.acceptChildren(visitor, params);
		}
		visitor.endVisit(this);
	}

	public Collection<DefUnit> findTargetDefUnits(boolean findFirstOnly) {
		return DefUnitSearch.wrapResult(IntrinsicFunction.instance);
	}
	
	@Override
	public String toString() {
		return rettype.toString() + " function"  
		+ DefinitionFunction.toStringParameterSig(params, varargs);
	}

	
	public static class IntrinsicFunction extends NativeDefUnit {
		public IntrinsicFunction() {
			super("<funtion>");
		}

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