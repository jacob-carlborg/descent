package dtool.ast.statements;

import java.util.Iterator;
import java.util.List;

import melnorme.miscutil.IteratorUtil;
import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.Catch;
import descent.internal.compiler.parser.TryCatchStatement;
import descent.internal.compiler.parser.TryFinallyStatement;
import descent.internal.compiler.parser.ast.IASTNode;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.definitions.FunctionParameter;
import dtool.ast.definitions.IFunctionParameter;
import dtool.ast.definitions.NamelessParameter;
import dtool.descentadapter.DescentASTConverter;
import dtool.refmodel.IScope;
import dtool.refmodel.IScopeNode;

public class StatementTry extends Statement {
	
	public static class CatchClause extends ASTNeoNode implements IScopeNode {
		
		public IFunctionParameter param;
		public IStatement body;

		public CatchClause(Catch elem) {
			convertNode(elem);
			this.body = Statement.convert(elem.handler);
			if(elem.type == null) {
				this.param = null;
			} else if(elem.ident == null) {
				this.param = new NamelessParameter(elem.type);
			} else {
				this.param = new FunctionParameter(elem.type, elem.ident);
			}
		}

		@Override
		public void accept0(IASTNeoVisitor visitor) {
			boolean children = visitor.visit(this);
			if (children) {
				TreeVisitor.acceptChildren(visitor, param);
				TreeVisitor.acceptChildren(visitor, body);
			}
			visitor.endVisit(this);
		}

		public Iterator<? extends IASTNode> getMembersIterator() {
			if(param != null)
				return IteratorUtil.singletonIterator(param);
			return IteratorUtil.getEMPTY_ITERATOR();
		}

		public List<IScope> getSuperScopes() {
			return null;
		}
		
		public boolean hasSequentialLookup() {
			return false;
		}

	}

	public IStatement body;
	public CatchClause[] params;
	public IStatement finallybody;


	public StatementTry(TryCatchStatement elem) {
		convertNode(elem);
		convertTryCatch(elem);
	}

	private void convertTryCatch(TryCatchStatement elem) {
		this.params = new CatchClause[elem.catches.size()];
		DescentASTConverter.convertMany(
				elem.catches.toArray(), this.params);
		this.body = Statement.convert(elem.body);
	}
	
	public StatementTry(TryFinallyStatement elem) {
		convertNode(elem);
		if(elem.body instanceof TryCatchStatement){
			convertTryCatch((TryCatchStatement)elem.body);
		} else {
			this.params = new CatchClause[0];
			this.body = Statement.convert(elem.body);
		}
		this.finallybody =  Statement.convert(elem.finalbody);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, params);
			TreeVisitor.acceptChildren(visitor, body);
			TreeVisitor.acceptChildren(visitor, finallybody);
		}
		visitor.endVisit(this);
	}

}
