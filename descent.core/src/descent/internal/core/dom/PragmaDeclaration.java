package descent.internal.core.dom;

import java.util.List;

import descent.core.dom.IDElementVisitor;
import descent.core.dom.IDeclaration;
import descent.core.dom.IExpression;
import descent.core.dom.IName;
import descent.core.dom.IPragmaDeclaration;

public class PragmaDeclaration extends Dsymbol implements IPragmaDeclaration {
	
	private IExpression[] expressions;
	private IDeclaration[] declDefs;

	public PragmaDeclaration(Loc loc, Identifier ident, List<Expression> args, List<IDeclaration> a) {
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
		return declDefs == null ? AbstractElement.NO_DECLARATIONS: declDefs;
	}

	public IName getIdentifier() {
		return ident;
	}
	
	public int getElementType() {
		return PRAGMA_DECLARATION;
	}
	
	public void accept(IDElementVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, ident);
			acceptChildren(visitor, expressions);
			acceptChildren(visitor, declDefs);
		}
		visitor.endVisit(this);
	}

}
