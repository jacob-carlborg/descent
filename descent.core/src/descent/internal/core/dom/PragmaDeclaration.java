package descent.internal.core.dom;

import java.util.List;

import descent.core.dom.IDElement;
import descent.core.dom.IDElementVisitor;
import descent.core.dom.IExpression;
import descent.core.dom.IName;
import descent.core.dom.IPragmaDeclaration;

public class PragmaDeclaration extends Dsymbol implements IPragmaDeclaration {
	
	private IExpression[] expressions;
	private IDElement[] declDefs;

	public PragmaDeclaration(Loc loc, Identifier ident, List<Expression> args, List<IDElement> a) {
		this.ident = ident;
		if (args != null) {
			expressions = args.toArray(new IExpression[args.size()]);
		}
		if (a != null) {
			a.toArray(new IDElement[a.size()]);
		}
	}

	public IExpression[] getArguments() {
		return expressions == null ? new IExpression[0] : expressions;
	}

	public IDElement[] getDeclarationDefinitions() {
		return declDefs == null ? AbstractElement.NO_ELEMENTS : declDefs;
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
