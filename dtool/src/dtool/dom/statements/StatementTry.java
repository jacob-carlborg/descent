package dtool.dom.statements;

import util.tree.TreeVisitor;
import descent.internal.core.dom.Catch;
import descent.internal.core.dom.TryCatchStatement;
import descent.internal.core.dom.TryFinallyStatement;
import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.definitions.FunctionParameter;
import dtool.dom.definitions.DefUnit.Symbol;
import dtool.dom.references.Entity;

public class StatementTry extends Statement {
	
	public static class CatchClause extends ASTNeoNode {
		
		public FunctionParameter param;
		public Statement body;

		public CatchClause(Catch elem) {
			convertNode(elem);
			this.body = Statement.convert(elem.handler);
			this.param = new FunctionParameter();
			this.param.type = Entity.convertType(elem.t);
			this.param.defname = new Symbol(elem.id);
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

	public Statement body;
	public CatchClause[] params;
	public Statement finallybody;


	public StatementTry(TryCatchStatement elem) {
		convertNode(elem);
		convertTryCatch(elem);
	}

	private void convertTryCatch(TryCatchStatement elem) {
		this.params = (CatchClause[]) DescentASTConverter.convertMany(
				elem.catches.toArray(), new CatchClause[elem.catches.size()]);
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
