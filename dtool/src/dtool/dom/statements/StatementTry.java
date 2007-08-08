package dtool.dom.statements;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.core.dom.Catch;
import descent.internal.core.dom.TryCatchStatement;
import descent.internal.core.dom.TryFinallyStatement;
import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.definitions.DefSymbol;
import dtool.dom.definitions.FunctionParameter;
import dtool.dom.references.Reference;

public class StatementTry extends Statement {
	
	public static class CatchClause extends ASTNeoNode {
		
		public FunctionParameter param;
		public IStatement body;

		public CatchClause(Catch elem) {
			convertNode(elem);
			this.body = Statement.convert(elem.handler);
			this.param = new FunctionParameter();
			this.param.type = Reference.convertType(elem.t);
			this.param.defname = new DefSymbol(elem.id, this.param);
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
		if(elem.s instanceof TryCatchStatement){
			convertTryCatch((TryCatchStatement)elem.s);
		} else {
			this.params = new CatchClause[0];
			this.body = Statement.convert(elem.s);
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
