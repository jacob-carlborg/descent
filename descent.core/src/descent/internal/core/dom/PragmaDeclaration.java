package descent.internal.core.dom;

import java.util.List;

import descent.core.dom.ASTVisitor;
import descent.core.dom.IDeclaration;
import descent.core.dom.IExpression;
import descent.core.dom.ISimpleName;
import descent.core.dom.IPragmaDeclaration;

public class PragmaDeclaration extends Declaration implements IPragmaDeclaration {
	
	private IExpression[] expressions;
	private IDeclaration[] declDefs;

	public PragmaDeclaration(Identifier ident, List<Expression> args, List<Declaration> a) {
		this.ident = ident;
		if (args != null) {
			expressions = args.toArray(new IExpression[args.size()]);
		}
		if (a != null) {
			a.toArray(new IDeclaration[a.size()]);
		}
	}

	public IExpression[] getArguments() {
		return expressions == null ? new IExpression[0] : expressions;
	}

	public IDeclaration[] getDeclarationDefinitions() {
		return declDefs == null ? ASTNode.NO_DECLARATIONS: declDefs;
	}

	public ISimpleName getIdentifier() {
		return ident;
	}
	
	public int getNodeType0() {
		return PRAGMA_DECLARATION;
	}
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, ident);
			acceptChildren(visitor, expressions);
			acceptChildren(visitor, declDefs);
		}
		visitor.endVisit(this);
	}

}
